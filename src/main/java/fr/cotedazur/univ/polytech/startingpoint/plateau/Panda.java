package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.List;

public class Panda {
    private Position positionActuellePanda;

    public Panda() {
        positionActuellePanda = new Position(0, 0);
    }

    public Position getPositionPanda() {
        return positionActuellePanda;
    }

    public void choisirEtDeplacer(Plateau plateau) {
        List<Position> cibles = plateau.getTrajetsLigneDroite(this.positionActuellePanda);

        if (!cibles.isEmpty()) {
            // Logique du Bot : choisir une position dans la liste (ex: la première)
            Position destination = cibles.get(0);
            this.positionActuellePanda = destination;
            // Action spécifique (manger pour le panda, pousser pour le jardinier)
            mangerBambou(destination, plateau);
        }
    }

    public void mangerBambou(Position positionCiblee, Plateau plateau) {
        if (plateau.getNombreDeSectionsAPosition(positionCiblee) >= 1) {
            Parcelle parcelle = plateau.getParcelle(positionCiblee);
            Bambou bambou = parcelle.getBambou();
            // Couleur couleurMangee = bambou.getSectionColour();
            bambou.retirerSection();
        }
    }

}
