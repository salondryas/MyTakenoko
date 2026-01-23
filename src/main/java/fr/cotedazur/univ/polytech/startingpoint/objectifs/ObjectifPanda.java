package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

import java.util.List;
import java.util.Map;

public class ObjectifPanda extends Objectif {
    private final Couleur couleur;
    private final int nombreRequis;
    private final int points; // AJOUT : le champ pour les points

    // CORRECTION : Constructeur à 3 paramètres pour correspondre à Partie.java
    // Signature : (int points, Couleur couleur, int nombreRequis)
    public ObjectifPanda(int points, Couleur couleur, int nombreRequis) {
        super();
        this.points = points;
        this.couleur = couleur;
        this.nombreRequis = nombreRequis;
    }

    @Override
    public int getPoints() {
        return points; // On retourne les points stockés
    }

    @Override
    public boolean valider(GameState gameState, Bot bot) {
        Map<Couleur,Integer> bambousDuBot = bot.getInventaire().getBambous();

        int nombreDeBambousDeLaBonneCouleur = bambousDuBot.get(this.couleur);

        // 2. Si on en a assez, on valide ET on consomme
        if (nombreDeBambousDeLaBonneCouleur >= nombreRequis) {
            // On retire les bambous utilisés
            for (int i = 0; i < nombreRequis; i++) {
                bot.getInventaire().retirerBambou(this.couleur);
            }
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "Objectif Panda : Manger " + nombreRequis + " bambous " + couleur + " (" + points + "pts)";
    }
}