package fr.cotedazur.univ.polytech.startingpoint.plateau;

import java.util.List;

//import fr.cotedazur.univ.polytech.startingpoint.utilitaires.InvalidDestinationException;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

public class Jardinier {
    private Position position;

    public Jardinier() {
        this.position = new Position(0, 0);
    }

    public Position getPosition() {
        return position;
    }

    public void DeplacerJardinier(Plateau plateau) {
        List<Position> cibles = plateau.getTrajetsLigneDroite(this.position);

        if (!cibles.isEmpty()) {
            // Logique du Bot : choisir une position dans la liste (ex: la première)
            Position destination = cibles.get(0);
            this.position = destination;
            // Action spécifique
            pousserBambou(destination, plateau);
        }
    }

    public void pousserBambou(Position positionCiblee, Plateau plateau) {
        Parcelle parcelle = plateau.getParcelle(positionCiblee);
        Bambou bambou = parcelle.getBambou();
        bambou.croissance(); // completer pour le faire sur les parcelles adjacentes aussi
    }
}
