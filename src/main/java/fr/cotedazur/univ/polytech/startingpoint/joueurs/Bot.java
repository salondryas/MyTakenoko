package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.ArrayList;
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

        // On essaie plusieurs fois de trouver une action valide
        // (ex: si Panda bloqué, on tente Jardinier ou Pioche)
        for (int i = 0; i < 10; i++) {
            // CORRECTION 1 : nextInt(4) pour inclure le cas 3 (Objectifs)
            int choixAction = random.nextInt(4);

            switch (choixAction) {
                case 0: // POSER PARCELLE
                    if (pioche.getSize() > 0 && !plateau.getEmplacementsDisponibles().isEmpty()) {
                        Parcelle parcellePiochee = pioche.piocherParcelle();
                        if (parcellePiochee != null) {
                            List<Position> emplacements = plateau.getEmplacementsDisponibles();
                            Position positionParcelle = emplacements.get(random.nextInt(emplacements.size()));
                            return new PoserParcelle(new Parcelle(positionParcelle, parcellePiochee.getCouleur()), positionParcelle);
                        }
                    }
                    break; // Action impossible, on boucle pour en choisir une autre

                case 1: // PANDA
                    List<Position> deplacementsPanda = plateau.getTrajetsLigneDroite(gameState.getPanda().getPositionPanda());
                    if (!deplacementsPanda.isEmpty()) {
                        Position destination = deplacementsPanda.get(random.nextInt(deplacementsPanda.size()));
                        return new DeplacerPanda(gameState.getPanda(), destination);
                    }
                    break;

                case 2: // JARDINIER
                    List<Position> deplacementsJardinier = plateau.getTrajetsLigneDroite(gameState.getJardinier().getPosition());
                    if (!deplacementsJardinier.isEmpty()) {
                        Position destination = deplacementsJardinier.get(random.nextInt(deplacementsJardinier.size()));
                        return new DeplacerJardinier(gameState.getJardinier(), destination);
                    }
                    break;

                case 3: // PIOCHER OBJECTIF
                    int choixType = random.nextInt(2);
                    TypeObjectif type = (choixType == 0) ? TypeObjectif.JARDINIER : TypeObjectif.PANDA;
                    // Cette action est toujours possible tant que les pioches ne sont pas vides
                    // On pourrait ajouter une vérification gameState.getPiocheX().isEmpty() ici
                    return new PiocherObjectif(type);
            }
        }

        // FILET DE SÉCURITÉ : Si vraiment rien n'est possible (ou pas de chance au random),
        // on renvoie une action par défaut qui ne plante pas (ex: Piocher Objectif)
        return new PiocherObjectif(TypeObjectif.JARDINIER);
    }

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

    public String getNom() { return nom; }
    public int getScore() { return inventaire.getScore(); }
    public InventaireJoueur getInventaire() { return inventaire; }
}