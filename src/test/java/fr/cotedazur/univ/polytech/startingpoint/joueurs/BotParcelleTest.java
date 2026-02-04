package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifParcelle;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.parcelle.CarteParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
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

class BotParcelleTest {

    BotParcelle bot;
    @Mock GameState gameState;
    @Mock Plateau plateau;
    @Mock SelectionParcelle selection;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bot = new BotParcelle("Architecte");
        // Par défaut, le plateau dit que n'importe quelle position est libre si on lui demande
        when(plateau.isPositionDisponible(any())).thenReturn(true);
    }

    @Test
    void testChoisirParcelle_PrivilegieObjectif() {
        // SCENARIO : Le bot a un objectif "Ligne Verte" (Besoin de Vert).
        // Il pioche : [VERT, ROSE, JAUNE].
        // Il doit choisir VERT.

        // 1. On donne l'objectif au bot
        bot.getInventaire().ajouterObjectif(new ObjectifParcelle(CarteParcelle.LIGNE_VERTE));

        // 2. On prépare la pioche
        Parcelle pVerte = new Parcelle(Couleur.VERT);
        Parcelle pRose = new Parcelle(Couleur.ROSE);
        Parcelle pJaune = new Parcelle(Couleur.JAUNE);
        when(selection.getParcellesAChoisir()).thenReturn(List.of(pVerte, pRose, pJaune));

        // 3. On prépare le plateau (une seule place dispo pour simplifier)
        Position pos1 = new Position(1, 0);
        when(plateau.getEmplacementsDisponibles()).thenReturn(List.of(pos1));

        // ACTION
        Parcelle choix = bot.choisirParcelle(selection, plateau);

        // VÉRIFICATION
        assertEquals(Couleur.VERT, choix.getCouleur(),
                "Le bot aurait dû choisir VERT car il a un objectif LIGNE_VERTE (+20 pts)");

        // Vérifie qu'il a bien validé son choix auprès de la sélection
        verify(selection).validerChoix(pVerte);

        // VÉRIFICATION DE LA MÉMORISATION
        // Le bot doit ressortir immédiatement la position qu'il a calculée (pos1)
        assertEquals(pos1, bot.choisirPosition(choix, plateau),
                "Le bot doit se souvenir de l'endroit où il a prévu de poser la tuile");
    }

    @Test
    void testChoisirParcelle_PrivilegieAdjacence() {
        // SCENARIO : Pas d'objectif.
        // Plateau : Il y a déjà une parcelle ROSE en (1,0).
        // Pioche : [VERT, ROSE].
        // Emplacement dispo : (0,1) qui est voisin de (1,0).
        // Le bot doit choisir ROSE pour coller à la ROSE existante (Bonus regroupement).

        // 1. Pioche
        Parcelle pVerte = new Parcelle(Couleur.VERT);
        Parcelle pRose = new Parcelle(Couleur.ROSE);
        when(selection.getParcellesAChoisir()).thenReturn(List.of(pVerte, pRose));

        // 2. Plateau
        Position posVoisine = new Position(0, 1);
        when(plateau.getEmplacementsDisponibles()).thenReturn(List.of(posVoisine));

        // Simulation : Si on regarde en (1,0) (voisin de posVoisine), il y a du ROSE
        // (Cela simule l'existence d'une parcelle rose sur le plateau)
        when(plateau.getParcelle(new Position(1, 0))).thenReturn(new Parcelle(Couleur.ROSE));

        // ACTION
        Parcelle choix = bot.choisirParcelle(selection, plateau);

        // VÉRIFICATION
        assertEquals(Couleur.ROSE, choix.getCouleur(),
                "Le bot doit choisir ROSE pour gagner le bonus d'adjacence (+10 pts)");
    }
}