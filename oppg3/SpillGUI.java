
/*
OBS!
Programmet startes ved call på java SpillGUI

*/

import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;

//GUI:
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.text.*;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;

class Terminal implements Brukergrensesnitt{
  Scanner sc;
  public Terminal(Scanner sc){
    this.sc=sc;
  }
  @Override
  public void giStatus(String status){
    System.out.println(status);
  }

  @Override
  public int beOmKommando(String spoersmaal, String[] alternativer){
    System.out.println(spoersmaal);
    for(int i=0; i<alternativer.length; i++){
      System.out.printf("%-3d : %s \n", i, alternativer[i]);
    }
    return(sc.nextInt());
  }
}
class Robot implements Brukergrensesnitt{
  @Override
  public void giStatus(String status){
    System.out.println(status);
  }
  @Override
  public int beOmKommando(String spoersmaal, String[] alternativer){
    Random rand = new Random();
    return (rand.nextInt(alternativer.length));
  }
}
class Gjenstand{
  String navn;
  int verdi;
  public Gjenstand(String navn, int verdi){
    this.navn = navn;
    this.verdi = verdi;
  }
  public String hentNavn(){
    return navn;
  }
  public int hentVerdi(){
    return verdi;
  }
}
class Skattekiste{
  Gjenstand[] gjenstander;
  int antallGjenstander;
  public Skattekiste() {
    gjenstander = new Gjenstand[3];
    Random random = new Random();
    int gjenstanderDenneKista = random.nextInt(4);
    for(int i=0; i<gjenstanderDenneKista; i++){
      String navn = Spill.gjenstandScanner.next();
      int verdi = Integer.parseInt(Spill.gjenstandScanner.next());
      Spill.gjenstandScanner.nextLine();
      if (Spill.gjenstandScanner.hasNextLine()==false){
        try{
          Spill.gjenstandScanner = new Scanner(Spill.gjenstandFil);
        }catch(FileNotFoundException e){
          e.printStackTrace();
        }
      }
      gjenstander[i] = new Gjenstand(navn, verdi);
      antallGjenstander++;
    }
  }
  public Gjenstand hentGjenstand(){
    Random random = new Random();
    Gjenstand gjenstand = null;
    int gjenstandIndex=69;
    while(gjenstand==null){
      gjenstandIndex = random.nextInt(3);
      gjenstand = gjenstander[gjenstandIndex];
    }
    antallGjenstander = antallGjenstander-1;
    gjenstander[gjenstandIndex] = null;
    return gjenstand;
  }
  public int settGjenstand(Gjenstand gjenstand){
    for(int i=0; i<3; i++){
      if (gjenstander[i]==null){
        gjenstander[i]=gjenstand;
        int pris = gjenstand.hentVerdi();
        Random random = new Random();
        int randomProsent = random.nextInt(20);
        int plussEllerMinus = random.nextInt(2);
        if(plussEllerMinus == 0){
          pris = pris - (pris/100)*randomProsent;
          antallGjenstander++;
          return pris;
        }else{
          pris = pris + (pris/100)*randomProsent;
          antallGjenstander++;
          return pris;
        }
      }
    }
    return 0;
    }
    public boolean erFull(){
      if(antallGjenstander==3){
        return true;
      }else{
        return false;
      }
    }
    public boolean erTom(){
      if (antallGjenstander==0){
        return true;
      }else{
        return false;
      }
    }
  }
class Sted{
  String beskrivelse;
  Skattekiste skattekiste;
  Sted utgang;
  public Sted(String beskrivelse){
    this.beskrivelse=beskrivelse;
    settSkattekiste();
  }
  public void settSkattekiste(){
    skattekiste = new Skattekiste();
  }
  public Skattekiste hentSkattekiste(){
    return skattekiste;
  }
  public Sted gaa(){
    return utgang;
  }
}

class Terreng{
  Sted startSted;
  public Terreng() throws FileNotFoundException{
    File stederFil = new File(Spill.filnavnSted);
    Scanner stedScanner = new Scanner(stederFil);
    startSted = new Sted(stedScanner.nextLine());
    Sted forrige = startSted;
    while (stedScanner.hasNextLine()){
      Sted neste = new Sted(stedScanner.nextLine());
      forrige.utgang = neste;
      forrige = neste;
    }
  }
  public Sted hentStart(){
    return startSted;
  }
}

class Spiller{
  Skattekiste ryggsekk;
  String navn;
  int formue = 0;
  Brukergrensesnitt grensesnitt;
  Sted posisjon;

  public Spiller(Sted startSted, Brukergrensesnitt grensesnitt, String navn){
    ryggsekk = new Skattekiste();
    posisjon = startSted;
    this.grensesnitt = grensesnitt;
    this.navn = navn;
  }

