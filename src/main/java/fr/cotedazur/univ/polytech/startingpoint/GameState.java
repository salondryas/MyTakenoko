package fr.cotedazur.univ.polytech.startingpoint;

import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.parcelle.CarteParcelle; // IMPORT CRUCIAL
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

    // --- CONSTRUCTEUR 1 : Principal ---
    public GameState(List<Bot> joueurs) {
        // 1. Initialisation du plateau et des personnages
        this.plateau = new Plateau();
        // On passe le plateau aux personnages pour qu'ils puissent interagir avec
        this.jardinier = new Jardinier(plateau);
        this.panda = new Panda(plateau);

        this.joueurs = joueurs;

        // 2. Initialisation des pioches
        this.piocheParcelle = new PiocheParcelle(); // Se remplit via TuileType
        this.piocheJardinier = new PiocheObjectif();
        this.piochePanda = new PiocheObjectif();
        this.piocheObjectifParcelle = new PiocheObjectif();

        // 3. Remplissage des pioches d'objectifs
        initialiserPioches();
    }

    // --- CONSTRUCTEUR 2 : Par défaut (Pour les tests) ---
    public GameState() {
        this(new ArrayList<>());
    }

    /**
     * Remplit les pioches d'objectifs à partir des Enums (CartePanda, CarteBambou, CarteParcelle)
     */
    private void initialiserPioches() {
        // 1. Remplir Pioche PANDA
        for (CartePanda carte : CartePanda.values()) {
            for (int i = 0; i < carte.getOccurrence(); i++) {
                piochePanda.ajouter(new ObjectifPanda(carte));
            }
        }
        piochePanda.melanger();

        // 2. Remplir Pioche JARDINIER
        for (CarteBambou carte : CarteBambou.values()) {
            // On suppose 1 exemplaire par carte définie
            piocheJardinier.ajouter(new ObjectifJardinier(carte));
        }
        piocheJardinier.melanger();

        // 3. Remplir Pioche PARCELLE (MISE À JOUR MAJEURE)
        // Avant : on générait des objectifs "compteurs"
        // Maintenant : on utilise les motifs géométriques définis dans CarteParcelle
        for (CarteParcelle carte : CarteParcelle.values()) {
            // On crée l'objectif qui contient le Motif (Ligne, Triangle, etc.)
            piocheObjectifParcelle.ajouter(new ObjectifParcelle(carte));
        }
        piocheObjectifParcelle.melanger();
    }

    // --- RESET ---
    public void reset() {
        this.plateau = new Plateau();
        this.jardinier = new Jardinier(plateau);
        this.panda = new Panda(plateau);

        this.piocheParcelle = new PiocheParcelle();
        this.piocheJardinier = new PiocheObjectif();
        this.piochePanda = new PiocheObjectif();
        this.piocheObjectifParcelle = new PiocheObjectif();

        // IMPORTANT : Re-remplir les pioches avec la nouvelle logique
        initialiserPioches();
    }

    // --- GETTERS ---
    public Plateau getPlateau() { return plateau; }
    public Jardinier getJardinier() { return jardinier; }
    public Panda getPanda() { return panda; }
    public List<Bot> getJoueurs() { return joueurs; }

    public PiocheParcelle getPiocheParcelle() { return piocheParcelle; }
    public PiocheParcelle getPioche() { return piocheParcelle; } // Alias

    public PiocheObjectif getPiocheJardinier() { return piocheJardinier; }
    public PiocheObjectif getPiochePanda() { return piochePanda; }
    public PiocheObjectif getPiocheObjectifParcelle() { return piocheObjectifParcelle; }

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