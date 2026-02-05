package fr.cotedazur.univ.polytech.startingpoint;

import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.parcelle.CarteParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Panda;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import java.util.ArrayList;
import java.util.List;

public class GameState {
    private Plateau plateau;
    private Jardinier jardinier;
    private Panda panda;
    private List<Bot> joueurs;

    // Les Pioches
    private PiocheParcelle piocheParcelle;
    private PiocheObjectif piocheJardinier;
    private PiocheObjectif piochePanda;
    private PiocheObjectif piocheObjectifParcelle;

    // Attribut pour gérer la météo VENT
    private boolean ventActif;

    // CONSTRUCTEUR 1
    public GameState(List<Bot> joueurs) {
        this.joueurs = joueurs;
        initialiserEtatJeu();
    }

    // CONSTRUCTEUR 2
    public GameState() {
        this.joueurs = new ArrayList<>();
        initialiserEtatJeu();
    }

    private void initialiserEtatJeu() {
        this.plateau = new Plateau();
        this.jardinier = new Jardinier(plateau);
        this.panda = new Panda(plateau);

        this.piocheParcelle = new PiocheParcelle();
        this.piocheJardinier = new PiocheObjectif();
        this.piochePanda = new PiocheObjectif();
        this.piocheObjectifParcelle = new PiocheObjectif();

        this.ventActif = false; // AJOUT METEO VENT

        initialiserPioches();
    }

    private void initialiserPioches() {
        // 1. Pioche PANDA
        for (CartePanda carte : CartePanda.values()) {
            for (int i = 0; i < carte.getOccurrence(); i++) {
                piochePanda.ajouter(new ObjectifPanda(carte));
            }
        }
        piochePanda.melanger();

        // 2. Pioche JARDINIER
        for (CarteBambou carte : CarteBambou.values()) {
            piocheJardinier.ajouter(new ObjectifJardinier(carte));
        }
        piocheJardinier.melanger();

        // 3. Pioche PARCELLE
        genererObjectifsParcelles();
        piocheObjectifParcelle.melanger();
    }

    private void genererObjectifsParcelles() {
        // On utilise l'Enum CarteParcelle
        for (CarteParcelle carte : CarteParcelle.values()) {
            piocheObjectifParcelle.ajouter(new ObjectifParcelle(carte));
        }
    }

    // --- GETTERS ---

    public Plateau getPlateau() {
        return plateau;
    }

    public Jardinier getJardinier() {
        if (jardinier == null)
            this.jardinier = new Jardinier(plateau);
        return jardinier;
    }

    public Panda getPanda() {
        if (panda == null)
            this.panda = new Panda(plateau);
        return panda;
    }

    public List<Bot> getJoueurs() {
        return joueurs;
    }

    public PiocheParcelle getPiocheParcelle() {
        return piocheParcelle;
    }

    public PiocheParcelle getPioche() {
        return piocheParcelle;
    }

    public PiocheObjectif getPiocheJardinier() {
        return piocheJardinier;
    }

    public PiocheObjectif getPiochePanda() {
        return piochePanda;
    }

    public PiocheObjectif getPiocheObjectifParcelle() {
        return piocheObjectifParcelle;
    }

    public boolean isVentActif() {
        return ventActif;
    }

    // --- RESET ---
    public void reset() {
        initialiserEtatJeu();
    }

    public void setVentActif(boolean ventActif) {
        this.ventActif = ventActif;
    }

    // Méthode pour réinitialiser le contexte au début d'un nouveau tour
    public void resetForNewTurn() {
        this.ventActif = false;
    }
    // Ajoutez ceci dans GameState.java
    public int getNbCanaux() {
        return 20;
    }

    public Bot determinerMeilleurJoueur() {
        Bot gagnant = null;
        int meilleurScore = -1;
        for (Bot bot : this.joueurs) {
            if (bot.getScore() > meilleurScore) {
                meilleurScore = bot.getScore();
                gagnant = bot;
            }
        }
        return gagnant;
    }
}