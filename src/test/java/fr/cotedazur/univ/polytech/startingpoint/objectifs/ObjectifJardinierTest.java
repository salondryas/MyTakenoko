package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.BotRandom; // IMPORT
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectifJardinierTest {

    GameState gameState;
    Plateau plateau;
    Bot bot;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        plateau = gameState.getPlateau();
        bot = new BotRandom("JardinierBot"); // CORRECTION
    }

    // ... le reste du fichier ne change pas ...
    @Test
    void validerObjectifJardinier() {
        ObjectifJardinier objVert4 = new ObjectifJardinier(Couleur.VERT, 4, 5);
        Position pos = new Position(1, -1);
        Parcelle parcelleVerte = new Parcelle(pos, Couleur.VERT);
        plateau.placerParcelle(parcelleVerte, pos);

        assertFalse(objVert4.valider(gameState, bot));

        parcelleVerte.pousserBambou();
        parcelleVerte.pousserBambou();
        parcelleVerte.pousserBambou();

        if (parcelleVerte.getNbSectionsSurParcelle() < 4) {
            assertFalse(objVert4.valider(gameState, bot));
            parcelleVerte.pousserBambou();
        }

        assertTrue(objVert4.valider(gameState, bot), "L'objectif devrait être validé");
    }

    @Test
    void validerMauvaiseCouleur() {
        ObjectifJardinier objRose = new ObjectifJardinier(Couleur.ROSE, 3, 4);
        Position pos = new Position(1, 0);
        Parcelle parcelleVerte = new Parcelle(pos, Couleur.VERT);
        plateau.placerParcelle(parcelleVerte, pos);
        for(int i=0; i<4; i++) parcelleVerte.pousserBambou();

        assertFalse(objRose.valider(gameState, bot));
    }
}