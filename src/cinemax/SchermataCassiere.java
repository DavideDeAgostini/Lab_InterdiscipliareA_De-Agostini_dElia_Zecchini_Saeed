package cinemax;

import java.util.Scanner;

/**
 * Menu per il bigliettaio autenticato: ricerca e visualizzazione delle
 * prenotazioni, con una vista rapida delle prenotazioni della giornata
 * corrente e una ricerca dedicata per nome/cognome del cliente (utile per
 * il lavoro quotidiano in cassa, senza dover impostare tutti i criteri di
 * ricerca insieme).
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class SchermataCassiere {

    /** Scanner per la lettura dell'input da console. */
    private Scanner tastiera;

    /** Motore di ricerca per filtrare le prenotazioni sui criteri desiderati. */
    private MotoreRicerca motoreRicerca;

    /** Gestore per accedere alla logica di business dei biglietti. */
    private GestoreBiglietti gestoreBiglietti;

    /** Gestore per la gestione dello stato della sessione e logout. */
    private GestoreAccessi gestoreAccessi;

    /**
     * Inizializza la schermata del cassiere iniettando i moduli e lo scanner di input.
     */
    public SchermataCassiere(Scanner tastiera, MotoreRicerca motoreRicerca,
            GestoreBiglietti gestoreBiglietti, GestoreAccessi gestoreAccessi) {
        this.tastiera = tastiera;
        this.motoreRicerca = motoreRicerca;
        this.gestoreBiglietti = gestoreBiglietti;
        this.gestoreAccessi = gestoreAccessi;
    }

    /**
     * Avvia il ciclo principale del menu cassa, gestendo le scelte dell'operatore.
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
     * Acquisisce i filtri opzionali da tastiera ed esegue la ricerca delle prenotazioni.
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
     * Mostra tutte le prenotazioni relative a proiezioni della giornata odierna, in
     * ordine di orario.
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
     * Ricerca dedicata per nome/cognome del cliente (anche parziale), senza dover
     * impostare altri criteri.
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
     * Legge una riga da tastiera restituendo null se vuota, utile per i parametri di ricerca facoltativi.
     */
    private String leggiOpzionale() {
        String testo = tastiera.nextLine().trim();
        if (testo.isEmpty()) {
            return null;
        }
        return testo;
    }
}