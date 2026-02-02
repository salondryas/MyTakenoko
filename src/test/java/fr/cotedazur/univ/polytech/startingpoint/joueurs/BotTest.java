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

    // Une classe interne concrète juste pour tester la classe abstraite Bot
    private class ConcreteBot extends Bot {
        public ConcreteBot(String nom) { super(nom); }

        @Override
        public Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {
            // Ce bot ne sait faire qu'une chose : Piocher un objectif Jardinier
            if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
                return new PiocherObjectif(TypeObjectif.JARDINIER);
            }
            // Si on lui interdit de piocher (2ème action du tour), il ne fait rien
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
    void testJouer_VerifieNombreActions() {
        List<Action> actions = bot.jouer(gameState);

        // Explication :
        // 1. Le bot joue "Piocher".
        // 2. Le jeu lui redemande une action, mais "Piocher" est interdit.
        // 3. Le bot renvoie null.
        // 4. La liste finale contient donc 1 action.
        assertEquals(1, actions.size(), "Ce bot spécifique ne devrait réussir à jouer qu'une seule action unique");
    }

    @Test
    void testVerifierObjectifs_ValidePoints() {
        // CORRECTION 1 : Constructeur avec List.of(Couleur)
        // Objectif : 2 points pour 2 parcelles VERTES
        ObjectifParcelle obj = new ObjectifParcelle(2, 2, List.of(Couleur.VERT));
        bot.getInventaire().ajouterObjectif(obj);

        Plateau plateau = gameState.getPlateau();

        // CORRECTION 2 : Constructeur Parcelle avec Position
        // On définit les positions explicitement
        Position pos1 = new Position(1, 0, -1);
        Position pos2 = new Position(0, 1, -1);

        // On place les parcelles sur le plateau (nécessaire pour la validation)
        // Note : placerParcelle demande (Parcelle, Position), et le constructeur Parcelle demande (Position, Couleur)
        plateau.placerParcelle(new Parcelle(pos1, Couleur.VERT), pos1);
        plateau.placerParcelle(new Parcelle(pos2, Couleur.VERT), pos2);

        // Action : Le bot vérifie s'il a rempli ses objectifs
        bot.verifierObjectifs(gameState);

        // Vérifications
        assertEquals(2, bot.getScore(), "Le score du bot doit être de 2 points après validation");
        assertEquals(1, bot.getNombreObjectifsValides(), "Le nombre d'objectifs validés doit être incrémenté");
        assertTrue(bot.getInventaire().getObjectifs().isEmpty(), "L'objectif doit être retiré de l'inventaire après validation");
    }
}