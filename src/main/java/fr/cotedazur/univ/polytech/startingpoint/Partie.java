package fr.cotedazur.univ.polytech.startingpoint;

import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Empereur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.affichage.AffichageFinPartie;
import fr.cotedazur.univ.polytech.startingpoint.weather.DeMeteo;
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;

import java.util.List;
import java.util.logging.Logger;

public class Partie {
    private static final Logger LOGGER = Logger.getLogger("Partie");
    private GameState gameState;
    public static final int OBJECTIFS_POUR_GAGNER = 9;
    private DeMeteo deMeteo;

    public Partie(List<Bot> bots) {
        this.gameState = new GameState(bots);
        this.deMeteo = new DeMeteo();
    }

    /**
     * Méthode principale (Chef d'orchestre).
     * Complexité cyclomatique réduite : ~5
     */
    public void jouer() {
        int tour = 1;
        boolean partieTerminee = false;

        LOGGER.info(" DÉBUT DE LA PARTIE : " + OBJECTIFS_POUR_GAGNER + " objectifs pour gagner !");

        while (!partieTerminee && tour < 1000) {
            LOGGER.info("\n--- Tour " + tour + " ---");
            gameState.resetForNewTurn();

            for (Bot bot : gameState.getJoueurs()) {

                bot.getActionJouableContext().resetTokenCount(); // Préparation du bot
                gererMeteo(bot, tour);

                List<Action> actionsJouees = bot.jouer(gameState);

                // Exécution et Vérification Victoire
                if (traiterActions(bot, actionsJouees)) {
                    partieTerminee = true;
                    break; // Sort de la boucle des joueurs
                }
            }
            tour++;
        }

        if (tour >= 1000) LOGGER.info(" La partie a été arrêtée (Trop longue).");

        AffichageFinPartie afp = new AffichageFinPartie(gameState);
        LOGGER.info(afp.afficher());
    }

    /**
     * Gère l'application de la météo pour un bot donné.
     */
    private void gererMeteo(Bot bot, int tour) {
        if (tour > 1) {
            Meteo meteo = deMeteo.roll();
            LOGGER.info("Météo : " + meteo);
            meteo.apply(gameState, bot, tour);
        }
    }

    /**
     * Exécute la liste des actions choisies par le bot et vérifie s'il gagne.
     * @return true si la partie est finie (victoire), false sinon.
     */
    private boolean traiterActions(Bot bot, List<Action> actionsJouees) {
        for (Action action : actionsJouees) {
            // Consommation et Logs
            bot.getActionJouableContext().consumeOneToken(action);
            LOGGER.info(bot.getNom() + " " + action.toString());

            // Application et Vérification Objectifs
            action.appliquer(gameState, bot);
            bot.verifierObjectifs(gameState);

            if (bot.getNombreObjectifsValides() >= OBJECTIFS_POUR_GAGNER) {
                gererFinDePartie(bot);
                return true;
            }
        }
        return false;
    }

    /**
     * Gère la logique de l'Empereur et l'affichage final immédiat.
     */
    private void gererFinDePartie(Bot bot) {
        LOGGER.info(" FIN DE PARTIE ! " + bot.getNom() + " a validé le dernier objectif !");
        Empereur empereur = new Empereur(bot);
        empereur.appliquer();
        LOGGER.info(empereur.toString());
    }

    public Bot getGagnant() {
        return gameState.determinerMeilleurJoueur();
    }
}