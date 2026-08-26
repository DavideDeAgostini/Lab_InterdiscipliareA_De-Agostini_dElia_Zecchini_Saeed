package cinemax;

import java.time.LocalDateTime;

/**
 * Contiene le regole applicative relative alle prenotazioni: creazione con
 * verifica dei posti disponibili, modifica solo tra date future, eliminazione
 * solo per proiezioni gia' passate (come da specifiche di progetto). Le
 * proiezioni sono referenziate con la loro chiave composta (titolo + data/ora).
 *
 * @author Davide De Agostini
 * @author Luigi d'Elia
 * @author Ahsan Saeed
 * @author Martina Zecchini
 */
public class GestoreBiglietti {
    private ArchivioBiglietti archivioBiglietti;
    private ArchivioSpettacoli archivioSpettacoli;
    private GestoreSpettacoli gestoreSpettacoli;

    public GestoreBiglietti(ArchivioBiglietti archivioBiglietti, ArchivioSpettacoli archivioSpettacoli,
            GestoreSpettacoli gestoreSpettacoli) {
        this.archivioBiglietti = archivioBiglietti;
        this.archivioSpettacoli = archivioSpettacoli;
        this.gestoreSpettacoli = gestoreSpettacoli;
    }

    /**
     * Crea una nuova prenotazione, a patto che il numero di posti richiesti
     * sia disponibile. Genera e restituisce il codice univoco assegnato.
     *
     * @return il codice della prenotazione creata, oppure null in caso di errore
     */
    public String creaPrenotazione(String usernameCliente, String titoloProiezione, LocalDateTime dataOraProiezione,
            int numeroPosti) {
        Spettacolo spettacolo = archivioSpettacoli.trovaPerChiave(titoloProiezione, dataOraProiezione);
        if (spettacolo == null) {
            System.out.println("Proiezione non trovata.");
            return null;
        }
        if (numeroPosti <= 0) {
            System.out.println("Numero di posti non valido.");
            return null;
        }
        int liberi = gestoreSpettacoli.postiLiberi(titoloProiezione, dataOraProiezione);
        if (numeroPosti > liberi) {
            System.out.println("Posti non disponibili: richiesti " + numeroPosti + ", liberi " + liberi +
                    ".");
            return null;
        }
        String codice = GeneratoreCodice.generaCodicePrenotazione(archivioBiglietti.elencoTutti());
        Biglietto nuovo = new Biglietto(codice, usernameCliente, spettacolo.getFilm().getTitolo(),
                spettacolo.getDataOra(), numeroPosti, spettacolo.getPrezzoBiglietto());
        archivioBiglietti.aggiungi(nuovo);
        return codice;
    }

    /**
     * Cambia la proiezione associata a una prenotazione, a patto che sia la
     * vecchia sia la nuova proiezione abbiano data successiva a oggi.
     */
    public boolean modificaPrenotazione(String codice, String nuovoTitolo, LocalDateTime nuovaDataOra) {
        Biglietto biglietto = archivioBiglietti.trovaPerCodice(codice);
        if (biglietto == null) {
            System.out.println("Prenotazione non trovata.");
            return false;
        }
        Spettacolo vecchioSpettacolo = archivioSpettacoli.trovaPerChiave(biglietto.getTitoloFilm(),
                biglietto.getDataOraSpettacolo());
        Spettacolo nuovoSpettacolo = archivioSpettacoli.trovaPerChiave(nuovoTitolo, nuovaDataOra);
        if (vecchioSpettacolo == null || nuovoSpettacolo == null) {
            System.out.println("Proiezione non trovata.");
            return false;
        }
        LocalDateTime adesso = LocalDateTime.now();
        if (!vecchioSpettacolo.getDataOra().isAfter(adesso) ||
                !nuovoSpettacolo.getDataOra().isAfter(adesso)) {
            System.out.println("La modifica e' possibile solo se sia la vecchia sia la nuova data sono future.");
            return false;
        }
        int liberi = gestoreSpettacoli.postiLiberi(nuovoTitolo, nuovaDataOra);
        if (biglietto.getNumeroPosti() > liberi) {
            System.out.println("Posti non disponibili nella nuova proiezione.");
            return false;
        }
        biglietto.setTitoloFilm(nuovoSpettacolo.getFilm().getTitolo());
        biglietto.setDataOraSpettacolo(nuovoSpettacolo.getDataOra());
        biglietto.setCostoUnitario(nuovoSpettacolo.getPrezzoBiglietto());
        archivioBiglietti.salvaSuFile();
        return true;
    }

    /**
     * Elimina una prenotazione, a patto che la proiezione collegata abbia
     * data precedente a oggi (regola esplicitamente richiesta dalle
     * specifiche di progetto).
     */
    public boolean eliminaPrenotazione(String codice) {
        Biglietto biglietto = archivioBiglietti.trovaPerCodice(codice);
        if (biglietto == null) {
            System.out.println("Prenotazione non trovata.");
            return false;
        }
        LocalDateTime adesso = LocalDateTime.now();
        if (!biglietto.getDataOraSpettacolo().isBefore(adesso)) {
            System.out.println("La cancellazione e' possibile solo per proiezioni gia' passate.");
            return false;
        }
        return archivioBiglietti.rimuovi(codice);
    }

    public Biglietto visualizzaPrenotazione(String codice) {
        return archivioBiglietti.trovaPerCodice(codice);
    }

    /** Restituisce tutte le prenotazioni effettuate da un determinato cliente. */
    public Biglietto[] prenotazioniDiCliente(String usernameCliente) {
        Biglietto[] tutti = archivioBiglietti.elencoTutti();
        int conteggio = 0;
        for (int i = 0; i < tutti.length; i++) {
            if (tutti[i].getUsernameCliente().equalsIgnoreCase(usernameCliente)) {
                conteggio++;
            }
        }
        Biglietto[] risultato = new Biglietto[conteggio];
        int indice = 0;
        for (int i = 0; i < tutti.length; i++) {
            if (tutti[i].getUsernameCliente().equalsIgnoreCase(usernameCliente)) {
                risultato[indice] = tutti[i];
                indice++;
            }
        }
        return risultato;
    }
}
