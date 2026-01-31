package fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Bambou;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.QuantityException;

public class Engrais implements Amenagement {
    private static int quantity = 3;

    public Engrais(Parcelle parcelle) {
    }

    public Engrais(Parcelle parcelle, Bambou bamboo) {
        try {
            quantityDeduction(quantity);
        } catch (QuantityException e) {
            System.out.println(e.getMessage());
        }
        addToParcelle(quantity, parcelle, bamboo);
    }

    @Override
    public void actionSurParcelle(Object element) {
        if (element instanceof Bambou bamboo) {
            bamboo.croissance();
        }
    }
}
