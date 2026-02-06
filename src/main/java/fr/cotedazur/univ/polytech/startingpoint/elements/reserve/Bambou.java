package fr.cotedazur.univ.polytech.startingpoint.elements.reserve;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.QuantityException;

public class Bambou {

    private int numberOfSections; // hauteur section
    private static final int MAX_HEIGHT = 5; // hauteur max d'un bambou (socle + sections)
    private Couleur sectionColour;

    private int sectionGrowth; // dicte le nombre de combien de sections on fait pousser le bambou

    public Bambou(Couleur colour) {
        numberOfSections = 0; // MODIFIÉ : était 1, maintenant 0 (pas de bambou par défaut)
        sectionColour = colour;
        sectionGrowth = 1;
    }

    public int getNumberOfSections() {
        return numberOfSections;
    }

    public Couleur getSectionColour() {
        return sectionColour;
    }

    public void croissance() {
        if (numberOfSections <= MAX_HEIGHT - sectionGrowth) {
            takeBambooSection();
            numberOfSections += sectionGrowth; // on agrandit le bambou i.e: on rajoute une section
        }
    }

    private void takeBambooSection() {
        switch (sectionColour) {
            case ROSE:
                try {
                    StockSectionBambou.ROSES.subtractToQuantity();
                } catch (QuantityException e) {
                    e.getMessage();
                }
                break;

            case VERT:
                try {
                    StockSectionBambou.VERTES.subtractToQuantity();
                } catch (QuantityException e) {
                    e.getMessage();
                }
                break;

            default:
                try {
                    StockSectionBambou.JAUNES.subtractToQuantity();
                } catch (QuantityException e) {
                    e.getMessage();
                }
                break;
        }
    }

    public void retirerSection() {
        if (numberOfSections > 1)
            numberOfSections -= 1;
    }

    /**
     * Fait apparaître le bambou lors de la première irrigation.
     * Passe de 0 à 1 section si et seulement si la taille vaut 0.
     * return true si le bambou est apparu, false sinon
     */
    public boolean faireApparaitre() {
        if (numberOfSections == 0) {
            numberOfSections = 1;
            return true;
        }
        return false;
    }

    public void increaseSectionGrowth() {
        sectionGrowth = 2;
    }

    public int getHauteurMax() {
        return MAX_HEIGHT;
    }
}
