import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Proiezioni {

    private final List<Proiezione> proiezioni = new ArrayList<>();


    public Proiezioni() {}

    public Proiezioni(String percorsoFile) throws IOException {
        caricaDaCsv(percorsoFile);
    }

    // Caricamento CSV

    /**
     * Carica le proiezioni da un file CSV con le seguenti colonne:
     * data_ora_proiezione, titolo_film, genere, regista, anno,
     * durata_minuti, eta_minima, prezzo_biglietto.
     *
     * @param percorsoFile percorso al file CSV
     * @throws IOException se il file non è leggibile
     */
    public void caricaDaCsv(String percorsoFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(percorsoFile))) {
            String riga = br.readLine(); // salta intestazione
            if (riga == null) return;

            while ((riga = br.readLine()) != null) {
                riga = riga.trim();
                if (riga.isEmpty()) continue;

                // Gestisce campi tra virgolette che contengono virgole
                String[] campi = parseCsvRiga(riga);
                if (campi.length < 8) continue;

                try {
                    Proiezione p = new Proiezione(
                            campi[0].trim(),
                            campi[1].trim(),
                            campi[2].trim(),
                            campi[3].trim(),
                            Integer.parseInt(campi[4].trim()),
                            Integer.parseInt(campi[5].trim()),
                            Integer.parseInt(campi[6].trim()),
                            Double.parseDouble(campi[7].trim())
                    );
                    proiezioni.add(p);
                } catch (Exception e) {
                    System.err.println("Riga CSV non valida (ignorata): " + riga);
                }
            }
        }
        System.out.printf("Caricate %d proiezioni da '%s'%n", proiezioni.size(), percorsoFile);
    }

    /** Parser CSV minimale che rispetta i campi tra doppi apici. */
    private static String[] parseCsvRiga(String riga) {
        List<String> campi = new ArrayList<>();
        StringBuilder corrente = new StringBuilder();
        boolean inApici = false;
        for (char c : riga.toCharArray()) {
            if (c == '"') {
                inApici = !inApici;
            } else if (c == ',' && !inApici) {
                campi.add(corrente.toString());
                corrente.setLength(0);
            } else {
                corrente.append(c);
            }
        }
        campi.add(corrente.toString());
        return campi.toArray(new String[0]);
    }

    // Aggiunta proiezione manuale

    /**
     * Aggiunge una proiezione alla lista.
     *
     * @param proiezione la proiezione da aggiungere
     */
    public void aggiungiProiezione(Proiezione proiezione) {
        if (proiezione != null) proiezioni.add(proiezione);
    }

    // Ricerca per nome

    /**
     * Cerca proiezioni il cui titolo contiene la stringa indicata
     *
     * @param titolo parte del titolo da cercare
     * @return lista delle proiezioni corrispondenti
     */
    public List<Proiezione> cercaPerTitolo(String titolo) {
        String query = titolo.toLowerCase();
        return proiezioni.stream().filter(p -> p.getTitoloFilm().toLowerCase().contains(query)).collect(Collectors.toList());
    }

    // Ricerca per categoria

    /**
     * Cerca proiezioni per genere (case-insensitive, corrispondenza parziale).
     *
     * @param genere il genere da cercare
     * @return lista delle proiezioni corrispondenti
     */
    public List<Proiezione> cercaPerGenere(String genere) {
        String query = genere.toLowerCase();
        return proiezioni.stream().filter(p -> p.getGenere().toLowerCase().contains(query)).collect(Collectors.toList());
    }

    // Ricerca per data

    /**
     * Restituisce tutte le proiezioni in una data specifica.
     *
     * @param data la data da cercare
     * @return lista delle proiezioni in quella data
     */
    public List<Proiezione> cercaPerData(LocalDate data) {
        return proiezioni.stream().filter(p -> p.getData().equals(data)).collect(Collectors.toList());
    }

    /**
     * Restituisce le proiezioni in un intervallo di date (estremi inclusi).
     *
     * @param da  data di inizio
     * @param a   data di fine
     * @return lista delle proiezioni nell'intervallo
     */
    public List<Proiezione> cercaPerIntervalloDati(LocalDate da, LocalDate a) {
        return proiezioni.stream().filter(p -> !p.getData().isBefore(da) && !p.getData().isAfter(a)).collect(Collectors.toList());
    }

    // Ricerca per ora

    /**
     * Restituisce le proiezioni il cui orario rientra in una finestra temporale.
     *
     * @param oraInizio ora minima (inclusa)
     * @param oraFine   ora massima (inclusa)
     * @return lista delle proiezioni nell'intervallo orario
     */
    public List<Proiezione> cercaPerFasceOrarie(LocalTime oraInizio, LocalTime oraFine) {
        return proiezioni.stream().filter(p -> !p.getOra().isBefore(oraInizio) && !p.getOra().isAfter(oraFine)).collect(Collectors.toList());
    }

    // Ricerca

    /**
     * Ricerca avanzata: tutti i parametri sono opzionali (passare {@code null}
     * per ignorarli).
     *
     * @param titolo  parte del titolo (o {@code null})
     * @param genere  genere  (o {@code null})
     * @param data    data specifica (o {@code null})
     * @param oraMin  ora minima (o {@code null})
     * @param oraMax  ora massima (o {@code null})
     * @return lista delle proiezioni che soddisfano tutti i criteri specificati
     */
    public List<Proiezione> cerca(String titolo, String genere,LocalDate data,LocalTime oraMin, LocalTime oraMax) {
        return proiezioni.stream()
                .filter(p -> titolo == null || p.getTitoloFilm().toLowerCase().contains(titolo.toLowerCase()))
                .filter(p -> genere == null || p.getGenere().toLowerCase().contains(genere.toLowerCase()))
                .filter(p -> data   == null || p.getData().equals(data))
                .filter(p -> oraMin == null || !p.getOra().isBefore(oraMin))
                .filter(p -> oraMax == null || !p.getOra().isAfter(oraMax))
                .collect(Collectors.toList());
    }

    // Accesso alla lista completa

    /**
     * Restituisce una copia della lista completa delle proiezioni.
     *
     * @return lista non modificabile di tutte le proiezioni
     */
    public List<Proiezione> getTutte() {
        return List.copyOf(proiezioni);
    }

    /**
     * Numero totale di proiezioni caricate.
     */
    public int size() {
        return proiezioni.size();
    }

    // ── main di esempio ───────────────────────────────────────────────────────

    public static void main(String[] args) throws IOException {
        Proiezioni p = new Proiezioni("proiezioni.csv");

        System.out.println("\n=== Film con 'will' nel titolo ===");
        p.cercaPerTitolo("will").forEach(System.out::println);

        System.out.println("\n=== Genere Drama ===");
        p.cercaPerGenere("Drama").stream().limit(5).forEach(System.out::println);

        System.out.println("\n=== Proiezioni del 2027-12-29 ===");
        p.cercaPerData(LocalDate.of(2027, 12, 29)).forEach(System.out::println);

        System.out.println("\n=== Fasce orarie 10:00–12:00 ===");
        p.cercaPerFasceOrarie(LocalTime.of(10, 0), LocalTime.of(12, 0))
         .stream().limit(5).forEach(System.out::println);

        System.out.println("\n=== Ricerca combinata: Drama, 29-dic, dopo le 12 ===");
        p.cerca(null, "Drama", LocalDate.of(2027, 12, 29),
                LocalTime.of(12, 0), null).forEach(System.out::println);
    }
}
