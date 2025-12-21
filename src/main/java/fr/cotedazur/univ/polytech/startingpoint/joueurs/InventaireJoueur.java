package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

import java.util.ArrayList;
import java.util.List;

public class InventaireJoueur {
    private int score;
    private List<Objectif> objectifs;
    // Ajout pour le Panda
    private List<Couleur> bambous;

    public InventaireJoueur() {
        this.score = 0;
        this.objectifs = new ArrayList<>();
        this.bambous = new ArrayList<>();
    }

    public void ajouterObjectif(Objectif objectif) {
        this.objectifs.add(objectif);
    }

    public List<Objectif> getObjectifs() {
        return new ArrayList<>(objectifs);
    }

    public void ajouterPoints(int points) {
        this.score += points;
    }

    public int getScore() {
        return score;
    }

    // --- METHODES POUR LE PANDA ---
    public void ajouterBambou(Couleur couleur) {
        bambous.add(couleur);
    }

    public boolean retirerBambou(Couleur couleur) {
        return bambous.remove(couleur);
    }

    public List<Couleur> getBambous() {
        return new ArrayList<>(bambous);
    }
    // -----------------------------

    @Override
    public String toString() {
        return "Score: " + score + ", Bambous: " + bambous;
    }
}