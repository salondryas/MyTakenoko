package fr.cotedazur.univ.polytech.startingpoint;

import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.BotJardinier;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.BotRandom;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.affichage.AffichageFinPartie;

import java.util.ArrayList;
import java.util.List;

public class Partie {
    private GameState gameState;
    static final int OBJECTIFS_POUR_GAGNER = 3; // Condition d'arrêt demandée

    public Partie() {
        this.gameState = new GameState();
        List<Bot> bots = new ArrayList<>();

        // --- CONFIGURATION DU MATCH ---

        // Joueur 1 : Le Bot Aléatoire (L'outsider)
        Bot botRandom = new BotRandom("Bot Random");
        botRandom.setCouleur(Couleur.ROSE); // Juste pour l'affichage si besoin

        // Joueur 2 : Le Bot Jardinier (Le favori)
        Bot botJardinier = new BotJardinier("Bot Jardinier");
        botJardinier.setCouleur(Couleur.VERT);

        bots.add(botRandom);
        bots.add(botJardinier);

        this.gameState.getJoueurs().addAll(bots);
    }

    public void jouer() {
        int tour = 1;
        boolean partieTerminee = false;

        System.out.println(" DÉBUT DE LA PARTIE : " + OBJECTIFS_POUR_GAGNER + " objectifs pour gagner !");

        // Sécurité anti-boucle infinie (1000 tours max)
        while (!partieTerminee && tour < 1000) {
            System.out.println("\n--- Tour " + tour + " ---");

            for (Bot bot : gameState.getJoueurs()) {
                // 1. Le Bot joue (choisit et exécute son action)
                bot.jouer(gameState);

                // 2. On vérifie s'il a validé des objectifs après son action
                bot.verifierObjectifs(gameState);

                // Affichage optionnel du score courant
                // System.out.println("   > " + bot.getNom() + " : " + bot.getNombreObjectifsValides() + " obj.");

                // 3. Condition de Victoire
                if (bot.getNombreObjectifsValides() >= OBJECTIFS_POUR_GAGNER) {
                    System.out.println(" VICTOIRE ! " + bot.getNom() + " a validé " + OBJECTIFS_POUR_GAGNER + " objectifs !");
                    partieTerminee = true;
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