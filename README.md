<div align="center">

# 🎬 CineMax

**Sistema di gestione cinema da terminale — proiezioni, biglietti e account multi-ruolo**

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk&logoColor=white)
![Status](https://img.shields.io/badge/status-completato-brightgreen)
![License](https://img.shields.io/badge/uso-didattico-blue)

Laboratorio Interdisciplinare A · a.a. 2025/2026 · Università degli Studi dell'Insubria

</div>

---

## 👥 Autori

| Nome | Matricola | Corso |
|---|---|---|
| Davide De Agostini | 766294 | CO |
| Luigi d'Elia | 765969 | CO |
| Martina Zecchini | 765842 | CO |
| Ahsan Saeed | 767241 | CO |

---

## 📖 Descrizione

**CineMax** è un'applicazione Java a riga di comando per la gestione completa di un cinema: catalogo proiezioni, vendita/prenotazione biglietti e gestione account, con quattro modalità di accesso pensate per ruoli diversi.

## ✨ Funzionalità

### Modalità di accesso

| Ruolo | Cosa può fare |
|---|---|
| 👤 **Ospite** | Cercare proiezioni per titolo, genere e intervallo di date, senza autenticarsi |
| 🎟️ **Spettatore** | Ricerca + prenotazione/acquisto biglietti + consultazione delle proprie prenotazioni |
| 💳 **Cassiere** | Vendita biglietti allo sportello + ricerca delle prenotazioni esistenti |
| 🎞️ **Operatore** *(proiezionista)* | Gestione del catalogo proiezioni: creazione, modifica, rimozione degli spettacoli |

### Sotto il cofano

- 🔐 **Autenticazione sicura** — password cifrate con hash SHA-256 (`Cifratura`), mai salvate in chiaro
- 🔎 **Motore di ricerca dedicato** (`MotoreRicerca`) per filtrare proiezioni e prenotazioni
- 🆔 **Codici prenotazione univoci** generati automaticamente (`GeneratoreCodice`)
- 💾 **Persistenza su CSV** — nessun database esterno richiesto

---

## 📂 Struttura del progetto

```
CineMax/
├── src/cinemax/               # Codice sorgente Java (package cinemax)
│   ├── CineMax.java               # 🚀 Entry point dell'applicazione
│   ├── Account.java                # Modello utente (spettatore/cassiere/operatore)
│   ├── Pellicola.java               # Modello film
│   ├── Spettacolo.java              # Modello proiezione (film + orario + sala)
│   ├── Biglietto.java               # Modello prenotazione/biglietto
│   ├── Archivio*.java               # Persistenza su CSV (account, spettacoli, biglietti)
│   ├── Gestore*.java                # Logica applicativa (accessi, spettacoli, biglietti)
│   ├── MotoreRicerca.java           # Ricerca proiezioni e prenotazioni
│   ├── Cifratura.java               # Hashing password (SHA-256)
│   ├── GeneratoreCodice.java        # Generazione codici prenotazione
│   ├── CsvUtile.java                # Utility di lettura/scrittura CSV
│   └── Schermata*.java              # Interfacce testuali per i diversi ruoli
│
├── data/                       # Dati persistenti (CSV)
│   ├── utenti.csv                  # Account registrati
│   ├── proiezioni.csv              # Catalogo proiezioni
│   └── prenotazioni.csv            # Biglietti/prenotazioni
│
├── bin/                        # Build compilata
│   └── CineMax.jar                 # Jar eseguibile
│
└── doc/                        # Documentazione
    ├── javadoc/                     # API reference generata
    ├── CineMax_Manuale_Tecnico.pdf  # Manuale tecnico
    └── CineMax_Manuale_Utente.pdf   # Manuale utente
```

---

## 🚀 Avvio rapido

### Requisiti

> Java **17** o superiore (JDK)

### Opzione 1 — Jar precompilato

```bash
java -jar bin/CineMax.jar
```

> ⚠️ Eseguire il comando dalla cartella principale del progetto, così che `data/` sia raggiungibile come percorso relativo.

### Opzione 2 — Compilazione da sorgente

```bash
javac -d bin src/cinemax/*.java
java -cp bin cinemax.CineMax
```

---

## 🧪 Dati di esempio

La cartella `data/` è già popolata con account e proiezioni di prova, utili per testare subito l'applicazione con utenti `OPERATORE`, `CASSIERE` e `SPETTATORE`. Le password sono salvate cifrate (SHA-256).

---

## 📚 Documentazione

| Risorsa | Percorso |
|---|---|
| 📘 Manuale utente | `doc/CineMax_Manuale_Utente.pdf` |
| 🛠️ Manuale tecnico | `doc/CineMax_Manuale_Tecnico.pdf` |
| 📑 Javadoc (API reference) | `doc/javadoc/index.html` |
