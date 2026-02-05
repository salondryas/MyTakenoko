package fr.cotedazur.univ.polytech.startingpoint.Strategies;


import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action; // Import nécessaire
import fr.cotedazur.univ.polytech.startingpoint.actions.DeplacerPanda; // Import nécessaire
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.InventaireJoueur;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifJardinier;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;


import java.util.Set;


public class StrategieSabotage {


    public DeplacerPanda getActionSabotage(GameState gameState, Bot bot) {
        Plateau plateau = gameState.getPlateau();
        Set<Position> positionsOccupees = plateau.getPositionOccupees();
        Position positionCibleSabotage = null;


        for (Position position : positionsOccupees) {
            // Condition 1 : C'est une menace (bambou haut)
            if (plateau.getNombreDeSectionsAPosition(position) >= 3) {
                Parcelle p = plateau.getParcelle(position);


                // Condition 2 :est-ce que ce bambou est important pour mes objectifs ?
                if (!aiJeBesoinDeFairePousser(p.getCouleur(), bot.getInventaire())) {
                    positionCibleSabotage = position;
                    break; // On a trouvé une cible
                }
            }
        }


        //alors on sabote l'adversaire
        if (positionCibleSabotage != null
                && gameState.getPanda().accessibleEnUnCoupParPanda(gameState, positionCibleSabotage)) {


            return new DeplacerPanda(gameState.getPanda(), positionCibleSabotage);
        }


        // Sinon, pas de sabotage possible
        return null;
    }


    private boolean aiJeBesoinDeFairePousser(Couleur couleur, InventaireJoueur inventaireJoueur) {
        for (Objectif obj : inventaireJoueur.getObjectifs()) {
            if (obj.getType() == TypeObjectif.JARDINIER) {
                ObjectifJardinier objJ = (ObjectifJardinier) obj;
                if (objJ.getCouleurs().contains(couleur) && objJ.getTaille() >= 3) {
                    return true;
                }
            }
        }
        return false;
    }
}
