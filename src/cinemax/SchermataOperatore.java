package cinemax;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Menu per il proiezionista autenticato: gestione del palinsesto
 * (aggiunta, modifica, cancellazione delle proiezioni). Per modificare o
 * eliminare, prima si cerca (per titolo, anche parziale) e poi si sceglie
 * dall'elenco numerato mostrato a schermo, invece di dover ridigitare
 * titolo e data/ora esatti a memoria.
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class SchermataOperatore {

    /** Scanner per la lettura dei dati inseriti da console. */
    private Scanner tastiera;

    /** Motore per la ricerca e il filtraggio delle proiezioni. */
    private MotoreRicerca motoreRicerca;

    /** Gestore contenente la logica di business e i vincoli sulle proiezioni. */
    private GestoreSpettacoli gestoreSpettacoli;

    /** Gestore per il controllo dell'accesso e la disconnessione della sessione. */
    private GestoreAccessi gestoreAccessi;

    /**
     * Costruttore: inizializza la schermata dell'operatore con lo scanner e i gestori necessari.
     */
    public SchermataOperatore(Scanner tastiera, MotoreRicerca motoreRicerca,
            GestoreSpettacoli gestoreSpettacoli, GestoreAccessi gestoreAccessi) {
        this.tastiera = tastiera;
        this.motoreRicerca = motoreRicerca;
        this.gestoreSpettacoli = gestoreSpettacoli;
        this.gestoreAccessi = gestoreAccessi;
    }

    /**
     * Avvia il ciclo principale del menu per l'operatore/proiezionista.
     */
    public void avvia() {
        boolean continuare = true;
        while (continuare) {
            System.out.println();
            System.out.println("=== MENU PROIEZIONISTA ===");
            System.out.println("1. Aggiungi proiezione");
            System.out.println("2. Modifica proiezione");
            System.out.println("3. Elimina proiezione");
            System.out.println("4. Visualizza palinsesto completo");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");
            String scelta = tastiera.nextLine().trim();

            switch (scelta) {
                case "1":
                    aggiungiProiezione();
                    break;
                case "2":
                    modificaProiezione();
                    break;
                case "3":
                    eliminaProiezione();
                    break;
                case "4":
                    stampaPalinsesto();
                    break;
                case "0":
                    gestoreAccessi.logout();
                    continuare = false;
                    break;
                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }

    /**
     * Raccoglie da console i dati del film e della programmazione per creare un nuovo spettacolo.
     */
    private void aggiungiProiezione() {
        System.out.print("Titolo film: ");
        String titolo = tastiera.nextLine().trim();
        System.out.print("Genere: ");
        String genere = tastiera.nextLine().trim();
        System.out.print("Regista: ");
        String regista = tastiera.nextLine().trim();
        System.out.print("Anno: ");
        int anno = leggiIntero();
        System.out.print("Durata (minuti): ");
        int durata = leggiIntero();
        System.out.print("Eta' minima: ");
        int etaMinima = leggiIntero();
        System.out.print("Data e ora (aaaa-mm-gg hh:mm): ");
        LocalDateTime dataOra = leggiDataOra();
        if (dataOra == null) {
            System.out.println("Data non valida, operazione annullata.");
            return;
        }
        System.out.print("Prezzo biglietto: ");
        double prezzo = leggiDouble();

        Pellicola film = new Pellicola(titolo, genere, regista, anno, durata, etaMinima);
        boolean aggiunta = gestoreSpettacoli.aggiungiProiezione(film, dataOra, prezzo);
        if (aggiunta) {
            System.out.println("Proiezione aggiunta.");
        }
    }

    /**
     * Permette di selezionare una proiezione tramite ricerca e di aggiornarne orario e prezzo.
     */
    private void modificaProiezione() {
        System.out.println("Cerca la proiezione da modificare:");
        Spettacolo scelta = cercaESeleziona();
        if (scelta == null) {
            return;
        }
        System.out.print("Nuova data e ora (aaaa-mm-gg hh:mm): ");
        LocalDateTime nuovaDataOra = leggiDataOra();
        if (nuovaDataOra == null) {
            System.out.println("Data non valida, operazione annullata.");
            return;
        }
        System.out.print("Nuovo prezzo: ");
        double prezzo = leggiDouble();

        boolean modificata = gestoreSpettacoli.modificaProiezione(
                scelta.getFilm().getTitolo(), scelta.getDataOra(), nuovaDataOra, prezzo);
        if (modificata) {
            System.out.println("Proiezione modificata.");
        }
    }

    /**
     * Permette di selezionare una proiezione tramite ricerca e di richiederne la rimozione.
     */
    private void eliminaProiezione() {
        System.out.println("Cerca la proiezione da eliminare:");
        Spettacolo scelta = cercaESeleziona();
        if (scelta == null) {
            return;
        }
        boolean eliminata = gestoreSpettacoli.eliminaProiezione(scelta.getFilm().getTitolo(), scelta.getDataOra());
        if (eliminata) {
            System.out.println("Proiezione eliminata.");
        }
    }

    /**
     * Chiede un titolo (anche parziale), cerca le proiezioni corrispondenti,
     * le mostra in un elenco numerato e chiede di sceglierne una. Restituisce
     * lo Spettacolo scelto, oppure null se l'utente annulla o non ci sono
     * risultati.
     */
    private Spettacolo cercaESeleziona() {
        System.out.print("Titolo (anche parziale, invio per vedere tutte le proiezioni): ");
        String titolo = leggiOpzionale();
        Spettacolo[] risultati = motoreRicerca.cercaProiezione(titolo, null, null, null, null, null);
        if (risultati.length == 0) {
            System.out.println("Nessuna proiezione trovata.");
            return null;
        }
        for (int i = 0; i < risultati.length; i++) {
            System.out.println((i + 1) + ". " + risultati[i]);
        }
        System.out.print("Numero della proiezione (0 per annullare): ");
        int scelta = leggiIntero();
        if (scelta < 1 || scelta > risultati.length) {
            System.out.println("Operazione annullata.");
            return null;
        }
        return risultati[scelta - 1];
    }

    /**
     * Legge una stringa da console e restituisce null se lasciata in bianco.
     */
    private String leggiOpzionale() {
        String testo = tastiera.nextLine().trim();
        if (testo.isEmpty()) {
            return null;
        }
        return testo;
    }

    /**
     * Mostra l'intero palinsesto in ordine cronologico, con l'indicazione se ogni
     * proiezione e' futura o gia' passata.
     */
    private void stampaPalinsesto() {
        Spettacolo[] palinsesto = gestoreSpettacoli.elencoPalinsesto();
        if (palinsesto.length == 0) {
            System.out.println("Nessuna proiezione in archivio.");
            return;
        }
        LocalDateTime adesso = LocalDateTime.now();
        for (int i = 0; i < palinsesto.length; i++) {
            Spettacolo spettacolo = palinsesto[i];
            String stato;
            if (spettacolo.getDataOra().isBefore(adesso)) {
                stato = "[PASSATA]";
            } else {
                stato = "[FUTURA]";
            }
            System.out.println(stato + " " + spettacolo);
        }
    }

    /**
     * Legge un numero intero gestendo l'eventuale errore di conversione del formato.
     */
    private int leggiIntero() {
        try {
            return Integer.parseInt(tastiera.nextLine().trim());
        } catch (NumberFormatException erroreFormato) {
            System.out.println("Valore non valido, considerato 0.");
            return 0;
        }
    }

    /**
     * Legge un valore decimale gestendo l'eventuale errore di conversione del formato.
     */
    private double leggiDouble() {
        try {
            return Double.parseDouble(tastiera.nextLine().trim());
        } catch (NumberFormatException erroreFormato) {
            System.out.println("Valore non valido, considerato 0.");
            return 0;
        }
    }

    /**
     * Legge ed effettua il parsing di data e ora nel formato standard definito per la lettura.
     */
    private LocalDateTime leggiDataOra() {
        String testo = tastiera.nextLine().trim();
        try {
            return LocalDateTime.parse(testo, Spettacolo.FORMATO_LETTURA);
        } catch (DateTimeParseException erroreFormato) {
            System.out.println("Data non valida.");
            return null;
        }
    }
}