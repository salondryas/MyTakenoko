package fr.cotedazur.univ.polytech.startingpoint.elements.amenagements;

import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Bambou;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;

public class Engrais implements Amenagement {
    // SUPPRESSION DU STATIC INT QUANTITY

    // Constructeur utilisé par la Pioche
    public Engrais(Parcelle parcelle) {
    }

    // Constructeur utilisé quand un joueur ACHÈTE un aménagement
    public Engrais(Parcelle parcelle, Bambou bamboo) {
        if (!parcelle.getIsAmenagee()) {
            parcelle.fetchAmenagementAcqui(this);
        }
    }

    @Override
    public void actionSurParcelle(Object element) {
        // L'engrais force la croissance (double la pousse ou fait pousser 2 fois selon vos règles)
        if (element instanceof Bambou bamboo) {
            bamboo.croissance();
        }
    }

    @Override
    public String toString() {
        return "Engrais";
    }
}