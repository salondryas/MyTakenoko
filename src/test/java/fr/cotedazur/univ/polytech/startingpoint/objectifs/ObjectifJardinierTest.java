package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.AmenagmentAttribuable;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.BotRandom;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.elements.amenagements.Bassin;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.jardinier.ObjectifJardinier;
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
        bot = new BotRandom("JardinierBot");
        gameState = new GameState(List.of(bot));
        plateau = gameState.getPlateau();
    }

    @Test
    void validerObjectifJardinierSimple() {
        // Objectif : Bambou VERT de taille 4 (Sans aménagement spécial)
        ObjectifJardinier objVert4 = new ObjectifJardinier(Couleur.VERT, 4, 5);

        Position pos = new Position(1, -1); // Adjacent Étang
        Parcelle parcelleVerte = new Parcelle(pos, Couleur.VERT);
        plateau.placerParcelle(parcelleVerte, pos); // Irriguée auto -> taille = 1

        // On fait pousser jusqu'à 4
        while(parcelleVerte.getNbSectionsSurParcelle() < 4) {
            parcelleVerte.pousserBambou();
        }

        assertTrue(objVert4.valider(gameState, bot), "L'objectif devrait être validé avec un bambou de taille 4");
    }

    @Test
    void validerObjectifJardinierAvecBassin() {
        // NOUVEAU TEST : Objectif Bambou VERT taille 4 AVEC BASSIN
        ObjectifJardinier objBassin = new ObjectifJardinier(Couleur.VERT, 4, 6, AmenagmentAttribuable.BASSIN, 1);

        Position pos = new Position(1, 0); // Adjacent Étang
        Parcelle parcelle = new Parcelle(pos, Couleur.VERT);
        plateau.placerParcelle(parcelle, pos);

        // On fait pousser le bambou au max (taille 4)
        while(parcelle.getNbSectionsSurParcelle() < 4) {
            parcelle.pousserBambou();
        }

        // Test 1 : Bambou taille 4 mais PAS de bassin -> DOIT ÉCHOUER
        assertFalse(objBassin.valider(gameState, bot),
                "L'objectif exige un Bassin, il ne doit pas être validé sans.");

        // Test 2 : On ajoute l'aménagement Bassin
        new Bassin(parcelle, parcelle.getBambou()); // L'attache automatiquement

        // Maintenant -> DOIT RÉUSSIR
        assertTrue(objBassin.valider(gameState, bot),
                "L'objectif devrait être validé car la parcelle a maintenant un Bassin.");
    }

    @Test
    void validerMauvaiseCouleur() {
        ObjectifJardinier objRose = new ObjectifJardinier(Couleur.ROSE, 3, 4);
        Position pos = new Position(1, 0);
        Parcelle parcelleVerte = new Parcelle(pos, Couleur.VERT);

        plateau.placerParcelle(parcelleVerte, pos);

        while(parcelleVerte.getNbSectionsSurParcelle() < 4) {
            parcelleVerte.pousserBambou();
        }

        assertFalse(objRose.valider(gameState, bot), "Couleur incorrecte");
    }
}