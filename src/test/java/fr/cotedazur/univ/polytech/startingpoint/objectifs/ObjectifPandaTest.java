package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.BotJardinier;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObjectifPandaTest {

    Bot bot;
    GameState gameState;

    @BeforeEach
    void setUp() {
        bot = new BotJardinier("JardinierTest");
        gameState = new GameState(List.of(bot));
    }

    @Test
    void testValiderObjectifPandaSucces() {
        // CORRECTION : On passe une liste contenant 2 fois VERT pour dire "2 bambous verts"
        ObjectifPanda objectif = new ObjectifPanda(4, List.of(Couleur.VERT, Couleur.VERT));

        // On donne exactement ce qu'il faut au bot
        bot.getInventaire().ajouterBambou(Couleur.VERT);
        bot.getInventaire().ajouterBambou(Couleur.VERT);

        // Act & Assert
        assertTrue(objectif.valider(gameState, bot), "L'objectif devrait être validé avec le bon nombre de bambous.");

        // EFFET DE BORD : Les bambous doivent avoir été consommés
        assertEquals(0, bot.getInventaire().getTotalNumberOfBambous(), "Les bambous auraient dû être retirés de l'inventaire après validation.");
    }

    @Test
    void testValiderObjectifPandaEchecPasAssez() {
        // Objectif = 2 Verts
        ObjectifPanda objectif = new ObjectifPanda(4, List.of(Couleur.VERT, Couleur.VERT));

        // Un seul bambou
        bot.getInventaire().ajouterBambou(Couleur.VERT);

        assertFalse(objectif.valider(gameState, bot), "L'objectif ne devrait pas être validé s'il manque des bambous.");

        // Vérifier que le bambou n'a PAS été consommé par erreur
        assertEquals(1, bot.getInventaire().getTotalNumberOfBambous(), "Le bambou ne doit pas être retiré si l'objectif échoue.");
    }

    @Test
    void testValiderObjectifPandaEchecMauvaiseCouleur() {
        // Objectif = 2 Verts
        ObjectifPanda objectif = new ObjectifPanda(4, List.of(Couleur.VERT, Couleur.VERT));

        // 2 bambous ROSES
        bot.getInventaire().ajouterBambou(Couleur.ROSE);
        bot.getInventaire().ajouterBambou(Couleur.ROSE);

        assertFalse(objectif.valider(gameState, bot), "L'objectif ne doit pas valider avec la mauvaise couleur.");
    }

    @Test
    void testValiderObjectifPandaSurplus() {
        // Arrange : Objectif = 2 Verts
        ObjectifPanda objectif = new ObjectifPanda(4, List.of(Couleur.VERT, Couleur.VERT));

        // On donne PLUS que nécessaire (3 Verts et 1 Rose)
        bot.getInventaire().ajouterBambou(Couleur.VERT);
        bot.getInventaire().ajouterBambou(Couleur.VERT);
        bot.getInventaire().ajouterBambou(Couleur.VERT);
        bot.getInventaire().ajouterBambou(Couleur.ROSE);

        boolean valide = objectif.valider(gameState, bot);

        assertTrue(valide, "L'objectif doit être validé même si on a plus de bambous que requis.");

        // On vérifie le reste : il doit rester 1 Vert et 1 Rose
        // Note : Utilisation de getOrDefault pour éviter les NullPointer si la map est vide pour une couleur
        int nbVertsRestants = bot.getInventaire().getBambous().getOrDefault(Couleur.VERT, 0);
        int nbRosesRestants = bot.getInventaire().getBambous().getOrDefault(Couleur.ROSE, 0);

        assertEquals(1, nbVertsRestants, "Il doit rester 1 bambou vert (3 - 2 consommés).");
        assertEquals(1, nbRosesRestants, "Le bambou rose ne doit pas être touché.");
        assertEquals(2, bot.getInventaire().getTotalNumberOfBambous(), "La taille totale de l'inventaire doit être de 2.");
    }

    @Test
    void testGetPoints() {
        // Objectif 3 Jaunes pour 10 points
        ObjectifPanda objectif = new ObjectifPanda(10, List.of(Couleur.JAUNE, Couleur.JAUNE, Couleur.JAUNE));
        assertEquals(10, objectif.getPoints(), "Le getter doit retourner le bon nombre de points.");
    }
}