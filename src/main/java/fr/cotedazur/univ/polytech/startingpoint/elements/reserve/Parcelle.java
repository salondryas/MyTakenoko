package fr.cotedazur.univ.polytech.startingpoint.elements.reserve;

// --- 1. IMPORTS AJOUTÉS ---
import fr.cotedazur.univ.polytech.startingpoint.elements.amenagements.Amenagement;
import fr.cotedazur.univ.polytech.startingpoint.elements.amenagements.Bassin; // <--- AJOUT
import fr.cotedazur.univ.polytech.startingpoint.elements.amenagements.Enclos; // <--- AJOUT
import fr.cotedazur.univ.polytech.startingpoint.elements.amenagements.Engrais;// <--- AJOUT

import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Placable;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.Objects;

public class Parcelle extends Placable {
    private Couleur couleur;
    private Bambou bambou;

    private boolean irriguee;
    private boolean isAmenagee;
    private Amenagement amenagementAcqui;

    // Constructeur Pioche
    public Parcelle(Couleur couleur) {
        super(null);
        this.couleur = couleur;
        this.bambou = new Bambou(couleur);
        this.irriguee = false;
        isAmenagee = false;
    }

    // Constructeur Jeu
    public Parcelle(Position position, Couleur couleur) {
        super(position);
        this.couleur = couleur;
        this.bambou = new Bambou(couleur);
        this.irriguee = false;
        isAmenagee = false;
    }

    public Bambou getBambou() {
        return bambou;
    }

    public int getNbSectionsSurParcelle() {
        if (this.bambou != null) {
            return this.bambou.getNumberOfSections();
        }
        return 0;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public boolean arrangementValide(AmenagmentAttribuable arrangementRequis) {
        // Si la carte demande "AUCUN", c'est valide par défaut
        if (arrangementRequis == AmenagmentAttribuable.AUCUN || arrangementRequis == null) {
            return true;
        }

        if (this.amenagementAcqui == null) {
            return false;
        }

        return switch (arrangementRequis) {
            case BASSIN -> this.amenagementAcqui instanceof Bassin;
            case ENCLOS -> this.amenagementAcqui instanceof Enclos;
            case ENGRAIS -> this.amenagementAcqui instanceof Engrais;
            default -> false;
        };
    }

    @Override
    public String toString() {
        if (position == null)
            return couleur.toString();
        return couleur + " : " + super.toString();
    }

    public void placer(Plateau plateau) {
        if (position == null) {
            plateau.placerParcelle(this, position);
        }
    }

    public boolean pousserBambou() {
        if (this.couleur == Couleur.AUCUNE)
            return false;
        if (bambou != null && getNbSectionsSurParcelle() < bambou.getHauteurMax() && irriguee) {
            bambou.croissance();
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null || o.getClass() != this.getClass())
            return false;
        Parcelle parcelle = (Parcelle) o;
        return Objects.equals(this.getPosition(), parcelle.getPosition()) &&
                this.getCouleur() == parcelle.getCouleur();
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, couleur);
    }

    public boolean estIrriguee() {
        return irriguee;
    }

    public void triggerIrrigation() {
        if (!irriguee) {
            irriguee = bambou.faireApparaitre();
        }
    }

    public void fetchAmenagementAcqui(Amenagement amenagement) {
        amenagementAcqui = amenagement;
        isAmenagee = true;
    }

    public boolean getIsAmenagee() {
        return isAmenagee;
    }

    public Amenagement getAmenagement() {
        return amenagementAcqui;
    }
}