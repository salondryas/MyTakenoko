package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifJardinier;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.PiocheObjectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.*;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BotJardinierTest {

    BotJardinier bot;

    @Mock GameState gameStateMock;
    @Mock Plateau plateauMock;
    @Mock Jardinier jardinierMock;
    @Mock Panda pandaMock;
    @Mock PiocheParcelle piocheParcelleMock;
    @Mock PiocheObjectif piocheObjectifJardinierMock;
    @Mock PiocheObjectif piocheObjectifPandaMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bot = new BotJardinier("JardinierTest");

        // Configuration de base du Mock GameState
        when(gameStateMock.getPlateau()).thenReturn(plateauMock);
        when(gameStateMock.getJardinier()).thenReturn(jardinierMock);
        when(gameStateMock.getPanda()).thenReturn(pandaMock);
        when(gameStateMock.getPiocheParcelle()).thenReturn(piocheParcelleMock);
        when(gameStateMock.getPiocheJardinier()).thenReturn(piocheObjectifJardinierMock);
        when(gameStateMock.getPiochePanda()).thenReturn(piocheObjectifPandaMock);

        // Par défaut, les pioches ne sont pas vides
        // (Assurez-vous que votre classe PiocheObjectif a bien la méthode estVide() ou getTaille())
        when(piocheObjectifJardinierMock.getTaille()).thenReturn(10);
        // Si vous utilisez estVide() dans le bot, ajoutez : when(piocheObjectifJardinierMock.estVide()).thenReturn(false);
        when(piocheParcelleMock.estVide()).thenReturn(false);
    }

    @Test
    void testPriorite1_InventaireVide_PiocheObjectif() {
        // Cas : Le bot n'a pas d'objectifs
        // Simulation : Pioche jardinier non vide
        when(piocheObjectifJardinierMock.getTaille()).thenReturn(5); // Exemple de taille > 0

        List<Action> actions = bot.jouer(gameStateMock);

        assertFalse(actions.isEmpty());
        // Le bot doit choisir de piocher un objectif
        assertTrue(actions.get(0) instanceof PiocherObjectif);
    }

    @Test
    void testPriorite2_AObjectifEtCanal_PoseIrrigation() {
        // Cas : A un objectif et un canal en stock
        // CORRECTION : Utilisation du constructeur (Couleur, Taille, Points)
        bot.getInventaire().ajouterObjectif(new ObjectifJardinier(Couleur.VERT, 4, 5));
        bot.getInventaire().ajouterIrrigation();

        // On vérifie simplement qu'il ne plante pas et renvoie une action
        assertDoesNotThrow(() -> bot.jouer(gameStateMock));
    }

    @Test
    void testPriorite3_PoseParcelle_SiRienDAutre() {
        // Cas : A un objectif mais ne peut pas l'avancer
        bot.getInventaire().ajouterObjectif(new ObjectifJardinier(Couleur.VERT, 4, 5));

        // Configuration pour qu'il puisse poser une parcelle
        when(plateauMock.getEmplacementsDisponibles()).thenReturn(List.of(new Position(1, 0)));
        when(piocheParcelleMock.piocher()).thenReturn(new Parcelle(Couleur.VERT));

        List<Action> actions = bot.jouer(gameStateMock);

        boolean aPoseParcelle = actions.stream().anyMatch(a -> a instanceof PoserParcelle);
        assertTrue(aPoseParcelle, "Le bot devrait poser une parcelle s'il ne peut rien faire d'autre");
    }
}