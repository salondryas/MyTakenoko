package fr.cotedazur.univ.polytech.startingpoint.objectifs.parcelle;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ObjectifParcelle extends Objectif {

    // Le cœur du changement : on stocke la carte qui contient le motif !
    private final CarteParcelle carte;

    public ObjectifParcelle(CarteParcelle carte) {
        super(carte.getPoints(), TypeObjectif.PARCELLE);
        this.carte = carte;
    }

    @Override
    public boolean valider(GameState gameState, Bot bot) {
        // ON UTILISE ENFIN VOTRE CLASSE MOTIF/CARTEPARCELLE !
        return carte.estValide(gameState.getPlateau());
    }

    // Pour l'affichage et l'intelligence des bots
    @Override
    public List<Couleur> getCouleurs() {
        // On peut récupérer la couleur depuis la définition du motif dans CarteParcelle
        // (Il faudra peut-être ajouter un getter getCouleur() dans CarteParcelle)
        // Pour l'instant, supposons que CarteParcelle gère une couleur principale.
        return Collections.emptyList(); // À adapter selon votre CarteParcelle
    }

    @Override
    public Map<Couleur, Integer> getObjMap() { //Pas utilisé (à modifier plus tard)
        return carte.getMap();
    }

    @Override
    public String toString() {
        return "Objectif Parcelle : " + carte.name() + " (" + getPoints() + "pts)";
    }
}