package cinemax;

import java.time.LocalDateTime;

/**
 * Contiene le regole applicative relative alle proiezioni: creazione senza
 * sovrapposizioni, modifica/eliminazione solo in assenza di prenotazioni,
 * calcolo dei posti liberi. Le proiezioni sono identificate dalla loro
 * chiave composta (titolo del film + data/ora), non da un id numerico.
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class GestoreSpettacoli {

    /** Capienza fissa della sala, come da specifiche di progetto. */
    public static final int CAPIENZA_SALA = 200;

    /** Riferimento all'archivio delle proiezioni. */
    private ArchivioSpettacoli archivioSpettacoli;

    /** Riferimento all'archivio dei biglietti per la verifica dei posti occupati. */
    private ArchivioBiglietti archivioBiglietti;

    /**
     * Inizializza il gestore con i riferimenti agli archivi di spettacoli e biglietti.
     */
    public GestoreSpettacoli(ArchivioSpettacoli archivioSpettacoli, ArchivioBiglietti archivioBiglietti) {
        this.archivioSpettacoli = archivioSpettacoli;
        this.archivioBiglietti = archivioBiglietti;
    }

    /**
     * Aggiunge una nuova proiezione, a patto che non si sovrapponga a una
     * proiezione esistente nell'intervallo temporale considerato.
     */
    public boolean aggiungiProiezione(Pellicola film, LocalDateTime dataOra, double prezzoBiglietto) {
        if (film == null || dataOra == null) {
            System.out.println("Dati proiezione non validi.");
            return false;
        }
        Spettacolo nuovo = new Spettacolo(film, dataOra, prezzoBiglietto);
        if (siSovrappone(nuovo)) {
            System.out.println("La proiezione si sovrappone a una proiezione gia' esistente.");
            return false;
        }
        archivioSpettacoli.aggiungi(nuovo);
        return true;
    }

    /**
     * Verifica se lo spettacolo candidato entra in conflitto temporale
     * con uno di quelli gia' presenti in palinsesto.
     */
    private boolean siSovrappone(Spettacolo candidata) {
        LocalDateTime inizioCandidata = candidata.getDataOra();
        LocalDateTime fineCandidata = candidata.getDataOraFine();
        Spettacolo[] esistenti = archivioSpettacoli.elencoTutti();
        for (int i = 0; i < esistenti.length; i++) {
            Spettacolo altra = esistenti[i];
            LocalDateTime inizioAltra = altra.getDataOra();
            LocalDateTime fineAltra = altra.getDataOraFine();
            boolean sovrapposte = inizioCandidata.isBefore(fineAltra) && inizioAltra.isBefore(fineCandidata);
            if (sovrapposte) {
                return true;
            }
        }
        return false;
    }

    /**
     * Modifica data/ora e prezzo di una proiezione esistente, a patto che
     * non esistano prenotazioni gia' collegate.
     */
    public boolean modificaProiezione(String titolo, LocalDateTime dataOraAttuale, LocalDateTime nuovaDataOra,
            double nuovoPrezzo) {
        if (esistonoPrenotazioniPer(titolo, dataOraAttuale)) {
            System.out.println("Impossibile modificare: esistono prenotazioni per questa proiezione.");
            return false;
        }
        Spettacolo spettacolo = archivioSpettacoli.trovaPerChiave(titolo, dataOraAttuale);
        if (spettacolo == null) {
            System.out.println("Proiezione non trovata.");
            return false;
        }
        if (nuovaDataOra == null) {
            System.out.println("Data non valida.");
            return false;
        }
        spettacolo.setDataOra(nuovaDataOra);
        spettacolo.setPrezzoBiglietto(nuovoPrezzo);
        archivioSpettacoli.salvaSuFile();
        return true;
    }

    /**
     * Elimina una proiezione dall'archivio, solo se non ci sono prenotazioni associate.
     */
    public boolean eliminaProiezione(String titolo, LocalDateTime dataOra) {
        if (esistonoPrenotazioniPer(titolo, dataOra)) {
            System.out.println("Impossibile eliminare: esistono prenotazioni per questa proiezione.");
            return false;
        }
        return archivioSpettacoli.rimuovi(titolo, dataOra);
    }

    /**
     * Controlla se esistono prenotazioni registrate per la specifica proiezione.
     */
    private boolean esistonoPrenotazioniPer(String titolo, LocalDateTime dataOra) {
        Biglietto[] tutti = archivioBiglietti.elencoTutti();
        for (int i = 0; i < tutti.length; i++) {
            if (tutti[i].getTitoloFilm().equalsIgnoreCase(titolo) && tutti[i].getDataOraSpettacolo().equals(dataOra)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Restituisce la proiezione corrispondente alla chiave composta specificata.
     */
    public Spettacolo visualizzaProiezione(String titolo, LocalDateTime dataOra) {
        return archivioSpettacoli.trovaPerChiave(titolo, dataOra);
    }

    /**
     * Calcola il numero di posti ancora disponibili sottraendo i posti prenotati dalla capienza totale della sala.
     */
    public int postiLiberi(String titolo, LocalDateTime dataOra) {
        int postiOccupati = 0;
        Biglietto[] tutti = archivioBiglietti.elencoTutti();
        for (int i = 0; i < tutti.length; i++) {
            if (tutti[i].getTitoloFilm().equalsIgnoreCase(titolo) && tutti[i].getDataOraSpettacolo().equals(dataOra)) {
                postiOccupati += tutti[i].getNumeroPosti();
            }
        }
        return CAPIENZA_SALA - postiOccupati;
    }

    /**
     * Restituisce l'intero palinsesto ordinato cronologicamente in ordine crescente tramite Selection Sort.
     */
    public Spettacolo[] elencoPalinsesto() {
        Spettacolo[] elenco = archivioSpettacoli.elencoTutti();
        for (int i = 0; i < elenco.length - 1; i++) {
            int indiceMinimo = i;
            for (int j = i + 1; j < elenco.length; j++) {
                if (elenco[j].getDataOra().isBefore(elenco[indiceMinimo].getDataOra())) {
                    indiceMinimo = j;
                }
            }
            if (indiceMinimo != i) {
                Spettacolo temporaneo = elenco[i];
                elenco[i] = elenco[indiceMinimo];
                elenco[indiceMinimo] = temporaneo;
            }
        }
        return elenco;
    }

    /**
     * Restituisce esclusivamente le proiezioni con orario successivo a quello attuale, ordinate cronologicamente.
     */
    public Spettacolo[] elencoProiezioniFuture() {
        Spettacolo[] tutte = elencoPalinsesto();
        LocalDateTime adesso = LocalDateTime.now();
        int conteggio = 0;
        for (int i = 0; i < tutte.length; i++) {
            if (tutte[i].getDataOra().isAfter(adesso)) {
                conteggio++;
            }
        }
        Spettacolo[] future = new Spettacolo[conteggio];
        int indice = 0;
        for (int i = 0; i < tutte.length; i++) {
            if (tutte[i].getDataOra().isAfter(adesso)) {
                future[indice] = tutte[i];
                indice++;
            }
        }
        return future;
    }
}