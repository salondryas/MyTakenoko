package fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Bambou;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;

public class Bassin implements Amenagement {
    public Bassin(Parcelle parcelle) {
    }

    // Constructeur Achat/Pose
    public Bassin(Parcelle parcelle, Bambou bamboo) {
        if (!parcelle.getIsAmenagee()) {
            // 1. On attache l'aménagement
            parcelle.fetchAmenagementAcqui(this);

            // 2. CORRECTION : On déclenche l'effet immédiat (Irrigation)
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