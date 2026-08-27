package cinemax;

import java.util.Scanner;

/**
 * Punto di ingresso dell'applicazione CineMax: inizializza gli archivi,
 * i gestori e avvia il menu iniziale.
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class CineMax {

    /**
     * Metodo principale (main): istanzia tutte le componenti del sistema
     * (archivi, gestori logici, motore di ricerca e interfaccia utente),
     * carica i dati su disco e avvia il ciclo principale dell'applicazione.
     */
    public static void main(String[] args) {
        String cartellaDati = "data";

        // Inizializzazione degli archivi per la persistenza su file CSV
        ArchivioSpettacoli archivioSpettacoli = new ArchivioSpettacoli(cartellaDati + "/proiezioni.csv");
        ArchivioBiglietti archivioBiglietti = new ArchivioBiglietti(cartellaDati + "/prenotazioni.csv");
        ArchivioAccount archivioAccount = new ArchivioAccount(cartellaDati + "/utenti.csv");

        // Inizializzazione dei moduli per la logica applicativa
        GestoreSpettacoli gestoreSpettacoli = new GestoreSpettacoli(archivioSpettacoli, archivioBiglietti);
        GestoreBiglietti gestoreBiglietti = new GestoreBiglietti(archivioBiglietti, archivioSpettacoli,
                gestoreSpettacoli);
        GestoreAccessi gestoreAccessi = new GestoreAccessi(archivioAccount);
        MotoreRicerca motoreRicerca = new MotoreRicerca(archivioSpettacoli, archivioBiglietti,
                archivioAccount);

        // Configurazione dello scanner di input e avvio del menu a riga di comando
        Scanner tastiera = new Scanner(System.in);
        SchermataIniziale schermataIniziale = new SchermataIniziale(
                tastiera, gestoreAccessi, gestoreSpettacoli, gestoreBiglietti, motoreRicerca);
        
        schermataIniziale.avvia();

        // Chiusura delle risorse al termine dell'esecuzione
        tastiera.close();
        System.out.println("Arrivederci!");
    }
}