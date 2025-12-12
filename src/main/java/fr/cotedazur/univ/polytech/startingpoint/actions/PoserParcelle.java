package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

public class PoserParcelle implements Action {
    private final Parcelle parcelle;
    private final Position position;

    public PoserParcelle(Parcelle parcelle, Position position) {
        this.parcelle = parcelle;
        this.position = position;
    }

    @Override
    public void appliquer(Plateau plateau) {
        plateau.placerParcelle(parcelle, position);
    }

    @Override
    public String toString() {
        return "pose " + parcelle + " en " + position;
    }
}