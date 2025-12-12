package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.plateau.PositionsVoisinesOrigine;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static fr.cotedazur.univ.polytech.startingpoint.plateau.PositionsVoisinesOrigine.*;

/**
 * Représente les formes géométriques (patterns) nécessaires pour valider un objectif de parcelle
 * sur le plateau de jeu hexagonal.
 * Cette énumération gère automatiquement la génération de toutes les rotations possibles
 * pour une forme donnée et élimine les doublons causés par les symétries
 * (ex: une ligne droite est identique si on la tourne de 180°).
 */
public enum ObjectifParcelleForme {
    // De même couleur
    LIGNE_DE_TROIS(List.of(TROIS, ZERO, QUATRE)),
    TRIANGLE(List.of(UN,DEUX,ZERO)),
    ;

    // La liste de toutes les configurations possibles de parcelle d'un objectif
    private final List<List<Position>> configurationsPossibles;

    ObjectifParcelleForme(List<PositionsVoisinesOrigine> positionList) {
        configurationsPossibles = new ArrayList<>();

        // On convertit les énums en position relative
        List<Position> patternDeBase = new ArrayList<>();
        for (PositionsVoisinesOrigine pvo : positionList) {
            patternDeBase.add(pvo.getPositionVoisine());
        }

        // Le pattern change selon chaque rotation.
        List<Position> patternCourant = patternDeBase;

        // Pour éviter les doublons, il nous faut une liste non triée des géométries déjà construites
        Set<Set<Position>> formesDejaVues = new HashSet<>();

        // On rajoute à la liste des configurations possibles chaque rotation de 60 degrés.
        for (int i = 0; i<6; i++) {
            Set<Position> formeEnSet = new HashSet<>(patternCourant);
            if (!formesDejaVues.contains(formeEnSet)) {
                formesDejaVues.add(formeEnSet);
                configurationsPossibles.add(patternCourant);
            }
            patternCourant = rotatePattern(patternCourant);
        }
    }

    private List<Position> rotatePattern(List<Position> patternCourant) {
        List<Position> rotatedPattern = new ArrayList<>();
        for (Position currentPosition : patternCourant) {
            rotatedPattern.add(currentPosition.rotate60());
        }
        return rotatedPattern;
    }

    public List<List<Position>> getConfigurationsPossibles() {
        return configurationsPossibles;
    }
}
