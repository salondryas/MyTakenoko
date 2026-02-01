package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.TypeAction;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Logger;

import java.util.*;

public abstract class Bot {

    private String nom;
    private InventaireJoueur inventaire;

    public Bot(String nom) {
        this.nom = nom;
        this.inventaire = new InventaireJoueur();
    }

    protected abstract Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits);

    public List<Action> jouer(GameState gameState) {
        List<Action> actionsChoisies = new ArrayList<>();
        Set<TypeAction> typesDejaFaits = new HashSet<>();
        int tentatives = 0;

        // Sécurité : Max 50 tentatives pour éviter les boucles infinies si le bot est bloqué
        while (actionsChoisies.size() < 2 && tentatives < 50) {
            Action actionProposee = choisirUneAction(gameState, typesDejaFaits);

            // CORRECTION : On vérifie non null ET type unique
            if (actionProposee != null && !typesDejaFaits.contains(actionProposee.getType())) {
                actionsChoisies.add(actionProposee);
                typesDejaFaits.add(actionProposee.getType());
                Logger.print(nom + " choisit l'action : " + actionProposee);
            }
            tentatives++;
        }

        // Vérification automatique des objectifs à la fin du tour
        verifierObjectifs(gameState);

        return actionsChoisies;
    }

    public void verifierObjectifs(GameState gameState) {
        List<Objectif> objectifsAValider = new ArrayList<>();

        for (Objectif obj : inventaire.getObjectifs()) {
            if (obj.valider(gameState, this)) {
                Logger.print(nom + " a validé l'objectif : " + obj.toString());
                objectifsAValider.add(obj);
            }
        }

        for (Objectif obj : objectifsAValider) {
            inventaire.ajouterPoints(obj.getPoints());
            inventaire.incrementerObjectifsValides();
            inventaire.retirerObjectif(obj);
        }
    }

    // --- GETTERS ---
    public String getNom() { return nom; }
    public int getScore() { return inventaire.getScore(); }
    public InventaireJoueur getInventaire() { return inventaire; }
    public int getNombreObjectifsValides() { return inventaire.getNombreObjectifsValides(); }

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