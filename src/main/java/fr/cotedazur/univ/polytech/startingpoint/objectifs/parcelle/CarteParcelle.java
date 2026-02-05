package fr.cotedazur.univ.polytech.startingpoint.objectifs.parcelle;

import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur.*;

public enum CarteParcelle {
    // -- VERT --
    LIGNE_VERTE( creationParcellesUniformes(Forme.LIGNE, VERT), 2 ),
    C_VERT( creationParcellesUniformes(Forme.C, VERT), 2 ),
    TRIANGLE_VERT(  creationParcellesUniformes(Forme.TRIANGLE, VERT), 2 ),
    LOSANGE_VERT(   creationParcellesUniformes(Forme.LOSANGE, VERT), 3 ),

    // -- ROSE --
    LIGNE_ROSE( creationParcellesUniformes(Forme.LIGNE, ROSE), 4 ),
    C_ROSE( creationParcellesUniformes(Forme.C, ROSE), 4 ),
    TRIANGLE_ROSE( creationParcellesUniformes(Forme.TRIANGLE, ROSE), 4 ),
    LOSANGE_ROSE( creationParcellesUniformes(Forme.LOSANGE, ROSE), 5 ),

    // -- JAUNE --
    LIGNE_JAUNE( creationParcellesUniformes(Forme.LIGNE, JAUNE), 3 ),
    C_JAUNE( creationParcellesUniformes(Forme.C, JAUNE), 3 ),
    TRIANGLE_JAUNE( creationParcellesUniformes(Forme.TRIANGLE, JAUNE), 3 ),
    LOSANGE_JAUNE( creationParcellesUniformes(Forme.LOSANGE, JAUNE), 4 ),

    // -- MIXTE --
    LOSANGE_ROSE_JAUNE(fusionListeParcellesUniformes(
            creationParcellesUniformes(Forme.MIXTE1,ROSE),
            creationParcellesUniformes(Forme.MIXTE2,JAUNE)), 5 ),
    LOSANGE_VERT_ROSE( fusionListeParcellesUniformes(
            creationParcellesUniformes(Forme.MIXTE1,VERT),
            creationParcellesUniformes(Forme.MIXTE2,ROSE)), 4 ),
    LOSANGE_VERT_JAUNE( fusionListeParcellesUniformes(
            creationParcellesUniformes(Forme.MIXTE1,VERT),
            creationParcellesUniformes(Forme.MIXTE2,JAUNE)), 3 ),
    ;
    //pour renvoyer une map contenant le nombre de parcelle et la couleur correspodante
    private final Map<Couleur, Integer> map=new HashMap<Couleur, Integer>();

    // -- INSTANCES --
    private final Motif motif;
    private final int points;

    // -- CONSTRUCTEUR --
    CarteParcelle(List<Parcelle> parcellesRelatives, int points) {
        this.motif = new Motif(parcellesRelatives);
        this.points=points;
        for (Parcelle p : parcellesRelatives) {
            this.map.merge(p.getCouleur(), 1, Integer::sum);
        }
    }

    // -- GETTER --
    public int getPoints() {
        return points;
    }
    public Map<Couleur, Integer> getMap() {
        return map;
    }

    // -- VÉRIFICATEURS --

    // La configuration demandée est-elle correspond-elle ?
    public boolean estValide(Plateau plateau) {
        // CORRECTION ICI : Utilisation de getParcellesMap().keySet()
        // au lieu de getPositionOccupees() qui n'existe plus dans le nouveau Plateau
        for (Position positionAncrage : plateau.getParcellesMap().keySet()) {
            if (verifierMotifDepuisAncrage(positionAncrage, plateau)){
                return true; // L'objectif est réalisé
            }
        }
        return false;
    }

    // Vérifie si à partir d'une position donnée (ancrage) une des configurations du motif fonctionne
    public boolean verifierMotifDepuisAncrage(Position positionAncrage, Plateau plateau) {

        // On regarde pour chaque rotation possible
        for (List<Parcelle> unMotif : motif.getConfigurationsPossibles()) {
            if (verifierConfigurationSpecifique(positionAncrage, unMotif, plateau)) {
                return true;
            }
        }
        return false;
    }

    // Vérifie une configuration géométrique sur le plateau
    public boolean verifierConfigurationSpecifique(Position positionAncrage, List<Parcelle> unMotif, Plateau plateau) {
        for (Parcelle parcelleRelative : unMotif) {
            // On calcule la position locale dont on doit vérifier la correspondance sur le plateau
            Position positionAbsolue = positionAncrage.add(parcelleRelative.getPosition());

            // On récupère la parcelle à vérifier sur le plateau
            Parcelle parcellePlateau = plateau.getParcelle(positionAbsolue);

            // Si la parcelle n'existe pas ou n'est pas de la bonne couleur, la parcelle n'est pas valide
            if (parcellePlateau == null) return false;
            if (parcellePlateau.getCouleur() != parcelleRelative.getCouleur()) return false;

            // Note : Pour les objectifs parcelle, on vérifie aussi souvent qu'elle est irriguée
            // dans les règles complètes (optionnel selon votre implémentation actuelle)
            // if (!parcellePlateau.estIrriguee()) return false;
        }
        // Toutes les parcelles du motif à vérifier ont les configurations attendues
        return true;
    }

    // -- METHODE CONSTRUCTION ENUM--

    // crée une liste de parcelles uniformes
    public static List<Parcelle> creationParcellesUniformes(Forme positions, Couleur couleur){
        List<Parcelle> parcellesList = new ArrayList<>();
        for (PositionsRelatives currentPositionRelative : positions.getPositions()) {
            parcellesList.add(new Parcelle(currentPositionRelative.getPosition(), couleur));
        }
        return parcellesList;
    }

    // Fusionne deux listes de parcelle uniforme
    public static List<Parcelle> fusionListeParcellesUniformes(List<Parcelle>... parcelles) {
        List<Parcelle> parcellesList = new ArrayList<>();
        for (int i = 0 ; i<parcelles.length ; i++) {
            parcellesList.addAll(parcelles[i]);
        }
        return parcellesList;
    }
}