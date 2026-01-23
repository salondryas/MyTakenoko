package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Bot {
    protected String nom;
    protected InventaireJoueur inventaire;
    protected Couleur couleur;

    public Bot(String nom) {
        this.nom = nom;
        this.inventaire = new InventaireJoueur();
    }

    // --- MÉTHODE ABSTRAITE (A définir par les fils) ---
    public abstract void jouer(GameState gameState);

    // --- MÉTHODES COMMUNES (Réintégrées ici) ---

    public void verifierObjectifs(GameState gameState) {
        List<Objectif> objectifsAValider = new ArrayList<>();

        for (Objectif obj : inventaire.getObjectifs()) {
            if (obj.valider(gameState, this)) {
                System.out.println(nom + " a validé l'objectif : " + obj.getClass().getSimpleName() + " (+" + obj.getPoints() + " pts)");
                objectifsAValider.add(obj);
            }
        }

        for (Objectif obj : objectifsAValider) {
            inventaire.ajouterPoints(obj.getPoints());
            inventaire.incrementerObjectifsValides();
            inventaire.retirerObjectif(obj);
        }
    }

    public int getNombreObjectifsValides() {
        return inventaire.getNombreObjectifsValides();
    }

    public int getScore() {
        return inventaire.getScore();
    }

    // --- GETTERS & SETTERS CLASSIQUES ---

    public String getNom() {
        return nom;
    }

    public InventaireJoueur getInventaire() {
        return inventaire;
    }

    public void setCouleur(Couleur couleur) {
        this.couleur = couleur;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bot bot)) return false;
        return Objects.equals(nom, bot.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom);
    }

    @Override
    public String toString() {
        return "Bot " + nom;
    }
}