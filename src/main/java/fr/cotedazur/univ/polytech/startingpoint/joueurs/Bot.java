package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.ArrayList; // Import nécessaire
import java.util.List;
import java.util.Random;

public class Bot {
    private String nom;
    private InventaireJoueur inventaire;
    private Random random;

    public Bot(String nom) {
        this.nom = nom;
        this.inventaire = new InventaireJoueur();
        this.random = new Random();
    }

    public Action jouer(GameState gameState) {
        Plateau plateau = gameState.getPlateau();
        PiocheParcelle pioche = gameState.getPioche();

        int choixAction = random.nextInt(3);

        switch (choixAction) {
            case 0: // POSER PARCELLE
                if (pioche.getSize() == 0) return null;
                Parcelle parcellePiochee = pioche.piocherParcelle();
                if (parcellePiochee == null) return null;

                List<Position> emplacements = plateau.getEmplacementsDisponibles();
                if (emplacements.isEmpty()) return null;

                Position positionParcelle = emplacements.get(random.nextInt(emplacements.size()));
                return new PoserParcelle(new Parcelle(positionParcelle, parcellePiochee.getCouleur()), positionParcelle);

            case 1: // PANDA
                // (Note: assurez-vous que getPanda() est bien dans GameState, sinon adaptez)
                List<Position> deplacementsPanda = plateau.getTrajetsLigneDroite(gameState.getPanda().getPositionPanda());
                if (deplacementsPanda.isEmpty()) return null;
                Position destinationPanda = deplacementsPanda.get(random.nextInt(deplacementsPanda.size()));
                return new DeplacerPanda(gameState.getPanda(), destinationPanda);

            case 2: // JARDINIER
                List<Position> deplacementsJardinier = plateau.getTrajetsLigneDroite(gameState.getJardinier().getPosition());
                if (deplacementsJardinier.isEmpty()) return null;
                Position destinationJardinier = deplacementsJardinier.get(random.nextInt(deplacementsJardinier.size()));
                return new DeplacerJardinier(gameState.getJardinier(), destinationJardinier);
            case 3: // PIOCHER OBJECTIF (Nouveau !)
                // Pour l'instant, on choisit le type au hasard
                int choixType = random.nextInt(2); // 0 ou 1 (Jardinier ou Panda)
                TypeObjectif typeChoisi = (choixType == 0) ? TypeObjectif.JARDINIER : TypeObjectif.PANDA;
                return new PiocherObjectif(typeChoisi);
            default:
                return null;
        }
    }

    // --- CORRECTION MAJEURE ICI ---
    public void verifierObjectifs(GameState gameState) {
        List<Objectif> objectifsAValider = new ArrayList<>();

        // 1. On identifie les objectifs remplis
        for (Objectif obj : inventaire.getObjectifs()) {
            if (obj.valider(gameState, this)) {
                System.out.println(nom + " a validé l'objectif : " + obj.getClass().getSimpleName() + " (+" + obj.getPoints() + " pts)");
                objectifsAValider.add(obj);
            }
        }

        // 2. On traite les récompenses et on retire les objectifs de l'inventaire
        for (Objectif obj : objectifsAValider) {
            inventaire.ajouterPoints(obj.getPoints());
            inventaire.incrementerObjectifsValides();
            inventaire.retirerObjectif(obj); // Suppression de la vraie liste
        }
    }
    // -----------------------------

    public int getNombreObjectifsValides() {
        return inventaire.getNombreObjectifsValides();
    }

    public String getNom() { return nom; }
    public int getScore() { return inventaire.getScore(); }
    public InventaireJoueur getInventaire() { return inventaire; }
}