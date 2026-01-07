package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
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
        bot = new Bot("JardinierBot");
    }

    @Test
    void validerObjectifJardinier() {
        // 1. On crée un objectif : Bambou VERT de taille 4
        ObjectifJardinier objVert4 = new ObjectifJardinier(Couleur.VERT, 4, 5);

        // 2. On place une parcelle VERTE
        Position pos = new Position(1, -1);
        Parcelle parcelleVerte = new Parcelle(pos, Couleur.VERT);
        plateau.placerParcelle(parcelleVerte, pos);

        // Au début, c'est vide -> Faux
        assertFalse(objVert4.valider(gameState, bot));

        // 3. On fait pousser le bambou jusqu'à 3 sections
        parcelleVerte.pousserBambou(); // taille 1 (initialisation +1 ou juste 1 selon votre implémentation)
        parcelleVerte.pousserBambou();
        parcelleVerte.pousserBambou(); // Disons qu'on arrive à 3
        // (Adaptez le nombre d'appels selon votre logique "pousserBambou")

        // Si taille < 4 -> Faux
        if (parcelleVerte.getNbSectionsSurParcelle() < 4) {
            assertFalse(objVert4.valider(gameState, bot));
            parcelleVerte.pousserBambou(); // On pousse à 4
        }

        // 4. Maintenant qu'il est grand -> Vrai
        assertTrue(objVert4.valider(gameState, bot), "L'objectif devrait être validé avec un bambou de taille 4");
    }

    @Test
    void validerMauvaiseCouleur() {
        ObjectifJardinier objRose = new ObjectifJardinier(Couleur.ROSE, 3, 4);

        // On met un bambou VERT très grand
        Position pos = new Position(1, 0);
        Parcelle parcelleVerte = new Parcelle(pos, Couleur.VERT);
        plateau.placerParcelle(parcelleVerte, pos);
        for(int i=0; i<4; i++) parcelleVerte.pousserBambou();

        // Ça ne doit pas valider l'objectif ROSE
        assertFalse(objRose.valider(gameState, bot));
    }
}