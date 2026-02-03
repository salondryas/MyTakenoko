package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Logger;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;

import java.util.ArrayList;
import java.util.List;

public class DeplacerJardinier implements Action {
    private Jardinier jardinier;
    private Position destination;

    public DeplacerJardinier(Jardinier jardinier, Position destination) {
        this.jardinier = jardinier;
        this.destination = destination;
    }

    @Override
    public void appliquer(GameState gameState, Bot bot) {
        Plateau plateau = gameState.getPlateau();
        Jardinier jardinierActuel = gameState.getJardinier(); // Renommé pour éviter confusion

        // 1. Déplacer le jardinier
        jardinierActuel.setPosition(destination);

        // 2. Identifier les parcelles cibles (Celle d'arrivée + Voisines même couleur)
        List<Parcelle> parcellesAArroser = new ArrayList<>();
        Parcelle parcelleArrivee = plateau.getParcelle(destination);

        if (parcelleArrivee != null) {
            parcellesAArroser.add(parcelleArrivee);

            // CORRECTION : Appel de la méthode locale au lieu de plateau.getParcellesVoisinesMemeCouleur
            parcellesAArroser.addAll(getParcellesVoisinesMemeCouleur(plateau, destination, parcelleArrivee));
        }

        // 3. Faire pousser le bambou
        for (Parcelle p : parcellesAArroser) {
            // Important : Le bambou ne pousse que si la parcelle est irriguée !
            // (La méthode pousserBambou devrait vérifier ça, mais on peut le doubler ici)
            if (p.estIrriguee()) {
                boolean aPousse = p.pousserBambou();
                if (aPousse) {
                    Logger.print(" Le jardinier fait pousser du bambou en " + p.getPosition());
                }
            }
        }
    }

    /**
     * Méthode utilitaire déplacée depuis Plateau.java pour garder le plateau léger.
     * Récupère les parcelles adjacentes qui partagent la même couleur que la parcelle centrale.
     */
    private List<Parcelle> getParcellesVoisinesMemeCouleur(Plateau plateau, Position positionCible, Parcelle parcelleCentrale) {
        List<Parcelle> voisinesMemeCouleur = new ArrayList<>();

        for (PositionsRelatives direction : PositionsRelatives.values()) {
            if (direction != PositionsRelatives.ZERO) {
                Position posVoisine = positionCible.add(direction.getPosition());
                Parcelle voisine = plateau.getParcelle(posVoisine);

                if (voisine != null && voisine.getCouleur() == parcelleCentrale.getCouleur()) {
                    voisinesMemeCouleur.add(voisine);
                }
            }
        }
        return voisinesMemeCouleur;
    }

    @Override
    public TypeAction getType() {
        return TypeAction.DEPLACER_JARDINIER;
    }

    @Override
    public String toString() {
        return "déplace le jardinier en " + destination;
    }
}