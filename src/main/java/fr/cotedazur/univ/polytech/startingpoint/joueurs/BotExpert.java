package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.TypeAction;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.SelectionParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;

import java.util.List;
import java.util.Set;

public class BotExpert extends Bot {

    private final ExpertStrategie strategie;
    private final ExpertMeteo meteoManager;

    public BotExpert(String nom) {
        super(nom);
        this.strategie = new ExpertStrategie(this);
        this.meteoManager = new ExpertMeteo();
    }

    @Override
    protected Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {
        Action actionChoisie = strategie.choisirMeilleureAction(gameState, typesInterdits);
        if (actionChoisie == null) return null;
        return actionChoisie;
    }

    // --- Délégation Météo (C'est ici qu'il manquait une méthode) ---

    @Override
    public Meteo choisirMeteo() {
        return meteoManager.choisirMeteoStrategy();
    }

    // CORRECTION : Ajout de la méthode manquante définie dans Bot.java
    @Override
    public Meteo choisirMeteoAlternative() {
        // On peut réutiliser la même stratégie ou en faire une autre
        return meteoManager.choisirMeteoStrategy();
    }

    @Override
    public Parcelle choisirParcelleMeteo(List<Parcelle> parcellesIrriguees) {
        return meteoManager.choisirParcellePourPluie(parcellesIrriguees);
    }

    @Override
    public Parcelle choisirDestinationPanda(List<Parcelle> parcelles) {
        return meteoManager.choisirParcellePourOrage(parcelles);
    }

    // --- Délégation Actions Complexes (Pour ne pas utiliser le défaut de Bot) ---

    @Override
    public Parcelle choisirParcelle(SelectionParcelle session, Plateau plateau) {
        // On délègue à la stratégie
        Parcelle p = strategie.choisirMeilleureParcelle(session);
        if (p != null) session.validerChoix(p);
        return p;
    }

    @Override
    public Position choisirPosition(Parcelle parcelleChoisie, Plateau plateau) {
        // On délègue à la stratégie
        return strategie.choisirMeilleurePosition(plateau);
    }
}