
import java.util.Scanner;
import java.util.Random;

//Klassen Terminal implementerer Brukergrensesnitt. Spiller-objekter som skal
//styres av mennesker skal ha en referanse til et Terminal-objekt.
class Terminal implements Brukergrensesnitt{
  Scanner sc;
  public Terminal(Scanner sc){
    this.sc=sc;
  }
  //giStatus skriver ut parameter-stringen i terminal-vinduet.
  @Override
  public void giStatus(String status){
    System.out.println(status);
  }

  //beOmKommando tar inn et spoersmaal og svaralternativer. Disse skrives ut for
  //brukeren, som faar velge et alternativ gjennom terminalen ved aa skrive et tall.
  @Override
  public int beOmKommando(String spoersmaal, String[] alternativer){
    System.out.println(spoersmaal);
    for(int i=0; i<alternativer.length; i++){
      System.out.printf("%-3d : %s", i, alternativer[i]);
    }
    return(sc.nextInt());
  }
}

//Klassen Terminal implementerer Brukergrensesnitt. Spiller-objekter som skal
//styres av roboter skal ha en referanse til et Robot-objekt.
class Robot implements Brukergrensesnitt{
  //giStatus skriver ut parameter-stringen i terminal-vinduet.
  @Override
  public void giStatus(String status){
    System.out.println(status);
  }
  //beOmKommando for robot returnerer et tilfeldig tall fra 0 til *antall svaralternativer*
  @Override
  public int beOmKommando(String spoersmaal, String[] alternativer){
    Random rand = new Random();
    return (rand.nextInt(alternativer.length));
  }
}
