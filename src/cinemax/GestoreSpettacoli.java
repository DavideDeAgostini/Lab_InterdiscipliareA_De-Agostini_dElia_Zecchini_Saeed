package cinemax;

import java.time.LocalDateTime;

/**
 * Contiene le regole applicative relative alle proiezioni: creazione senza
 * sovrapposizioni, modifica/eliminazione solo in assenza di prenotazioni,
 * calcolo dei posti liberi.
 * <p>
 * Le proiezioni sono identificate dalla loro chiave composta (titolo del
 * film + data/ora), non da un id numerico, vedi
 * {@link Spettacolo#corrispondeA(String, LocalDateTime)}.
 *
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
public class GestoreSpettacoli {

    /** Capienza fissa della sala, come da specifiche di progetto. */
    public static final int CAPIENZA_SALA = 200;

    /** L'archivio delle proiezioni su cui il gestore opera. */
    private ArchivioSpettacoli archivioSpettacoli;
    /** L'archivio delle prenotazioni, usato per i controlli di occupazione e vincolo. */
    private ArchivioBiglietti archivioBiglietti;

    /**
     * Costruttore che collega il gestore agli archivi necessari.
     *
     * @param archivioSpettacoli l'archivio delle proiezioni
     * @param archivioBiglietti  l'archivio delle prenotazioni
     */
    public GestoreSpettacoli(ArchivioSpettacoli archivioSpettacoli, ArchivioBiglietti archivioBiglietti) {
        this.archivioSpettacoli = archivioSpettacoli;
        this.archivioBiglietti = archivioBiglietti;
    }

    /**
     * Aggiunge una nuova proiezione, a patto che non si sovrapponga a una
     * proiezione esistente (stesso intervallo di tempo, calcolato con la
     * durata del film).
     *
     * @param film           il film da proiettare
     * @param dataOra        la data e l'ora di inizio della proiezione
     * @param prezzoBiglietto il prezzo del biglietto per questa proiezione
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

    /**
     * Verifica se una proiezione candidata si sovrappone temporalmente a una
     * proiezione gia' esistente in archivio.
     * <p>
     * Due intervalli [inizio, fine) si sovrappongono se e solo se l'inizio
     * del primo precede la fine del secondo e l'inizio del secondo precede
     * la fine del primo.
     *
     * @param candidata la proiezione candidata da verificare
     * @return true se la proiezione candidata si sovrappone ad almeno una proiezione esistente
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
     * Modifica data/ora e prezzo di una proiezione esistente (identificata
     * da titolo e data/ora attuali), a patto che non esistano prenotazioni
     * collegate.
     * <p>
     * Poiche' la modifica e' bloccata in presenza di prenotazioni, cambiare
     * la data/ora (parte della chiave composta) qui non puo' mai invalidare
     * un riferimento gia' salvato altrove.
     *
     * @param titolo        il titolo della proiezione da modificare
     * @param dataOraAttuale la data/ora attuale della proiezione da modificare
     * @param nuovaDataOra  la nuova data/ora da assegnare
     * @param nuovoPrezzo   il nuovo prezzo del biglietto
     * @return true se la modifica e' andata a buon fine, false altrimenti
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
     * Elimina una proiezione, a patto che non esistano prenotazioni collegate.
     *
     * @param titolo  il titolo della proiezione da eliminare
     * @param dataOra la data/ora della proiezione da eliminare
     * @return true se la proiezione e' stata trovata ed eliminata, false altrimenti
     */
    public boolean eliminaProiezione(String titolo, LocalDateTime dataOra) {
        if (esistonoPrenotazioniPer(titolo, dataOra)) {
            System.out.println("Impossibile eliminare: esistono prenotazioni per questa proiezione.");
            return false;
        }
        return archivioSpettacoli.rimuovi(titolo, dataOra);
    }

    /**
     * Verifica se esiste almeno una prenotazione collegata a una determinata
     * proiezione.
     * <p>
     * Metodo di supporto usato da {@link #modificaProiezione} e
     * {@link #eliminaProiezione} per far rispettare il vincolo che vieta
     * di alterare proiezioni gia' prenotate.
     *
     * @param titolo  il titolo della proiezione da verificare
     * @param dataOra la data/ora della proiezione da verificare
     * @return true se esiste almeno una prenotazione collegata alla proiezione
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
     * Cerca una singola proiezione tramite titolo e data/ora.
     *
     * @param titolo  il titolo della proiezione da visualizzare
     * @param dataOra la data/ora della proiezione da visualizzare
     * @return la proiezione trovata, oppure null se non esiste
     */
    public Spettacolo visualizzaProiezione(String titolo, LocalDateTime dataOra) {
        return archivioSpettacoli.trovaPerChiave(titolo, dataOra);
    }

    /**
     * Restituisce il numero di posti liberi per una proiezione (capienza
     * meno posti gia' prenotati).
     *
     * @param titolo  il titolo della proiezione di cui calcolare i posti liberi
     * @param dataOra la data/ora della proiezione di cui calcolare i posti liberi
     * @return il numero di posti ancora disponibili, calcolato come {@link #CAPIENZA_SALA} meno i posti occupati
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
     * Restituisce l'intero palinsesto (tutte le proiezioni presenti in
     * archivio), ordinato cronologicamente dalla data/ora piu' vicina nel
     * passato/futuro fino alla piu' lontana.
     * <p>
     * L'ordinamento e' fatto con una selection sort su un array copia
     * (nessuna modifica all'archivio): semplice da spiegare e, per le
     * dimensioni tipiche di un palinsesto (anche alcune migliaia di
     * proiezioni), abbastanza veloce da non essere un problema.
     *
     * @return un array con tutte le proiezioni, ordinate cronologicamente
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
     * Restituisce solo le proiezioni future (data/ora successiva all'istante
     * corrente), ordinate cronologicamente dalla piu' vicina alla piu'
     * lontana.
     * <p>
     * Pensata per l'ospite: una lista pronta da consultare senza dover
     * impostare criteri di ricerca. Si appoggia a {@link #elencoPalinsesto()}
     * per l'ordinamento cronologico.
     *
     * @return un array con le sole proiezioni future, ordinate cronologicamente
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
