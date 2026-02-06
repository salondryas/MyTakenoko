package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.TypeAction;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.elements.pioche.SelectionParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class BotEquipe extends Bot {

    private final EquipeStrategie strategie;
    private final Random random; // Remplacement de ExpertMeteo

    public BotEquipe(String nom) {
        super(nom);
        this.strategie = new EquipeStrategie(this);
        this.random = new Random(); // Initialisation du générateur aléatoire interne
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

    // --- Gestion Météo (Intégrée directement ici) ---

    @Override
    public Meteo choisirMeteo() {
        // Logique récupérée de ExpertMeteo : Aléatoire parmi les options
        Meteo[] options = { Meteo.SOLEIL, Meteo.PLUIE, Meteo.VENT, Meteo.ORAGE, Meteo.NUAGES };
        return options[random.nextInt(options.length)];
    }

    @Override
    public Meteo choisirMeteoAlternative() {
        // Même logique pour l'alternative
        Meteo[] options = { Meteo.SOLEIL, Meteo.PLUIE, Meteo.VENT, Meteo.ORAGE, Meteo.NUAGES };
        return options[random.nextInt(options.length)];
    }

    @Override
    public Parcelle choisirParcelleMeteo(List<Parcelle> parcellesIrriguees) {
        // Logique récupérée de choisirParcellePourPluie
        if (parcellesIrriguees.isEmpty()) return null;
        return parcellesIrriguees.get(random.nextInt(parcellesIrriguees.size()));
    }

    @Override
    public Parcelle choisirDestinationPanda(List<Parcelle> parcelles) {
        // Logique récupérée de choisirParcellePourOrage
        if (parcelles.isEmpty()) return null;
        return parcelles.get(random.nextInt(parcelles.size()));
    }
}