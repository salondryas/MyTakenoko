package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.TypeAction;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Logger;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.SelectionParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.*;

public abstract class Bot {

    private String nom;
    private InventaireJoueur inventaire;

    // Ajout d'une constante pour faciliter le retour à 2 actions plus tard
    private static final int NombreActionsParTour = 2;

    public Bot(String nom) {
        this.nom = nom;
        this.inventaire = new InventaireJoueur();
    }

    protected abstract Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits);

    public List<Action> jouer(GameState gameState) {
        List<Action> actionsChoisies = new ArrayList<>();
        Set<TypeAction> typesDejaFaits = new HashSet<>();
        int tentatives = 0;

        // MODIFICATION ICI : On utilise la constante (1 action)
        while (actionsChoisies.size() < NombreActionsParTour && tentatives < 50) {
            Action actionProposee = choisirUneAction(gameState, typesDejaFaits);

            if (actionProposee != null && !typesDejaFaits.contains(actionProposee.getType())) {
                actionsChoisies.add(actionProposee);
                typesDejaFaits.add(actionProposee.getType());
                Logger.print(nom + " choisit l'action : " + actionProposee);
            }
            tentatives++;
        }

        verifierObjectifs(gameState);

        return actionsChoisies;
    }

    public void verifierObjectifs(GameState gameState) {
        List<Objectif> objectifsAValider = new ArrayList<>();

        for (Objectif obj : inventaire.getObjectifs()) {
            if (obj.valider(gameState, this)) {
                Logger.print(nom + " a validé l'objectif : " + obj);
                objectifsAValider.add(obj);
            }
        }

        for (Objectif obj : objectifsAValider) {
            inventaire.ajouterPoints(obj.getPoints());
            inventaire.incrementerObjectifsValides();
            inventaire.retirerObjectif(obj);
        }
    }

    // La selection de base d'une parcelle : on ne regarde pas les trois parcelles piochées, on ne prend que la première piochée.
    public Parcelle choisirParcelle(SelectionParcelle session, Plateau plateau) {
        Parcelle parcelleChoisie = session.getFirst();
        session.validerChoix(parcelleChoisie);
        return parcelleChoisie;
    }

    public Position choisirPosition(Parcelle parcelleChoisie, Plateau plateau) {
        List<Position> positionsDisponibles = plateau.getEmplacementsDisponibles();
        if (!positionsDisponibles.isEmpty()) {
            return positionsDisponibles.getFirst();
        }
        return null;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bot bot)) return false;
        return Objects.equals(nom, bot.nom);
    }

    @Override
    public int hashCode() { return Objects.hash(nom); }

    @Override
    public String toString() { return nom + " (" + getScore() + " pts)"; }
}