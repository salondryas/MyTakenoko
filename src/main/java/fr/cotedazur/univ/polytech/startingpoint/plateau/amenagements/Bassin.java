package fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Bambou;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.QuantityException;

public class Bassin implements Amenagement {
    private static int quantity = 3;

    public Bassin(Parcelle parcelle) {
    }

    public Bassin(Parcelle parcelle, Bambou bamboo) {
        try {
            quantityDeduction(quantity);
        } catch (QuantityException e) {
            System.out.println(e.getMessage());
        }
        addToParcelle(quantity, parcelle, bamboo);
    }

    @Override
    public void actionSurParcelle(Object element) {
        if (element instanceof Parcelle parcelle) {
            parcelle.triggerIrrigation();
        }
    }
}
