package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.AmenagmentAttribuable;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.affichage.AfficherInventaireJoueur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventaireJoueur {
    private int score;
    private List<Objectif> objectifs; // Objectifs à réaliser en main
    private Map<Couleur, Integer> bambous; // Sections de bambous mangées (stockées par couleur)
    private int nombreObjectifsValides = 0;
    private int nombreCanauxDisponibles; // Nombre de canaux d'irrigation disponibles

    private List<AmenagmentAttribuable> amenagements; // Attribut pour stocker les aménagements reçus via météo NUAGES

    public InventaireJoueur() {
        this.score = 0;
        this.objectifs = new ArrayList<>();
        this.nombreCanauxDisponibles = 0;

        this.bambous = new HashMap<>();
        bambous.put(Couleur.ROSE, 0);
        bambous.put(Couleur.VERT, 0);
        bambous.put(Couleur.JAUNE, 0);
        this.amenagements = new ArrayList<>();
    }

    public void ajouterObjectif(Objectif objectif) {
        this.objectifs.add(objectif);
    }

    public void retirerObjectif(Objectif objectif) {
        this.objectifs.remove(objectif);
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

    public void ajouterBambou(Couleur couleur) {
        // CORRECTION : On vérifie que la couleur est valide et existe dans la map
        if (couleur == Couleur.AUCUNE || !bambous.containsKey(couleur)) {
            return; // On ne fait rien si c'est l'étang ou une couleur invalide
        }

        int nombreBambouCouleur = bambous.get(couleur);
        bambous.put(couleur, ++nombreBambouCouleur);
    }

    public boolean retirerBambou(Couleur couleur) {
        int nombreBambouCouleur = bambous.get(couleur);
        if (nombreBambouCouleur <= 0) {
            return false;
        }
        bambous.put(couleur, --nombreBambouCouleur);
        return true;
    }

    public Map<Couleur, Integer> getBambous() {
        return bambous;
    }

    public boolean isBambouEmpty() {
        return (bambous.get(Couleur.ROSE) == 0 && bambous.get(Couleur.VERT) == 0 && bambous.get(Couleur.JAUNE) == 0);
    }

    public int getTotalNumberOfBambous() {
        return bambous.get(Couleur.ROSE) + bambous.get(Couleur.VERT) + bambous.get(Couleur.JAUNE);
    }

    public void incrementerObjectifsValides() {
        this.nombreObjectifsValides++;
    }

    public int getNombreObjectifsValides() {
        return this.nombreObjectifsValides;
    }

    public void ajouterIrrigation() {
        nombreCanauxDisponibles++;
    }

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

    // ===== METEO =====

    public void ajouterAmenagement(AmenagmentAttribuable amenagement) {
        this.amenagements.add(amenagement);
    }

    public List<AmenagmentAttribuable> getAmenagements() {
        return new ArrayList<>(amenagements);
    }

    public boolean retirerAmenagement(AmenagmentAttribuable amenagement) {
        return amenagements.remove(amenagement);
    }

    // Méthode pour vérifier si le joueur a un type d'aménagement
    public boolean hasAmenagement(AmenagmentAttribuable amenagement) {
        return amenagements.contains(amenagement);
    }

    public int getNombreAmenagements() {
        return amenagements.size();
    }

    @Override
    public String toString() {
        AfficherInventaireJoueur aij = new AfficherInventaireJoueur(this);
        return aij.afficher();
    }
}