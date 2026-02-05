package fr.cotedazur.univ.polytech.startingpoint.elements.reserve;

import fr.cotedazur.univ.polytech.startingpoint.elements.amenagements.*;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

import java.util.function.Function;

public enum TuileType {
    // VERTES (11)
    VERTE_STANDARD(Couleur.VERT, 5, null),
    VERTE_BASSIN(Couleur.VERT, 2, Bassin::new),
    VERTE_ENCLOS(Couleur.VERT, 2, Enclos::new),
    VERTE_ENGRAIS(Couleur.VERT, 2, Engrais::new),

    // JAUNES (9)
    JAUNE_STANDARD(Couleur.JAUNE, 6, null),
    JAUNE_BASSIN(Couleur.JAUNE, 1, Bassin::new),
    JAUNE_ENCLOS(Couleur.JAUNE, 1, Enclos::new),
    JAUNE_ENGRAIS(Couleur.JAUNE, 1, Engrais::new),

    // ROSES (7)
    ROSE_STANDARD(Couleur.ROSE, 4, null),
    ROSE_BASSIN(Couleur.ROSE, 1, Bassin::new),
    ROSE_ENCLOS(Couleur.ROSE, 1, Enclos::new),
    ROSE_ENGRAIS(Couleur.ROSE, 1, Engrais::new);

    private final Couleur couleur;
    private final int nombreExemplaires;
    private final Function<Parcelle, Amenagement> generateurAmenagement;

    TuileType(Couleur couleur, int nombreExemplaires, Function<Parcelle, Amenagement> generateurAmenagement) {
        this.couleur = couleur;
        this.nombreExemplaires = nombreExemplaires;
        this.generateurAmenagement = generateurAmenagement;
    }

    public Couleur getCouleur() { return couleur; }
    public int getNombreExemplaires() { return nombreExemplaires; }
    public Function<Parcelle, Amenagement> getGenerateurAmenagement() { return generateurAmenagement; }
}