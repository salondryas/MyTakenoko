package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum CartePanda {
    PANDA_VERT(Map.of(Couleur.VERT, 2), 2, 5),      // 2 Verts, 2 pts, 5 exemplaires
    PANDA_JAUNE(Map.of(Couleur.JAUNE, 2), 4, 4),     // 2 Jaunes, 4 pts, 4 exemplaires
    PANDA_ROSE(Map.of(Couleur.ROSE, 2), 5, 3),      // 2 Roses, 5 pts, 3 exemplaires
    PANDA_TRIO(Map.of(Couleur.VERT, 1, Couleur.JAUNE, 1, Couleur.ROSE, 1), 2, 3); // Trio, 2 pts, 3 exemplaires

    private final Map<Couleur, Integer> besoins;
    private final int points;
    private final int occurrence;

    CartePanda(Map<Couleur, Integer> besoins, int points, int occurrence) {
        this.besoins = besoins;
        this.points = points;
        this.occurrence = occurrence;
    }

    public Map<Couleur, Integer> getBesoins() { return besoins; }
    public int getPoints() { return points; }
    public int getOccurrence() { return occurrence; }

    public List<Couleur> getCouleurs() {
        // keySet() retourne un Set, on le transforme en List pour qu'il soit plus facile à utiliser
        return new ArrayList<>(besoins.keySet());
    }

    //renvoit le total du nombre de bambous à manger (et non pas le nombre requis PAR couleur)
    public int getNombreRequis() {
        int total = 0;
        // On parcourt toutes les valeurs (les quantités) de la Map
        for (int quantite : besoins.values()) {
            total += quantite;
        }
        return total;
    }


}