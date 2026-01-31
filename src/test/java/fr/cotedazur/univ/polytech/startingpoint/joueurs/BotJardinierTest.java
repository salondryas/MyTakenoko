package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.CarteBambou;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifJardinier;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.PiocheObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.*;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class BotJardinierTest {

    BotJardinier bot;

    @Mock GameState gameStateMock;
    @Mock Plateau plateauMock;
    @Mock Jardinier jardinierMock;
    @Mock Panda pandaMock;
    @Mock PiocheParcelle piocheParcelleMock;
    @Mock PiocheObjectif piocheObjectifMock;

    // On mock la carte pour ne pas dépendre de l'Enum réel
    @Mock CarteBambou carteBambouMock;

    @BeforeEach
    void setUp() {
        // 1. Initialisation du Bot
        bot = new BotJardinier("BotJardinierTest");

        // 2. CRUCIAL : Initialisation manuelle des Mocks (C'est ce qui manquait !)
        gameStateMock = mock(GameState.class);
        plateauMock = mock(Plateau.class);
        jardinierMock = mock(Jardinier.class);
        pandaMock = mock(Panda.class);
        piocheParcelleMock = mock(PiocheParcelle.class);
        piocheObjectifMock = mock(PiocheObjectif.class);
        carteBambouMock = mock(fr.cotedazur.univ.polytech.startingpoint.objectifs.CarteBambou.class); // Si tu l'utilises

        // 3. Configuration du comportement des Mocks (les "when")
        // Lenient permet d'éviter les erreurs si un mock n'est pas utilisé dans un test précis
        lenient().when(gameStateMock.getPlateau()).thenReturn(plateauMock);
        lenient().when(gameStateMock.getJardinier()).thenReturn(jardinierMock);
        lenient().when(gameStateMock.getPanda()).thenReturn(pandaMock);
        lenient().when(gameStateMock.getPioche()).thenReturn(piocheParcelleMock);

        // Configuration de la carte factice par défaut (si utilisée)
        lenient().when(carteBambouMock.getCouleur()).thenReturn(fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur.VERT);
        lenient().when(carteBambouMock.getTaille()).thenReturn(4);
        lenient().when(carteBambouMock.getPoints()).thenReturn(5);

        // Configuration par défaut des listes pour éviter d'autres NullPointer
        lenient().when(plateauMock.getEmplacementsDisponibles()).thenReturn(java.util.Collections.emptyList());
        lenient().when(plateauMock.getTrajetsLigneDroite(any())).thenReturn(java.util.Collections.emptyList());
    }

    @Test
    void testStrat1_PrioriteDeplacerJardinier() {
        // SCÉNARIO : Le bot a un objectif VERT.
        // ATTENDU : Il doit renvoyer une action DeplacerJardinier.

        // 1. On donne un objectif au bot (Nouveau Constructeur avec CarteBambou)
        ObjectifJardinier objVert = new ObjectifJardinier(carteBambouMock);
        bot.getInventaire().ajouterObjectif(objVert);

        // 2. On prépare le terrain
        Position posJardinier = new Position(0,0);
        Position posCible = new Position(1,0);

        when(jardinierMock.getPosition()).thenReturn(posJardinier);
        when(plateauMock.getTrajetsLigneDroite(posJardinier)).thenReturn(List.of(posCible));

        // La parcelle cible est parfaite pour l'objectif
        Parcelle parcelleCible = mock(Parcelle.class);
        when(parcelleCible.getCouleur()).thenReturn(Couleur.VERT);
        when(parcelleCible.estIrriguee()).thenReturn(true);
        when(parcelleCible.getNbSectionsSurParcelle()).thenReturn(1);
        when(plateauMock.getParcelle(posCible)).thenReturn(parcelleCible);

        // 3. Action : Le bot réfléchit
        List<Action> actions = bot.jouer(gameStateMock);

        // 4. Vérification
        // IMPORTANT : Le bot n'exécute plus l'action, il la retourne !
        // On vérifie donc le contenu de la liste retournée.
        assertFalse(actions.isEmpty(), "Le bot aurait dû proposer une action");
        assertTrue(actions.get(0) instanceof DeplacerJardinier, "L'action devrait être un déplacement de jardinier");

        // (Optionnel) Vérifier que c'est la bonne destination en castant
        // DeplacerJardinier action = (DeplacerJardinier) actions.get(0);
        // ... vérification plus poussée si besoin
    }

    @Test
    void testStrat2_PlanB_PoserParcelle() {
        // SCÉNARIO : Objectif VERT, mais Jardinier bloqué.
        // ATTENDU : Il doit proposer PoserParcelle.

        // 1. Objectif Vert
        bot.getInventaire().ajouterObjectif(new ObjectifJardinier(carteBambouMock));

        // 2. Pas de trajet utile (Plan A échoue)
        when(plateauMock.getTrajetsLigneDroite(any())).thenReturn(Collections.emptyList());

        // 3. La pioche contient une parcelle VERTE
        Parcelle parcellePiochee = new Parcelle(Couleur.VERT);
        when(piocheParcelleMock.getSize()).thenReturn(10);
        when(piocheParcelleMock.piocherParcelle()).thenReturn(parcellePiochee);

        Position posDispo = new Position(1, 0);
        when(plateauMock.getEmplacementsDisponibles()).thenReturn(List.of(posDispo));

        // 4. Action
        List<Action> actions = bot.jouer(gameStateMock);

        // 5. Vérification
        assertFalse(actions.isEmpty());
        assertTrue(actions.get(0) instanceof PoserParcelle, "Le bot devrait poser une parcelle en Plan B");
    }

    @Test
    void testStrat3_RienEnMain_PiocherObjectif() {
        // SCÉNARIO : Inventaire vide.
        // ATTENDU : Proposer PiocherObjectif.

        // 1. Inventaire vide par défaut

        // 2. Mock de la pioche
        // Note: Ici, le bot vérifie juste s'il a le droit de piocher.
        // Il ne pioche pas vraiment "pendant la réflexion", il retourne l'action "Je veux piocher".
        // Donc on n'a même pas besoin de mocker le retour de piocher() dans ce test précis,
        // car c'est la classe Partie qui exécutera l'action plus tard.

        // 3. Action
        List<Action> actions = bot.jouer(gameStateMock);

        // 4. Vérification
        assertFalse(actions.isEmpty());
        assertTrue(actions.get(0) instanceof PiocherObjectif, "Le bot devrait vouloir piocher un objectif");
    }
}