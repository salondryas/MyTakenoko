package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Logger;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.SelectionParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.ArrayList;
import java.util.List;

public class PoserParcelle implements Action {
    private Parcelle parcelle = null;
    private Position position = null;

    public void piocherTroisCartes(List<Parcelle> parcellesAChoisir, PiocheParcelle pioche) {
        for (int i = 0 ; i < 3 ; i++) {
            if (!pioche.estVide())
                parcellesAChoisir.add(pioche.piocherParcelle());
        }
    }

    public Parcelle piocherParcelle(GameState gameState, Bot joueur) {
        PiocheParcelle pioche = gameState.getPiocheParcelle();
        List<Parcelle> parcellesAChoisir = new ArrayList<>();

        piocherTroisCartes(parcellesAChoisir, pioche);

        Parcelle parcelleChoisie = choisirParcelle(parcellesAChoisir, gameState, joueur);

        return parcelleChoisie;
    }

    public Parcelle choisirParcelle(List<Parcelle> parcellesAChoisir, GameState gameState, Bot joueur) {
        Plateau plateau = gameState.getPlateau();
        PiocheParcelle pioche = gameState.getPiocheParcelle();

        SelectionParcelle session = new SelectionParcelle(parcellesAChoisir, pioche);

        Logger.print(joueur.getNom() + "a le choix entre les parcelles : "+ parcellesAChoisir);

        return joueur.choisirParcelle(session, plateau);
    }

    public Position choisirPosition(Bot joueur, Parcelle parcelleChoisie, Plateau plateau) {
        return joueur.choisirPosition(parcelleChoisie, plateau);
    }

    public void poserParcelle(Plateau plateau, Position positionChoisie, Parcelle parcelleChoisie) {
        position = positionChoisie;
        parcelle = parcelleChoisie;
        // SÉCURITÉ : On ne touche au plateau que si la parcelle existe vraiment
        if (parcelleChoisie != null) {
            plateau.placerParcelle(parcelleChoisie, positionChoisie);
        } else {
            // Optionnel : Un petit log pour le débug
            System.err.println("⚠️ Attention : Tentative de poser une parcelle nulle en " + positionChoisie);
        }
    }

    @Override
    public void appliquer(GameState gameState, Bot joueur) {
        Plateau plateau = gameState.getPlateau();

        Parcelle parcelleChoisie = piocherParcelle(gameState, joueur);
        Position positionChoisie = choisirPosition(joueur, parcelleChoisie, gameState.getPlateau());

        poserParcelle(plateau, positionChoisie, parcelleChoisie);

        Logger.print(joueur.getNom() + " " + this);
    }
    @Override
    public TypeAction getType() {
        return TypeAction.POSER_PARCELLE;
    }

    @Override
    public String toString() {
        // L'action n'a pas encore été exécutée (ou a échoué)
        if (parcelle == null) {
            return "pioche et pose une parcelle";
        }
        // L'action a été faite, on a les infos
        return "pose une parcelle " + parcelle.getCouleur() + " en " + position;
    }
}