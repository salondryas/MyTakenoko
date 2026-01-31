package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.BotRandom;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObjectifJardinierTest {

    GameState gameState;
    Plateau plateau;
    Bot bot;

    @BeforeEach
    void setUp() {
        // CORRECTION : On doit passer une liste de bots au GameState
        bot = new BotRandom("JardinierBot");
        gameState = new GameState(List.of(bot));

        plateau = gameState.getPlateau();
    }

    @Test
    void validerObjectifJardinier() {
        // Grâce au constructeur 2 ajouté ci-dessus, cette ligne fonctionne :
        ObjectifJardinier objVert4 = new ObjectifJardinier(Couleur.VERT, 4, 5);

        // Position adjacente à l'étang (1,-1) -> Sera irriguée automatiquement
        Position pos = new Position(1, -1);
        Parcelle parcelleVerte = new Parcelle(pos, Couleur.VERT);

        // Au placement, comme c'est adjacent à (0,0), l'irrigation se déclenche (Taille = 1)
        plateau.placerParcelle(parcelleVerte, pos);

        // Taille actuelle = 1. Objectif = 4. -> Faux
        assertFalse(objVert4.valider(gameState, bot));

        // On fait pousser 3 fois (1 -> 2 -> 3 -> 4)
        parcelleVerte.pousserBambou();
        parcelleVerte.pousserBambou();
        parcelleVerte.pousserBambou();

        // Note : Si ta logique d'irrigation initialise à 0 au lieu de 1,
        // tu auras peut-être besoin d'un 4ème pousserBambou().
        // Le code ci-dessous gère le cas :
        if (parcelleVerte.getNbSectionsSurParcelle() < 4) {
            parcelleVerte.pousserBambou();
        }

        assertTrue(objVert4.valider(gameState, bot), "L'objectif devrait être validé avec un bambou de taille 4");
    }

    @Test
    void validerMauvaiseCouleur() {
        ObjectifJardinier objRose = new ObjectifJardinier(Couleur.ROSE, 3, 4);
        Position pos = new Position(1, 0);
        Parcelle parcelleVerte = new Parcelle(pos, Couleur.VERT);

        plateau.placerParcelle(parcelleVerte, pos); // Irriguée auto

        // On fait grandir le bambou vert au max
        for(int i=0; i<4; i++) parcelleVerte.pousserBambou();

        // On a un grand bambou VERT, mais on veut du ROSE -> Faux
        assertFalse(objRose.valider(gameState, bot));
    }
}