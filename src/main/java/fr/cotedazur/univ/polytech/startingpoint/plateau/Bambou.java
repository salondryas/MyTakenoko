package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

public class Bambou {

    private int number_of_sections; // hauteur section
    private Couleur section_colour;

    private int section_growth = 1; // dicte le nombre de combien de sections on fait pousser le bambou

    public Bambou(Couleur colour) {
        number_of_sections = 0; // MODIFIÉ : était 1, maintenant 0 (pas de bambou par défaut)
        section_colour = colour;
        // number_of_bamboo -= 1;
    }

    public int getNumberOfSections() {
        return number_of_sections;
    }

    public Couleur getSectionColour() {
        return section_colour;
    }

    public void croissance() {
        if (number_of_sections <= 4 - section_growth)
            number_of_sections += section_growth; // on agrandit le bambou i.e: on rajoute une section
    }

    public void retirerSection() {
        if (number_of_sections >= 1)
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
}
