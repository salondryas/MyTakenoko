package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.List;
import java.util.Map;

public class ObjectifParcelle extends Objectif {
    private final int nombre;
    private final List<Couleur> couleurs;

    // Constructeur adapté pour prendre une Liste
    public ObjectifParcelle(int points, int nombre, List<Couleur> couleurs) {
        super(points, TypeObjectif.PARCELLE);
        this.nombre = nombre;
        this.couleurs = couleurs;
    }

    @Override
    public boolean valider(GameState gameState, Bot bot) {
        int compteur = 0;
        Map<Position, Parcelle> parcelles = gameState.getPlateau().getParcellesMap();

        for (Parcelle p : parcelles.values()) {
            // On vérifie si la couleur de la parcelle fait partie des couleurs acceptées
            if (couleurs.contains(p.getCouleur())) {
                compteur++;
            }
        }
        return compteur >= nombre;
    }

    @Override
    public List<Couleur> getCouleurs() {
        return this.couleurs;
    }

    @Override
    public String toString() {
        return "Objectif Parcelle : Avoir " + nombre + " parcelles " + couleurs + " sur le plateau (" + super.getPoints() + "pts)";
    }
}