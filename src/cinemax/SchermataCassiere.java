package cinemax;

import java.util.Scanner;

/**
 * Menu per il bigliettaio autenticato: ricerca e visualizzazione delle
 * prenotazioni.
 *
 * @author Davide De Agostini
 * @author Luigi d'Elia
 * @author Ahsan Saeed
 * @author Martina Zecchini
 */
public class SchermataCassiere {
    private Scanner tastiera;
    private MotoreRicerca motoreRicerca;
    private GestoreAccessi gestoreAccessi;

    public SchermataCassiere(Scanner tastiera, MotoreRicerca motoreRicerca, GestoreAccessi gestoreAccessi) {
        this.tastiera = tastiera;
        this.motoreRicerca = motoreRicerca;
        this.gestoreAccessi = gestoreAccessi;
    }

    public void avvia() {
        boolean continuare = true;
        while (continuare) {
            System.out.println();

            System.out.println("=== MENU BIGLIETTAIO ===");
            System.out.println("1. Cerca prenotazioni");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");
            String scelta = tastiera.nextLine().trim();
            switch (scelta) {
                case "1":
                    cercaPrenotazioni();
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

    private String leggiOpzionale() {
        String testo = tastiera.nextLine().trim();
        if (testo.isEmpty()) {
            return null;
        }
        return testo;
    }
}