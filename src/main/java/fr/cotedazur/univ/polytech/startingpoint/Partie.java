package fr.cotedazur.univ.polytech.startingpoint;

import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifPoseur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

import java.util.ArrayList;
import java.util.List;

public class Partie {
    private GameState gameState;

    public Partie() {
        List<Bot> bots = new ArrayList<>();
        Bot bot1 = new Bot("Bot 1");
        Bot bot2 = new Bot("Bot 2");
        bots.add(bot1);
        bots.add(bot2);

        // Objectifs de départ
        bot1.getInventaire().ajouterObjectif(new ObjectifPoseur(3, Couleur.VERT));
        bot2.getInventaire().ajouterObjectif(new ObjectifPoseur(3, Couleur.ROSE));

        // On initialise le GameState (qui va créer Plateau, Panda, Jardinier en interne)
        this.gameState = new GameState(bots);
    }

    public void jouer() {
        int tour = 1;
        while (gameState.getPioche().getSize() > 0) {
            System.out.println("\n--- Tour " + tour + " ---");

            for (Bot bot : gameState.getJoueurs()) {
                // Le bot décide de son action en regardant le GameState
                Action action = bot.jouer(gameState);

                if (action != null) {
                    action.appliquer(gameState, bot);
                    System.out.println(bot.getNom() + " " + action.toString());
                    bot.verifierObjectifs(gameState);
                }
            }
            tour++;
        }
        afficherResultats();
    }

    private void afficherResultats() {
        System.out.println("\n--- Fin de partie ---");
        for (Bot bot : gameState.getJoueurs()) {
            System.out.println(bot.getNom() + " Score : " + bot.getScore());
        }
    }
}