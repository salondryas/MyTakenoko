package fr.cotedazur.univ.polytech.startingpoint.weather;

import java.util.Random;

/**
 * Classe pour le de meteo à 6 faces (associees aux 6 conditions meteos)
 */
public class DeMeteo {
    private static final Random RANDOM = new Random();

    /**
     * ON lance le dé
     * 
     * @return Une des 6 météos possibles
     */
    public Meteo roll() {
        Meteo[] values = Meteo.values();
        return values[RANDOM.nextInt(values.length)];
    }

    /**
     * Lance le dé avec une seed pour des tests reproductibles.
     * 
     * @param seed La seed pour le générateur aléatoire
     * @return Une météo déterminée par la seed
     */
    public Meteo rollWithSeed(long seed) {
        Random seededRandom = new Random(seed);
        Meteo[] values = Meteo.values();
        return values[seededRandom.nextInt(values.length)];
    }
}
