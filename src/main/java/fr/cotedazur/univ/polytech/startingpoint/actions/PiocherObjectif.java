package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.PiocheObjectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;

import java.util.Optional;

public class PiocherObjectif implements Action {
    private final TypeObjectif type;

    public PiocherObjectif(TypeObjectif type) {
        this.type = type;
    }

    @Override
    public void appliquer(GameState gameState, Bot bot) {
        // 1. Sélectionner la bonne pioche
        PiocheObjectif piocheCible = switch(type) {
            case JARDINIER -> gameState.getPiocheJardinier();
            case PANDA -> gameState.getPiochePanda();
            // Ajoutez le cas PARCELLE si vous l'avez créé
            default -> gameState.getPiocheJardinier(); // Par défaut (sécurité)
        };

        // 2. Piocher
        Optional<Objectif> objectifPioche = piocheCible.piocher();

        if (objectifPioche.isPresent()) {
            Objectif obj = objectifPioche.get();
            bot.getInventaire().ajouterObjectif(obj);
            System.out.println(bot.getNom() + " a pioché un objectif " + type);
        } else {
            System.out.println(bot.getNom() + " a voulu piocher " + type + " mais la pioche est vide !");
        }
    }

    @Override
    public String toString() {
        return "pioche un objectif " + type;
    }
}