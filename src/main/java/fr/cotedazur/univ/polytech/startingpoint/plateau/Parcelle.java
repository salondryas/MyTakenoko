package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Amenagement;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Placable;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.Objects;

public class Parcelle extends Placable {
    private Couleur couleur;
    private Bambou bambou;

    private boolean irriguee; // Booléen pour suivre l'état d'irrigation

    private boolean isAmenagee;
    private Amenagement amenagementAcqui;

    // Constructeur pour la pioche des parcelles, elles ont une position null si
    // elles sont dans l'inventaire d'un joueur

    public Parcelle(Couleur couleur) {
        super(null);
        this.couleur = couleur;
        this.bambou = new Bambou(couleur);
        this.irriguee = false;
        isAmenagee = false;
    }

    // constructeur pour l'utilisation des parcelles dans le jeu
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

    // retourne le nombre de sections présentes sur cette parcelle précisément
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
        // CORRECTION : Rien ne pousse sur l'étang (Couleur AUCUNE)
        if (this.couleur == Couleur.AUCUNE) {
            return false;
        }

        if (bambou != null && getNbSectionsSurParcelle() < 4) {
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
        return parcelle.getPosition().equals(this.getPosition()) && parcelle.getCouleur().equals(couleur);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, couleur);
    }

    public boolean estIrriguee() {
        return irriguee;
    }

    /**
     * Déclenche l'irrigation de cette parcelle.
     * Marque la parcelle comme irriguée et fait apparaître le bambou (0 -> 1).
     */
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
}
