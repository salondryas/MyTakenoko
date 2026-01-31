package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.TypeAction;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;

import java.util.*;

/**
 * - La méthode jouer() gère la structure du tour (la boucle, les règles du jeu).
 * - La méthode abstraite choisirUneAction() délègue l'intelligence aux sous-classes (BotJardinier, BotPanda...).
 */
public abstract class Bot {

    private String nom;
    private InventaireJoueur inventaire;

    public Bot(String nom) {
        this.nom = nom;
        this.inventaire = new InventaireJoueur();
    }

    /**
     * Méthode abstraite que les enfants DOIVENT implémenter.
     * C'est ici que réside l'intelligence spécifique du Bot.
     */

    protected abstract Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits);

    /**
     * Méthode principale appelée par le moteur de jeu.
     * Elle essaie de trouver 2 actions distinctes à jouer.
     */
    public List<Action> jouer(GameState gameState) {
        List<Action> actionsChoisies = new ArrayList<>();
        Set<TypeAction> typesDejaFaits = new HashSet<>();

        int nombreActionsVoulues = 2;
        int tentativesSecurite = 0; // Pour éviter une boucle infinie si le bot est bloqué

        // On boucle tant qu'on n'a pas trouvé nos 2 actions
        while (actionsChoisies.size() < nombreActionsVoulues && tentativesSecurite < 10) {
            tentativesSecurite++;

            // Appel à l'intelligence de la sous-classe (Polymorphisme)
            Action actionProposee = choisirUneAction(gameState, typesDejaFaits);

            // Validation : l'action ne doit pas être nulle et son type ne doit pas avoir déjà été joué
            if (actionProposee != null && !typesDejaFaits.contains(actionProposee.getType())) {
                actionsChoisies.add(actionProposee);
                typesDejaFaits.add(actionProposee.getType());
            }
        }

        // Si après 10 tentatives on a moins de 2 actions, on renvoie ce qu'on a trouvé (même si c'est vide ou 1 seule action)
        return actionsChoisies;
    }

    /**
     * Vérifie si les objectifs en main sont validés par l'état actuel du jeu.
     */
    public void verifierObjectifs(GameState gameState) {
        List<Objectif> objectifsAValider = new ArrayList<>();

        // 1. On identifie les objectifs validés
        for (Objectif obj : inventaire.getObjectifs()) {
            if (obj.valider(gameState, this)) {
                System.out.println(nom + " a validé l'objectif : " + obj.toString());
                objectifsAValider.add(obj);
            }
        }

        // 2. On met à jour l'inventaire (points + retrait de la main)
        for (Objectif obj : objectifsAValider) {
            inventaire.ajouterPoints(obj.getPoints());
            inventaire.incrementerObjectifsValides();
            inventaire.retirerObjectif(obj);
        }
    }

    // --- GETTERS ---

    public String getNom() {
        return nom;
    }

    public int getScore() {
        return inventaire.getScore();
    }

    public InventaireJoueur getInventaire() {
        return inventaire;
    }

    public int getNombreObjectifsValides() {
        return inventaire.getNombreObjectifsValides();
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