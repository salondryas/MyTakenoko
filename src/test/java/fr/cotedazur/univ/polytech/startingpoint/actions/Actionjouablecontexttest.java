package fr.cotedazur.univ.polytech.startingpoint.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;

import static org.junit.jupiter.api.Assertions.*;

class ActionJouableContextTest {

    private ActionJouableContext context;

    @BeforeEach
    void setUp() {
        context = new ActionJouableContext();
    }

    @Test
    void testInitialTokenCount() {
        assertEquals(2, context.getTokenCount());
    }

    @Test
    void testGetTokenCount() {
        int tokenCount = context.getTokenCount();
        assertEquals(2, tokenCount);
    }

    @Test
    void testSetTokenCount() {
        context.setTokenCount(5);
        assertEquals(5, context.getTokenCount());
    }

    @Test
    void testSetTokenCountToZero() {
        context.setTokenCount(0);
        assertEquals(0, context.getTokenCount());
    }

    @Test
    void testSetTokenCountNegative() {
        context.setTokenCount(-1);
        assertEquals(-1, context.getTokenCount());
    }

    @Test
    void testResetTokenCount() {
        context.setTokenCount(10);
        context.resetTokenCount();
        assertEquals(2, context.getTokenCount());
    }

    @Test
    void testResetTokenCountFromZero() {
        context.setTokenCount(0);
        context.resetTokenCount();
        assertEquals(2, context.getTokenCount());
    }

    @Test
    void testConsumeOneTokenWithActionJeton() {
        Action action = new TestAction(TypeAction.DEPLACER_PANDA);
        context.consumeOneToken(action);
        assertEquals(1, context.getTokenCount());
    }

    @Test
    void testConsumeOneTokenMultipleTimes() {
        Action action = new TestAction(TypeAction.DEPLACER_JARDINIER);
        context.consumeOneToken(action);
        context.consumeOneToken(action);
        assertEquals(0, context.getTokenCount());
    }

    @Test
    void testConsumeOneTokenWithNonActionJeton() {
        Action action = new TestAction(TypeAction.POSER_IRRIGATION);
        int initialCount = context.getTokenCount();
        context.consumeOneToken(action);
        assertEquals(initialCount, context.getTokenCount());
    }

    @Test
    void testConsumeTokenCanGoNegative() {
        Action action = new TestAction(TypeAction.POSER_PARCELLE);
        context.consumeOneToken(action);
        context.consumeOneToken(action);
        context.consumeOneToken(action);
        assertEquals(-1, context.getTokenCount());
    }

    @Test
    void testMixedActionConsumption() {
        Action actionJeton = new TestAction(TypeAction.DEPLACER_PANDA);
        Action actionNonJeton = new TestAction(TypeAction.POSER_IRRIGATION);

        context.consumeOneToken(actionJeton);
        assertEquals(1, context.getTokenCount());

        context.consumeOneToken(actionNonJeton);
        assertEquals(1, context.getTokenCount());

        context.consumeOneToken(actionJeton);
        assertEquals(0, context.getTokenCount());
    }

    @Test
    void testTypeActionDeplacerPandaIsActionJeton() {
        assertTrue(TypeAction.DEPLACER_PANDA.getIsActionJeton());
    }

    @Test
    void testTypeActionDeplacerJardinierIsActionJeton() {
        assertTrue(TypeAction.DEPLACER_JARDINIER.getIsActionJeton());
    }

    @Test
    void testTypeActionPoserParcelleIsActionJeton() {
        assertTrue(TypeAction.POSER_PARCELLE.getIsActionJeton());
    }

    @Test
    void testTypeActionPiocherObjectifIsActionJeton() {
        assertTrue(TypeAction.PIOCHER_OBJECTIF.getIsActionJeton());
    }

    @Test
    void testTypeActionPrendreIrrigationIsActionJeton() {
        assertTrue(TypeAction.PRENDRE_IRRIGATION.getIsActionJeton());
    }

    @Test
    void testTypeActionPoserIrrigationIsNotActionJeton() {
        assertFalse(TypeAction.POSER_IRRIGATION.getIsActionJeton());
    }

    @Test
    void testAllTypeActionsExist() {
        TypeAction[] types = TypeAction.values();
        assertEquals(6, types.length);
    }

    @Test
    void testTypeActionEnumValues() {
        TypeAction[] types = TypeAction.values();
        assertTrue(containsType(types, TypeAction.DEPLACER_PANDA));
        assertTrue(containsType(types, TypeAction.DEPLACER_JARDINIER));
        assertTrue(containsType(types, TypeAction.POSER_PARCELLE));
        assertTrue(containsType(types, TypeAction.PIOCHER_OBJECTIF));
        assertTrue(containsType(types, TypeAction.PRENDRE_IRRIGATION));
        assertTrue(containsType(types, TypeAction.POSER_IRRIGATION));
    }

    @Test
    void testOnlyPoserIrrigationIsNotActionJeton() {
        for (TypeAction type : TypeAction.values()) {
            if (type == TypeAction.POSER_IRRIGATION) {
                assertFalse(type.getIsActionJeton());
            } else {
                assertTrue(type.getIsActionJeton());
            }
        }
    }

    @Test
    void testConsumeWithAllActionTypes() {
        for (TypeAction type : TypeAction.values()) {
            ActionJouableContext ctx = new ActionJouableContext();
            Action action = new TestAction(type);
            ctx.consumeOneToken(action);

            if (type.getIsActionJeton()) {
                assertEquals(1, ctx.getTokenCount());
            } else {
                assertEquals(2, ctx.getTokenCount());
            }
        }
    }

    @Test
    void testSetAndResetCycle() {
        context.setTokenCount(7);
        assertEquals(7, context.getTokenCount());

        context.resetTokenCount();
        assertEquals(2, context.getTokenCount());

        context.setTokenCount(3);
        assertEquals(3, context.getTokenCount());

        context.resetTokenCount();
        assertEquals(2, context.getTokenCount());
    }

    @Test
    void testConsumeAfterReset() {
        context.setTokenCount(5);
        context.resetTokenCount();

        Action action = new TestAction(TypeAction.PIOCHER_OBJECTIF);
        context.consumeOneToken(action);

        assertEquals(1, context.getTokenCount());
    }

    private boolean containsType(TypeAction[] types, TypeAction target) {
        for (TypeAction type : types) {
            if (type == target) {
                return true;
            }
        }
        return false;
    }

    private static class TestAction implements Action {
        private final TypeAction type;

        public TestAction(TypeAction type) {
            this.type = type;
        }

        @Override
        public void appliquer(GameState gameState, Bot joueur) {
        }

        @Override
        public TypeAction getType() {
            return type;
        }

        @Override
        public String toString() {
            return type.toString();
        }
    }
}