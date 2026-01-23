package fr.cotedazur.univ.polytech.startingpoint.utilitaires.affichage;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;

public class AffichageFinPartie implements Afficher {
    static final String HEADER = """
            ==========================================
                         RÉSULTATS FINAUX            \s
            ==========================================
           """;
    GameState gameState;

    public AffichageFinPartie(GameState gameState) {
        this.gameState=gameState;
    }

    public String afficher() {
        StringBuilder sb = new StringBuilder();

        // Afficher "Résultats finaux"
        sb.append(HEADER).append('\n');

        // Afficher le Plateau
        sb.append(gameState.getPlateau().toString()).append('\n');

        // Afficher les inventaires joueurs
        for (Bot bot : gameState.getJoueurs()) {
            sb.append(" JOUEUR : ").append(bot.getNom()).append('\n');
            sb.append(bot.getInventaire().toString()); // Appel du nouveau toString()
        }

        // Afficher le(s) gagnant(s)
        Bot gagnant = gameState.determinerMeilleurJoueur();
        int meilleurScore = gagnant.getScore();
        sb.append(" LE GAGNANT EST : " + (gagnant != null ? gagnant.getNom() : "Personne") + " avec " + meilleurScore + " points !");

        return sb.toString();
    }
}
