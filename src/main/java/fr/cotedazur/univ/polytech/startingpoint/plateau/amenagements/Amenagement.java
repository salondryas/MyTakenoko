package fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements;

public interface Amenagement {
    // On supprime quantityDeduction et addToParcelle par défaut qui étaient trop complexes
    // et contenaient du static.

    void actionSurParcelle(Object elem);
}