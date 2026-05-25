import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Rappresenta una singola proiezione cinematografica.
 */
public class Proiezione {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LocalDateTime dataOraProiezione;
    private String titoloFilm;
    private String genere;
    private String regista;
    private int anno;
    private int durataMinuti;
    private int etaMinima;
    private double prezzoBiglietto;

    public Proiezione(String dataOraProiezione, String titoloFilm, String genere,
                      String regista, int anno, int durataMinuti,
                      int etaMinima, double prezzoBiglietto) {
        this.dataOraProiezione = LocalDateTime.parse(dataOraProiezione, FORMATTER);
        this.titoloFilm = titoloFilm;
        this.genere = genere;
        this.regista = regista;
        this.anno = anno;
        this.durataMinuti = durataMinuti;
        this.etaMinima = etaMinima;
        this.prezzoBiglietto = prezzoBiglietto;
    }

    // ── Getter ───────────────────────────────────────────────────────────────

    public LocalDateTime getDataOraProiezione() { return dataOraProiezione; }
    public LocalDate getData() { return dataOraProiezione.toLocalDate(); }
    public LocalTime getOra() { return dataOraProiezione.toLocalTime(); }
    public String getTitoloFilm() { return titoloFilm; }
    public String getGenere() { return genere; }
    public String getRegista() { return regista; }
    public int getAnno() { return anno; }
    public int getDurataMinuti() { return durataMinuti; }
    public int getEtaMinima() { return etaMinima; }
    public double getPrezzoBiglietto() { return prezzoBiglietto; }

    @Override
    public String toString() {
        return String.format("[%s] %-35s | %-12s | %s | %.2f€",
                dataOraProiezione.format(FORMATTER),
                titoloFilm, genere, regista, prezzoBiglietto);
    }
}
