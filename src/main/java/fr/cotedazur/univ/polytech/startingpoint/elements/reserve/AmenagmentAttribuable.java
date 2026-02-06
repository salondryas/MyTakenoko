package fr.cotedazur.univ.polytech.startingpoint.elements.reserve;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.QuantityException;

public enum AmenagmentAttribuable { // amenagement pour les parcelles qui n'en ont pas par défault
    ENCLOS(3),
    ENGRAIS(3),
    BASSIN(3),
    AUCUN();

    private int quantite;

    AmenagmentAttribuable() {
    }

    AmenagmentAttribuable(int quantite) {
        this.quantite = quantite;
    }

    public int getQuantite() {
        return quantite;
    }

    public void pickAmenagement() throws QuantityException {
        if (quantite > 0)
            quantite--;
        else
            throw new QuantityException("Il n'y plus d'amenagements " + this.name() + " !");
    }

}
