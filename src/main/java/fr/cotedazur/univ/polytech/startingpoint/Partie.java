package fr.cotedazur.univ.polytech.startingpoint;

import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifJardinier;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifPanda;
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

        // --- MISE EN PLACE DES 3 OBJECTIFS FIXES ---

        // Bot 1 : Un peu de tout (Vert / Jaune / Rose)
        bot1.getInventaire().ajouterObjectif(new ObjectifPoseur(3, Couleur.VERT)); // Poseur
        bot1.getInventaire().ajouterObjectif(new ObjectifJardinier(Couleur.JAUNE, 4)); // Jardinier (Bambou taille 4)
        bot1.getInventaire().ajouterObjectif(new ObjectifPanda(Couleur.ROSE, 2)); // Panda (Manger 2 bambous roses)

        // Bot 2 : Une autre combinaison
        bot2.getInventaire().ajouterObjectif(new ObjectifPoseur(3, Couleur.JAUNE));
        bot2.getInventaire().ajouterObjectif(new ObjectifJardinier(Couleur.ROSE, 4));
        bot2.getInventaire().ajouterObjectif(new ObjectifPanda(Couleur.VERT, 2));

        this.gameState = new GameState(bots);
    }

    public void jouer() {
        int tour = 1;
        boolean partieTerminee = false;

        // Condition de fin : On joue tant que personne n'a gagné ET qu'il reste des tuiles (sécurité)
        while (!partieTerminee && gameState.getPioche().getSize() > 0) {
            System.out.println("\n--- Tour " + tour + " ---");

            for (Bot bot : gameState.getJoueurs()) {
                Action action = bot.jouer(gameState);

                if (action != null) {
                    action.appliquer(gameState, bot);
                    System.out.println(bot.getNom() + " " + action.toString());
                    bot.verifierObjectifs(gameState);
                }

                // Vérification de la condition de victoire (>= 2 objectifs validés)
                if (bot.getNombreObjectifsValides() >= 2) {
                    System.out.println( bot.getNom() + " a validé 2 objectifs ! Fin de la partie.");
                    partieTerminee = true;
                    break; // On sort de la boucle for
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