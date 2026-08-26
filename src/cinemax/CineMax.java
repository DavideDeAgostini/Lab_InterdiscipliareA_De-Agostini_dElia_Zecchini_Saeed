package cinemax;

import java.util.Scanner;

/**
 * Punto di ingresso dell'applicazione CineMax: inizializza gli archivi,
 * i gestori e avvia il menu iniziale.
 *
 * @author Davide De Agostini
 * @author Luigi d'Elia
 * @author Ahsan Saeed
 * @author Martina Zecchini
 */
public class CineMax {
    public static void main(String[] args) {
        String cartellaDati = "data";
        ArchivioSpettacoli archivioSpettacoli = new ArchivioSpettacoli(cartellaDati + "/proiezioni.csv");
        ArchivioBiglietti archivioBiglietti = new ArchivioBiglietti(cartellaDati + "/prenotazioni.csv");
        ArchivioAccount archivioAccount = new ArchivioAccount(cartellaDati + "/utenti.csv");
        GestoreSpettacoli gestoreSpettacoli = new GestoreSpettacoli(archivioSpettacoli, archivioBiglietti);
        GestoreBiglietti gestoreBiglietti = new GestoreBiglietti(archivioBiglietti, archivioSpettacoli,
                gestoreSpettacoli);
        GestoreAccessi gestoreAccessi = new GestoreAccessi(archivioAccount);
        MotoreRicerca motoreRicerca = new MotoreRicerca(archivioSpettacoli, archivioBiglietti,
                archivioAccount);
        Scanner tastiera = new Scanner(System.in);
        SchermataIniziale schermataIniziale = new SchermataIniziale(
                tastiera, gestoreAccessi, gestoreSpettacoli, gestoreBiglietti, motoreRicerca);
        schermataIniziale.avvia();
        tastiera.close();
        System.out.println("Arrivederci!");
    }
}
