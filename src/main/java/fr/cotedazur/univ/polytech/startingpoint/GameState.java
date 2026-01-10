package fr.cotedazur.univ.polytech.startingpoint;

import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.*;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Panda;
import fr.cotedazur.univ.polytech.startingpoint.plateau.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import java.util.ArrayList;
import java.util.List;

public class GameState {
    private Plateau plateau;
    private PiocheParcelle pioche;
    private List<Bot> joueurs;

    // Ne supprimez pas ces deux lignes, les actions DeplacerPanda/Jardinier en ont besoin !
    private Panda panda;
    private Jardinier jardinier;

    // --- PIOCHES D'OBJECTIFS ---
    private PiocheObjectif piocheJardinier;
    private PiocheObjectif piochePanda;
    private PiocheObjectif piocheObjectifParcelle;

    public GameState() {
        this.plateau = new Plateau();
        this.pioche = new PiocheParcelle();
        this.joueurs = new ArrayList<>();
        this.panda = new Panda();      // Initialisation indispensable
        this.jardinier = new Jardinier(); // Initialisation indispensable

        // --- INITIALISATION DES PIOCHES ---
        this.piocheJardinier = new PiocheObjectif();
        this.piochePanda = new PiocheObjectif();
        this.piocheObjectifParcelle = new PiocheObjectif();

        initialiserPioches();
    }

    private void initialiserPioches() {
        // 1. Remplir Pioche Jardinier
        piocheJardinier.ajouter(new ObjectifJardinier(Couleur.VERT, 4, 4));
        piocheJardinier.ajouter(new ObjectifJardinier(Couleur.JAUNE, 4, 5));
        piocheJardinier.ajouter(new ObjectifJardinier(Couleur.ROSE, 4, 6));
        piocheJardinier.melanger();

        // 2. Remplir Pioche Panda (Décommentez dès que ObjectifPanda est prêt)
        piochePanda.ajouter(new ObjectifPanda(4, Couleur.VERT, 2));
        piochePanda.ajouter(new ObjectifPanda(4, Couleur.JAUNE, 2));
        piochePanda.ajouter(new ObjectifPanda(4, Couleur.ROSE, 2));
        piochePanda.melanger();

        // 3. Remplir Pioche Parcelle (Avec votre classe ObjectifPoseur)
        // Exemple : 3 parcelles VERTES rapportent 3 points (adaptez selon votre constructeur)
        piocheObjectifParcelle.ajouter(new ObjectifPoseur(3, Couleur.VERT, 3));
        piocheObjectifParcelle.ajouter(new ObjectifPoseur(3, Couleur.ROSE, 4));
        piocheObjectifParcelle.ajouter(new ObjectifPoseur(3, Couleur.JAUNE, 5));
        piocheObjectifParcelle.melanger();
    }

    // --- GETTERS ---
    public PiocheObjectif getPiocheJardinier() { return piocheJardinier; }
    public PiocheObjectif getPiochePanda() { return piochePanda; }
    public PiocheObjectif getPiocheObjectifParcelle() { return piocheObjectifParcelle; }

    public Plateau getPlateau() { return plateau; }
    public PiocheParcelle getPioche() { return pioche; }
    public List<Bot> getJoueurs() { return joueurs; }
    public Panda getPanda() { return panda; }       // Getter indispensable
    public Jardinier getJardinier() { return jardinier; } // Getter indispensable

    public void reset() {
        this.plateau = new Plateau();
        this.pioche = new PiocheParcelle();
        this.panda = new Panda();
        this.jardinier = new Jardinier();

        // Reset des 3 pioches
        this.piocheJardinier = new PiocheObjectif();
        this.piochePanda = new PiocheObjectif();
        this.piocheObjectifParcelle = new PiocheObjectif();

        initialiserPioches();
    }
}