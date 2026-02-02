package fr.cotedazur.univ.polytech.startingpoint;

import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.*;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Panda;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

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
        this.plateau = new Plateau();
        // On passe le plateau aux personnages (Correction précédente maintenue)
        this.jardinier = new Jardinier(plateau);
        this.panda = new Panda(plateau);

        this.joueurs = joueurs;

        // Création des pioches
        this.piocheParcelle = new PiocheParcelle(); // Se remplit toute seule via son constructeur
        this.piocheJardinier = new PiocheObjectif();
        this.piochePanda = new PiocheObjectif();
        this.piocheObjectifParcelle = new PiocheObjectif();

        // CORRECTION MAJEURE : On remplit les pioches d'objectifs !
        initialiserPioches();
    }

    // --- CONSTRUCTEUR 2 : Par défaut ---
    public GameState() {
        this(new ArrayList<>());
    }

    /**
     * Remplit les pioches d'objectifs à partir des Enums (CartePanda, CarteBambou...)
     */
    private void initialiserPioches() {
        // 1. Remplir Pioche PANDA
        for (CartePanda carte : CartePanda.values()) {
            // On ajoute autant d'exemplaires que défini dans l'Enum (ex: 5 cartes PANDA_VERT)
            for (int i = 0; i < carte.getOccurrence(); i++) {
                piochePanda.ajouter(new ObjectifPanda(carte));
            }
        }
        piochePanda.melanger();

        // 2. Remplir Pioche JARDINIER
        for (CarteBambou carte : CarteBambou.values()) {
            // On suppose 1 exemplaire de chaque carte définie dans l'Enum CarteBambou
            // (Si vous ajoutez un champ 'occurrence' dans CarteBambou plus tard, faites une boucle comme pour Panda)
            piocheJardinier.ajouter(new ObjectifJardinier(carte));
        }
        piocheJardinier.melanger();

        // 3. Remplir Pioche PARCELLE (Génération manuelle car pas d'Enum dédié aux objectifs parcelles)
        // On génère quelques objectifs classiques pour que le jeu fonctionne
        genererObjectifsParcelles();
        piocheObjectifParcelle.melanger();
    }

    // ... Le début de ta classe GameState reste identique ...

    private void genererObjectifsParcelles() {
        // CORRECTION : On utilise List.of(...) pour respecter le nouveau constructeur

        // Exemple : 3 objectifs de chaque couleur demandant 2 parcelles
        for (int i = 0; i < 3; i++) {
            piocheObjectifParcelle.ajouter(new ObjectifParcelle(2, 2, List.of(Couleur.VERT)));
            piocheObjectifParcelle.ajouter(new ObjectifParcelle(2, 2, List.of(Couleur.JAUNE)));
            piocheObjectifParcelle.ajouter(new ObjectifParcelle(2, 2, List.of(Couleur.ROSE)));
        }

        // Exemple : 2 objectifs de chaque couleur demandant 3 parcelles
        for (int i = 0; i < 2; i++) {
            piocheObjectifParcelle.ajouter(new ObjectifParcelle(4, 3, List.of(Couleur.VERT)));
            piocheObjectifParcelle.ajouter(new ObjectifParcelle(4, 3, List.of(Couleur.JAUNE)));
            piocheObjectifParcelle.ajouter(new ObjectifParcelle(4, 3, List.of(Couleur.ROSE)));
        }
    }

    // ... Le reste de la classe reste identique ...

    // --- GETTERS ---

    public Plateau getPlateau() { return plateau; }
    public Jardinier getJardinier() { return jardinier; }
    public Panda getPanda() { return panda; }
    public List<Bot> getJoueurs() { return joueurs; }

    public PiocheParcelle getPiocheParcelle() { return piocheParcelle; }
    public PiocheParcelle getPioche() { return piocheParcelle; }

    public PiocheObjectif getPiocheJardinier() { return piocheJardinier; }
    public PiocheObjectif getPiochePanda() { return piochePanda; }
    public PiocheObjectif getPiocheObjectifParcelle() { return piocheObjectifParcelle; }

    // --- RESET ---

    public void reset() {
        this.plateau = new Plateau();
        this.jardinier = new Jardinier(plateau);
        this.panda = new Panda(plateau);

        this.piocheParcelle = new PiocheParcelle();
        this.piocheJardinier = new PiocheObjectif();
        this.piochePanda = new PiocheObjectif();
        this.piocheObjectifParcelle = new PiocheObjectif();

        // IMPORTANT : Ne pas oublier de reremplir lors du reset !
        initialiserPioches();
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