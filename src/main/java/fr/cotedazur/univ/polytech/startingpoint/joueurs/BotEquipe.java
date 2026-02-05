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

public class BotEquipe extends Bot {

    private final EquipeStrategie strategie;
    private final ExpertMeteo meteoManager; // Réutilisation de votre ExpertMeteo existant !

    public BotEquipe(String nom) {
        super(nom);
        this.strategie = new EquipeStrategie(this);
        this.meteoManager = new ExpertMeteo(); // On réutilise le code météo intelligent
    }

    @Override
    protected Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {
        return strategie.arbitrerLesPropositions(gameState, typesInterdits);
    }

    // --- Délégations Techniques (Architecte) ---

    @Override
    public Parcelle choisirParcelle(SelectionParcelle session, Plateau plateau) {
        return strategie.choisirParcelle(session, plateau);
    }

    @Override
    public Position choisirPosition(Parcelle parcelleChoisie, Plateau plateau) {
        return strategie.choisirPosition(parcelleChoisie, plateau);
    }

    // --- Délégations Météo (Réutilisation ExpertMeteo) ---

    @Override
    public Meteo choisirMeteo() {
        return meteoManager.choisirMeteoStrategy();
    }

    @Override
    public Meteo choisirMeteoAlternative() {
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
}