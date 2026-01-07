package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PiocheObjectif {
    private List<Objectif> deck;

    public PiocheObjectif() {
        this.deck = new ArrayList<>();
    }

    public void ajouter(Objectif objectif) {
        this.deck.add(objectif);
    }

    // Pour remplir la pioche d'un coup
    public void ajouterTout(List<Objectif> objectifs) {
        this.deck.addAll(objectifs);
    }

    public void melanger() {
        Collections.shuffle(deck);
    }

    // Retourne un Optional car la pioche peut être vide
    public Optional<Objectif> piocher() {
        if (deck.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(deck.remove(0));
    }

    public boolean estVide() {
        return deck.isEmpty();
    }

    public int size() {
        return deck.size();
    }
}