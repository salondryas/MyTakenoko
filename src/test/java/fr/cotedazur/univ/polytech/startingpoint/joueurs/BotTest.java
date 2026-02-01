package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.PiocherObjectif;
import fr.cotedazur.univ.polytech.startingpoint.actions.TypeAction;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifParcelle;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BotTest {

    // Classe concrète interne pour tester la classe abstraite Bot
    private class ConcreteBot extends Bot {
        public ConcreteBot(String nom) { super(nom); }

        @Override
        public Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {
            if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
                return new PiocherObjectif(TypeObjectif.JARDINIER);
            }
            return null;
        }
    }

    Bot bot;
    GameState gameState;

    @BeforeEach
    void setUp() {
        bot = new ConcreteBot("TestBot");
        gameState = new GameState(List.of(bot));
    }

    @Test
    void testJouer_MaximumDeuxActions() {
        List<Action> actions = bot.jouer(gameState);
        assertTrue(actions.size() <= 2, "Le bot ne doit pas jouer plus de 2 actions");
    }

    @Test
    void testVerifierObjectifs_ValidePoints() {
        // 1. On donne un objectif facile : Avoir 2 parcelles vertes
        // CORRECTION : Constructeur (points, nombre, couleur) -> (2 pts, 2 parcelles, VERT)
        ObjectifParcelle obj = new ObjectifParcelle(2, 2, Couleur.VERT);
        bot.getInventaire().ajouterObjectif(obj);

        // 2. On prépare le plateau (on pose 2 parcelles vertes)
        Plateau plateau = gameState.getPlateau();
        plateau.placerParcelle(new Parcelle(Couleur.VERT), new Position(1, 0, -1));
        plateau.placerParcelle(new Parcelle(Couleur.VERT), new Position(0, 1, -1));

        // 3. Vérification
        bot.verifierObjectifs(gameState);

        assertEquals(2, bot.getScore(), "Le bot doit avoir marqué les points");
        assertEquals(1, bot.getNombreObjectifsValides());
        assertTrue(bot.getInventaire().getObjectifs().isEmpty(), "L'objectif validé doit être retiré de la main");
    }
}