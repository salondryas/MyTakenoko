package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;

import static fr.cotedazur.univ.polytech.startingpoint.Partie.OBJECTIFS_POUR_GAGNER;

public class Empereur {
    private static final int POINTS_EMPEREUR = 2;
    private Bot joueur;
    public Empereur(Bot joueur) {
        this.joueur=joueur;
    }

    public boolean isValide() {
        return joueur.getInventaire().getNombreObjectifsValides() >= OBJECTIFS_POUR_GAGNER;
    }

    private void updateScore() {
        joueur.getInventaire().ajouterPoints(POINTS_EMPEREUR);
    }

    public boolean appliquer() {
        if (isValide()) {
            updateScore();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "\nLe joueur " + joueur.getNom() + " a validé ses "+OBJECTIFS_POUR_GAGNER+ " objectifs en premier.\n" +
                "L'empereur est content de son travail (+"+POINTS_EMPEREUR+"pts). La partie est FINIE.";
    }
}
