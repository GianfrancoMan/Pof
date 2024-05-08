package manca.pof.model;

public enum PathOption {
  USERS("assets/utenti.csv"),
  PRODUCTS("assets/prodotti.csv"),
  SALES("assets/vendite.csv");

  /* Instance variables */
  private final String value;

  /* Constructor */
  private PathOption(String value) {
    this.value  = value;
  }

  /* Getter */
  public String value() { return value; }

}
