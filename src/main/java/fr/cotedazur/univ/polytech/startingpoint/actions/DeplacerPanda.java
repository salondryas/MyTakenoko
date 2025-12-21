package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Panda;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

public class DeplacerPanda implements Action {
    private Panda panda;
    private Position destination;

    public DeplacerPanda(Panda panda, Position destination) {
        this.panda = panda;
        this.destination = destination;
    }

    @Override
    public void appliquer(GameState gameState, Bot bot) {
        Plateau plateau = gameState.getPlateau();

        // 1. Vérification : y a-t-il du bambou à manger ?
        if (plateau.getNombreDeSectionsAPosition(destination) > 0) {
            Parcelle parcelle = plateau.getParcelle(destination);

            // 2. On récupère la couleur AVANT de manger (pour l'inventaire du bot)
            // Note: On assume que la couleur du bambou est la même que la parcelle
            Couleur couleurBambou = parcelle.getCouleur();

            // 3. Le Panda mange (retire une section sur le plateau)
            panda.mangerBambou(destination, plateau);

            // 4. Le Bot gagne le bambou dans son inventaire
            bot.getInventaire().ajouterBambou(couleurBambou);

            // Optionnel : Message de debug
            // System.out.println("Miam ! Le panda a mangé un bambou " + couleurBambou);
        }
    }

    @Override
    public String toString() {
        return "déplace le panda en " + destination;
    }
}