package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.ActionJouableContext;
import fr.cotedazur.univ.polytech.startingpoint.actions.TypeAction;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.AmenagmentAttribuable;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.elements.pioche.SelectionParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;

import java.util.*;

import static fr.cotedazur.univ.polytech.startingpoint.GameEngine.LOGGER;

public abstract class Bot {

    private String nom;
    private InventaireJoueur inventaire;
    private ActionJouableContext contexteActionJouable = new ActionJouableContext();

    public Bot(String nom) {
        this.nom = nom;
        this.inventaire = new InventaireJoueur();
    }

    protected abstract Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits);

    public List<Action> jouer(GameState gameState) {
        List<Action> actionsChoisies = new ArrayList<>();
        Set<TypeAction> typesInterdits = new HashSet<>();

        for (int i = 0; i < contexteActionJouable.getTokenCount(); i++) { // nombre d'action par tour :
                                                                          // contexteActionJouable.getTokenCount()
            Action action = choisirUneAction(gameState, typesInterdits);
            if (action == null)
                break;
            actionsChoisies.add(action);
            typesInterdits.add(action.getType());
        }
        return actionsChoisies;
    }

    public void verifierObjectifs(GameState gameState) {
        List<fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif> copy = new ArrayList<>(
                inventaire.getObjectifs());
        for (var obj : copy) {
            if (obj.valider(gameState, this)) {
                LOGGER.info(getNom() + " a validé l'objectif : " + obj.toString());
                inventaire.ajouterPoints(obj.getPoints());
                inventaire.incrementerObjectifsValides();
                inventaire.retirerObjectif(obj);
            }
        }
    }

    // --- MÉTHODES AJOUTÉES POUR COMPATIBILITÉ BOTS AVANCÉS ---

    /**
     * Requis par l'action PoserParcelle.
     */
    public Parcelle choisirParcelle(SelectionParcelle session, Plateau plateau) {
        if (session.getParcellesAChoisir().isEmpty())
            return null;
        Parcelle p = session.getFirst();
        session.validerChoix(p);
        return p;
    }

    /**
     * Requis par l'action PoserParcelle.
     */
    public Position choisirPosition(Parcelle parcelleChoisie, Plateau plateau) {
        List<Position> dispos = plateau.getEmplacementsDisponibles();
        return dispos.isEmpty() ? null : dispos.get(0);
    }

    /**
     * Requis par BotEquipe pour synchroniser l'inventaire avec les sous-bots.
     */
    public void setInventaire(InventaireJoueur inventairePartage) {
        this.inventaire = inventairePartage;
    }

    // --- GETTERS ---
    public String getNom() {
        return nom;
    }

    public InventaireJoueur getInventaire() {
        return inventaire;
    }

    public int getNombreObjectifsValides() {
        return inventaire.getNombreObjectifsValides();
    }

    public int getScore() {
        return inventaire.getScore();
    }

    public ActionJouableContext getActionJouableContext() {
        return contexteActionJouable;
    }

    /// ===== METEO =====

    public abstract Parcelle choisirParcelleMeteo(List<Parcelle> parcellesIrriguees);

    public abstract Parcelle choisirDestinationPanda(List<Parcelle> parcelles);

    public abstract Meteo choisirMeteo();

    public abstract Meteo choisirMeteoAlternative();

    public void recevoirAmenagement(AmenagmentAttribuable amenagement) {
        inventaire.ajouterAmenagement(amenagement);
    }
}