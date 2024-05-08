package manca.pof.model;

import java.time.LocalDate;

public class Prodotto {
  private int id; 
  private LocalDate date;
  private String brand;
  private String name; 
  private double price;
  private boolean available;

  public Prodotto() {}
  public Prodotto( int id,  LocalDate date,  String brand,  String name, double price,  boolean available){
    this.id = id;
    this.date = date;
    this.brand = brand;
    this.name = name;
    this.price = price;
    this.available = available; 
  }

  @Override
  public final String toString() {
    var month = date.getMonth().getValue();
    var year = date.getYear();
    var day = date.getDayOfMonth();
    return  "ID: " + id + "\n" +
            "NOME PRODOTTO: " + name + "\n" +
            "MARCA: " + brand +  "\n" +
            "PREZZO: " + price + "\n" +
            "DATA DI SCADENZA: " + day + "/" + month + "/" + year + "\n" +
            "DISPONIBILE: " + (available ? "Si" : "No") + "\n" ;
  }

  /***** GETTERS AND SETTERS *****/
  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public LocalDate getDate() {
    return date;
  }
  public void setDate(LocalDate date) {
    this.date = date;
  }
  public String getBrand() {
    return brand;
  }
  public void setBrand(String brand) {
    this.brand = brand;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public double getPrice() {
    return price;
  }
  public void setPrice(double price) {
    this.price = price;
  }
  public boolean isAvailable() {
    return available;
  }
  public void setAvailable(boolean available) {
    this.available = available;
  }


  
}
