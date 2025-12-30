package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
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
        gameState = new GameState(List.of(bot));
    }

    @Test
    void testJouer() {
        assertDoesNotThrow(() -> {
            bot.jouer(gameState);
        });
    }

    @Test
    void testVerifierObjectifs_ValidationEtSuppression() {
        // 1. On donne un objectif au bot
        ObjectifPoseur obj = new ObjectifPoseur(2, Couleur.VERT);
        bot.getInventaire().ajouterObjectif(obj);

        // Vérif avant : 1 objectif dans la liste, 0 validé
        assertEquals(1, bot.getInventaire().getObjectifs().size());
        assertEquals(0, bot.getNombreObjectifsValides());

        // 2. On prépare le plateau pour valider
        Plateau plateau = gameState.getPlateau();
        plateau.placerParcelle(new Parcelle(Couleur.VERT), new Position(1, -1, 0));
        plateau.placerParcelle(new Parcelle(Couleur.VERT), new Position(1, 0, -1));

        // 3. On lance la vérification
        bot.verifierObjectifs(gameState);

        // 4. VERIFICATIONS CRUCIALES (Mises à jour)

        // Le bot doit avoir des points
        assertTrue(bot.getScore() > 0);

        // L'objectif doit avoir été RETIRÉ de la liste (pour ne pas être revalidé)
        assertEquals(0, bot.getInventaire().getObjectifs().size(), "L'objectif validé doit être retiré de l'inventaire");

        // Le compteur d'objectifs validés doit être à 1
        assertEquals(1, bot.getNombreObjectifsValides());
    }
}