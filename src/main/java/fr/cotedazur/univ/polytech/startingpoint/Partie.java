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
        Bot bot1 = new Bot("Player 1");
        Bot bot2 = new Bot("Player 2");
        bots.add(bot1);
        bots.add(bot2);

        // --- CORRECTION : AJOUT DES POINTS DANS LES CONSTRUCTEURS ---

        // Bot 1 :
        // 3 parcelles VERTES = 3 points
        bot1.getInventaire().ajouterObjectif(new ObjectifPoseur(3, Couleur.VERT, 3));
        // Bambou JAUNE taille 4 = 4 points
        bot1.getInventaire().ajouterObjectif(new ObjectifJardinier(Couleur.JAUNE, 4, 4));
        // 2 bambous ROSES = 4 points
        bot1.getInventaire().ajouterObjectif(new ObjectifPanda(4, Couleur.ROSE, 2));

        // Bot 2 :
        bot2.getInventaire().ajouterObjectif(new ObjectifPoseur(3, Couleur.JAUNE, 3));
        bot2.getInventaire().ajouterObjectif(new ObjectifJardinier(Couleur.ROSE, 4, 4));
        bot2.getInventaire().ajouterObjectif(new ObjectifPanda(4, Couleur.VERT, 2));

        this.gameState = new GameState(); // Constructeur vide
        this.gameState.getJoueurs().addAll(bots); // Ajout manuel
    }

    public void jouer() {
        int tour = 1;
        boolean partieTerminee = false;

        // On joue tant que personne n'a gagné ET qu'il reste des tuiles (ou que la pioche est vide mais qu'on finit le tour)
        // Note: gameState.getPioche().getSize() > 0 est une sécurité, mais le jeu peut continuer un peu après.
        while (!partieTerminee && tour < 100) { // Sécurité anti-boucle infinie
            System.out.println("\n--- Tour " + tour + " ---");

            for (Bot bot : gameState.getJoueurs()) {
                Action action = bot.jouer(gameState);

                if (action != null) {
                    System.out.println(bot.getNom() + " " + action.toString()); // 1. On annonce
                    action.appliquer(gameState, bot); // 2. On exécute (les logs de pousse s'afficheront ici)
                    bot.verifierObjectifs(gameState);
                } else {
                    System.out.println(bot.getNom() + " ne peut rien faire ce tour-ci.");
                }

                if (bot.getNombreObjectifsValides() >= 2) { // Victoire à 2 objectifs pour le test
                    System.out.println( bot.getNom() + " a validé 2 objectifs ! Fin de la partie.");
                    partieTerminee = true;
                    break;
                }
            }
            tour++;
        }
        afficherResultats();
    }

    private void afficherResultats() {
        System.out.println("\n--- Résultats finaux ---");
        for (Bot bot : gameState.getJoueurs()) {
            System.out.println(bot.getNom() + " - Score : " + bot.getScore() + " pts (" + bot.getNombreObjectifsValides() + " obj. validés)");
        }
    }
}