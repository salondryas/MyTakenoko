package fr.cotedazur.univ.polytech.startingpoint.elements.amenagements;

public interface Amenagement {
    // On supprime quantityDeduction et addToParcelle par défaut qui étaient trop complexes
    // et contenaient du static.

    void actionSurParcelle(Object elem);
}