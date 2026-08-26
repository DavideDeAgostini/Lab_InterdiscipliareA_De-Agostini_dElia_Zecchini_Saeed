package cinemax;

import java.time.LocalDateTime;

/**
 * Contiene le regole applicative relative alle proiezioni: creazione senza
 * sovrapposizioni, modifica/eliminazione solo in assenza di prenotazioni,
 * calcolo dei posti liberi. Le proiezioni sono identificate dalla loro
 * chiave composta (titolo del film + data/ora), non da un id numerico.
 *
 * @author Davide De Agostini
 * @author Luigi d'Elia
 * @author Ahsan Saeed
 * @author Martina Zecchini
 */
public class GestoreSpettacoli {
    /** Capienza fissa della sala, come da specifiche di progetto. */
    public static final int CAPIENZA_SALA = 200;
    private ArchivioSpettacoli archivioSpettacoli;
    private ArchivioBiglietti archivioBiglietti;

    public GestoreSpettacoli(ArchivioSpettacoli archivioSpettacoli, ArchivioBiglietti archivioBiglietti) {
        this.archivioSpettacoli = archivioSpettacoli;
        this.archivioBiglietti = archivioBiglietti;
    }

    /**
     * Aggiunge una nuova proiezione, a patto che non si sovrapponga a una
     * proiezione esistente (stesso intervallo di tempo, calcolato con la
     * durata del film).
     *
     * @return true se la proiezione e' stata aggiunta
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

    private boolean siSovrappone(Spettacolo candidata) {
        LocalDateTime inizioCandidata = candidata.getDataOra();
        LocalDateTime fineCandidata = candidata.getDataOraFine();
        Spettacolo[] esistenti = archivioSpettacoli.elencoTutti();
        for (int i = 0; i < esistenti.length; i++) {
            Spettacolo altra = esistenti[i];
            LocalDateTime inizioAltra = altra.getDataOra();
            LocalDateTime fineAltra = altra.getDataOraFine();
            boolean sovrapposte = inizioCandidata.isBefore(fineAltra) &&
                    inizioAltra.isBefore(fineCandidata);
            if (sovrapposte) {
                return true;
            }
        }
        return false;
    }

    /**
     * Modifica data/ora e prezzo di una proiezione esistente (identificata
     * da titolo e data/ora attuali), a patto che non esistano prenotazioni
     * collegate. Poiche' la modifica e' bloccata in presenza di
     * prenotazioni, cambiare la data/ora (parte della chiave composta) qui
     * non puo' mai invalidare un riferimento gia' salvato altrove.
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

    /** Elimina una proiezione, a patto che non esistano prenotazioni collegate. */
    public boolean eliminaProiezione(String titolo, LocalDateTime dataOra) {
        if (esistonoPrenotazioniPer(titolo, dataOra)) {
            System.out.println("Impossibile eliminare: esistono prenotazioni per questa proiezione.");
            return false;
        }
        return archivioSpettacoli.rimuovi(titolo, dataOra);
    }

    private boolean esistonoPrenotazioniPer(String titolo, LocalDateTime dataOra) {
        Biglietto[] tutti = archivioBiglietti.elencoTutti();
        for (int i = 0; i < tutti.length; i++) {
            if (tutti[i].getTitoloFilm().equalsIgnoreCase(titolo) &&
                    tutti[i].getDataOraSpettacolo().equals(dataOra)) {
                return true;
            }
        }
        return false;
    }

    public Spettacolo visualizzaProiezione(String titolo, LocalDateTime dataOra) {
        return archivioSpettacoli.trovaPerChiave(titolo, dataOra);
    }

    /**
     * Restituisce il numero di posti liberi per una proiezione (capienza meno posti
     * gia' prenotati).
     */
    public int postiLiberi(String titolo, LocalDateTime dataOra) {
        int postiOccupati = 0;
        Biglietto[] tutti = archivioBiglietti.elencoTutti();
        for (int i = 0; i < tutti.length; i++) {
            if (tutti[i].getTitoloFilm().equalsIgnoreCase(titolo) &&
                    tutti[i].getDataOraSpettacolo().equals(dataOra)) {
                postiOccupati += tutti[i].getNumeroPosti();
            }
        }
        return CAPIENZA_SALA - postiOccupati;
    }
}
