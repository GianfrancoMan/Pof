package manca.pof.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import manca.pof.model.PathOption;
import manca.pof.model.Prodotto;
import manca.pof.model.Utente;
import manca.pof.model.Vendita;

public class DataService {
  /* Class variables */
  private static DataService dataService;
  /* Instance variables */
  FileManager fileManager = FileManager.getInstance();
  List<Utente> utenti = new ArrayList<>();
  List<Prodotto> prodotti = new ArrayList<>();
  List<Vendita> vendite = new ArrayList<>();


  /* Constructor */
  private DataService() {
    loadData(PathOption.USERS);
    loadData(PathOption.PRODUCTS);
    loadData(PathOption.SALES);
  }
 
  
  
  /**
   * If there is a DataService Object already instantiated return the current object, otherwise create and return a new DataService instance.
   * @return a DataService instance
   */
  public static DataService getInstance() {
    if(dataService == null)
      return new DataService();
    else
      return dataService;
  }

  
  
  
  /**
   * Filles a List with Data that come from a csv file 
   * @param pathOption Indicates both the csv file path and the type of instance that will be loaded in the list.
   */
  private void loadData(PathOption pathOption) {
    List<String> lines;
    Path path = Paths.get(pathOption.value());
    lines = fileManager.read(path);
    if(!lines.isEmpty()) {
      lines.forEach(line -> {
        if(!line.equals("")) {
          String[] fields = line.split(";");
          createObjects(fields, pathOption);
        }
      });
    }
  }

  
  
  
  
  /**
   * 
   * @param fields Data that comes from a CSV file is associated with one of the Utente, Prodotto, and Vendita instances.
   * @param pathOption Its value indicates which of Utente, Prodotto and Vendita is the instance that needs to be created
   */
  private void createObjects(String[] fields, PathOption pathOption) {
    switch (pathOption) {
      case USERS : utenti.add(createUser(fields));
      break;
      case PRODUCTS : prodotti.add(createProducts(fields));
      break;      
      case SALES : vendite.add(createSale(fields));
      break;
    }
  }

  
  
  
  
  /**
   * Create an Utente Instance based on data received from the utenti.csv file
   * @param fields an array of strings that will be turned  Utente's attributes
   * @return An Utente instance.
   */
  private Utente createUser(String[] fields) {
    String[] temporals = fields[3].split("/"); 
    Utente utente = new Utente(
      Integer.parseInt(fields[0]),
      fields[1],
      fields[2],
      LocalDate.of(Integer.parseInt(temporals[2]), Integer.parseInt(temporals[1]), Integer.parseInt(temporals[0])),
      fields[4],
      fields[5]
    );
    return utente;
  }

   
  
  
  
