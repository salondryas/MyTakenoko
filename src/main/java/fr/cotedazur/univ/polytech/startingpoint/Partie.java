package fr.cotedazur.univ.polytech.startingpoint;

import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.*;
import java.util.logging.Logger;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.affichage.AffichageFinPartie;
import fr.cotedazur.univ.polytech.startingpoint.weather.DeMeteo;
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;

import java.util.List;
import java.util.logging.Logger; // <--- LE VRAI LOGGER STANDARD

public class Partie {
    // On récupère le logger global
    private static final Logger LOGGER = Logger.getLogger("Partie");
    private GameState gameState;
    static final int OBJECTIFS_POUR_GAGNER = 9;
    private DeMeteo deMeteo;

    // MODIFICATION : On passe les bots en paramètre !
    public Partie(List<Bot> bots) {
        this.gameState = new GameState(bots);

        // 3. On prepare le dé météo
        deMeteo = new DeMeteo();

    }

    public void jouer() {
        int tour = 1;
        boolean partieTerminee = false;

        LOGGER.info(" DÉBUT DE LA PARTIE : " + OBJECTIFS_POUR_GAGNER + " objectifs pour gagner !");

        while (!partieTerminee && tour < 1000) {
            LOGGER.info("\n--- Tour " + tour + " ---");

            // Reset du contexte météo au début du tour
            gameState.resetForNewTurn();

            // Boucle sur chaque Joueur
            for (Bot bot : gameState.getJoueurs()) {

                // 0. On (re)initialise le nombre de jetons (Logique HEAD conservée)
                bot.getActionJouableContext().resetTokenCount();

                // 1. Le bot lance le dé et applique les effets de la météo

                // Application de la météo (sauf au tour 1)
                if (tour > 1) {
                    Meteo meteo = deMeteo.roll();
                    System.out.println("Météo : " + meteo); // Affichage de la météo à changer avec Logger
                    meteo.apply(gameState, bot, tour);
                }

                // 2. Le bot choisit ses 2 actions
                List<Action> actionsJouees = bot.jouer(gameState);

                // 3. On exécute les actions une par une
                for (Action action : actionsJouees) {
                    // FUSION : On garde la conso de jeton (HEAD) ET on utilise le Logger (Feature)

                    // 4. Consommation d'un jeton selon le type d'action jouée
                    bot.getActionJouableContext().consumeOneToken(action);

                    LOGGER.info(bot.getNom() + " " + action.toString());

                    action.appliquer(gameState, bot);

                    // 5. Vérification des objectifs après CHAQUE action
                    bot.verifierObjectifs(gameState);

                    if (bot.getNombreObjectifsValides() >= OBJECTIFS_POUR_GAGNER) {
                        LOGGER.info(" FIN DE PARTIE ! " + bot.getNom() + " a validé le dernier objectif !");
                        // FUSION : Utilisation du Logger
                        Empereur empereur = new Empereur(bot);
                        empereur.appliquer();

                        LOGGER.info(empereur.toString()); // Affichage de fin de partie

                        partieTerminee = true;
                        break;
                    }
                }
                if (partieTerminee) break;
            }
            tour++;
        }

        if (tour >= 1000) LOGGER.info(" La partie a été arrêtée (Trop longue).");

        // On n'affiche le détail que si le Logger est activé (donc pas en mode stats)
        AffichageFinPartie afp = new AffichageFinPartie(gameState);
        LOGGER.info(afp.afficher());
    }

    // Ajout nécessaire pour les stats
    public Bot getGagnant() {
        return gameState.determinerMeilleurJoueur();
    }
}