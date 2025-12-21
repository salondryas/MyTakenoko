package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Placable;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

public class Bambou {
    // static int number_of_bamboo = 27;
    static int number_of_green_sections = 36;
    static int number_of_yellow_sections = 30; // on track les quantités de sections par couleur
    static int number_of_pink_sections = 24;
    // pas certain de garder c'est variables statiques ici car on pourrait les
    // deplacer dans une autre classe plus globale comme plateau
    private int number_of_sections; // hauteur section
    private Couleur section_colour;
    private Position positionBambou;

    public Bambou(Couleur colour) {
        number_of_sections = 1;
        section_colour = colour;
        // number_of_bamboo -= 1;
    }

    public int getNumberOfSections(){return number_of_sections;}

    public Couleur getSectionColour() {
        return section_colour;
    }

    public void croissance() {
        if (number_of_sections < 4)
            number_of_sections += 1; // on agrandit le bambou i.e: on rajoute une section
    }

    public void retirerSection() {
        if (number_of_sections >=1)
            number_of_sections -= 1;
    }

    public void sectionQuantityUpdate() {
        switch (section_colour) {
            case ROSE -> {
                if (number_of_pink_sections > 0)
                    number_of_pink_sections -= 1;
            }
            case VERT -> {
                if (number_of_green_sections > 0)
                    number_of_green_sections -= 1;
            }
            default -> {
                if (number_of_yellow_sections > 0)
                    number_of_yellow_sections -= 1;
            }
            // si c'est ni rose ni vert alors c'est forcément jaune car un
            // bambou possède necessairement une couleur
        }
    }
}
