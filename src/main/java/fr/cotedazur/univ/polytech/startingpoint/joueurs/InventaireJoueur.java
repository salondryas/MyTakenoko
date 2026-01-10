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
    private int nombreCanauxDisponibles; // Nombre de canaux d'irrigation disponibles

    public InventaireJoueur() {
        this.score = 0;
        this.objectifs = new ArrayList<>();
        this.bambous = new ArrayList<>();
        this.nombreCanauxDisponibles = 0;
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

    /**
     * Ajoute une irrigation à l'inventaire du joueur.
     * Appelée lorsque l'action "irrigation" est choisie.
     */
    public void ajouterIrrigation() {
        nombreCanauxDisponibles++;
    }

    /**
     * Retire une irrigation de l'inventaire.
     * Appelée quand un canal est placé avec succès.
     * 
     * return true si une irrigation était disponible, false sinon
     */
    public boolean retirerIrrigation() {
        if (nombreCanauxDisponibles > 0) {
            nombreCanauxDisponibles--;
            return true;
        }
        return false;
    }

    public int getNombreCanauxDisponibles() {
        return nombreCanauxDisponibles;
    }

    public boolean aDesCanaux() {
        return nombreCanauxDisponibles > 0;
    }

    @Override
    public String toString() {
        return "Score: " + score + ", Bambous: " + bambous;
    }
}