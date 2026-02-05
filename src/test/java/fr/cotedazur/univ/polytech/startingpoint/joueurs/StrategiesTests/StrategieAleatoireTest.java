package fr.cotedazur.univ.polytech.startingpoint.joueurs.StrategiesTests;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.elements.movables.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.elements.movables.Panda;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Strategies.StrategieAleatoire;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif; // Attention à l'import ici (pas PiocheObjectif du package objectifs)
import fr.cotedazur.univ.polytech.startingpoint.objectifs.PiocheObjectif; // Pour le mock des pioches
import fr.cotedazur.univ.polytech.startingpoint.elements.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class StrategieAleatoireTest {

    StrategieAleatoire strategie;

    @Mock GameState gameState;
    @Mock
    Plateau plateau;

    // Mocks pour les pioches
    @Mock PiocheObjectif piochePanda;
    @Mock PiocheObjectif piocheJardinier;
    @Mock PiocheObjectif piocheObjParcelle;
    @Mock PiocheParcelle piocheParcelle;

    // Mocks pour les personnages
    @Mock
    Panda panda;
    @Mock
    Jardinier jardinier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        strategie = new StrategieAleatoire();

        // Liaison des mocks au GameState
        when(gameState.getPlateau()).thenReturn(plateau);
        when(gameState.getPiochePanda()).thenReturn(piochePanda);
        when(gameState.getPiocheJardinier()).thenReturn(piocheJardinier);
        when(gameState.getPiocheObjectifParcelle()).thenReturn(piocheObjParcelle);
        when(gameState.getPiocheParcelle()).thenReturn(piocheParcelle);
        when(gameState.getPanda()).thenReturn(panda);
        when(gameState.getJardinier()).thenReturn(jardinier);

        // Par défaut, les pioches ne sont pas vides
        when(piochePanda.getTaille()).thenReturn(5);
        when(piocheJardinier.getTaille()).thenReturn(5);
        when(piocheObjParcelle.getTaille()).thenReturn(5);
        when(piocheParcelle.estVide()).thenReturn(false);
    }

    @Test
    void testAucuneActionPossible() {
        // Si on interdit tout
        Set<TypeAction> interdits = Set.of(
                TypeAction.PIOCHER_OBJECTIF,
                TypeAction.POSER_PARCELLE,
                TypeAction.DEPLACER_PANDA,
                TypeAction.DEPLACER_JARDINIER
        );

        Action action = strategie.getActionAleatoire(gameState, interdits);

        assertNull(action, "Si tout est interdit, l'action doit être null");
    }

    @Test
    void testForcePoserParcelle() {
        // On interdit tout SAUF Poser Parcelle
        Set<TypeAction> interdits = Set.of(
                TypeAction.PIOCHER_OBJECTIF,
                TypeAction.DEPLACER_PANDA,
                TypeAction.DEPLACER_JARDINIER
        );

        Action action = strategie.getActionAleatoire(gameState, interdits);

        assertInstanceOf(PoserParcelle.class, action, "La seule action possible devrait être PoserParcelle");
    }

    @Test
    void testPoserParcelleImpossibleSiPiocheVide() {
        // On autorise Poser Parcelle, mais la pioche est vide
        when(piocheParcelle.estVide()).thenReturn(true);

        Set<TypeAction> interdits = Set.of(
                TypeAction.PIOCHER_OBJECTIF,
                TypeAction.DEPLACER_PANDA,
                TypeAction.DEPLACER_JARDINIER
        );

        Action action = strategie.getActionAleatoire(gameState, interdits);

        assertNull(action, "Si la pioche parcelle est vide, on ne peut pas poser de parcelle.");
    }

    @Test
    void testForceDeplacerPanda() {
        // Setup : Le panda peut bouger vers (1,0)
        Position posActuelle = new Position(0,0);
        Position posCible = new Position(1,0);
        when(panda.getPositionPanda()).thenReturn(posActuelle);

        // On doit renvoyer une liste mutable car ta méthode fait un .remove() dessus
        List<Position> trajets = new ArrayList<>(List.of(posActuelle, posCible));
        when(plateau.getTrajetsLigneDroite(posActuelle)).thenReturn(trajets);

        // On interdit tout sauf Panda
        Set<TypeAction> interdits = Set.of(
                TypeAction.PIOCHER_OBJECTIF,
                TypeAction.POSER_PARCELLE,
                TypeAction.DEPLACER_JARDINIER
        );

        Action action = strategie.getActionAleatoire(gameState, interdits);

        assertInstanceOf(DeplacerPanda.class, action);
        // Note: Assurez-vous d'avoir ajouté getDestination() dans DeplacerPanda
        assertEquals(posCible, ((DeplacerPanda) action).getDestination(), "Le panda doit aller sur la seule case disponible");
    }

    @Test
    void testForceDeplacerJardinier() {
        // Setup Jardinier
        Position posActuelle = new Position(0,0);
        Position posCible = new Position(0,1);
        when(jardinier.getPosition()).thenReturn(posActuelle);

        List<Position> trajets = new ArrayList<>(List.of(posActuelle, posCible));
        when(plateau.getTrajetsLigneDroite(posActuelle)).thenReturn(trajets);

        // On interdit tout sauf Jardinier
        Set<TypeAction> interdits = Set.of(
                TypeAction.PIOCHER_OBJECTIF,
                TypeAction.POSER_PARCELLE,
                TypeAction.DEPLACER_PANDA
        );

        Action action = strategie.getActionAleatoire(gameState, interdits);

        assertInstanceOf(DeplacerJardinier.class, action);
        // Note: Assurez-vous d'avoir ajouté getDestination() dans DeplacerJardinier
        assertEquals(posCible, ((DeplacerJardinier) action).getDestination());
    }

    @Test
    void testForcePiocherObjectif() {
        Set<TypeAction> interdits = Set.of(
                TypeAction.POSER_PARCELLE,
                TypeAction.DEPLACER_PANDA,
                TypeAction.DEPLACER_JARDINIER
        );

        Action action = strategie.getActionAleatoire(gameState, interdits);

        assertInstanceOf(fr.cotedazur.univ.polytech.startingpoint.actions.PiocherObjectif.class, action);
    }

    @Test
    void testPiocherObjectifFallback() {
        // TEST SPECIFIQUE : Le random tombe sur Panda (0) mais la pioche Panda est vide.
        // On s'assure qu'il pioche Jardinier ou Parcelle à la place.

        when(piochePanda.getTaille()).thenReturn(0); // Vide
        when(piocheJardinier.getTaille()).thenReturn(5); // Pleine
        when(piocheObjParcelle.getTaille()).thenReturn(0); // Vide

        Set<TypeAction> interdits = Set.of(
                TypeAction.POSER_PARCELLE,
                TypeAction.DEPLACER_PANDA,
                TypeAction.DEPLACER_JARDINIER
        );

        // On boucle plusieurs fois pour vaincre l'aléatoire
        for (int i = 0; i < 20; i++) {
            Action action = strategie.getActionAleatoire(gameState, interdits);

            if (action instanceof fr.cotedazur.univ.polytech.startingpoint.actions.PiocherObjectif) {
                fr.cotedazur.univ.polytech.startingpoint.actions.PiocherObjectif pioche =
                        (fr.cotedazur.univ.polytech.startingpoint.actions.PiocherObjectif) action;

                // On vérifie le Type de Carte (JARDINIER), pas le Type d'Action
                assertEquals(TypeObjectif.JARDINIER, pioche.getTypeObjectif());
            }
        }
    }
}