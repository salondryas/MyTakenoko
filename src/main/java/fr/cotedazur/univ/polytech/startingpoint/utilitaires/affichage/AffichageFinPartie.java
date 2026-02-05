package fr.cotedazur.univ.polytech.startingpoint.utilitaires.affichage;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;

public class AffichageFinPartie implements Afficher {
    static final String HEADER = """
            ==========================================
                         RÉSULTATS FINAUX            \s
            ==========================================
           """;
    private final GameState gameState;

    public AffichageFinPartie(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public String afficher() {
        StringBuilder sb = new StringBuilder();

        sb.append(HEADER).append('\n');

        // Délégation à l'afficheur de plateau
        sb.append(new AfficherEtatPlateau(gameState.getPlateau()).afficher()).append('\n');

        // Afficher les joueurs
        Bot gagnant = gameState.determinerMeilleurJoueur();

        for (Bot bot : gameState.getJoueurs()) {
            sb.append(" JOUEUR : ").append(bot.getNom());
            if (bot.equals(gagnant)) {
                sb.append("  (GAGNANT)"); // Petit bonus visuel
            }
            sb.append('\n');
            // Délégation à l'afficheur d'inventaire
            sb.append(new AfficherInventaireJoueur(bot.getInventaire()).afficher());
        }

        sb.append("\n LE GAGNANT EST : ")
                .append(gagnant != null ? gagnant.getNom() : "Personne")
                .append(" avec ")
                .append(gagnant != null ? gagnant.getScore() : 0)
                .append(" points !");

        return sb.toString();
    }
}