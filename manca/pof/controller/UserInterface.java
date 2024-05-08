package manca.pof.controller;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.PatternSyntaxException;


import manca.pof.model.PathOption;
import manca.pof.service.DataService;

public class UserInterface {
  private DataService dataService;
  private static final int REPEAT_LOOP = -1;


  /* Constructor */
  public UserInterface() {
    dataService = DataService.getInstance();
  }




  /* Instance methods */
  public void start() {
    int choice = REPEAT_LOOP;
    String message = "";
    do {
      this.clear();
      this.printMenu(message);
      choice = getInput();
      message = choice < 0 ? "Puoi digitare solo i valori presenti nel Menu." : ""; 
    } while (choice < 0);

    this.executeChoice(choice);

  }




  /*Displays the main menu */
  private void printMenu(String msg) {
    for(int i=0; i<22; ++i) System.out.print("-");
    System.out.print(" MENU ");
    for(int i=0; i<23; ++i) System.out.print("-");
    System.out.println("\n\n 1 per visualizzare tutti i prodotti.");
    System.out.println(" 2 per acquistare un prodotto.");
    System.out.println(" 3 per restituire un prodotto.");
    System.out.println(" 4 per aggiungere un nuovo utente");
    System.out.println(" 5 per esportare un file con i prodotti disponibili");
    System.out.println(" 0 per uscire dal programma. ");
    for(int i=0; i<51; ++i) System.out.print("-");
    System.out.println("\n");
    System.out.println(msg);
    System.out.print("Digita la tua scelta: ");

  }





  /*Clear the console leveraging to ANSI Escape Code*/
  private void clear() {
    System.out.print("\033[H\033[2J");  
    System.out.flush();  
  }




  /* read from the standard input  and */
  @SuppressWarnings("resource")
  private int getInput() {
    Scanner sc = new Scanner(System.in);
    int x = REPEAT_LOOP;
    try {
      x = sc.nextInt();
    }catch(InputMismatchException e) {
      x = REPEAT_LOOP;
      return x;
    }
    if (x < 0 || x > 5) {
      x = REPEAT_LOOP;
      return x;
    }

    return x;
  }



  
  /* executes the user choice */
  private void executeChoice(int value) {
    switch (value) {
      case 0:
        System.out.println("Arrivederci .");
        System.exit(0);         
        break;
      case 1:
        this.allProducts();       
        break;
      case 2:
        this.sale();
        break;
      case 3:
        this.productReturn();      
        break;
      case 4:
        this.addUser();          
        break;
      case 5:
        this.exportsAvailable();
        break; 
    }
    this.start();
  }





  /**
   * Displays a formatted list of Utente instances based on the toString() method of the Utente class
   */
  // @SuppressWarnings("resource")
  // private void allUsers() {    
  //   dataService.getUtenti().forEach(System.out::println);
  //   System.out.format("%nPremi qualunque lettera + ENTER per tornare al Menu:  ");
  //   Scanner sc = new Scanner(System.in);
  //   sc.next();
  // }


  


  /**
   * Displays a formatted list of Prodotto instances based on the toString() method of the Prodotto class
   */
  @SuppressWarnings("resource")
  private void allProducts() {    
    dataService.getProdotti().forEach(System.out::println);
    System.out.format("%nPremi qualunque lettera + ENTER per tornare al Menu:  ");
    Scanner sc = new Scanner(System.in);
    sc.next();
  }

  

  
  
