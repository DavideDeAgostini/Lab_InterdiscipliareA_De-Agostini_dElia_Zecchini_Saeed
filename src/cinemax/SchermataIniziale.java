package cinemax;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Menu iniziale mostrato all'avvio dell'applicazione: login, registrazione
 * o accesso come ospite.
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class SchermataIniziale {

    /** Scanner per la lettura dell'input da console. */
    private Scanner tastiera;

    /** Gestore per l'autenticazione e la registrazione degli account. */
    private GestoreAccessi gestoreAccessi;

    /** Gestore per le operazioni relative alla programmazione degli spettacoli. */
    private GestoreSpettacoli gestoreSpettacoli;

    /** Gestore per le operazioni sulle prenotazioni e biglietti. */
    private GestoreBiglietti gestoreBiglietti;

    /** Motore per la ricerca filtrata di proiezioni e prenotazioni. */
    private MotoreRicerca motoreRicerca;

    /**
     * Inizializza la schermata principale ricevendo i riferimenti a tutti i gestori dell'applicazione.
     */
    public SchermataIniziale(Scanner tastiera, GestoreAccessi gestoreAccessi, GestoreSpettacoli gestoreSpettacoli,
            GestoreBiglietti gestoreBiglietti, MotoreRicerca motoreRicerca) {
        this.tastiera = tastiera;
        this.gestoreAccessi = gestoreAccessi;
        this.gestoreSpettacoli = gestoreSpettacoli;
        this.gestoreBiglietti = gestoreBiglietti;
        this.motoreRicerca = motoreRicerca;
    }

    /**
     * Avvia il ciclo del menu di benvenuto consentendo l'accesso, la registrazione,
     * la navigazione da ospite o l'uscita dal programma.
     */
    public void avvia() {
        boolean continuare = true;
        while (continuare) {
            System.out.println();
            System.out.println("=== CINEMAX ===");
            System.out.println("1. Login");
            System.out.println("2. Registrati come cliente");
            System.out.println("3. Continua come ospite");
            System.out.println("0. Esci");
            System.out.print("Scelta: ");
            String scelta = tastiera.nextLine().trim();

            switch (scelta) {
                case "1":
                    effettuaLogin();
                    break;
                case "2":
                    effettuaRegistrazione();
                    break;
                case "3":
                    new SchermataOspite(tastiera, motoreRicerca, gestoreSpettacoli).avvia();
                    break;
                case "0":
                    continuare = false;
                    break;
                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }

    /**
     * Raccoglie le credenziali da console, autentica l'utente e reindirizza
     * alla schermata specifica in base al ruolo assegnato.
     */
    private void effettuaLogin() {
        System.out.print("Username: ");
        String username = tastiera.nextLine().trim();
        System.out.print("Password: ");
        String password = tastiera.nextLine();

        Account account = gestoreAccessi.login(username, password);
        if (account == null) {
            return;
        }

        System.out.println("Benvenuto/a " + account.getNome() + "!");
        String ruolo = account.getRuolo();
        if (Account.SPETTATORE.equals(ruolo)) {
            new SchermataSpettatore(tastiera, account, motoreRicerca, gestoreSpettacoli, gestoreBiglietti,
                    gestoreAccessi).avvia();
        } else if (Account.OPERATORE.equals(ruolo)) {
            new SchermataOperatore(tastiera, motoreRicerca, gestoreSpettacoli, gestoreAccessi).avvia();
        } else if (Account.CASSIERE.equals(ruolo)) {
            new SchermataCassiere(tastiera, motoreRicerca, gestoreBiglietti, gestoreAccessi).avvia();
        } else {
            System.out.println("Ruolo non riconosciuto.");
        }
    }

    /**
     * Raccoglie i dati anagrafici e registra un nuovo account con profilo SPETTATORE.
     */
    private void effettuaRegistrazione() {
        System.out.print("Nome: ");
        String nome = tastiera.nextLine().trim();
        System.out.print("Cognome: ");
        String cognome = tastiera.nextLine().trim();
        System.out.print("Username: ");
        String username = tastiera.nextLine().trim();
        System.out.print("Password: ");
        String password = tastiera.nextLine();
        System.out.print("Data di nascita aaaa-mm-gg (facoltativa, invio per saltare): ");
        LocalDate dataNascita = leggiDataNascitaOpzionale();
        System.out.print("Domicilio: ");
        String domicilio = tastiera.nextLine().trim();

        boolean creato = gestoreAccessi.registraCliente(nome, cognome, username, password, dataNascita, domicilio);
        if (creato) {
            System.out.println("Registrazione completata! Ora puoi effettuare il login.");
        }
    }

    /**
     * Effettua il parsing della data di nascita in formato ISO (AAAA-MM-GG),
     * restituendo null in caso di campo vuoto o formato non valido.
     */
    private LocalDate leggiDataNascitaOpzionale() {
        String testo = tastiera.nextLine().trim();
        if (testo.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(testo);
        } catch (DateTimeParseException erroreFormato) {
            System.out.println("Data non valida, campo lasciato vuoto.");
            return null;
        }
    }
}