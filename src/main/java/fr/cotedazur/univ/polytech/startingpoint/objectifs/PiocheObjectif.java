package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class PiocheObjectif {
    // On utilise une Stack pour simuler une pile de cartes (LIFO)
    private final Stack<Objectif> cartes;

    public PiocheObjectif() {
        this.cartes = new Stack<>();
    }

    /**
     * Ajoute un objectif à la pioche (utilisé lors de l'initialisation dans GameState)
     */
    public void ajouter(Objectif objectif) {
        if (objectif != null) {
            cartes.push(objectif);
        }
    }

    /**
     * Mélange les cartes présentes dans la pioche
     */
    public void melanger() {
        Collections.shuffle(cartes);
    }

    /**
     * Pioche la carte du dessus.
     * Retourne un Optional pour éviter les erreurs si la pioche est vide.
     */
    public Optional<Objectif> piocher() {
        if (cartes.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(cartes.pop());
    }

    /**
     * Retourne le nombre de cartes restantes dans la pioche
     */
    public int getTaille() {
        return cartes.size();
    }

    public boolean estVide() {
        return cartes.isEmpty();
    }

    // Utile pour le debug
    @Override
    public String toString() {
        return "Pioche de " + getTaille() + " cartes";
    }
}