package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifPoseur;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BotTest {
    Bot bot;
    GameState gameState;

    @BeforeEach
    void setUp() {
        bot = new Bot("BotTest");
        // On initialise le GameState avec notre bot
        gameState = new GameState(List.of(bot));
    }

    @Test
    void testJouer() {
        // Le bot doit renvoyer une action (ou null si rien n'est possible)
        // On vérifie juste que la méthode ne plante pas
        assertDoesNotThrow(() -> {
            bot.jouer(gameState);
        });
    }

    @Test
    void testVerifierObjectifs() {
        // 1. On donne un objectif au bot : 2 parcelles VERTES
        ObjectifPoseur obj = new ObjectifPoseur(2, Couleur.VERT);
        bot.getInventaire().ajouterObjectif(obj);

        // 2. On prépare le plateau pour valider l'objectif
        Plateau plateau = gameState.getPlateau();
        // On place 2 parcelles vertes
        plateau.placerParcelle(new Parcelle(Couleur.VERT), new Position(1, -1, 0));
        plateau.placerParcelle(new Parcelle(Couleur.VERT), new Position(1, 0, -1));

        // 3. On lance la vérification
        bot.verifierObjectifs(gameState);

        // 4. Le bot doit avoir gagné des points (ObjectifPoseur rapporte 2 pts)
        assertTrue(bot.getScore() > 0, "Le bot devrait avoir marqué des points après avoir validé l'objectif");
    }
}