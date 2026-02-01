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

    private PiocheParcelle piocheParcelle;
    private PiocheObjectif piocheJardinier;
    private PiocheObjectif piochePanda;
    private PiocheObjectif piocheObjectifParcelle;

    // Constructeur principal
    public GameState(List<Bot> joueurs) {
        // 1. On crée le Plateau EN PREMIER
        this.plateau = new Plateau();

        // 2. On passe ce plateau aux personnages (CORRECTION ICI)
        this.jardinier = new Jardinier(plateau);
        this.panda = new Panda(plateau);

        this.joueurs = joueurs;

        this.piocheParcelle = new PiocheParcelle();
        this.piocheJardinier = new PiocheObjectif();
        this.piochePanda = new PiocheObjectif();
        this.piocheObjectifParcelle = new PiocheObjectif();
    }

    public GameState() {
        this(new ArrayList<>());
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

    public void reset() {
        // Même logique pour le reset
        this.plateau = new Plateau();
        this.jardinier = new Jardinier(plateau); // CORRECTION
        this.panda = new Panda(plateau);         // CORRECTION

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