  public void nyttTrekk(){
      grensesnitt.giStatus(posisjon.beskrivelse);
      if(ryggsekk.erTom()==false && posisjon.hentSkattekiste().erFull()==false){
        int selge = grensesnitt.beOmKommando("Vil du selge en gjenstand?", new String[]{"Ja", "Nei"});
        while (!(selge == 1 || selge==0)){
          grensesnitt.giStatus("Du maa skrive enten 0 eller 1!");
          selge = grensesnitt.beOmKommando("Vil du selge en gjenstand?", new String[]{"Ja", "Nei"});
        }
        if (selge==0){
          Gjenstand gjenstandSomSelges = ryggsekk.hentGjenstand();
          int salgspris = posisjon.hentSkattekiste().settGjenstand(gjenstandSomSelges);
          formue = formue + salgspris;
          grensesnitt.giStatus("Du har solgt: " + gjenstandSomSelges.hentNavn() + " for " + salgspris + "kr. Din formue er: " + formue);
        }else{
          ;
        }
      }else{
        grensesnitt.giStatus("Enten er sekken din tom, eller kista full. Du faar ikke solgt noe her i dag! ");
      }
      if(ryggsekk.erFull()==false && posisjon.hentSkattekiste().erTom()==false){
        int taMed = grensesnitt.beOmKommando("Vil du ta en gjenstand fra kista?", new String[]{"Ja", "Nei"});
        while(taMed!=0 && taMed!=1){
          grensesnitt.giStatus("Du maa skrive 0 eller 1");
          taMed = grensesnitt.beOmKommando("Vil du ta en gjenstand fra kista?", new String[]{"Ja", "Nei"});
        }
        if (taMed==0){
          for (int i=0; i<3; i++){
            if (ryggsekk.gjenstander[i]==null){
              Gjenstand gjenstandSomTaes = posisjon.hentSkattekiste().hentGjenstand();
              ryggsekk.settGjenstand(gjenstandSomTaes);
            //  ryggsekk.gjenstander[i]= gjenstandSomTaes;
              grensesnitt.giStatus("Du fikk: " + gjenstandSomTaes.hentNavn()+"!");
              break;
            }
          }
        }else{
          ;
        }
      }else{
        grensesnitt.giStatus("Enten er sekken din full, eller så er kista tom. Det blir ingen gjenstand på deg i dag.");
      }
      int veivalg = grensesnitt.beOmKommando("Hvor vil du gå videre?", new String[]{"Ja.."});
      posisjon = posisjon.gaa();
  }
}

class Spill{
  public static int trekk = 5;
  static Spiller spiller;
  static Terreng terreng;
  static public Brukergrensesnitt grensesnitt;
  public static String filnavnSted ="steder.txt";
  public static File gjenstandFil = new File("gjenstander.txt");
  static Scanner gjenstandScanner;
  static String scoreBoard;
  public static void main(String[] args) throws FileNotFoundException{
    gjenstandScanner = new Scanner(gjenstandFil);
    Scanner startGameScanner = new Scanner(System.in);
    System.out.println("Navn paa spiller?");
    String navn = startGameScanner.next();

    //Her kan man velge mellom terminal eller robot
    grensesnitt = new Terminal(new Scanner(System.in));
    //grensesnitt = new Robot();
    terreng = new Terreng();
    spiller = new Spiller(terreng.hentStart(), grensesnitt, navn);
    for(int i=0; i<trekk; i++){
      spiller.nyttTrekk();
    }
    scoreBoard = (navn + ": "+spiller.formue);
    System.out.println(navn + ": "+spiller.formue);
  }
public String hentScoreboard(){
  return scoreBoard;
}
}

public class SpillGUI extends Application{
  public static void main(String[] args) throws FileNotFoundException{
    launch(args);
  }
  @Override
  public void start(Stage primaryStage) throws FileNotFoundException{
    Stage stage = primaryStage;
//Kjor spillet:
    Spill spill = new Spill();
    spill.main(null);
//Sett GUI ScoreBoard:
    //Enkeltelementer:
    Button avsluttButton = new Button("Avslutt");
    avsluttButton.setOnAction(e-> {
      stage.close();
    });
    Text text = new Text(spill.hentScoreboard());
    //Panes:
    VBox scoreBoardPane = new VBox(8);
    scoreBoardPane.setPadding(new Insets(20,20,20,20));
    scoreBoardPane.getChildren().addAll(text, avsluttButton);
    //Scene:
    Scene scene = new Scene(scoreBoardPane, 200, 200);
    //Stage:
    stage.setScene(scene);
    new Thread() {
      @Override
      public void run() {
        try{
          sleep(5000);
        }catch(InterruptedException e){
          e.printStackTrace();
        }
        Platform.exit();
      }
    }.start();
    stage.show();
  }
}
