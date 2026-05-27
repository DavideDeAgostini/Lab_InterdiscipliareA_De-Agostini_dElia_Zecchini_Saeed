import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Prenotazione {
      private final String titoloFilm;
      private final LocalDate dataProiezione;
      private final LocalTime oraProiezione;
      private final LocalDateTime timestampPrenotazione;

      public Prenotazione(String titoloFilm, LocalDate dataProiezione, LocalTime oraProiezione) {
            this.titoloFilm           = titoloFilm;
            this.dataProiezione       = dataProiezione;
            this.oraProiezione        = oraProiezione;
            this.timestampPrenotazione = LocalDateTime.now();
      }

      public Prenotazione(String titoloFilm, LocalDate dataProiezione, LocalTime oraProiezione, LocalDateTime timeStamp){
            this.titoloFilm = titoloFilm;
            this.dataProiezione = dataProiezione;
            this.oraProiezione = oraProiezione;
            this.timestampPrenotazione = timeStamp;            
       }

      public String getTitoloFilm() { return titoloFilm; }
      public LocalDate getDataProiezione() { return dataProiezione; }
      public LocalTime getOraProiezione() { return oraProiezione; }
      public LocalDateTime getTimestampPrenotazione() { return timestampPrenotazione; }

      public String toCsvRiga() {
            return String.format("\"%s\",%s,%s,%s",
                    titoloFilm.replace("\"", "\"\""),
                    dataProiezione.format(FMT_DATA),
                    oraProiezione.format(FMT_ORA),
                    timestampPrenotazione.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
      }

      private static final String CSV_HEADER = "titolo_film,data_prenotazione,ora_prenotazione,timestamp";
      private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      private static final DateTimeFormatter FMT_ORA  = DateTimeFormatter.ofPattern("HH:mm");

      private String percorsoFile;
      private final List<Prenotazione> prenotazioni = new ArrayList<>();

      public void avviaPrenotazione(String titolo) throws IOException{
            List<Proiezione> temp = new ArrayList<Proiezione>();
            Proiezioni p = new Proiezioni("proiezioni.csv");
            temp = p.cercaPerTitolo(titolo);

            
      }

      private void salvaSuCsv(Prenotazione p) throws IOException {
        File file = new File(percorsoFile);
        boolean fileNuovo = !file.exists() || file.length() == 0;
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            if (fileNuovo) pw.println(CSV_HEADER);
            pw.println(p.toCsvRiga());
        }
    }

      public Prenotazione aggiungi(Proiezione proiezione)throws IOException{
            Prenotazione p = new Prenotazione(
                  proiezione.getTitoloFilm(),
                  proiezione.getData(),
                  proiezione.getOra()
            );
            prenotazioni.add(p);
            salvaSuCsv(p);
            return p;
      }

      public Prenotazione aggiungi(String titoloFilm,
                                  LocalDate dataProiezione,
                                  LocalTime oraProiezione) throws IOException {
            Prenotazione p = new Prenotazione(titoloFilm, dataProiezione, oraProiezione);
            prenotazioni.add(p);
            salvaSuCsv(p);
            return p;
      }

}
