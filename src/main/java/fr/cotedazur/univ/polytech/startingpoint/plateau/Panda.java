package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

public class Panda {
    private Position positionActuellePanda;
    private boolean canEAT; // dicte s'il est autorisé ou non a manger (les amenagement peuvent le lui
                            // interdire)

    public Panda() {
        this.positionActuellePanda = new Position(0, 0);
        canEAT = true;
    }

    public Position getPositionPanda() {
        return positionActuellePanda;
    }

    // AJOUT CRUCIAL : C'est cette méthode que l'Action appellera
    public void setPositionPanda(Position position) {
        this.positionActuellePanda = position;
        canEAT = true;
    }

    public boolean mangerBambou(Position destination, Plateau plateau) {
        if ((plateau.getNombreDeSectionsAPosition(positionActuellePanda) > 0) && canEAT) {
            Parcelle parcelle = plateau.getParcelle(positionActuellePanda);
            parcelle.getBambou().retirerSection();
            return true; // A mangé
        }
        return false; // Rien à manger
    }

    public void cannotEat() {
        canEAT = false;
    }
}