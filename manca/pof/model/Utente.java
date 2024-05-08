package manca.pof.model;

import java.time.LocalDate;

public record Utente(  int id, String name, String surname, LocalDate birthday, String address, String documentId) {
  
  @Override
  public final String toString() {
    var month = birthday.getMonth().getValue();
    var year = birthday.getYear();
    var day = birthday.getDayOfMonth();
    return  "ID: " + id + "\n" +
            "NOME: " + name + "\n" +
            "COGNOME: " + surname + "\n" +
            "DATA DI NASCITA: "+ day + "/" + month + "/" + year + "\n" +
            "INDIRIZZO: " + address +  "\n" +
            "DOCUMENTO DI IDENTITA' N. " + documentId + "\n" ;
  }
} 
  
