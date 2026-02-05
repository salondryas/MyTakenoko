package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.BotRandom;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.parcelle.CarteParcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.parcelle.ObjectifParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectifParcelleTest {
    Bot bot;
    GameState gameState;
    ObjectifParcelle objectifLigneVerte;

    @BeforeEach
    void setUp() {
        bot = new BotRandom("Testeur");
        gameState = new GameState(List.of(bot));
        objectifLigneVerte = new ObjectifParcelle(CarteParcelle.LIGNE_VERTE);
    }

    @Test
    void testObjectifValide() {
        Plateau plateau = gameState.getPlateau();

        // Construction manuelle d'une ligne
        Position p1 = new Position(1, 0);
        Position p2 = new Position(2, 0);
        Position p3 = new Position(3, 0);

        // Force l'ajout dans la grille (bypass règles de pose pour le test du motif uniquement)
        plateau.getGrille().ajouterParcelle(new Parcelle(p1, Couleur.VERT), p1);
        plateau.getGrille().ajouterParcelle(new Parcelle(p2, Couleur.VERT), p2);
        plateau.getGrille().ajouterParcelle(new Parcelle(p3, Couleur.VERT), p3);

        assertTrue(objectifLigneVerte.valider(gameState, bot));
    }
}