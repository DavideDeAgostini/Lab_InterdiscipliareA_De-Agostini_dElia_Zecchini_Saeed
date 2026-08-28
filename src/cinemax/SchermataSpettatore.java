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
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
public class SchermataSpettatore {

    /** Il lettore da cui acquisire l'input dell'utente. */
    private Scanner tastiera;
    /** L'account del cliente attualmente autenticato. */
    private Account account;
    /** Il motore di ricerca usato per le ricerche di proiezioni. */
    private MotoreRicerca motoreRicerca;
    /** Il gestore delle proiezioni, usato per il calcolo dei posti liberi. */
    private GestoreSpettacoli gestoreSpettacoli;
    /** Il gestore delle prenotazioni, su cui vengono eseguite le operazioni del menu. */
    private GestoreBiglietti gestoreBiglietti;
    /** Il gestore degli accessi, usato per il logout. */
    private GestoreAccessi gestoreAccessi;

    /**
     * Costruttore che collega la schermata al lettore di input, all'account
     * del cliente e ai gestori necessari.
     *
     * @param tastiera          il lettore da cui acquisire l'input
     * @param account           l'account del cliente autenticato
     * @param motoreRicerca     il motore di ricerca per le proiezioni
     * @param gestoreSpettacoli il gestore delle proiezioni
     * @param gestoreBiglietti  il gestore delle prenotazioni
     * @param gestoreAccessi    il gestore degli accessi, per il logout
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
     * Avvia il ciclo del menu cliente, mostrando le opzioni disponibili
     * finche' l'utente non sceglie il logout.
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
     * Chiede un titolo (anche parziale) e stampa le proiezioni trovate
     * tramite {@link MotoreRicerca#cercaProiezione}.
     */
    private void cercaProiezioni() {
        System.out.print("Titolo (invio per saltare): ");
        String titolo = leggiOpzionale();
        Spettacolo[] risultati = motoreRicerca.cercaProiezione(titolo, null, null, null, null, null);
        stampaRisultatiNumerati(risultati);
    }

    /**
     * Cerca e seleziona una proiezione tramite {@link #cercaESeleziona()},
     * chiede il numero di posti desiderato e crea la prenotazione tramite
     * {@link GestoreBiglietti#creaPrenotazione}.
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
     * Stampa tutte le prenotazioni del cliente autenticato, ottenute tramite
     * {@link GestoreBiglietti#prenotazioniDiCliente(String)}.
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
     * Chiede il codice di una prenotazione esistente, cerca e seleziona la
     * nuova proiezione tramite {@link #cercaESeleziona()} e applica la
     * modifica tramite {@link GestoreBiglietti#modificaPrenotazione}.
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
     * Chiede il codice di una prenotazione esistente e la elimina tramite
     * {@link GestoreBiglietti#eliminaPrenotazione(String)}.
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
     * le mostra in un elenco numerato (con i posti liberi) e chiede di
     * sceglierne una.
     *
     * @return lo Spettacolo scelto, oppure null se l'utente annulla o non ci sono risultati
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
     * Stampa un elenco numerato di proiezioni, indicando per ciascuna anche
     * i posti liberi calcolati tramite {@link GestoreSpettacoli#postiLiberi}.
     *
     * @param risultati le proiezioni da stampare, nell'ordine desiderato
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
     * Legge una riga di input, restituendo null se e' vuota.
     *
     * @return il testo inserito (senza spazi iniziali/finali), oppure null se vuoto
     */
    private String leggiOpzionale() {
        String testo = tastiera.nextLine().trim();
        if (testo.isEmpty()) {
            return null;
        }
        return testo;
    }

    /**
     * Legge un numero intero dall'input, restituendo 0 se il testo inserito
     * non e' un numero valido.
     *
     * @return il numero intero inserito, oppure 0 se il formato non e' valido
     */
    private int leggiIntero() {
        try {
            return Integer.parseInt(tastiera.nextLine().trim());
        } catch (NumberFormatException erroreFormato) {
            return 0;
        }
    }
}
