package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DeplacerJardinierTest {

    DeplacerJardinier action;
    GameState gameState;
    Plateau plateau;
    Jardinier jardinier;

    @Mock
    Bot botMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // On utilise un VRAI plateau et un VRAI jardinier pour tester la logique réelle
        plateau = new Plateau();
        jardinier = new Jardinier(plateau);

        // On configure le GameState pour retourner nos objets réels
        gameState = new GameState();
        // Attention : GameState crée ses propres plateaux/jardinier par défaut,
        // mais pour le test on veut manipuler ceux qu'on a créés ici.
        // Comme on n'a pas de setters dans GameState, le plus simple est d'utiliser
        // le GameState par défaut et de récupérer ses références.
        plateau = gameState.getPlateau();
        jardinier = gameState.getJardinier();
    }

    @Test
    void testAppliquer_DeplaceEtFaitPousser_SurParcelleIrriguee() {
        // SCENARIO : Le jardinier va sur une parcelle VERTE adjacente à l'étang (donc
        // irriguée)
        Position posCible = new Position(1, 0);
        Parcelle pVerte = new Parcelle(Couleur.VERT);

        // 1. On place la parcelle (elle s'irrigue auto et bambou = 1)
        plateau.placerParcelle(pVerte, posCible);
        assertEquals(1, pVerte.getNbSectionsSurParcelle(), "Doit avoir 1 bambou à la pose (irrigation immédiate)");

        // 2. Action : Déplacer le jardinier
        action = new DeplacerJardinier(jardinier, posCible);
        action.appliquer(gameState, botMock);

        // VÉRIFICATIONS
        assertEquals(posCible, jardinier.getPosition(), "Le jardinier doit être sur la case cible");
        assertEquals(2, pVerte.getNbSectionsSurParcelle(), "Le bambou doit passer de 1 à 2");
    }

    @Test
    void testAppliquer_NeFaitPasPousser_SiNonIrriguee() {
        // SCENARIO : Parcelle LOIN de l'étang, sans canal -> NON IRRIGUÉE

        // On pose des parcelles intermédiaires pour atteindre (1,1)
        plateau.placerParcelle(new Parcelle(Couleur.ROSE), new Position(1, 0));
        plateau.placerParcelle(new Parcelle(Couleur.ROSE), new Position(0, 1));

        Position posLoin = new Position(1, 1);
        Parcelle pJaune = new Parcelle(Couleur.JAUNE);
        plateau.placerParcelle(pJaune, posLoin);

        // Vérif pré-conditions
        assertFalse(pJaune.estIrriguee());
        assertEquals(0, pJaune.getNbSectionsSurParcelle());

        // Action
        action = new DeplacerJardinier(jardinier, posLoin);
        action.appliquer(gameState, botMock);

        // VÉRIFICATIONS
        assertEquals(posLoin, jardinier.getPosition());
        assertEquals(0, pJaune.getNbSectionsSurParcelle(), "Le bambou ne doit PAS pousser car pas d'eau !");
    }

    @Test
    void testAppliquer_PropagationAuxVoisinsMemeCouleur() {
        // SCENARIO :
        // (1,0) = VERT (Irrigué car touche étang)
        // (0,1) = VERT (Irrigué car touche étang)
        // (1,-1) = ROSE (Irrigué car touche étang) - Ne doit pas pousser

        Position p1 = new Position(1, 0);
        Position p2 = new Position(0, 1);
        Position p3 = new Position(1, -1); // Voisin de p1 mais couleur différente

        Parcelle parcelleVert1 = new Parcelle(Couleur.VERT);
        Parcelle parcelleVert2 = new Parcelle(Couleur.VERT);
        Parcelle parcelleRose = new Parcelle(Couleur.ROSE);

        plateau.placerParcelle(parcelleVert1, p1);
        plateau.placerParcelle(parcelleVert2, p2);
        plateau.placerParcelle(parcelleRose, p3);

        // Vérif état initial (tous à 1 car irrigués à la pose)
        assertEquals(1, parcelleVert1.getNbSectionsSurParcelle());
        assertEquals(1, parcelleVert2.getNbSectionsSurParcelle());
        assertEquals(1, parcelleRose.getNbSectionsSurParcelle());

        // Action : Le jardinier va sur p1 (VERT)
        action = new DeplacerJardinier(jardinier, p1);
        action.appliquer(gameState, botMock);

        // VÉRIFICATIONS
        assertEquals(2, parcelleVert1.getNbSectionsSurParcelle(), "Cible principale : +1");
        assertEquals(2, parcelleVert2.getNbSectionsSurParcelle(), "Voisin même couleur : +1");
        assertEquals(1, parcelleRose.getNbSectionsSurParcelle(), "Voisin autre couleur : Inchangé");
    }

    @Test
    void testAppliquer_Max4Sections() {
        // SCENARIO : La parcelle est déjà pleine (4 sections)
        Position pos = new Position(1, 0);
        Parcelle p = new Parcelle(Couleur.VERT);
        plateau.placerParcelle(p, pos);

        // On force la taille à 4
        p.getBambou().croissance(); // 2
        p.getBambou().croissance(); // 3
        p.getBambou().croissance(); // 4
        p.getBambou().croissance(); // 5
        assertEquals(5, p.getNbSectionsSurParcelle());

        // Action
        action = new DeplacerJardinier(jardinier, pos);
        action.appliquer(gameState, botMock);

        // Vérification : Bloqué à 4
        assertEquals(5, p.getNbSectionsSurParcelle());
    }
}