package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

import java.util.ArrayList;
import java.util.List;

public class InventaireJoueur {
    private int score;
    private List<Objectif> objectifs;
    private List<Couleur> bambous;
    private int nombreObjectifsValides = 0;

    public InventaireJoueur() {
        this.score = 0;
        this.objectifs = new ArrayList<>();
        this.bambous = new ArrayList<>();
    }

    public void ajouterObjectif(Objectif objectif) {
        this.objectifs.add(objectif);
    }

    // --- CORRECTION : Méthode pour retirer un objectif validé ---
    public void retirerObjectif(Objectif objectif) {
        this.objectifs.remove(objectif);
    }
    // ------------------------------------------------------------

    public List<Objectif> getObjectifs() {
        // Retourne une copie pour protéger la liste (ce qui a causé le bug avant)
        return new ArrayList<>(objectifs);
    }

    public void ajouterPoints(int points) {
        this.score += points;
    }

    public int getScore() {
        return score;
    }

    public void ajouterBambou(Couleur couleur) {
        bambous.add(couleur);
    }

    public boolean retirerBambou(Couleur couleur) {
        return bambous.remove(couleur);
    }

    public List<Couleur> getBambous() {
        return new ArrayList<>(bambous);
    }

    public void incrementerObjectifsValides() {
        this.nombreObjectifsValides++;
    }

    public int getNombreObjectifsValides() {
        return this.nombreObjectifsValides;
    }

    @Override
    public String toString() {
        return "Score: " + score + ", Bambous: " + bambous;
    }
}