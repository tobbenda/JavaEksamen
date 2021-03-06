


import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;

//Klassen Terminal implementerer Brukergrensesnitt og objektene skal bli referert
//til fra klassen Spiller for å implementere et brukergrensesnitt.
class Terminal implements Brukergrensesnitt{
  Scanner sc;
  public Terminal(Scanner sc){
    this.sc=sc;
  }
  @Override
  public synchronized void giStatus(String status){
    System.out.println(status);
  }

  //beOmKommando tar inn et spoersmaal og svaralternativer. Disse skrives ut for
  //brukeren, som faar velge et alternativ gjennom terminalen ved aa skrive et tall.
  @Override
  public synchronized int beOmKommando(String spoersmaal, String[] alternativer){
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
  //beOmKommando for robot returnerer et tilfeldig tall fra 0 til *antall svaralternativer*
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
  Gjenstand[] gjenstander;  // Oppretter en liste for gjenstander med *kisteplasser* plasser. Eks med 3: (index: 0, 1, 2)  (OPPRETTES EGENTLIG I KONSTRUKTØREN)
  int antallGjenstander; //Hjelpevariabel for å ha kontroll på hvor mange gjenstander som er i kista.
  public Skattekiste() { // Fyller opp random plasser (0 til *kisteplasser* plasser). Må oppdatere int gjenstander.
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
  public synchronized Gjenstand hentGjenstand(){ //Trekker en tilfeldig gjenstand fra lista
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
  public synchronized int settGjenstand(Gjenstand gjenstand){    //returnerer en "magisk" pris med random-faktor - basert på gjenstandens pris
    for(int i=0; i<3; i++){
      if (gjenstander[i]==null){
        gjenstander[i]=gjenstand;             //sett inn gjenstanden
        int pris = gjenstand.hentVerdi();       //hent verdien til gjenstanden
         //siden kista er magisk, varierer prisen med +/+ 20% av verdien til gjenstanden
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
  Sted[] utganger = new Sted[3];
  public String[] utgangBeskrivelser = {"Venstre", "Rett frem", "Hoyre"};
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
  public Sted gaa(int index){
    return(utganger[index]);
  }
}

class VeivalgSted extends Sted{
  public VeivalgSted(String beskrivelse){
    super(beskrivelse);
  }
}


class Terreng{
  Sted startSted;
  ArrayList<Sted> steder = new ArrayList<>();

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
    forrige.utganger[0]=startSted;  // Forhindre en null-pointer på utganger[0] på det bakerste Sted-objektet i lenken.
  }
  public Terreng(boolean flervei) throws FileNotFoundException{
    File stederFil = new File(Spill.filnavnSted);
    Scanner stedScanner = new Scanner(stederFil);
    startSted = new VeivalgSted(stedScanner.nextLine());
    Sted forrige = startSted;
    steder.add(startSted);
    while (stedScanner.hasNextLine()){
      Sted neste = new VeivalgSted(stedScanner.nextLine());
      steder.add(neste);
      forrige.utgang = neste;
      forrige = neste;
    }
    forrige.utganger[0]=startSted;  // Forhindre en null-pointer på utganger[0] på det bakerste Sted-objektet i lenken.
  }
  public Sted hentStart(){
    return startSted;
  }
}

class VeivalgTerreng extends Terreng{
  public VeivalgTerreng() throws FileNotFoundException{
    super(true);
    Sted midlertidigSted = startSted;
    Random randomStedIndex = new Random();
    while (midlertidigSted.utgang!=null){
      midlertidigSted.utganger[0]=midlertidigSted.utgang;
      midlertidigSted.utganger[1]=steder.get(randomStedIndex.nextInt((steder.size())));
      midlertidigSted.utganger[2]=steder.get(randomStedIndex.nextInt((steder.size())));
      midlertidigSted=midlertidigSted.utgang;
    }
  }
}



class Spiller{

  /*
  For aa loese oppgaven med flere traader har jeg gjort flere metoder synkroniserte:
  giStatus og beOmKommando i brukergrensesnittene.
  hentGjenstand og settGjenstand i Skattekiste.
  I tillegg har jeg lagt til en synchronized metode Tur i Spill, slik at ikke turene
  til flere spillere interfererer med hverandre.
  */

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
  public String hentNavn(){
    return navn;
  }
  public int hentFormue(){
    return formue;
  }

  public void nyttTrekk(){
    System.out.println("");
    grensesnitt.giStatus(navn + " sin tur.");
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
class VeivalgSpiller extends Spiller{
  public VeivalgSpiller(Sted startSted, Brukergrensesnitt grensesnitt, String navn){
    super(startSted, grensesnitt, navn);
  }
  @Override
  public void nyttTrekk(){
      System.out.println("");
      grensesnitt.giStatus(navn + " sin tur.");
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
      int veivalg = grensesnitt.beOmKommando("Hvor vil du gå videre?", posisjon.utgangBeskrivelser);
      posisjon = posisjon.gaa(veivalg);
  }
}

class Spill{
  public static int trekk = 5;
  static Spiller[] spillere;
  static Terreng terreng;
  static public Brukergrensesnitt terminalGrensesnitt = new Terminal(new Scanner(System.in));
  static public Brukergrensesnitt robotGrensesnitt = new Robot();
  public static String filnavnSted ="steder.txt";
  public static File gjenstandFil = new File("gjenstander.txt");
  static Scanner gjenstandScanner;
  static ArrayList<Thread> traader = new ArrayList<Thread>();

  public static void main(String[] args) throws FileNotFoundException{
    gjenstandScanner = new Scanner(gjenstandFil);
    Scanner startGameScanner = new Scanner(System.in);

    System.out.println("Hvor mange spillere? (Menneskelige + Roboter) ");
    int antSpillere = startGameScanner.nextInt();
    spillere = new Spiller[antSpillere];

    System.out.println("Vil du spille med enkelt terreng, eller flere mulige veivalg?\n 0: Enkelt\n 1: Veivalg");
    int veiValgType = startGameScanner.nextInt();
    while(veiValgType!= 0 && veiValgType!=1){
      System.out.println("Ugyldig input! Prov paa nytt: ");
      veiValgType = startGameScanner.nextInt();
    }
    System.out.println("Veivalgtype: " + veiValgType);
    if(veiValgType==1){
      terreng= new VeivalgTerreng();
    }else{
      terreng= new Terreng();
    }

    for(int i=0; i<antSpillere; i++){
      Spiller spiller;
      System.out.println("Navn paa spiller? ");
      String spillerNavn = startGameScanner.next();
      System.out.println("Er "+ spillerNavn+ " menneske eller robot?\n 0: Menneske\n 1: Robot");
      int spillerType = startGameScanner.nextInt();
      while (spillerType!=0 && spillerType!=1){
        System.out.println("Ugylding input! Prov igjen. ");
        spillerType = startGameScanner.nextInt();
      }
      if(spillerType==0){
        if(veiValgType==1){
          spiller = new VeivalgSpiller(terreng.hentStart(), terminalGrensesnitt, spillerNavn);
        }else{
          spiller = new Spiller(terreng.hentStart(), terminalGrensesnitt, spillerNavn);
          }
      }else{
        if(veiValgType==1){
          spiller = new VeivalgSpiller(terreng.hentStart(), robotGrensesnitt, spillerNavn);
        }else{
          spiller = new Spiller(terreng.hentStart(), robotGrensesnitt, spillerNavn);
          }
      }
      spillere[i]=spiller;
      Thread t = new Thread() {
        @Override
        public void run() {
          for (int j=0; j<trekk;j++){
            tur(spiller);
          }
        }
      };
      traader.add(t);
      }
      for(Thread traad: traader){
        traad.start();
      }
      for(Thread traad: traader){
        try{
          traad.join();
        }catch(InterruptedException e){
          e.printStackTrace();
        }
      }
      skrivScoreBoard(antSpillere);
      }
      public static synchronized void tur(Spiller s){
        s.nyttTrekk();
      }

      public static void skrivScoreBoard(int antSpillere){
        ArrayList<Spiller> deltakere = new ArrayList<>();
        ArrayList<Spiller> sorterteDeltakere = new ArrayList<>();
        System.out.println("ScoreBoard: ");
        for (int i=0;i<antSpillere;i++){
          deltakere.add(spillere[i]);
        }

        for(int j=0; j<antSpillere; j++){
          Spiller best = deltakere.get(0);
          for(Spiller s: deltakere){
            if(s.formue>best.formue){
              best=s;
            }
          }
          System.out.println(best.hentNavn() + ": " + best.hentFormue() +"kr.");
          deltakere.remove(best);
        }
      }

}
