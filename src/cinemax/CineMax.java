/**
 * CineMax - Laboratorio Interdisciplinare A - a.a. 2025/2026
 * Universita' degli Studi dell'Insubria
 *
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
package cinemax;

import java.util.Scanner;

/**
 * Punto di ingresso dell'applicazione CineMax: inizializza gli archivi,
 * i gestori e avvia il menu iniziale.
 *
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
public class CineMax {
        /**
         * Costruttore privato: la classe espone solo il metodo {@link #main}
         * e non deve essere istanziata.
         */
        private CineMax() {
        }

        /**
         * Punto di ingresso dell'applicazione.
         * <p>
         * Inizializza gli archivi ({@link ArchivioSpettacoli},
         * {@link ArchivioBiglietti}, {@link ArchivioAccount}), i gestori
         * ({@link GestoreSpettacoli}, {@link GestoreBiglietti},
         * {@link GestoreAccessi}, {@link MotoreRicerca}) e avvia
         * {@link SchermataIniziale#avvia()}, che gestisce l'intero ciclo di
         * interazione a terminale.
         *
         * @param args argomenti da linea di comando (non utilizzati)
         */
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
