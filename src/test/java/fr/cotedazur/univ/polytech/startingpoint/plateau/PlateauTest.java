package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlateauTest {
    Position positionProche, positionProche2, positionLoin;
    List<Position> positionsAdjacentesOrigine,positionsAdjacentesProches;
    Parcelle parcelleRose;

    Plateau plateau;

    @BeforeEach
    void init() {
        plateau = new Plateau();

        positionProche = new Position(1,-1,0);
        parcelleRose = new Parcelle (Couleur.ROSE);
        positionsAdjacentesOrigine= List.of(
                new Position(0,-1),
                new Position(1,-1),
                new Position(-1,0),
                new Position(1,0),
                new Position(-1,1),
                new Position(0,1)
        );

        positionProche2 = new Position(1, 0, -1);
        positionsAdjacentesProches = List.of(
                new Position(1,-2),
                new Position(2,-2),
                new Position(0, -1),
                new Position(2,-1),
                new Position(1,0),
                new Position(-1,0),
                new Position(-1,1),
                new Position(0,1)
        );

        positionLoin = new Position(5, -5, 0);

        plateau = new Plateau();
    }

    @Test
    public void testParcellesVoisinesDisponibles(){
        // Positions voisines à l'origine
        assertEquals(positionsAdjacentesOrigine,plateau.getEmplacementsDisponibles(), "Pour la position voisine à l'origine, la liste des positions adjacentes disponibles ne concorde pas");
        // Position isolée
        assertEquals(6,plateau.getEmplacementsDisponibles().size(), "Pour la position isolée, la taille de la liste des positions adjacentes disponibles ne concorde pas");

        // On rajoute une parcelle au plateau pour un nouveau test avec deux tuiles adjacentes
        plateau.placerParcelle(parcelleRose, positionProche);

        // On refait le test avec une case en plus sur le plateau (et donc une case en moins de disponible)
        assertEquals(positionsAdjacentesProches, plateau.getEmplacementsDisponibles());
    }
}