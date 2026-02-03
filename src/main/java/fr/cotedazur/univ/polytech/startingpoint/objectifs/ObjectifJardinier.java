package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Arrangement;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Bassin;
import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Enclos;
import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Engrais;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObjectifJardinier extends Objectif {
    private final Couleur couleur;
    private final int tailleRequise;
    // NOUVEAU : On stocke l'arrangement requis
    private final Arrangement arrangementRequis;

    // --- CONSTRUCTEUR 1 : Via l'Enum (Pour le Jeu) ---
    public ObjectifJardinier(CarteBambou carte) {
        super(carte.getPoints(), TypeObjectif.JARDINIER);
        this.couleur = carte.getCouleur();
        this.tailleRequise = carte.getTaille();
        this.arrangementRequis = carte.getArrangement(); // On récupère l'info depuis la carte
    }

    // --- CONSTRUCTEUR 2 : Manuel (Pour les Tests) ---
    public ObjectifJardinier(Couleur couleur, int tailleRequise, int points, Arrangement arrangement) {
        super(points, TypeObjectif.JARDINIER);
        this.couleur = couleur;
        this.tailleRequise = tailleRequise;
        this.arrangementRequis = arrangement;
    }

    // Constructeur simplifié pour tests (sans arrangement spécifié -> AUCUN)
    public ObjectifJardinier(Couleur couleur, int tailleRequise, int points) {
        this(couleur, tailleRequise, points, Arrangement.AUCUN);
    }

    @Override
    public List<Couleur> getCouleurs() {
        return new ArrayList<>(List.of(this.couleur));
    }

    public int getTaille() {
        return tailleRequise;
    }

    @Override
    public boolean valider(GameState gameState, Bot bot) {
        Map<Position, Parcelle> parcelles = gameState.getPlateau().getParcellesMap();

        for (Parcelle p : parcelles.values()) {
            if (p == null) continue;

            // 1. Vérification Couleur
            boolean bonneCouleur = (p.getCouleur() == this.couleur);

            // 2. Vérification Taille
            boolean bonneTaille = (p.getNbSectionsSurParcelle() >= tailleRequise);

            // 3. Vérification Aménagement (NOUVEAU)
            boolean bonAmenagement = verifierAmenagement(p);

            if (bonneCouleur && bonneTaille && bonAmenagement) {
                return true; // Tout correspond
            }
        }
        return false;
    }

    /**
     * Vérifie si la parcelle possède l'aménagement requis par la carte.
     */
    private boolean verifierAmenagement(Parcelle p) {
        // Si la carte ne demande rien de spécial ("AUCUN"), c'est valide peu importe ce qu'il y a sur la parcelle
        // (Sauf si vos règles disent que "AUCUN" exige une parcelle VIDE d'aménagement, mais standardement c'est "peu importe")
        if (this.arrangementRequis == Arrangement.AUCUN) {
            return true;
        }

        // Si la parcelle n'a pas d'aménagement mais qu'on en veut un, c'est faux
        if (p.getAmenagement() == null) {
            return false;
        }

        // Vérification du type spécifique
        return switch (this.arrangementRequis) {
            case BASSIN -> p.getAmenagement() instanceof Bassin;
            case ENCLOS -> p.getAmenagement() instanceof Enclos;
            case ENGRAIS -> p.getAmenagement() instanceof Engrais;
            default -> true;
        };
    }

    @Override
    public String toString() {
        String amenagementTxt = (arrangementRequis != Arrangement.AUCUN) ? " avec " + arrangementRequis : "";
        return "Objectif Jardinier : Bambou " + couleur + " de taille " + tailleRequise + amenagementTxt + " (" + getPoints() + "pts)";
    }
}