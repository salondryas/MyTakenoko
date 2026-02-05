package fr.cotedazur.univ.polytech.startingpoint.utilitaires.affichage;

import fr.cotedazur.univ.polytech.startingpoint.joueurs.InventaireJoueur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

public class AfficherInventaireJoueur implements Afficher {

    // Modification : %d devient %s pour accepter la chaîne colorée
    static final String FORMAT = """
            ╔════ INVENTAIRE ══════════════════════════════╗
            ║  Bambous   : Vert(%s) Jaune(%s) Rose(%s)
            ║  Canaux    : %d
            ║  Objectifs : %d validés
            ║  Score     : %d points
            ╚══════════════════════════════════════════════╝
            """;

    private final InventaireJoueur inventaire;

    public AfficherInventaireJoueur(InventaireJoueur inventaire) {
        this.inventaire = inventaire;
    }

    @Override
    public String afficher() {
        // Utilisation de getOrDefault pour la sécurité
        int nbVert = inventaire.getBambous().getOrDefault(Couleur.VERT, 0);
        int nbJaune = inventaire.getBambous().getOrDefault(Couleur.JAUNE, 0);
        int nbRose = inventaire.getBambous().getOrDefault(Couleur.ROSE, 0);

        // Création des chaînes colorées : COULEUR + nombre + RESET
        String strVert = ConsoleColors.GREEN + nbVert + ConsoleColors.RESET;
        String strJaune = ConsoleColors.YELLOW + nbJaune + ConsoleColors.RESET;
        String strRose = ConsoleColors.PURPLE + nbRose + ConsoleColors.RESET; // Purple/Magenta fait office de Rose

        return String.format(FORMAT,
                strVert,
                strJaune,
                strRose,
                inventaire.getNombreCanauxDisponibles(),
                inventaire.getNombreObjectifsValides(),
                inventaire.getScore()
        );
    }
}