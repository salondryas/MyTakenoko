package fr.cotedazur.univ.polytech.startingpoint.utilitaires.affichage;

import fr.cotedazur.univ.polytech.startingpoint.joueurs.InventaireJoueur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

import java.util.Map;

public class AfficherInventaireJoueur implements Afficher{
   static final String FORMAT = """
            ╔════ INVENTAIRE ══════════════════════════════╗
            ║  Bambous   : Vert(%d) Jaune(%d) Rose(%d)
            ║  Canaux    : %d
            ║  Objectifs : %d validés
            ║  Score     : %d points
            ╚══════════════════════════════════════════════╝\n
            """;
   private InventaireJoueur inventaireJoueur;
    private Map<Couleur, Integer> bambous;

    public AfficherInventaireJoueur(InventaireJoueur inventaireJoueur) {
        this.inventaireJoueur = inventaireJoueur;
        bambous = inventaireJoueur.getBambous();
    }

    public String afficher() {
        // 2. Construire l'affichage avec les bons champs
        return String.format(FORMAT,
                bambous.get(Couleur.JAUNE), bambous.get(Couleur.VERT), bambous.get(Couleur.ROSE),
                inventaireJoueur.getNombreCanauxDisponibles(), // Utilisation du compteur int
                        inventaireJoueur.getNombreObjectifsValides(),  // Utilisation du compteur int
                        inventaireJoueur.getScore());
    }
}
