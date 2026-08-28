package cinemax;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Contiene le regole applicative relative alle prenotazioni: creazione con
 * verifica dei posti disponibili, modifica solo tra date future, eliminazione
 * solo per proiezioni gia' passate (come da specifiche di progetto).
 * <p>
 * Le proiezioni sono referenziate con la loro chiave composta (titolo +
 * data/ora), vedi {@link Spettacolo#corrispondeA(String, LocalDateTime)}.
 *
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
public class GestoreBiglietti {

    /** L'archivio delle prenotazioni su cui il gestore opera. */
    private ArchivioBiglietti archivioBiglietti;
    /** L'archivio delle proiezioni, usato per validare i riferimenti delle prenotazioni. */
    private ArchivioSpettacoli archivioSpettacoli;
    /** Il gestore delle proiezioni, usato per calcolare i posti liberi. */
    private GestoreSpettacoli gestoreSpettacoli;

    /**
     * Costruttore che collega il gestore agli archivi e al gestore delle
     * proiezioni necessari.
     *
     * @param archivioBiglietti  l'archivio delle prenotazioni
     * @param archivioSpettacoli l'archivio delle proiezioni
     * @param gestoreSpettacoli  il gestore delle proiezioni, per il calcolo dei posti liberi
     */
    public GestoreBiglietti(ArchivioBiglietti archivioBiglietti, ArchivioSpettacoli archivioSpettacoli,
            GestoreSpettacoli gestoreSpettacoli) {
        this.archivioBiglietti = archivioBiglietti;
        this.archivioSpettacoli = archivioSpettacoli;
        this.gestoreSpettacoli = gestoreSpettacoli;
    }

    /**
     * Crea una nuova prenotazione, a patto che il numero di posti richiesti
     * sia disponibile.
     * <p>
     * Genera il codice univoco della prenotazione tramite
     * {@link GeneratoreCodice#generaCodicePrenotazione(Biglietto[])}.
     *
     * @param usernameCliente     lo username del cliente che prenota
     * @param titoloProiezione    il titolo della proiezione da prenotare
     * @param dataOraProiezione   la data/ora della proiezione da prenotare
     * @param numeroPosti         il numero di posti richiesti
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
            System.out.println("Posti non disponibili: richiesti " + numeroPosti + ", liberi " + liberi + ".");
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
     *
     * @param codice       il codice della prenotazione da modificare
     * @param nuovoTitolo  il titolo della nuova proiezione
     * @param nuovaDataOra la data/ora della nuova proiezione
     * @return true se la modifica e' andata a buon fine, false altrimenti
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
        if (!vecchioSpettacolo.getDataOra().isAfter(adesso) || !nuovoSpettacolo.getDataOra().isAfter(adesso)) {
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
     *
     * @param codice il codice della prenotazione da eliminare
     * @return true se la prenotazione e' stata trovata ed eliminata, false altrimenti
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

    /**
     * Cerca una singola prenotazione per codice.
     *
     * @param codice il codice della prenotazione da visualizzare
     * @return la prenotazione trovata, oppure null se non esiste
     */
    public Biglietto visualizzaPrenotazione(String codice) {
        return archivioBiglietti.trovaPerCodice(codice);
    }

    /**
     * Restituisce tutte le prenotazioni effettuate da un determinato cliente.
     *
     * @param usernameCliente lo username del cliente di cui cercare le prenotazioni
     * @return un array con le prenotazioni del cliente specificato
     */
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

    /**
     * Restituisce le prenotazioni relative a proiezioni che si tengono
     * nella giornata odierna (stesso giorno di calendario di
     * {@link LocalDate#now()}), ordinate per orario di proiezione.
     * <p>
     * Pensata per il bigliettaio.
     *
     * @return un array con le prenotazioni della giornata odierna, ordinate cronologicamente
     */
    public Biglietto[] prenotazioniDiOggi() {
        Biglietto[] tutti = archivioBiglietti.elencoTutti();
        LocalDate oggi = LocalDate.now();
        int conteggio = 0;
        for (int i = 0; i < tutti.length; i++) {
            if (tutti[i].getDataOraSpettacolo().toLocalDate().equals(oggi)) {
                conteggio++;
            }
        }
        Biglietto[] risultato = new Biglietto[conteggio];
        int indice = 0;
        for (int i = 0; i < tutti.length; i++) {
            if (tutti[i].getDataOraSpettacolo().toLocalDate().equals(oggi)) {
                risultato[indice] = tutti[i];
                indice++;
            }
        }
        ordinaPerDataSpettacolo(risultato);
        return risultato;
    }

    /**
     * Ordina un array di {@link Biglietto} per data/ora della proiezione
     * (selection sort, in place).
     * <p>
     * Metodo di supporto usato esclusivamente da {@link #prenotazioniDiOggi()}.
     *
     * @param elenco l'array di prenotazioni da ordinare
     */
    private void ordinaPerDataSpettacolo(Biglietto[] elenco) {
        for (int i = 0; i < elenco.length - 1; i++) {
            int indiceMinimo = i;
            for (int j = i + 1; j < elenco.length; j++) {
                if (elenco[j].getDataOraSpettacolo().isBefore(elenco[indiceMinimo].getDataOraSpettacolo())) {
                    indiceMinimo = j;
                }
            }
            if (indiceMinimo != i) {
                Biglietto temporaneo = elenco[i];
                elenco[i] = elenco[indiceMinimo];
                elenco[indiceMinimo] = temporaneo;
            }
        }
    }
}
