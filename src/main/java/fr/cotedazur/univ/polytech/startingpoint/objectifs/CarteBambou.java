package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Arrangement;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

import static fr.cotedazur.univ.polytech.startingpoint.plateau.Arrangement.*;
import static fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur.*;

public enum CarteBambou {
    VERT_UN_PAR_QUATRE_SUR_VIDE(VERT, 1,4,AUCUN,5),                    // Un bambou vert de taille 4 sur une tuile sans arrangement
    VERT_UN_PAR_QUATRE_SUR_ENCLOS(VERT, 1,4, ENCLOS,4),                // Un bambou vert de taille 4 sur une tuile enclos
    VERT_UN_PAR_QUATRE_SUR_ENGRAIS(VERT, 1,4,ENGRAIS,3),               // Un bambou vert de taille 4 sur une tuile engrais
    VERT_UN_PAR_QUATRE_SUR_BASSIN(VERT, 1,4,BASSIN,4),                 // Un bambou vert de taille 4 sur une tuile bassin

    ROSE_UN_PAR_QUATRE_SUR_VIDE(ROSE, 1, 4,AUCUN, 7),                  // Un bambou rose de taille 4 sur une tuile sans arrangement
    ROSE_UN_PAR_QUATRE_SUR_ENCLOS(ROSE, 1,4,ENCLOS,6),                 // Un bambou rose de taille 4 sur une tuile enclos
    ROSE_UN_PAR_QUATRE_SUR_ENGRAIS(ROSE, 1,4,ENGRAIS,5),               // Un bambou rose de taille 4 sur une tuile engrais
    ROSE_UN_PAR_QUATRE_SUR_BASSIN(ROSE, 1,4,BASSIN,6),                 // Un bambou rose de taille 4 sur une tuile bassin

    JAUNE_UN_PAR_QUATRE_SUR_VIDE(JAUNE, 1, 4,AUCUN, 6),                // Un bambou jaune de taille 4 sur une tuile sans arrangement
    JAUNE_UN_PAR_QUATRE_SUR_ENCLOS(JAUNE, 1,4,ENCLOS,5),               // Un bambou jaune de taille 4 sur une tuile enclos
    JAUNE_UN_PAR_QUATRE_SUR_ENGRAIS(JAUNE, 1,4,ENGRAIS,4),             // Un bambou jaune de taille 4 sur une tuile engrais
    JAUNE_UN_PAR_QUATRE_SUR_BASSIN(JAUNE, 1,4,BASSIN,5),               // Un bambou jaune de taille 4 sur une tuile bassin

    ROSE_DEUX_PAR_TROIS_SUR_VIDE(ROSE, 2, 3,AUCUN, 6),                 // Deux bambous roses de taille 3 sur n'importe quelle tuile
    JAUNE_TROIS_PAR_TROIS_SUR_VIDE(JAUNE, 3, 3,AUCUN, 7),              // Trois bambous jaunes de taille 3 sur n'importe quelle tuile
    VERT_QUATRE_PAR_TROIS_SUR_VIDE(VERT, 3, 4,AUCUN, 8),               // Quatre bambous verts de taille 3 sur n'importe quelle tuile
    ;

    Couleur couleur;
    int taille;
    int nombreDeBambous;
    Arrangement arrangement;
    int points;

    CarteBambou(Couleur couleur, int nombreDeBambous, int taille, Arrangement arrangement, int points) {
        this.couleur = couleur;
        this.taille = taille;
        this.nombreDeBambous = nombreDeBambous;
        this.arrangement = arrangement;
        this.points = points;
    }

    public Couleur getCouleur() { return couleur; }
    public int getTaille() { return taille; }
    public int getNombreDeBambous() { return nombreDeBambous; }
    public Arrangement getArrangement() { return (Arrangement) arrangement; } // Cast nécessaire si tu as laissé "Object"
    public int getPoints() { return points; }
}
