package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.QuantityException;

public class Bambou {

    private int number_of_sections; // hauteur section
    private static final int maxHeight = 5; // hauteur max d'un bambou (socle + sections)
    private Couleur section_colour;

    private int section_growth; // dicte le nombre de combien de sections on fait pousser le bambou

    public Bambou(Couleur colour) {
        number_of_sections = 0; // MODIFIÉ : était 1, maintenant 0 (pas de bambou par défaut)
        section_colour = colour;
        section_growth = 1;
    }

    public int getNumberOfSections() {
        return number_of_sections;
    }

    public Couleur getSectionColour() {
        return section_colour;
    }

    public void croissance() {
        if (number_of_sections <= maxHeight - section_growth) {
            takeBambooSection(section_colour);
            number_of_sections += section_growth; // on agrandit le bambou i.e: on rajoute une section
        }
    }

    private void takeBambooSection(Couleur section_colour) {
        switch (section_colour) {
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
        if (number_of_sections > 1)
            number_of_sections -= 1;
    }

    /**
     * Fait apparaître le bambou lors de la première irrigation.
     * Passe de 0 à 1 section si et seulement si la taille vaut 0.
     * return true si le bambou est apparu, false sinon
     */
    public boolean faireApparaitre() {
        if (number_of_sections == 0) {
            number_of_sections = 1;
            return true;
        }
        return false;
    }

    public void increaseSectionGrowth() {
        section_growth = 2;
    }

    public int getHauteurMax() {
        return maxHeight;
    }
}
