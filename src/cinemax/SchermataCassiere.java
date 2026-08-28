package cinemax;

import java.util.Scanner;

/**
 * Menu per il bigliettaio autenticato: ricerca e visualizzazione delle
 * prenotazioni, con una vista rapida delle prenotazioni della giornata
 * corrente e una ricerca dedicata per nome/cognome del cliente (utile per
 * il lavoro quotidiano in cassa, senza dover impostare tutti i criteri di
 * ricerca insieme).
 *
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
public class SchermataCassiere {

    /** Il lettore da cui acquisire l'input dell'utente. */
    private Scanner tastiera;
    /** Il motore di ricerca usato per le ricerche di prenotazioni. */
    private MotoreRicerca motoreRicerca;
    /** Il gestore delle prenotazioni, usato per la vista della giornata corrente. */
    private GestoreBiglietti gestoreBiglietti;
    /** Il gestore degli accessi, usato per il logout. */
    private GestoreAccessi gestoreAccessi;

    /**
     * Costruttore che collega la schermata al lettore di input e ai gestori
     * necessari.
     *
     * @param tastiera         il lettore da cui acquisire l'input
     * @param motoreRicerca    il motore di ricerca per le prenotazioni
     * @param gestoreBiglietti il gestore delle prenotazioni
     * @param gestoreAccessi   il gestore degli accessi, per il logout
     */
    public SchermataCassiere(Scanner tastiera, MotoreRicerca motoreRicerca,
            GestoreBiglietti gestoreBiglietti, GestoreAccessi gestoreAccessi) {
        this.tastiera = tastiera;
        this.motoreRicerca = motoreRicerca;
        this.gestoreBiglietti = gestoreBiglietti;
        this.gestoreAccessi = gestoreAccessi;
    }

    /**
     * Avvia il ciclo del menu bigliettaio, mostrando le opzioni disponibili
     * finche' l'utente non sceglie il logout.
     */
    public void avvia() {
        boolean continuare = true;
        while (continuare) {
            System.out.println();
            System.out.println("=== MENU BIGLIETTAIO ===");
            System.out.println("1. Cerca prenotazioni");
            System.out.println("2. Prenotazioni della giornata corrente");
            System.out.println("3. Ricerca per nome/cognome cliente (parziale)");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");
            String scelta = tastiera.nextLine().trim();

            switch (scelta) {
                case "1":
                    cercaPrenotazioni();
                    break;
                case "2":
                    stampaPrenotazioniDiOggi();
                    break;
                case "3":
                    cercaPerNomeCliente();
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
     * Chiede codice, nome/cognome cliente e titolo film (tutti facoltativi)
     * e stampa le prenotazioni trovate tramite
     * {@link MotoreRicerca#cercaPrenotazione}.
     */
    private void cercaPrenotazioni() {
        System.out.print("Codice (invio per saltare): ");
        String codice = leggiOpzionale();
        System.out.print("Nome e cognome cliente (invio per saltare): ");
        String nomeCognome = leggiOpzionale();
        System.out.print("Titolo film (invio per saltare): ");
        String titolo = leggiOpzionale();

        Biglietto[] risultati = motoreRicerca.cercaPrenotazione(codice, nomeCognome, titolo, null, null);
        if (risultati.length == 0) {
            System.out.println("Nessuna prenotazione trovata.");
            return;
        }
        for (int i = 0; i < risultati.length; i++) {
            System.out.println(risultati[i]);
        }
    }

    /**
     * Mostra tutte le prenotazioni relative a proiezioni della giornata
     * odierna, in ordine di orario, tramite
     * {@link GestoreBiglietti#prenotazioniDiOggi()}.
     */
    private void stampaPrenotazioniDiOggi() {
        Biglietto[] prenotazioniOggi = gestoreBiglietti.prenotazioniDiOggi();
        if (prenotazioniOggi.length == 0) {
            System.out.println("Nessuna prenotazione per proiezioni di oggi.");
            return;
        }
        for (int i = 0; i < prenotazioniOggi.length; i++) {
            System.out.println(prenotazioniOggi[i]);
        }
    }

    /**
     * Ricerca dedicata per nome/cognome del cliente (anche parziale), senza
     * dover impostare altri criteri.
     */
    private void cercaPerNomeCliente() {
        System.out.print("Nome e/o cognome cliente (anche parziale): ");
        String nomeCognome = leggiOpzionale();
        if (nomeCognome == null) {
            System.out.println("Devi inserire almeno una parte del nome o cognome.");
            return;
        }
        Biglietto[] risultati = motoreRicerca.cercaPrenotazione(null, nomeCognome, null, null, null);
        if (risultati.length == 0) {
            System.out.println("Nessuna prenotazione trovata per \"" + nomeCognome + "\".");
            return;
        }
        for (int i = 0; i < risultati.length; i++) {
            System.out.println(risultati[i]);
        }
    }

    /**
     * Legge una riga di input, restituendo null se e' vuota.
     * <p>
     * Usato per i criteri di ricerca facoltativi: premere Invio equivale a
     * non applicare quel criterio.
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
}
