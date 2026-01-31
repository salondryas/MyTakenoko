package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

import java.util.Map;

public class ObjectifPanda extends Objectif {
    private final Couleur couleur;
    private final int nombreRequis;

    // --- CONSTRUCTEUR 1 : Celui utilisé par le jeu  ---
    public ObjectifPanda(CartePanda carte) {
        super(carte.getPoints(), TypeObjectif.PANDA);
        // Attention : Pour l'instant on gère les cartes simples (1 seule couleur).
        // Pour le Trio, il faudra adapter la logique plus tard.
        this.couleur = carte.getCouleurs().get(0);
        this.nombreRequis = carte.getNombreRequis();
    }

    // --- CONSTRUCTEUR 2 : Celui utilisé par les tests ---
    public ObjectifPanda(int points, Couleur couleur, int nombreRequis) {
        super(points, TypeObjectif.PANDA);
        this.couleur = couleur;
        this.nombreRequis = nombreRequis;
    }
    // -------------------------------------------------------------

    @Override
    public boolean valider(GameState gameState, Bot bot) {
        Map<Couleur, Integer> bambousDuBot = bot.getInventaire().getBambous();
        int nombreDeBambousDeLaBonneCouleur = bambousDuBot.getOrDefault(this.couleur, 0);

        if (nombreDeBambousDeLaBonneCouleur >= nombreRequis) {
            for (int i = 0; i < nombreRequis; i++) {
                bot.getInventaire().retirerBambou(this.couleur);
            }
            return true;
        }
        return false;
    }

    public Couleur getCouleur() {
        return this.couleur;
    }

    @Override
    public String toString() {
        return "Objectif Panda : Manger " + nombreRequis + " bambous " + couleur + " (" + super.getPoints() + "pts)";
    }
}