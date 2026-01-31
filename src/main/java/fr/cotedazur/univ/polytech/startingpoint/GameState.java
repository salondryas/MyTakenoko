package fr.cotedazur.univ.polytech.startingpoint;

import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.parcelle.CarteParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Panda;
import fr.cotedazur.univ.polytech.startingpoint.plateau.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import java.util.List;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.parcelle.CarteParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Panda;
import fr.cotedazur.univ.polytech.startingpoint.plateau.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import java.util.List;

public class GameState {
    private Plateau plateau;
    private PiocheParcelle pioche;
    private List<Bot> joueurs;
    private Panda panda;
    private Jardinier jardinier;

    // --- PIOCHES D'OBJECTIFS ---
    private PiocheObjectif piocheJardinier;
    private PiocheObjectif piochePanda;
    private PiocheObjectif piocheObjectifParcelle;

    public GameState(List<Bot> joueurs) {
        this.plateau = new Plateau();
        this.pioche = new PiocheParcelle();
        this.joueurs = joueurs;
        this.panda = new Panda();
        this.jardinier = new Jardinier();

        // Initialisation des objets pioches
        this.piocheJardinier = new PiocheObjectif();
        this.piochePanda = new PiocheObjectif();
        this.piocheObjectifParcelle = new PiocheObjectif();

        // Remplissage automatique des 45 cartes
        initialiserPioches();
    }

    private void initialiserPioches() {
        // 1. Remplir Pioche Jardinier (via CarteBambou)
        for (CarteBambou cb : CarteBambou.values()) {
            piocheJardinier.ajouter(new ObjectifJardinier(cb));
        }
        piocheJardinier.melanger();

        // 2. Remplir Pioche Panda (via CartePanda avec occurrences officielles)
        for (CartePanda cp : CartePanda.values()) {
            for (int i = 0; i < cp.getOccurrence(); i++) {
                piochePanda.ajouter(new ObjectifPanda(cp));
            }
        }
        piochePanda.melanger();

        // 3. Remplir Pioche Parcelle (via CarteParcelle)
        /*
        for (CarteParcelle cp : CarteParcelle.values()) {
            piocheObjectifParcelle.ajouter(new ObjectifParcelle(cp));
        }
        piocheObjectifParcelle.melanger();
         */
    }


    // --- GETTERS ---
    public PiocheObjectif getPiocheJardinier() { return piocheJardinier; }
    public PiocheObjectif getPiochePanda() { return piochePanda; }
    public PiocheObjectif getPiocheObjectifParcelle() { return piocheObjectifParcelle; }
    public Plateau getPlateau() { return plateau; }
    public List<Bot> getJoueurs() { return joueurs; }
    public Panda getPanda() { return panda; }
    public Jardinier getJardinier() { return jardinier; }

    public void reset() {
        this.plateau = new Plateau();
        this.pioche = new PiocheParcelle();
        this.panda = new Panda();
        this.jardinier = new Jardinier();
        this.piocheJardinier = new PiocheObjectif();
        this.piochePanda = new PiocheObjectif();
        this.piocheObjectifParcelle = new PiocheObjectif();
        initialiserPioches();
    }

    public Bot determinerMeilleurJoueur() {
        Bot gagnant = null;
        int meilleurScore=-1;
        for (Bot bot : this.getJoueurs()) {
            if (bot.getScore() > meilleurScore) {
                meilleurScore = bot.getScore();
                gagnant = bot;
            }
        }
        return gagnant;
    }

    public PiocheParcelle getPioche() {
        return pioche;
    }
}