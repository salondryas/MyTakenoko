package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;

import java.util.List;
import java.util.Random;

public class ExpertMeteo {

    private final Random random;

    public ExpertMeteo() {
        this.random = new Random();
    }

    public Meteo choisirMeteoStrategy() {
        // Stratégie simple : Aléatoire pondéré ou choix fixe
        // Ici on remet votre logique aléatoire
        Meteo[] options = { Meteo.SOLEIL, Meteo.PLUIE, Meteo.VENT, Meteo.ORAGE, Meteo.NUAGES };
        return options[random.nextInt(options.length)];
    }

    public Parcelle choisirParcellePourPluie(List<Parcelle> parcellesIrriguees) {
        if (parcellesIrriguees.isEmpty()) return null;

        // Stratégie : Choisir celle qui a le moins de bambou pour maximiser la pousse ?
        // Ou aléatoire comme avant :
        return parcellesIrriguees.get(random.nextInt(parcellesIrriguees.size()));
    }

    public Parcelle choisirParcellePourOrage(List<Parcelle> parcelles) {
        if (parcelles.isEmpty()) return null;

        // Stratégie : Manger sur une parcelle adverse ?
        // Ici aléatoire :
        return parcelles.get(random.nextInt(parcelles.size()));
    }
}