package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.actions.PoserParcelle;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

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

    // On change le type de retour : Action au lieu de void
    public Action jouer(Plateau plateau, PiocheParcelle pioche) {
        Parcelle parcellePiochee = pioche.piocherParcelle();
        if (parcellePiochee == null) return null;

        List<Position> coupsPossibles = plateau.getEmplacementsDisponibles();
        if (coupsPossibles.isEmpty()) return null;

        Position positionChoisie = coupsPossibles.get(random.nextInt(coupsPossibles.size()));

        // Ici il utilise PoserParcelle (reconnu grâce à l'import)
        // ATTENTION : Le Bot ne modifie plus le plateau, il renvoie l'action !
        return new PoserParcelle(new Parcelle(positionChoisie, parcellePiochee.getCouleur()), positionChoisie);
    }

    public void verifierObjectifs(Plateau plateau) {
        for (Objectif obj : inventaire.getObjectifs()) {
            if (!obj.isValide() && obj.valider(plateau)) {
                System.out.println(nom + " a validé l'objectif : " + obj.getClass().getSimpleName() + " (+" + obj.getPoints() + " pts)");
                inventaire.ajouterPoints(obj.getPoints());
            }
        }
    }

    public String getNom() { return nom; }
    public int getScore() { return inventaire.getScore(); }
    public InventaireJoueur getInventaire() { return inventaire; }
}