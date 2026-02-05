package fr.cotedazur.univ.polytech.startingpoint.weather;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.TypeAction;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.AmenagmentAttribuable;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.QuantityException;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class MeteoTest {

    private GameState gameState;
    private TestBot bot;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        bot = new TestBot("TestBot");
    }

    @Test
    void testMeteoEnumHasSixValues() {
        Meteo[] values = Meteo.values();
        assertEquals(6, values.length);
    }

    @Test
    void testSoleilAddsExtraAction() {
        assertEquals(2, bot.getActionJouableContext().getTokenCount());

        Meteo.SOLEIL.apply(gameState, bot, 2);

        assertEquals(3, bot.getActionJouableContext().getTokenCount());
    }

    @Test
    void testPluieMakesBambooGrowOnIrrigatedParcelle() {
        Parcelle parcelle = new Parcelle(new Position(0, 1, -1), Couleur.VERT);
        parcelle.triggerIrrigation();
        gameState.getPlateau().placerParcelle(parcelle, new Position(0, 1, -1));

        bot.setParcelleChoisie(parcelle);

        int initialSections = parcelle.getNbSectionsSurParcelle();
        Meteo.PLUIE.apply(gameState, bot, 2);

        assertEquals(initialSections + 1, parcelle.getNbSectionsSurParcelle());
    }

    @Test
    void testPluieRespectsMaximumFiveSections() {
        Parcelle parcelle = new Parcelle(new Position(0, 1, -1), Couleur.VERT);
        parcelle.triggerIrrigation();
        for (int i = 0; i < 5; i++) {
            parcelle.pousserBambou();
        }
        gameState.getPlateau().placerParcelle(parcelle, new Position(0, 1, -1));

        bot.setParcelleChoisie(parcelle);

        assertEquals(5, parcelle.getNbSectionsSurParcelle());
        Meteo.PLUIE.apply(gameState, bot, 2);
        assertEquals(5, parcelle.getNbSectionsSurParcelle());
    }

    @Test
    void testPluieDoesNothingIfNoIrrigatedParcelles() {
        Meteo.PLUIE.apply(gameState, bot, 2);
        assertTrue(true);
    }

    @Test
    void testVentActivatesFlag() {
        assertFalse(gameState.isVentActif());

        Meteo.VENT.apply(gameState, bot, 2);

        assertTrue(gameState.isVentActif());
    }

    @Test
    void testOrageMovesPandaAndEatsBamboo() {
        Parcelle parcelle = new Parcelle(new Position(0, 1, -1), Couleur.JAUNE);
        parcelle.triggerIrrigation();
        gameState.getPlateau().placerParcelle(parcelle, new Position(0, 1, -1));
        parcelle.pousserBambou();
        parcelle.pousserBambou();

        bot.setDestinationPanda(parcelle);

        assertEquals(3, parcelle.getNbSectionsSurParcelle());
        assertEquals(0, bot.getInventaire().getBambous().get(Couleur.JAUNE));

        Meteo.ORAGE.apply(gameState, bot, 2);

        assertEquals(2, parcelle.getNbSectionsSurParcelle());
        assertEquals(1, bot.getInventaire().getBambous().get(Couleur.JAUNE));
        assertEquals(parcelle.getPosition(), gameState.getPanda().getPositionPanda());
    }

    @Test
    void testOrageDoesNotEatIfNoBamboo() {
        Parcelle parcelle = new Parcelle(new Position(0, 1, -1), Couleur.ROSE);
        gameState.getPlateau().placerParcelle(parcelle, new Position(0, 1, -1));

        bot.setDestinationPanda(parcelle);

        assertEquals(1, parcelle.getNbSectionsSurParcelle());

        Meteo.ORAGE.apply(gameState, bot, 2);

        assertEquals(1, parcelle.getNbSectionsSurParcelle());
        assertEquals(0, bot.getInventaire().getBambous().get(Couleur.ROSE));
    }

    @Test
    void testNuagesGivesAmenagementWhenAvailable() {
        assertTrue(AmenagmentAttribuable.BASSIN.getQuantite() > 0);

        Meteo.NUAGES.apply(gameState, bot, 2);

        assertTrue(bot.hasReceivedAmenagement());
        assertEquals(1, bot.getInventaire().getNombreAmenagements());
    }

    @Test
    void testNuagesAllowsAlternativeMeteoWhenNoAmenagements() {
        for (AmenagmentAttribuable amenagement : AmenagmentAttribuable.values()) {
            while (amenagement.getQuantite() > 0) {
                try {
                    amenagement.pickArrangement();
                } catch (QuantityException e) {
                    break;
                }
            }
        }

        bot.setMeteoAlternative(Meteo.SOLEIL);

        Meteo.NUAGES.apply(gameState, bot, 2);

        assertEquals(3, bot.getActionJouableContext().getTokenCount());
    }

    @Test
    void testChoixLibreAllowsAnyMeteo() {
        bot.setMeteoChoisie(Meteo.SOLEIL);

        Meteo.CHOIX_LIBRE.apply(gameState, bot, 2);

        assertEquals(3, bot.getActionJouableContext().getTokenCount());
    }

    @Test
    void testChoixLibreCannotChooseItself() {
        bot.setMeteoChoisie(Meteo.CHOIX_LIBRE);

        int initialTokens = bot.getActionJouableContext().getTokenCount();
        Meteo.CHOIX_LIBRE.apply(gameState, bot, 2);

        assertEquals(initialTokens, bot.getActionJouableContext().getTokenCount());
    }

    @Test
    void testDeMeteoReturnsValidMeteo() {
        DeMeteo deMeteo = new DeMeteo();

        for (int i = 0; i < 100; i++) {
            Meteo meteo = deMeteo.roll();
            assertNotNull(meteo);
        }
    }

    @Test
    void testDeMeteoWithSeed() {
        DeMeteo deMeteo = new DeMeteo();

        Meteo meteo1 = deMeteo.rollWithSeed(12345L);
        Meteo meteo2 = deMeteo.rollWithSeed(12345L);

        assertEquals(meteo1, meteo2);
    }

    @Test
    void testGameStateResetForNewTurn() {
        gameState.setVentActif(true);

        gameState.resetForNewTurn();

        assertFalse(gameState.isVentActif());
    }

    @Test
    void testMeteoNotAppliedOnTour1() {
        int tour = 1;

        if (tour == 1) {
            assertEquals(2, bot.getActionJouableContext().getTokenCount());
        }
    }

    @Test
    void testMeteoAppliedOnTour2() {
        int tour = 2;

        if (tour > 1) {
            Meteo.SOLEIL.apply(gameState, bot, tour);
            assertEquals(3, bot.getActionJouableContext().getTokenCount());
        }
    }

    private static class TestBot extends Bot {
        private Parcelle parcelleChoisie;
        private Parcelle destinationPanda;
        private Meteo meteoChoisie;
        private Meteo meteoAlternative;
        private boolean receivedAmenagement;

        public TestBot(String nom) {
            super(nom);
        }

        @Override
        public Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {
            return null;
        }

        @Override
        public Parcelle choisirParcelleMeteo(List<Parcelle> parcellesIrriguees) {
            if (parcelleChoisie != null) {
                return parcelleChoisie;
            }
            return parcellesIrriguees.isEmpty() ? null : parcellesIrriguees.get(0);
        }

        @Override
        public Parcelle choisirDestinationPanda(List<Parcelle> parcelles) {
            if (destinationPanda != null) {
                return destinationPanda;
            }
            for (Parcelle p : parcelles) {
                if (p.getPosition() != null && p.getCouleur() != Couleur.AUCUNE) {
                    return p;
                }
            }
            return null;
        }

        @Override
        public Meteo choisirMeteo() {
            return meteoChoisie != null ? meteoChoisie : Meteo.SOLEIL;
        }

        @Override
        public Meteo choisirMeteoAlternative() {
            return meteoAlternative != null ? meteoAlternative : Meteo.SOLEIL;
        }

        public void setParcelleChoisie(Parcelle parcelle) {
            this.parcelleChoisie = parcelle;
        }

        public void setDestinationPanda(Parcelle parcelle) {
            this.destinationPanda = parcelle;
        }

        public void setMeteoChoisie(Meteo meteo) {
            this.meteoChoisie = meteo;
        }

        public void setMeteoAlternative(Meteo meteo) {
            this.meteoAlternative = meteo;
        }

        public boolean hasReceivedAmenagement() {
            return receivedAmenagement;
        }

        @Override
        public void recevoirAmenagement(AmenagmentAttribuable amenagement) {
            super.recevoirAmenagement(amenagement);
            receivedAmenagement = true;
        }
    }
}