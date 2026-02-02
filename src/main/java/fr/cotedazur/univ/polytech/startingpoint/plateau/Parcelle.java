package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Amenagement;
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

    @Override
    public String toString() {
        if (position == null) return couleur.toString();
        return couleur + " : " + super.toString();
    }

    public void placer(Plateau plateau) {
        if (position == null) {
            plateau.placerParcelle(this, position);
        }
    }

    public boolean pousserBambou() {
        if (this.couleur == Couleur.AUCUNE) return false;

        if (bambou != null && getNbSectionsSurParcelle() < 4) {
            bambou.croissance();
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
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
            irriguee = true;
            bambou.faireApparaitre();
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