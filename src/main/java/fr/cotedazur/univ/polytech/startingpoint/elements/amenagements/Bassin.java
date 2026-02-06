package fr.cotedazur.univ.polytech.startingpoint.elements.amenagements;

import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Bambou;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;

public class Bassin implements Amenagement {
    public Bassin(Parcelle parcelle) {
        //
    }

    public Bassin(Parcelle parcelle, Bambou bamboo) {
        if (!parcelle.getIsAmenagee()) {
            // 1. On attache l'aménagement
            parcelle.fetchAmenagementAcqui(this);

            // 2. On déclenche l'effet immédiat (Irrigation)
            this.actionSurParcelle(parcelle);
        }
    }

    @Override
    public void actionSurParcelle(Object element) {
        if (element instanceof Parcelle parcelle) {
            parcelle.triggerIrrigation();
        }
    }

    @Override
    public String toString() {
        return "Bassin";
    }
}