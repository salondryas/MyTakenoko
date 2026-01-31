package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Bassin;
import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Enclos;
import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Engrais;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PiocheParcelle {
    public static final int NOMBRE_PARCELLES_INITIAL = 27;
    static int NOMBRE_PARCELLES_INITIAL_JAUNES = 9;
    static int NOMBRE_PARCELLES_INITIAL_ROSES = 7; // on track les quantités de sections par couleur
    static int NOMBRE_PARCELLES_INITIAL_VERTES = 11;

    private List<Parcelle> piocheDeParcelle = new ArrayList<>(NOMBRE_PARCELLES_INITIAL);

    public static final int NOMBRE_PARCELLES_NOT_AMENAGEES_GREEN = 5;
    public static final int NOMBRE_PARCELLES_NOT_AMENAGEES_YELLOW = 6;
    public static final int NOMBRE_PARCELLES_NOT_AMENAGEES_PINK = 4;

    public PiocheParcelle() {
        remplirPioche();
        melangePioche();
    }

    public int getSize() {
        return piocheDeParcelle.size();
    }

    // pour remplir la pioche créée vide précédemment, on ajoute 9 parcelles de
    // chaque couleur pour un total de 27 parcelles

    /*
     * D'après {https://fr.boardgamearena.com/doc/Gamehelptakenoko} , on a les
     * données suivantes:
     * Distribution des parcelles
     * 
     * Il y a 28 parcelles séparées de la façon suivantes:
     * 
     * 11 parcelles vertes: 5 sans aménagement, 2 bassin, 2 engrais, 2 enclos
     * 9 parcelles jaunes: 6 sans aménagement, 1 bassin, 1 engrais, 1 enclos
     * 7 parcelles roses : 4 sans aménagement, 1 bassin, 1 engrais, 1 enclos
     * 1 étang bleu (La parcelle de départ)
     */

    public void remplirPioche() {
        for (int i = 0; i < NOMBRE_PARCELLES_INITIAL_ROSES; i++) {
            if (i < NOMBRE_PARCELLES_NOT_AMENAGEES_PINK)
                piocheDeParcelle.add(new Parcelle(Couleur.ROSE));
            else {
                piocheDeParcelle.add(remplirPiocheAvecAmenagement(Couleur.ROSE, new Parcelle(Couleur.ROSE), i));
            }

        }

        for (int i = 0; i < NOMBRE_PARCELLES_INITIAL_VERTES; i++) {
            if (i < NOMBRE_PARCELLES_NOT_AMENAGEES_GREEN)
                piocheDeParcelle.add(new Parcelle(Couleur.VERT));
            else {
                piocheDeParcelle.add(remplirPiocheAvecAmenagement(Couleur.VERT, new Parcelle(Couleur.VERT), i));
            }
        }

        for (int i = 0; i < NOMBRE_PARCELLES_INITIAL_JAUNES; i++) {
            if (i < NOMBRE_PARCELLES_NOT_AMENAGEES_YELLOW)
                piocheDeParcelle.add(new Parcelle(Couleur.JAUNE));
            else {
                piocheDeParcelle.add(remplirPiocheAvecAmenagement(Couleur.JAUNE, new Parcelle(Couleur.JAUNE), i));
            }
        }
    }

    private Parcelle remplirPiocheAvecAmenagement(Couleur colour, Parcelle tuile, int indice) {
        switch (colour) {
            case VERT:
                return imprimerAmenagement(tuile, indice, 2, NOMBRE_PARCELLES_NOT_AMENAGEES_GREEN);

            case JAUNE:
                return imprimerAmenagement(tuile, indice, 1, NOMBRE_PARCELLES_NOT_AMENAGEES_YELLOW);

            default:
                return imprimerAmenagement(tuile, indice, 1, NOMBRE_PARCELLES_NOT_AMENAGEES_PINK);
        }
    }

    private Parcelle imprimerAmenagement(Parcelle tuile, int indice, int nbAmenangementParType,
            int NB_AMENAGEMENT_LESS) {

        int offset = indice - NB_AMENAGEMENT_LESS;

        if (offset < nbAmenangementParType) {
            tuile.fetchAmenagementAcqui(new Bassin(tuile));
            return tuile;
        } else if (offset < 2 * nbAmenangementParType) {
            tuile.fetchAmenagementAcqui(new Engrais(tuile));
            return tuile;
        }
        tuile.fetchAmenagementAcqui(new Enclos(tuile));
        return tuile;
    }

    public void melangePioche() {
        Collections.shuffle(piocheDeParcelle);
    }

    public Parcelle piocherParcelle() {
        // Vérification si la pioche n'est pas vide
        if (this.piocheDeParcelle.isEmpty()) {
            System.out.println("La pioche de parcelles est vide !");
            return null; // ou lance une exception
        }

        // Retirer et retourner la première parcelle
        Parcelle elementPioche = this.piocheDeParcelle.remove(0);
        return elementPioche;
    }
    public boolean estVide() {
        return piocheDeParcelle.isEmpty();
    }
    public String toString() {
        return piocheDeParcelle.toString();
    }
}
