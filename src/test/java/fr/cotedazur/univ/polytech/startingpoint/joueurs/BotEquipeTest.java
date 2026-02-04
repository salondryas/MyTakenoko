package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.DeplacerPanda;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifPanda;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.PiocheObjectif; // IMPORT AJOUTÉ
import fr.cotedazur.univ.polytech.startingpoint.plateau.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Panda;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.SelectionParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BotEquipeTest {

    BotEquipe bot;
    @Mock GameState gameState;
    @Mock Plateau plateau;
    @Mock Panda panda;
    @Mock Jardinier jardinier;
    @Mock SelectionParcelle selection;
    @Mock PiocheParcelle piocheParcelle;

    // Mocks pour les pioches d'objectifs (CAUSE DU CRASH)
    @Mock PiocheObjectif piochePanda;
    @Mock PiocheObjectif piocheJardinier;
    @Mock PiocheObjectif piocheObjParcelle;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bot = new BotEquipe("DreamTeam");

        // --- Configuration du Mock GameState ---

        // 1. Les Composants principaux
        when(gameState.getPlateau()).thenReturn(plateau);
        when(gameState.getPanda()).thenReturn(panda);
        when(gameState.getJardinier()).thenReturn(jardinier);

        // 2. La Pioche Parcelle (avec son alias getPioche)
        when(gameState.getPiocheParcelle()).thenReturn(piocheParcelle);
        when(gameState.getPioche()).thenReturn(piocheParcelle);
        when(piocheParcelle.estVide()).thenReturn(false);
        when(piocheParcelle.getSize()).thenReturn(10); // Non vide

        // 3. Les Pioches Objectifs (CORRECTION ICI)
        when(gameState.getPiochePanda()).thenReturn(piochePanda);
        when(gameState.getPiocheJardinier()).thenReturn(piocheJardinier);
        when(gameState.getPiocheObjectifParcelle()).thenReturn(piocheObjParcelle);

        // Par défaut, les pioches d'objectifs ne sont pas vides
        when(piochePanda.getTaille()).thenReturn(5);
        when(piocheJardinier.getTaille()).thenReturn(5);
        when(piocheObjParcelle.getTaille()).thenReturn(5);

        // --- Comportements par défaut des personnages ---
        when(panda.getPositionPanda()).thenReturn(Plateau.POSITION_ORIGINE);
        when(jardinier.getPosition()).thenReturn(Plateau.POSITION_ORIGINE);
    }

    @Test
    void testVotePandaGagneAvecObjectifs() {
        // SCENARIO : Le bot a un objectif Panda.
        // L'expert Panda doit proposer une action et GAGNER le vote.

        bot.getInventaire().ajouterObjectif(new ObjectifPanda(2, List.of(Couleur.VERT)));

        Position posCible = new Position(1, 0);
        Parcelle parcelleVerte = new Parcelle(posCible, Couleur.VERT);
        parcelleVerte.pousserBambou();

        when(plateau.getTrajetsLigneDroite(any())).thenReturn(List.of(Plateau.POSITION_ORIGINE, posCible));
        when(plateau.getParcelle(posCible)).thenReturn(parcelleVerte);

        List<Action> actions = bot.jouer(gameState);

        assertFalse(actions.isEmpty());
        // On s'attend à ce qu'il veuille manger ou bouger le panda
        assertNotNull(actions.get(0));
    }

    @Test
    void testDelegationArchitectePourChoisirParcelle() {
        Parcelle pVert = new Parcelle(Couleur.VERT);
        Parcelle pRose = new Parcelle(Couleur.ROSE);
        when(selection.getParcellesAChoisir()).thenReturn(List.of(pVert, pRose));

        Position posVoisine = new Position(1, 0);
        when(plateau.getEmplacementsDisponibles()).thenReturn(List.of(posVoisine));

        when(plateau.getParcelle(any())).thenReturn(new Parcelle(Couleur.ROSE));

        Parcelle choix = bot.choisirParcelle(selection, plateau);

        assertNotNull(choix);
        assertEquals(Couleur.ROSE, choix.getCouleur());
    }
}