package cinemax;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Contiene le regole applicative relative alle prenotazioni: creazione con
 * verifica dei posti disponibili, modifica solo tra date future, eliminazione
 * solo per proiezioni gia' passate (come da specifiche di progetto). Le
 * proiezioni sono referenziate con la loro chiave composta (titolo + data/ora).
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class GestoreBiglietti {

    /** Riferimento all'archivio dei biglietti/prenotazioni. */
    private ArchivioBiglietti archivioBiglietti;

    /** Riferimento all'archivio degli spettacoli in programmazione. */
    private ArchivioSpettacoli archivioSpettacoli;

    /** Riferimento al gestore spettacoli per la verifica della disponibilita' dei posti. */
    private GestoreSpettacoli gestoreSpettacoli;

    /**
     * Costruttore: inizializza il gestore associando gli archivi e i servizi necessari.
     */
    public GestoreBiglietti(ArchivioBiglietti archivioBiglietti, ArchivioSpettacoli archivioSpettacoli,
            GestoreSpettacoli gestoreSpettacoli) {
        this.archivioBiglietti = archivioBiglietti;
        this.archivioSpettacoli = archivioSpettacoli;
        this.gestoreSpettacoli = gestoreSpettacoli;
    }

    /**
     * Crea una nuova prenotazione verificando che lo spettacolo esista e che vi siano
     * posti sufficienti, generando infine il codice univoco del biglietto.
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
     * Cambia la proiezione associata a una prenotazione, verificando che sia la vecchia
     * sia la nuova proiezione siano future e che vi siano posti disponibili.
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
     * Elimina una prenotazione a patto che lo spettacolo sia gia' passato,
     * come richiesto dai requisiti di cancellazione storico.
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
     * Recupera una prenotazione cercandola tramite il suo codice identificativo.
     */
    public Biglietto visualizzaPrenotazione(String codice) {
        return archivioBiglietti.trovaPerCodice(codice);
    }

    /**
     * Restituisce un array contenente tutte le prenotazioni associate a un determinato cliente.
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
     * Restituisce tutte le prenotazioni relative agli spettacoli della giornata odierna,
     * ordinate cronologicamente per orario di proiezione.
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
     * Ordina un array di biglietti in base alla data e ora di proiezione (Selection Sort in-place).
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