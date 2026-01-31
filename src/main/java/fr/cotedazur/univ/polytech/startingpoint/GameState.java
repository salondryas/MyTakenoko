package fr.cotedazur.univ.polytech.startingpoint;

import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.*;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Panda;
import fr.cotedazur.univ.polytech.startingpoint.plateau.PiocheParcelle;
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

    // --- CONSTRUCTEUR 1 : Utilisé par Partie.java (CELUI QUI MANQUAIT) ---
    public GameState(List<Bot> joueurs) {
        // 1. Initialisation du plateau et des personnages
        this.plateau = new Plateau();
        this.jardinier = new Jardinier();
        this.panda = new Panda();

        // 2. On récupère les joueurs fournis par la Partie
        this.joueurs = joueurs;

        // 3. Initialisation des pioches
        this.piocheParcelle = new PiocheParcelle(); // Se remplit automatiquement grâce à votre refactor
        this.piocheJardinier = new PiocheObjectif();
        this.piochePanda = new PiocheObjectif();
        this.piocheObjectifParcelle = new PiocheObjectif();
    }

    // --- CONSTRUCTEUR 2 : Par défaut (Pour les tests ou compatibilité) ---
    public GameState() {
        this(new ArrayList<>()); // Appelle l'autre constructeur avec une liste vide
    }

    // --- GETTERS ---

    public Plateau getPlateau() { return plateau; }
    public Jardinier getJardinier() { return jardinier; }
    public Panda getPanda() { return panda; }
    public List<Bot> getJoueurs() { return joueurs; }

    public PiocheParcelle getPiocheParcelle() { return piocheParcelle; }
    // Alias pour compatibilité
    public PiocheParcelle getPioche() { return piocheParcelle; }

    public PiocheObjectif getPiocheJardinier() { return piocheJardinier; }
    public PiocheObjectif getPiochePanda() { return piochePanda; }
    public PiocheObjectif getPiocheObjectifParcelle() { return piocheObjectifParcelle; }

    // --- MÉTHODES UTILITAIRES ---

    public void reset() {
        this.plateau = new Plateau();
        this.jardinier = new Jardinier();
        this.panda = new Panda();

        // Attention : On garde la liste des joueurs existante si on reset juste le plateau !
        // Si besoin de vider les joueurs, faire : this.joueurs.clear();

        this.piocheParcelle = new PiocheParcelle();
        this.piocheJardinier = new PiocheObjectif();
        this.piochePanda = new PiocheObjectif();
        this.piocheObjectifParcelle = new PiocheObjectif();
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