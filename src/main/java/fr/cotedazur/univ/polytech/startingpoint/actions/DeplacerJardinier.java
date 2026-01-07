package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.ArrayList;
import java.util.List;

public class DeplacerJardinier implements Action {
    private Jardinier jardinier;
    private Position destination;

    public DeplacerJardinier(Jardinier jardinier, Position destination) {
        this.jardinier = jardinier;
        this.destination = destination;
    }

    @Override
    public void appliquer(GameState gameState, Bot bot) {
        Plateau plateau = gameState.getPlateau();
        Jardinier jardinier = gameState.getJardinier();

        // 1. Déplacer le jardinier
        jardinier.setPosition(destination);

        // 2. Identifier les parcelles cibles (Celle d'arrivée + Voisines même couleur)
        List<Parcelle> parcellesAArroser = new ArrayList<>();
        Parcelle parcelleArrivee = plateau.getParcelle(destination);

        if (parcelleArrivee != null) {
            parcellesAArroser.add(parcelleArrivee);
            parcellesAArroser.addAll(plateau.getParcellesVoisinesMemeCouleur(destination));
        }

        // 3. Faire pousser le bambou
        for (Parcelle p : parcellesAArroser) {
            // On utilise la nouvelle méthode créée à l'étape 1
            boolean aPousse = p.pousserBambou();

            if (aPousse) {
                System.out.println(" Le jardinier fait pousser du bambou en " + p.getPosition());
            }
        }
    }

    @Override
    public String toString() {
        return "déplace le jardinier en " + destination;
    }
}