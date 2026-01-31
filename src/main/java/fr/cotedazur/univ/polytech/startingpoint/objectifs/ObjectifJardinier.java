package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.Map;

public class ObjectifJardinier extends Objectif {
    private final Couleur couleur;
    private final int tailleRequise;

    // --- CONSTRUCTEUR 1 : Via l'Enum (Pour le Jeu) ---
    public ObjectifJardinier(CarteBambou carte) {
        super(carte.getPoints(), TypeObjectif.JARDINIER);
        this.couleur = carte.getCouleur();
        this.tailleRequise = carte.getTaille();
    }

    // --- CONSTRUCTEUR 2 : Manuel (Pour les Tests) ---
    public ObjectifJardinier(Couleur couleur, int tailleRequise, int points) {
        super(points, TypeObjectif.JARDINIER);
        this.couleur = couleur;
        this.tailleRequise = tailleRequise;
    }
    // ------------------------------------------------

    public Couleur getCouleur() {
        return this.couleur;
    }

    public int getTaille() {
        return tailleRequise;
    }

    @Override
    public boolean valider(GameState gameState, Bot bot) {
        Map<Position, Parcelle> parcelles = gameState.getPlateau().getParcellesMap();

        for (Parcelle p : parcelles.values()) {
            if (p.getCouleur() == this.couleur && p.getNbSectionsSurParcelle() >= tailleRequise) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Objectif Jardinier : Bambou " + couleur + " de taille " + tailleRequise + " (" + super.getPoints() + "pts)";
    }
}