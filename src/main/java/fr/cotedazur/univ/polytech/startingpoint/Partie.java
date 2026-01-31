package fr.cotedazur.univ.polytech.startingpoint;

import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.BotJardinier;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.BotRandom;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.affichage.AffichageFinPartie;

import java.util.ArrayList;
import java.util.List;

public class Partie {
    private GameState gameState;
    static final int OBJECTIFS_POUR_GAGNER = 3;

    public Partie() {
        // 1. On prépare la liste des bots AVANT de créer le GameState
        List<Bot> bots = new ArrayList<>();

        // Joueur 1 : Bot Random
        bots.add(new BotRandom("Bot Random"));
        // Joueur 2 : Bot Jardinier
        bots.add(new BotJardinier("Bot Jardinier"));

        // 2. On passe la liste au constructeur de GameState
        // C'est ici que l'erreur est corrigée (on passe 1 argument)
        this.gameState = new GameState(bots);
    }

    public void jouer() {
        int tour = 1;
        boolean partieTerminee = false;

        System.out.println(" DÉBUT DE LA PARTIE : " + OBJECTIFS_POUR_GAGNER + " objectifs pour gagner !");

        while (!partieTerminee && tour < 1000) {
            System.out.println("\n--- Tour " + tour + " ---");

            // Boucle sur chaque Joueur
            for (Bot bot : gameState.getJoueurs()) {

                // 1. Le bot choisit ses 2 actions
                List<Action> actionsJouees = bot.jouer(gameState);

                // 2. On exécute les actions une par une
                for (Action action : actionsJouees) {
                    System.out.println(bot.getNom() + " " + action.toString());
                    action.appliquer(gameState, bot);

                    // 3. Vérification des objectifs après CHAQUE action
                    bot.verifierObjectifs(gameState);

                    // Condition de victoire
                    if (bot.getNombreObjectifsValides() >= OBJECTIFS_POUR_GAGNER) {
                        System.out.println(" VICTOIRE ! " + bot.getNom() + " a validé " + OBJECTIFS_POUR_GAGNER + " objectifs !");
                        partieTerminee = true;
                        break; // On sort de la boucle des Actions
                    }
                }

                // IMPORTANT : Si la partie est finie, on sort de la boucle des Joueurs
                if (partieTerminee) {
                    break;
                }
            }
            tour++;
        }

        if (tour >= 1000) {
            System.out.println(" La partie a été arrêtée (Trop longue).");
        }

        afficherResultats();
    }

    private void afficherResultats() {
        AffichageFinPartie afp = new AffichageFinPartie(gameState);
        System.out.println(afp.afficher());
    }
}