package fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Bambou;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.QuantityException;

public interface Amenagement {
    default void quantityDeduction(int quantity) throws QuantityException {
        if (quantity == 0)
            throw new QuantityException("Amenagements indisponibles");
        return;
    }

    default void addToParcelle(int quantity, Parcelle parcelle, Bambou bamboo) {
        if (!parcelle.getIsAmenagee() && (bamboo.getNumberOfSections() == 0)) {
            parcelle.fetchAmenagementAcqui(this);
            quantity -= 1;
        }
    }

    abstract void actionSurParcelle(Object elem);

}