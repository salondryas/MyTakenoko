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

    public TypeObjectif getTypeObjectif() {
        return this.type;
    }

    @Override
    public void appliquer(GameState gameState, Bot bot) {
        // 1. Sélectionner la bonne pioche
        PiocheObjectif piocheCible = switch(type) {
            case JARDINIER -> gameState.getPiocheJardinier();
            case PANDA -> gameState.getPiochePanda();
            case PARCELLE -> gameState.getPiocheObjectifParcelle();
        };

        // 2. Piocher
        Optional<Objectif> objectifPioche = piocheCible.piocher();

        if (objectifPioche.isPresent()) {
            Objectif obj = objectifPioche.get();
            bot.getInventaire().ajouterObjectif(obj);
        }
    }

    @Override
    public TypeAction getType() {
        return TypeAction.PIOCHER_OBJECTIF;
    }

    @Override
    public String toString() {
        return "pioche un objectif " + type;
    }
}