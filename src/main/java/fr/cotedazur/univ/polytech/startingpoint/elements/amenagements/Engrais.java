package fr.cotedazur.univ.polytech.startingpoint.elements.amenagements;

import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Bambou;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;

public class Engrais implements Amenagement {

    // Constructeur utilisé par la Pioche
    public Engrais(Parcelle parcelle) {
    }

    // Constructeur utilisé quand un joueur récupérer un aménagement
    public Engrais(Parcelle parcelle, Bambou bamboo) {
        if (!parcelle.getIsAmenagee()) {
            parcelle.fetchAmenagementAcqui(this);
        }
    }

    @Override
    public void actionSurParcelle(Object element) {
        // L'engrais augmente la croissance d'un bambou à deux sections desormais
        if (element instanceof Bambou bamboo) {
            bamboo.increaseSectionGrowth();
        }
    }

    @Override
    public String toString() {
        return "Engrais";
    }
}