  /**
   * Create a new Vendita instance and store its data in the vendite.csv file
   * then mark the product as not available.
   * @return true if the operation is successful otherwise false;
   */
  @SuppressWarnings("resource")
  private void sale() {
    int id = REPEAT_LOOP,
        idProduct = 0,
        idUser = 0;

    System.out.println();
    while ( id == REPEAT_LOOP ) {   
      System.out.print("Digita l'ID del prodotto o 0 per tornare al Menu pricipale: ");   
      Scanner sc = new Scanner(System.in);
      try {
        id = sc.nextInt();
      } catch(InputMismatchException e) {
        id = REPEAT_LOOP;
        System.out.println("\nID NON VALIDO!!!");
      }
      if(id != REPEAT_LOOP) {
        if (id == 0) {
          this.clear();
          this.printMenu("");
        } else {
          if(this.dataService.checkId(id, PathOption.PRODUCTS) == 1) {
            System.out.println("Questo ID Prodotto non è presente nello Store".toUpperCase());
            id = REPEAT_LOOP;
          }
          if(this.dataService.checkId(id, PathOption.PRODUCTS) == 2) {
            System.out.println("questo prodotto non è disponibile".toUpperCase());
            id = REPEAT_LOOP;
          }
          if(this.dataService.checkId(id, PathOption.PRODUCTS) == 0) {
            idProduct = id;
          }
        }
      }
    }
    if(id != 0) {
      System.out.println();
      id = REPEAT_LOOP;
      while ( id == REPEAT_LOOP ) {
        System.out.print("Digita ID dell'utente o 0 per tornare al Menu pricipale: ");   
        Scanner sc = new Scanner(System.in);
        try {
          id = sc.nextInt();
        } catch(InputMismatchException e) {
          id = REPEAT_LOOP;
          System.out.println("\nID NON VALIDO!!!");
        }
        if(id != REPEAT_LOOP) {
          if (id == 0) {
            this.clear();
            this.printMenu("");
          } else {
            if(this.dataService.checkId(id, PathOption.USERS) == 1) {
              System.out.println("Questo ID utente non è presente nello Store".toUpperCase());
              id = REPEAT_LOOP;
            }
            if(this.dataService.checkId(id, PathOption.USERS) == 0) {
              idUser = id;
            }
          }
        }
      }
      if(id != 0) {
        var msg="";
        if(idProduct != 0 &&  idUser != 0) msg = dataService.addSale(idProduct, idUser) ? "Prodotto venduto correttamente" : "Ops Something was wrong!";
        System.out.println(msg);
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {}}
    }
  }

  

  
  
  /**
   * This method asks the user for a code of a purchase that he would like to return.
   * it performs several checks to verifies that:
   * The user id s typing valid input(an integer).
   * The typed code exists in the data store.
   */
  @SuppressWarnings("resource")
  private void productReturn() {    
    int id = REPEAT_LOOP;
    
    while (id == REPEAT_LOOP) {
      System.out.print("Digita ID della vendita o digita 0 per tornare al Menu pricipale: ");    
      Scanner sc = new Scanner(System.in);
      try {
        id = sc.nextInt();
      } catch (Exception e) {
        id = REPEAT_LOOP;
        System.out.println("id non valido.".toUpperCase());
      }
      if(id != REPEAT_LOOP) {
        if(id == 0) {
          this.clear();
          this.printMenu("");
          return;
        } else {
          if(this.dataService.checkId(id, PathOption.SALES) == 1) {
            System.out.println("Questo ID vendita non è presente nello Store".toUpperCase());
            id = REPEAT_LOOP;
          }
        }
      }         
    }    
    var msg = "";
    if(id != REPEAT_LOOP && id != 0 ) {
      msg = (dataService.removeSale(id)) ? "Prodotto restituito correttamente" : "Ops Something was wrong!";
      System.out.println(msg);
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {}
    }
  }





