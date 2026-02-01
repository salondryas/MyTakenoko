package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

public class Panda {
    private Position positionActuellePanda;
    private final Plateau plateau; // Référence au plateau
    private boolean canEAT;

    // CORRECTION : On demande le Plateau à la création
    public Panda(Plateau plateau) {
        this.plateau = plateau;
        this.positionActuellePanda = new Position(0, 0);
        canEAT = true;
    }

    public Position getPositionPanda() {
        return positionActuellePanda;
    }

    public void setPositionPanda(Position position) {
        this.positionActuellePanda = position;
        canEAT = true;
    }

    // On peut garder le paramètre 'plateau' pour compatibilité avec les Actions existantes,
    // ou l'enlever. Pour l'instant, on le garde mais on peut utiliser this.plateau.
    public boolean mangerBambou(Position destination, Plateau plateauPasseEnParametre) {
        // On utilise de préférence le plateau passé en paramètre s'il est là,
        // sinon this.plateau (sécurité). Ici les deux sont le même objet.
        Plateau p = (plateauPasseEnParametre != null) ? plateauPasseEnParametre : this.plateau;

        if ((p.getNombreDeSectionsAPosition(positionActuellePanda) > 0) && canEAT) {
            Parcelle parcelle = p.getParcelle(positionActuellePanda);
            parcelle.getBambou().retirerSection();
            return true; // A mangé
        }
        return false; // Rien à manger
    }

    public void cannotEat() {
        canEAT = false;
    }
}