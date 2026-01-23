package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

public class Panda {
    private Position positionActuellePanda;

    public Panda() {
        this.positionActuellePanda = new Position(0, 0);
    }

    public Position getPositionPanda() {
        return positionActuellePanda;
    }

    // AJOUT CRUCIAL : C'est cette méthode que l'Action appellera
    public void setPositionPanda(Position position) {
        this.positionActuellePanda = position;
    }

    public boolean mangerBambou(Position destination, Plateau plateau) {
        if (plateau.getNombreDeSectionsAPosition(positionActuellePanda) > 0) {
            Parcelle parcelle = plateau.getParcelle(positionActuellePanda);
            parcelle.getBambou().retirerSection();
            return true; // A mangé
        }
        return false; // Rien à manger
    }
}