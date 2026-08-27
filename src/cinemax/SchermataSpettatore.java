package cinemax;

import java.util.Scanner;

/**
 * Menu per il cliente autenticato: ricerca proiezioni, creazione e gestione
 * delle proprie prenotazioni.
 * <p>
 * Sia per prenotare sia per scegliere la nuova proiezione quando si modifica
 * una prenotazione, il flusso e' sempre "cerca poi seleziona dall'elenco
 * numerato": il numero mostrato a schermo e' una comodita' della sessione
 * corrente, non un identificativo salvato su file (che resta la chiave
 * composta titolo+data/ora).
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class SchermataSpettatore {

    /** Scanner per la lettura dell'input inserito da tastiera. */
    private Scanner tastiera;

    /** Account dell'utente autenticato nella sessione corrente. */
    private Account account;

    /** Motore per la ricerca filtrata delle proiezioni. */
    private MotoreRicerca motoreRicerca;

    /** Gestore per il recupero delle informazioni sulle proiezioni e disponibilita' posti. */
    private GestoreSpettacoli gestoreSpettacoli;

    /** Gestore contenente la logica applicativa per creazione, modifica ed eliminazione prenotazioni. */
    private GestoreBiglietti gestoreBiglietti;

    /** Gestore per la gestione dello stato della sessione e logout. */
    private GestoreAccessi gestoreAccessi;

    /**
     * Costruttore: inizializza la schermata dello spettatore associando la sessione utente e i moduli applicativi.
     */
    public SchermataSpettatore(Scanner tastiera, Account account, MotoreRicerca motoreRicerca,
            GestoreSpettacoli gestoreSpettacoli, GestoreBiglietti gestoreBiglietti,
            GestoreAccessi gestoreAccessi) {
        this.tastiera = tastiera;
        this.account = account;
        this.motoreRicerca = motoreRicerca;
        this.gestoreSpettacoli = gestoreSpettacoli;
        this.gestoreBiglietti = gestoreBiglietti;
        this.gestoreAccessi = gestoreAccessi;
    }

    /**
     * Avvia il menu principale per lo spettatore, gestendo le operazioni su proiezioni e prenotazioni personali.
     */
    public void avvia() {
        boolean continuare = true;
        while (continuare) {
            System.out.println();
            System.out.println("=== MENU CLIENTE (" + account.getUsername() + ") ===");
            System.out.println("1. Cerca proiezioni");
            System.out.println("2. Prenota");
            System.out.println("3. Visualizza le mie prenotazioni");
            System.out.println("4. Modifica una prenotazione");
            System.out.println("5. Elimina una prenotazione");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");
            String scelta = tastiera.nextLine().trim();

            switch (scelta) {
                case "1":
                    cercaProiezioni();
                    break;
                case "2":
                    creaPrenotazione();
                    break;
                case "3":
                    visualizzaPrenotazioni();
                    break;
                case "4":
                    modificaPrenotazione();
                    break;
                case "5":
                    eliminaPrenotazione();
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
     * Permette la ricerca rapida per titolo e mostra i risultati numerati.
     */
    private void cercaProiezioni() {
        System.out.print("Titolo (invio per saltare): ");
        String titolo = leggiOpzionale();
        Spettacolo[] risultati = motoreRicerca.cercaProiezione(titolo, null, null, null, null, null);
        stampaRisultatiNumerati(risultati);
    }

    /**
     * Guida l'utente nella selezione di uno spettacolo tramite ricerca e finalizza la prenotazione dei posti.
     */
    private void creaPrenotazione() {
        Spettacolo scelta = cercaESeleziona();
        if (scelta == null) {
            return;
        }
        System.out.print("Numero di posti: ");
        int posti;
        try {
            posti = Integer.parseInt(tastiera.nextLine().trim());
        } catch (NumberFormatException erroreFormato) {
            System.out.println("Numero non valido.");
            return;
        }
        String codice = gestoreBiglietti.creaPrenotazione(
                account.getUsername(), scelta.getFilm().getTitolo(), scelta.getDataOra(), posti);
        if (codice != null) {
            System.out.println("Prenotazione creata! Codice: " + codice);
        }
    }

    /**
     * Mostra l'elenco di tutte le prenotazioni associate all'utente corrente.
     */
    private void visualizzaPrenotazioni() {
        Biglietto[] mie = gestoreBiglietti.prenotazioniDiCliente(account.getUsername());
        if (mie.length == 0) {
            System.out.println("Nessuna prenotazione.");
            return;
        }
        for (int i = 0; i < mie.length; i++) {
            System.out.println(mie[i]);
        }
    }

    /**
     * Modifica lo spettacolo associato a un biglietto esistente, richiedendo all'utente di selezionare la nuova proiezione.
     */
    private void modificaPrenotazione() {
        System.out.print("Codice prenotazione: ");
        String codice = tastiera.nextLine().trim();
        System.out.println("Cerca la nuova proiezione:");
        Spettacolo nuovaScelta = cercaESeleziona();
        if (nuovaScelta == null) {
            return;
        }
        boolean modificata = gestoreBiglietti.modificaPrenotazione(
                codice, nuovaScelta.getFilm().getTitolo(), nuovaScelta.getDataOra());
        if (modificata) {
            System.out.println("Prenotazione modificata.");
        }
    }

    /**
     * Richiede l'eliminazione di una prenotazione tramite il suo codice identificativo.
     */
    private void eliminaPrenotazione() {
        System.out.print("Codice prenotazione: ");
        String codice = tastiera.nextLine().trim();
        boolean eliminata = gestoreBiglietti.eliminaPrenotazione(codice);
        if (eliminata) {
            System.out.println("Prenotazione eliminata.");
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
        stampaRisultatiNumerati(risultati);
        if (risultati.length == 0) {
            return null;
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
     * Stampa a schermo i risultati della ricerca numerandoli e indicando i posti disponibili per ciascuno.
     */
    private void stampaRisultatiNumerati(Spettacolo[] risultati) {
        if (risultati.length == 0) {
            System.out.println("Nessuna proiezione trovata.");
            return;
        }
        for (int i = 0; i < risultati.length; i++) {
            Spettacolo spettacolo = risultati[i];
            System.out.println((i + 1) + ". " + spettacolo + " - posti liberi: "
                    + gestoreSpettacoli.postiLiberi(spettacolo.getFilm().getTitolo(), spettacolo.getDataOra()));
        }
    }

    /**
     * Legge una stringa da console, restituendo null se vuota.
     */
    private String leggiOpzionale() {
        String testo = tastiera.nextLine().trim();
        if (testo.isEmpty()) {
            return null;
        }
        return testo;
    }

    /**
     * Legge un valore intero da console gestendo le eccezioni di formato non valido.
     */
    private int leggiIntero() {
        try {
            return Integer.parseInt(tastiera.nextLine().trim());
        } catch (NumberFormatException erroreFormato) {
            return 0;
        }
    }
}