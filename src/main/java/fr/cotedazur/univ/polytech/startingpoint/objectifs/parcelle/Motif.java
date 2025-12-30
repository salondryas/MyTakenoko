package fr.cotedazur.univ.polytech.startingpoint.objectifs.parcelle;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Représente les formes géométriques (patterns) nécessaires pour valider un objectif de parcelle
 * sur le plateau de jeu hexagonal.
 * Cette énumération gère automatiquement la génération de toutes les rotations possibles
 * pour une forme donnée et élimine les doublons causés par les symétries
 * (ex: une ligne droite est identique si on la tourne de 180°).
 */
public class Motif {
    // La liste de toutes les configurations possibles de parcelle d'un objectif
    private final List<List<Parcelle>> configurationsPossibles;

    Motif(List<Parcelle> pattern) {
        configurationsPossibles = new ArrayList<>();
        genererToutesRotations(pattern);
    }

    public void genererToutesRotations(List<Parcelle> pattern){
        // Le pattern change selon chaque rotation.
        List<Parcelle> patternCourant = pattern;

        // Pour éviter les doublons, il nous faut une liste non triée des géométries déjà construites
        Set<Set<Parcelle>> formesDejaVues = new HashSet<>();

        // On rajoute à la liste des configurations possibles chaque rotation de 60 degrés.
        for (int i = 0; i<6; i++) {
            Set<Parcelle> formeEnSet = new HashSet<>(patternCourant);

            if (!formesDejaVues.contains(formeEnSet)) {
                formesDejaVues.add(formeEnSet);
                configurationsPossibles.add(patternCourant);
            }
            patternCourant = rotatePattern(patternCourant);
        }
    }

    private List<Parcelle> rotatePattern(List<Parcelle> patternCourant) {
        // La nouvelle liste du motif tourné de 60°
        List<Parcelle> rotatedPattern = new ArrayList<>();

        for (Parcelle currrentParcelle : patternCourant) {
            // On tourne la position de la parcelle de 60° et on crée une nouvelle parcelle de la même couleur avec cette position.
            Position nouvellePosition = currrentParcelle.getPosition().rotate60();
            rotatedPattern.add(new Parcelle(nouvellePosition, currrentParcelle.getCouleur()));
        }
        return rotatedPattern;
    }

    public List<List<Parcelle>> getConfigurationsPossibles() {
        return configurationsPossibles;
    }
}