  /**
   * Create a Prodotto Instance based on data received from the prodotti.csv file
   * @param fields an array of strings that will be turned  Prodotto's attributes
   * @return A Prodotto instance.
   */
  private Prodotto createProducts(String[] fields) {
    String[] temporals = fields[1].split("/"); 
    Prodotto prodotto = new Prodotto(
      Integer.parseInt(fields[0]),
      LocalDate.of(Integer.parseInt(temporals[2]), Integer.parseInt(temporals[1]), Integer.parseInt(temporals[0])),
      fields[2],
      fields[3],
      Double.parseDouble(fields[4]),
      Boolean.parseBoolean(fields[5].trim())
    );
    return prodotto;
  }

  
   
  
  
  
  /**
   * Create a Vendita Instance based on data received from the vendite.csv file
   * @param fields an array of strings that will be turned  Vendita's attributes
   * @return A Vendita instance.
   */
  private Vendita createSale(String[] fields) {
    return new Vendita(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));
  }

  
  
  
  
  /**
   * Check if the id of the instance that option is related exists in the datastore i the case of PRODUCTS option 
   * check also if the product is available;
   * @param id the id of the instance(instance can be one of Prodott, Utente, Vendita).
   * @param option indicates which is the type of instance to check
   * @return 0 if the id is present, 1 if it is not present, 2 if product id is present but the product is not available. 
   */
  public int checkId(int id, PathOption option) {
    return switch (option) {
      case PRODUCTS -> {
        Prodotto prd = this.prodotti.stream().filter(p -> p.getId() == id).findFirst().orElse(null);   
        if(prd == null) yield  1;
        else if (!prd.isAvailable()) yield 2;
        else yield 0;
      }
      case USERS -> {
        Utente utn = this.utenti.stream().filter(u -> u.id() == id).findFirst().orElse(null);   
        if(utn == null) yield 1;
        else yield 0;
      }
      case SALES -> {
        Vendita vdn = this.vendite.stream().filter(v -> v.id() == id).findFirst().orElse(null);   
        if(vdn == null) yield 1;
        else yield 0;
      }  
    };
  }

  


  
  /**
   * Removes a sale from the data store.
   * @param idSale the unique ID of the sale to remove.
   * @return true if operation is successful otherwise false.
   */
  public boolean removeSale(int idSale) {
    Vendita vendita = this.vendite.stream().filter(v -> v.id() == idSale).findFirst().orElse(null);
    if(vendita != null) {
      if(vendite.remove(vendita)) {
        List<String> stringSales = new ArrayList<>();
        vendite.forEach(v -> {
          stringSales.add(Integer.toString(v.id()) + ";" + v.productId() + ";" + v.userId());
        });
        if(this.fileManager.replaceCSVByOption(PathOption.SALES, stringSales)) {
          if(this.fileManager.toggleProductAvailability(vendita.productId())) {
            Prodotto prodotto = prodotti.stream().filter( p -> p.getId() == vendita.productId()).findFirst().orElse(null);
            if(prodotto != null) {
              prodotto.setAvailable(true);
              return true;
            }
          }
        }
      }
    }
    return false;
  }
  
  
  
  
  
  /**
   * Store a new sale in the Data Store 
   * @param inProduct
   * @param idUser
   * @return
   */
  public boolean addSale(int idProduct, int idUser) {
    Integer lastId = this.vendite.stream().map(v -> v.id()).reduce((a,b) -> Math.max(a,b)).orElse(null);
    if(lastId == null) {
      lastId = 3000;
    }

    var productToMakeNotAvailable = prodotti.stream().filter(p -> p.getId() == idProduct).findFirst().orElse(null);
    if(productToMakeNotAvailable != null) {
      productToMakeNotAvailable.setAvailable(false);
      this.fileManager.toggleProductAvailability(idProduct);
      
      Vendita vendita = new Vendita( (lastId + 1) , idProduct, idUser);
      vendite.add(vendita);
      String line = vendita.id() + ";" + vendita.productId() + ";" + vendita.userId() + "\n";
  
      return fileManager.appendByOption(line, PathOption.SALES);
    }

    return false;
  }
  
  

  
  /**
   * Inserts a new user in the data store
   * @param birthDay The birth date of the new user.
   * @param otherFields The other fields necessary to create a new user such as, name, surname, address and document.
   * @return true if the operation is successful otherwise false. 
   */
  public boolean addUser(LocalDate birthDay, String ...otherFields ) {
    Integer lastId = this.utenti.stream().map(u -> u.id()).reduce((a,b) -> Math.max(a,b)).orElse(null);
    if(lastId != null) {
      Utente utente = new Utente(++lastId , otherFields[0], otherFields[1], birthDay, otherFields[2], otherFields[3]);
      if(utente != null) {
        this.utenti.add(utente);
        String dateString = Integer.toString(birthDay.getDayOfMonth()) + "/" +
                            Integer.toString(birthDay.getMonthValue()) + "/" +
                            Integer.toString(birthDay.getYear());
        String line = utente.id() + ";" + utente.name() + ";" +utente.surname() + ";" + dateString + ";" + utente.address() + ";" + utente.documentId() + "\n";    
        return fileManager.appendByOption(line, PathOption.USERS);
      }
    }
    

    return false;
  }




  /**
   * Creates CSV file that contains only tha available products.
   * it does this by transforming any instance of 'Product' contained in the 'products' List,
   * into a string that fits the CSV style. 
   * @return true if operation is successful otherwise false.
   */
  public boolean exports() {
    String fileName = "assets/exported/products_";
    LocalDateTime dateTime = LocalDateTime.now();
    fileName += Integer.toString(dateTime.getDayOfMonth()) +
                Integer.toString(dateTime.getMonthValue()) +
                Integer.toString(dateTime.getYear()) +
                Integer.toString(dateTime.getHour()) +
                Integer.toString(dateTime.getMinute()) +
                Integer.toString(dateTime.getSecond()) + ".csv";
    
    List<Prodotto> availableProducts = prodotti.stream().filter( p -> p.isAvailable() == true).collect(Collectors.toList());
    List<String> availableProductsCSV  = this.prepareToCSV(availableProducts);
    if(availableProductsCSV.size() > 0) {
      return fileManager.writeFile(fileName, availableProductsCSV);
    }
    return false;
  }




  /**
   * Transforms a List instance of Prodotto instances in a List instance of String instances
   * that fits the style of the CSV file 
   * @param products a List instance of Prodotto instances that will be transformed as List instance of String instances.
   * @return a List instance of String instances
   */
  private List<String> prepareToCSV(List<Prodotto> products) {
    List<String> productsString = new ArrayList<>();
    for(Prodotto p : products) {
      String insertingDate = Integer.toString(p.getDate().getDayOfMonth()) + "/" +
                             Integer.toString(p.getDate().getMonthValue()) + "/" +
                             Integer.toString(p.getDate().getYear());
      
      productsString.add(p.getId() + ";" + insertingDate + ";" + p.getName() + ";" + p.getBrand() + ";" + p.getPrice());
    }
    return productsString;
  }





  /**** GETTERS & SETTERS ****/
  public List<Utente> getUtenti() {
    return utenti;
  }
  public void setUtenti(List<Utente> utenti) {
    this.utenti = utenti;
  }
  public List<Prodotto> getProdotti() {
    return prodotti;
  }
  public void setProdotti(List<Prodotto> prodotti) {
    this.prodotti = prodotti;
  }
  public List<Vendita> getVendite() {
    return vendite;
  }
  public void setVendite(List<Vendita> vendite) {
    this.vendite = vendite;
  } 

}


