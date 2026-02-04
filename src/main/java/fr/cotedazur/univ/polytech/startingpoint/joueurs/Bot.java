package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.TypeAction;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.SelectionParcelle;
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

    /**
     * Méthode principale appelée par le moteur de jeu.
     * Gère la boucle de prise de décision pour (NombreActionsParTour) actions.
     */
    public List<Action> jouer(GameState gameState) {
        List<Action> actionsChoisies = new ArrayList<>();
        Set<TypeAction> typesInterdits = new HashSet<>();

        // Le bot doit choisir X actions
        for (int i = 0; i < NombreActionsParTour; i++) {
            Action action = choisirUneAction(gameState, typesInterdits);

            // Sécurité : si le bot ne sait pas quoi faire (null), on arrête son tour
            if (action == null) break;

            actionsChoisies.add(action);
            typesInterdits.add(action.getType()); // On ne peut pas refaire la même action
        }

        return actionsChoisies;
    }

    public void verifierObjectifs(GameState gameState) {
        // Copie défensive pour éviter ConcurrentModificationException si on retire un objectif en itérant
        List<fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif> objectifsACheck = new ArrayList<>(inventaire.getObjectifs());

        for (fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif obj : objectifsACheck) {
            if (obj.valider(gameState, this)) {
                fr.cotedazur.univ.polytech.startingpoint.utilitaires.Logger.print(getNom() + " a validé l'objectif : " + obj.toString());
                inventaire.ajouterPoints(obj.getPoints());
                inventaire.incrementerObjectifsValides();
                inventaire.retirerObjectif(obj);
            }
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

    // AJOUT NECESSAIRE POUR LE BOT STRATÉGIQUE
    // Permet de synchroniser l'inventaire entre le Chef et ses Sous-Bots
    protected void setInventaire(InventaireJoueur inventairePartage) {
        this.inventaire = inventairePartage;
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
}