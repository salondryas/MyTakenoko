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

    public ObjectifJardinier(Couleur couleur, int tailleRequise) {
        this.couleur = couleur;
        this.tailleRequise = tailleRequise;
    }

    @Override
    public int getPoints() {
        return 4;
    }

    @Override
    public boolean valider(GameState gameState, Bot bot) {
        Map<Position, Parcelle> parcelles = gameState.getPlateau().getParcellesMap();

        for (Parcelle p : parcelles.values()) {
            // CORRECTION ICI : Utilisation de getNbSectionsSurParcelle()
            if (p.getCouleur() == this.couleur && p.getNbSectionsSurParcelle() >= tailleRequise) {
                return true;
            }
        }
        return false;
    }
}