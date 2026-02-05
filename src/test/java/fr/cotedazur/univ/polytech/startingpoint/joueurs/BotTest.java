package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.PiocherObjectif;
import fr.cotedazur.univ.polytech.startingpoint.actions.TypeAction;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.parcelle.ObjectifParcelle;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.parcelle.CarteParcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BotTest {

    // Une classe interne concrète juste pour tester la classe abstraite Bot
    private class ConcreteBot extends Bot {

        public ConcreteBot(String nom) {
            super(nom);
        }

        private final Random random = new Random(); // pour les choix de meteo

        @Override
        public Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {
            // Ce bot ne sait faire qu'une chose : Piocher un objectif Jardinier
            if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
                return new PiocherObjectif(TypeObjectif.JARDINIER);
            }
            return null;
        }

        //// Implementation obligatoire pour garantir la faisabilité de ces tests ci
        // Implémentation pour l'orage
        @Override
        public Parcelle choisirDestinationPanda(List<Parcelle> parcelles) {
            if (parcelles.isEmpty()) {
                return null;
            }
            // Choisit une parcelle aléatoire pour placer le panda
            return parcelles.get(random.nextInt(parcelles.size()));
        }

        // Implémentation pour le choix libre
        @Override
        public Meteo choisirMeteo() {
            Meteo[] options = { Meteo.SOLEIL, Meteo.PLUIE, Meteo.VENT, Meteo.ORAGE, Meteo.NUAGES };
            return options[random.nextInt(options.length)];
        }

        // Implémentation pour les nuages sans aménagement
        @Override
        public Meteo choisirMeteoAlternative() {
            Meteo[] options = { Meteo.SOLEIL, Meteo.PLUIE, Meteo.VENT, Meteo.ORAGE };
            return options[random.nextInt(options.length)];
        }

        // Implémentation pour la pluie
        @Override
        public Parcelle choisirParcelleMeteo(List<Parcelle> parcellesIrriguees) {
            if (parcellesIrriguees.isEmpty()) {
                return null;
            }
            // Choisit une parcelle aléatoire parmi les parcelles irriguées
            return parcellesIrriguees.get(random.nextInt(parcellesIrriguees.size()));
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
    void testVerifierObjectifs_ValidePoints() {
        // CORRECTION 1 : Utilisation de CarteParcelle
        ObjectifParcelle obj = new ObjectifParcelle(CarteParcelle.LIGNE_VERTE);
        bot.getInventaire().ajouterObjectif(obj);

        Plateau plateau = gameState.getPlateau();

        // CORRECTION 2 : Construction d'un motif valide
        // (1, -1, 0) -> (2, -2, 0) -> (3, -3, 0) est une ligne droite valide
        Position pos1 = new Position(1, -1, 0);
        Position pos2 = new Position(2, -2, 0);
        Position pos3 = new Position(3, -3, 0);

        // ASTUCE TEST : On force l'irrigation pour être sûr que l'objectif est valide
        // (Souvent les objectifs parcelles requièrent l'irrigation)
        Parcelle p1 = new Parcelle(pos1, Couleur.VERT);
        p1.triggerIrrigation();
        Parcelle p2 = new Parcelle(pos2, Couleur.VERT);
        p2.triggerIrrigation();
        Parcelle p3 = new Parcelle(pos3, Couleur.VERT);
        p3.triggerIrrigation();

        // On utilise la méthode interne de la grille pour bypasser les règles
        // d'adjacence strictes du test unitaire
        plateau.getGrille().ajouterParcelle(p1, pos1);
        plateau.getGrille().ajouterParcelle(p2, pos2);
        plateau.getGrille().ajouterParcelle(p3, pos3);

        // Action : Le bot vérifie s'il a rempli ses objectifs
        bot.verifierObjectifs(gameState);

        // Vérifications
        assertEquals(2, bot.getScore(), "Le score du bot doit être de 2 points (points de LIGNE_VERTE)");
        assertEquals(1, bot.getNombreObjectifsValides(), "Le nombre d'objectifs validés doit être incrémenté");
    }
}