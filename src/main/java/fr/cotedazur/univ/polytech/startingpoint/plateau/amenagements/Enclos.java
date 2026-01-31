package fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Bambou;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Panda;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;

public class Enclos implements Amenagement {
    // SUPPRESSION DU STATIC INT QUANTITY

    // Constructeur utilisé par la Pioche (pour les parcelles pré-aménagées)
    public Enclos(Parcelle parcelle) {
        // Rien à faire, l'association se fait dans la Pioche
    }

    // Constructeur utilisé quand un joueur ACHÈTE un aménagement
    public Enclos(Parcelle parcelle, Bambou bamboo) {
        // On applique l'effet directement si la parcelle n'est pas déjà aménagée
        if (!parcelle.getIsAmenagee()) {
            parcelle.fetchAmenagementAcqui(this);
        }
    }

    @Override
    public void actionSurParcelle(Object element) {
        // Le panda ne peut pas manger ici
        if (element instanceof Panda panda) {
            panda.cannotEat();
        }
    }

    @Override
    public String toString() {
        return "Enclos";
    }
}