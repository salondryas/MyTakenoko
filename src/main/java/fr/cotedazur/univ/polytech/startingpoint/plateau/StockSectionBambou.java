package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.QuantityException;

public enum StockSectionBambou {

    VERTES(36),
    JAUNES(30),
    ROSES(28);

    private int quantity;
    private final int MAX_QUANTITY;

    StockSectionBambou(int maxQuantity) {
        this.quantity = maxQuantity;
        this.MAX_QUANTITY = maxQuantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void subtractToQuantity() throws QuantityException {
        if (quantity > 0) {
            quantity--;
        } else {
            throw new QuantityException(
                    "Toutes les sections " + this.name().toLowerCase() + " ont été jouées actuellement !");
        }
    }

    public void addToQuantity() throws QuantityException {
        if (quantity < MAX_QUANTITY) {
            quantity++;
        } else {
            throw new QuantityException(
                    "Le nombre maximum de sections " + this.name().toLowerCase() + " est atteint !");
        }
    }
}
