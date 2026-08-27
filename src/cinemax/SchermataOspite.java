package cinemax;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Menu per l'utente non autenticato ("ospite"): ricerca e visualizzazione
 * delle proiezioni, nessuna operazione che richiede login.
 * <p>
 * Dopo ogni ricerca i risultati vengono mostrati come elenco numerato
 * (1, 2, 3, ...): il numero e' solo una comodita' della schermata per la
 * selezione (esattamente come chiedeva la specifica: "funzionalita' di
 * selezione da effettuarsi dopo la ricerca") e non viene mai salvato su
 * file. La proiezione scelta viene poi identificata internamente dalla sua
 * chiave composta (titolo + data/ora), presa direttamente dall'oggetto
 * selezionato.
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class SchermataOspite {

    private Scanner tastiera;
    private MotoreRicerca motoreRicerca;
    private GestoreSpettacoli gestoreSpettacoli;

    public SchermataOspite(Scanner tastiera, MotoreRicerca motoreRicerca, GestoreSpettacoli gestoreSpettacoli) {
        this.tastiera = tastiera;
        this.motoreRicerca = motoreRicerca;
        this.gestoreSpettacoli = gestoreSpettacoli;
    }

    public void avvia() {
        System.out.print("Inserisci il titolo (anche parziale) di un film per iniziare: ");
        String titoloIniziale = tastiera.nextLine().trim();
        Spettacolo[] ultimiRisultati = motoreRicerca.cercaProiezione(titoloIniziale, null, null, null, null, null);
        stampaRisultatiNumerati(ultimiRisultati);

        boolean continuare = true;
        while (continuare) {
            System.out.println();
            System.out.println("=== MENU OSPITE ===");
            System.out.println("1. Cerca proiezioni (altri criteri)");
            System.out.println("2. Visualizza dettagli di una proiezione (scegli dall'elenco sopra)");
            System.out.println("3. Lista completa delle proiezioni future");
            System.out.println("0. Torna al menu principale");
            System.out.print("Scelta: ");
            String scelta = tastiera.nextLine().trim();

            switch (scelta) {
                case "1":
                    ultimiRisultati = eseguiRicercaAvanzata();
                    stampaRisultatiNumerati(ultimiRisultati);
                    break;
                case "2":
                    visualizzaDettaglio(ultimiRisultati);
                    break;
                case "3":
                    ultimiRisultati = gestoreSpettacoli.elencoProiezioniFuture();
                    stampaRisultatiNumerati(ultimiRisultati);
                    break;
                case "0":
                    continuare = false;
                    break;
                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }

    private Spettacolo[] eseguiRicercaAvanzata() {
        System.out.print("Titolo (invio per saltare): ");
        String titolo = leggiOpzionale();
        System.out.print("Genere (invio per saltare): ");
        String genere = leggiOpzionale();
        System.out.print("Data/ora inizio intervallo aaaa-mm-gg hh:mm (invio per saltare): ");
        LocalDateTime dataInizio = leggiDataOraOpzionale();
        System.out.print("Data/ora fine intervallo aaaa-mm-gg hh:mm (invio per saltare): ");
        LocalDateTime dataFine = leggiDataOraOpzionale();
        System.out.print("Prezzo minimo (invio per saltare): ");
        Double prezzoMinimo = leggiPrezzoOpzionale();
        System.out.print("Prezzo massimo (invio per saltare): ");
        Double prezzoMassimo = leggiPrezzoOpzionale();

        return motoreRicerca.cercaProiezione(titolo, genere, dataInizio, dataFine, prezzoMinimo, prezzoMassimo);
    }

    private void visualizzaDettaglio(Spettacolo[] risultati) {
        if (risultati.length == 0) {
            System.out.println("Nessuna proiezione tra cui scegliere: esegui prima una ricerca.");
            return;
        }
        System.out.print("Numero della proiezione (0 per annullare): ");
        int scelta = leggiIntero();
        if (scelta < 1 || scelta > risultati.length) {
            System.out.println("Operazione annullata.");
            return;
        }
        stampaDettaglio(risultati[scelta - 1]);
    }

    private void stampaRisultatiNumerati(Spettacolo[] risultati) {
        if (risultati.length == 0) {
            System.out.println("Nessuna proiezione trovata.");
            return;
        }
        for (int i = 0; i < risultati.length; i++) {
            System.out.println((i + 1) + ". " + risultati[i]);
        }
    }

    private void stampaDettaglio(Spettacolo spettacolo) {
        System.out.println("Titolo: " + spettacolo.getFilm().getTitolo());
        System.out.println("Genere: " + spettacolo.getFilm().getGenere());
        System.out.println("Regista: " + spettacolo.getFilm().getRegista());
        System.out.println("Anno: " + spettacolo.getFilm().getAnno());
        System.out.println("Durata: " + spettacolo.getFilm().getDurataMinuti() + " minuti");
        System.out.println("Eta' minima: " + spettacolo.getFilm().getEtaMinima());
        System.out.println("Data e ora: " + spettacolo.getDataOra().format(Spettacolo.FORMATO_SCRITTURA));
        System.out.println("Prezzo biglietto: " + spettacolo.getPrezzoBiglietto() + " EUR");
        System.out.println("Posti liberi: "
                + gestoreSpettacoli.postiLiberi(spettacolo.getFilm().getTitolo(), spettacolo.getDataOra()));
    }

    private String leggiOpzionale() {
        String testo = tastiera.nextLine().trim();
        if (testo.isEmpty()) {
            return null;
        }
        return testo;
    }

    private LocalDateTime leggiDataOraOpzionale() {
        String testo = tastiera.nextLine().trim();
        if (testo.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(testo, Spettacolo.FORMATO_LETTURA);
        } catch (DateTimeParseException erroreFormato) {
            System.out.println("Data non valida, criterio ignorato.");
            return null;
        }
    }

    private Double leggiPrezzoOpzionale() {
        String testo = tastiera.nextLine().trim();
        if (testo.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(testo);
        } catch (NumberFormatException erroreFormato) {
            System.out.println("Valore non valido, criterio ignorato.");
            return null;
        }
    }

    private int leggiIntero() {
        try {
            return Integer.parseInt(tastiera.nextLine().trim());
        } catch (NumberFormatException erroreFormato) {
            return 0;
        }
    }
}