  /**
   * This method provides ui to insert a new user in the data store.
   */
  private void addUser() {
    String msg = "Digita il Nome(per tornare al menu principale m/M): ";
    boolean repeat = false, success = false;
    int stage = 0;
    String name = "", surname = "", address = "", document = "";
    LocalDate birthDay = LocalDate.now();
    System.out.println("\ndigita i dati che richiesti".toUpperCase());
    while (repeat == false) {
      System.out.print(msg);
      String userInput = scannerUtility();
      if(this.isNumber(userInput)) {
        System.out.println("\nFormato non valido".toUpperCase());
        repeat = false;
      } else if (userInput.toLowerCase().equals("m")) {
          this.clear();
          this.printMenu("");
          repeat = true;
          return;
      } else {
        msg = switch (stage) {
          case 0 -> {
            name = userInput;
            stage ++;
            yield "Digita il Cognome(per tornare al menu principale m/M): ";
          }
          case 1 -> {
            surname = userInput;
            stage ++;
            yield "Digita la data di nascita nel formato gg/mm/aaaa(per tornare al menu principale m/M): ";
          }
          case 2 -> {
            if(checkDate(userInput)) {
              String [] dateArr = (userInput.split("/"));
              birthDay = LocalDate.of(Integer.parseInt(dateArr[2]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[0]));
              stage ++;
              yield "Digita l'indirizzo(per tornare al menu principale m/M): ";
            } else {
              System.out.format("%nformato non valido%n");
              yield "Digita la data di nascita nel formato gg/mm/aaaa(per tornare al menu principale m/M): ";
            }
          }
          case 3 -> {
            address = userInput;
            stage ++;
            yield "Digita il numero di un documento di identità(per tornare al menu principale m/M): ";
          }
          case 4 -> {
            document = userInput;
            stage ++;
            success = dataService.addUser(birthDay, name, surname, address, document);
            System.out.println(success ? "il nuovo utente è stato inserito correttamente" : "Opss something was wrong!"); 
            try {
              Thread.sleep(3000);
            } catch(InterruptedException e) {}
            repeat = true;
            yield "";
          }
          default -> {
            repeat = true;
            yield "";
          }
        };
      }
    }
    if(stage == 5)  {
      clear();
      printMenu(msg);
    } 
  }

  
  
  
  /**
   * Exports CSV file containing only available products.
   * The file will be stored in the "pof/assets/exported" directory.
   * The file will be named with the "products" word followed by an underscore and a number derived from the current date-time
   */
  private void exportsAvailable() {
    String success = "Operazione eseguita con successo\npuoi trovare il file nella directory 'assets/exported'.",
           fail = "Opss! something was wrong.";
    System.out.println(this.dataService.exports() ? success : fail);
    try {
      Thread.sleep(3500);
    } catch (InterruptedException e) {}
  }





  private String scannerUtility() {
    @SuppressWarnings("resource")
    Scanner scanner = new Scanner(System.in);
    var result = scanner.nextLine();
    return result.toLowerCase();
  }

  
  
  
  
  private boolean checkDate(String dateString) {
    String [] dateArr = new String[10];
    try {
      dateArr = (dateString.split("/"));
      @SuppressWarnings("unused")
      LocalDate date = LocalDate.of(Integer.parseInt(dateArr[2]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[0]));
    } catch(PatternSyntaxException pse){
      return false;
    } catch(IndexOutOfBoundsException iobe) {
      return false;
    } catch(NumberFormatException nfe) {
      return false;
    } catch(DateTimeException dte) {
      return false;
    }
    if(dateArr[1].length() != 2 || dateArr[0].length() != 2 || dateArr[2].length() != 4) return false;
    return true;
  }

  
  
  
  
  public boolean isNumber(String value) {
    final int VALUE_TO_BE_NUMBER = 0;    
    int counter = 6;
    try { Integer.parseInt(value); } catch(NumberFormatException e) { --counter; }
    try { Long.parseLong(value); } catch(NumberFormatException e) { --counter; }
    try { Short.parseShort(value); } catch(NumberFormatException e) { --counter; }
    try { Byte.parseByte(value); } catch(NumberFormatException e) { --counter; }
    try { Double.parseDouble(value); } catch(NumberFormatException e) { --counter; }
    try { Float.parseFloat(value); } catch(NumberFormatException e) { --counter; }
    return counter > VALUE_TO_BE_NUMBER;
  }

}
