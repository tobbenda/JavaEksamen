public interface Brukergrensesnitt{
  void giStatus(String status); // gir brukeren informasjon om det som skjer i spillet (for
//eksempel om det stedet spilleren er kommet til).
  int beOmKommando(String spoersmaal, String[] alternativer); // gir brukeren et valg
//(parameteren spoersmaal) med noen alternativer (parameteren alternativer). Metoden
//returnerer et heltall som angir indeksen for det valgte alternativet. 
}
