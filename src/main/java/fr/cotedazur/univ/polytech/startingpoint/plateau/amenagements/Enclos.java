package fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Bambou;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Panda;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.QuantityException;

public class Enclos implements Amenagement {
    private static int quantity = 3;

    public Enclos(Parcelle parcelle) {
    }

    public Enclos(Parcelle parcelle, Bambou bamboo) {
        try {
            quantityDeduction(quantity);
        } catch (QuantityException e) {
            System.out.println(e.getMessage());
        }
        addToParcelle(quantity, parcelle, bamboo);
    }

    @Override
    public void actionSurParcelle(Object element) {
        if (element instanceof Panda panda) {
            panda.cannotEat();
        }
    }
}
