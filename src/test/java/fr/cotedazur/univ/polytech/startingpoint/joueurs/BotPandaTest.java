package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifPanda;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.PiocheObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.*;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BotPandaTest {

    BotPanda bot;

    @Mock
    GameState gameStateMock;
    @Mock
    Plateau plateauMock;
    @Mock
    Jardinier jardinierMock;
    @Mock
    Panda pandaMock;
    @Mock
    PiocheParcelle piocheParcelleMock;
    @Mock
    PiocheObjectif piocheObjectifMock;

    @BeforeEach
    void setUp() {
        bot = new BotPanda("BotPandaTest");

        // Initialisation des Mocks
        gameStateMock = mock(GameState.class);
        plateauMock = mock(Plateau.class);
        jardinierMock = mock(Jardinier.class);
        pandaMock = mock(Panda.class);
        piocheParcelleMock = mock(PiocheParcelle.class);
        piocheObjectifMock = mock(PiocheObjectif.class);

        // Configuration Lenient de base
        lenient().when(gameStateMock.getPlateau()).thenReturn(plateauMock);
        lenient().when(gameStateMock.getJardinier()).thenReturn(jardinierMock);
        lenient().when(gameStateMock.getPanda()).thenReturn(pandaMock);
        lenient().when(gameStateMock.getPiocheParcelle()).thenReturn(piocheParcelleMock);
        lenient().when(gameStateMock.getPiochePanda()).thenReturn(piocheObjectifMock);
        lenient().when(piocheObjectifMock.getTaille()).thenReturn(10);

        // IMPORTANT : Le nouveau bot itère sur les parcelles du plateau.
        // On renvoie une map vide par défaut pour éviter les NullPointerException.
        lenient().when(plateauMock.getParcellesMap()).thenReturn(Collections.emptyMap());

        // Par défaut, pas de déplacements possibles
        lenient().when(plateauMock.getTrajetsLigneDroite(any())).thenReturn(Collections.emptyList());
        lenient().when(plateauMock.getEmplacementsDisponibles()).thenReturn(Collections.emptyList());
    }

    // --- NOUVEAU TEST : PRIORITÉ IRRIGATION ---
    @Test
    void testStrat0_PrioritePoserIrrigation() {
        // SCÉNARIO : Le bot a un objectif VERT.
        // Il a un canal en stock.
        // Il y a une parcelle VERTE sur le plateau, mais elle est SÈCHE.

        // 1. Objectif Vert
        bot.getInventaire().ajouterObjectif(new ObjectifPanda(2, List.of(Couleur.VERT, Couleur.VERT)));
        // 2. On donne un canal au bot
        bot.getInventaire().ajouterIrrigation();

        // 3. Setup du plateau avec une parcelle verte SECHE
        Position posVerte = new Position(1, 0);
        Parcelle parcelleSeche = mock(Parcelle.class);
        when(parcelleSeche.getCouleur()).thenReturn(Couleur.VERT);
        when(parcelleSeche.estIrriguee()).thenReturn(false); // Sèche !

        // On met cette parcelle dans la Map du plateau
        Map<Position, Parcelle> mapParcelles = new HashMap<>();
        mapParcelles.put(posVerte, parcelleSeche);
        when(plateauMock.getParcellesMap()).thenReturn(mapParcelles);

        // 4. On dit qu'il est possible de placer un canal ici
        when(plateauMock.peutPlacerCanal(any(), any())).thenReturn(true);
        when(plateauMock.aCanalEntre(any(), any())).thenReturn(false);

        // Action
        List<Action> actions = bot.jouer(gameStateMock);

        // Vérification
        assertFalse(actions.isEmpty());
        assertTrue(actions.get(0) instanceof PoserCanalDirrigation,
                "Si j'ai un canal et une parcelle sèche utile, je dois irriguer en priorité !");
    }

    @Test
    void testStrat1_PrioriteDeplacerPanda() {
        // SCÉNARIO : Le bot a un objectif Panda "2 VERTS".
        // Une parcelle VERTE avec du bambou est accessible.

        ObjectifPanda objVert = new ObjectifPanda(2, List.of(Couleur.VERT, Couleur.VERT));
        bot.getInventaire().ajouterObjectif(objVert);
        // Note : Bot a 0 canal par défaut, donc il ne tente pas d'irriguer

        Position posPanda = new Position(0, 0);
        Position posCible = new Position(1, 0);

        when(pandaMock.getPositionPanda()).thenReturn(posPanda);
        when(plateauMock.getTrajetsLigneDroite(posPanda)).thenReturn(List.of(posCible));

        Parcelle parcelleCible = mock(Parcelle.class);
        when(parcelleCible.getCouleur()).thenReturn(Couleur.VERT);

        // Mises à jour des mocks pour la cohérence
        when(plateauMock.getParcellesMap()).thenReturn(Map.of(posCible, parcelleCible));

        when(plateauMock.getNombreDeSectionsAPosition(posCible)).thenReturn(1);
        when(plateauMock.getParcelle(posCible)).thenReturn(parcelleCible);

        // Action
        List<Action> actions = bot.jouer(gameStateMock);

        // Vérification
        assertFalse(actions.isEmpty());
        assertTrue(actions.get(0) instanceof DeplacerPanda, "Le bot devrait prioriser de manger le bambou vert");
    }

    @Test
    void testStrat2_PlanB_DeplacerJardinier() {
        // SCÉNARIO : Pas de bambou à manger, mais on peut faire pousser du VERT.

        bot.getInventaire().ajouterObjectif(new ObjectifPanda(2, List.of(Couleur.VERT, Couleur.VERT)));

        // Le panda est bloqué
        when(plateauMock.getTrajetsLigneDroite(any())).thenReturn(Collections.emptyList());

        // Setup du Jardinier
        Position posJardinier = new Position(2, 2);
        Position posCible = new Position(2, 3);

        when(jardinierMock.getPosition()).thenReturn(posJardinier);
        when(plateauMock.getTrajetsLigneDroite(posJardinier)).thenReturn(List.of(posCible));

        Parcelle parcelleJardinier = mock(Parcelle.class);
        when(parcelleJardinier.getCouleur()).thenReturn(Couleur.VERT);
        when(parcelleJardinier.estIrriguee()).thenReturn(true);
        when(parcelleJardinier.getNbSectionsSurParcelle()).thenReturn(1);

        // Mise à jour de la Map (important pour le scan du bot)
        when(plateauMock.getParcellesMap()).thenReturn(Map.of(posCible, parcelleJardinier));
        when(plateauMock.getParcelle(posCible)).thenReturn(parcelleJardinier);

        // Action
        List<Action> actions = bot.jouer(gameStateMock);

        // Vérification
        assertFalse(actions.isEmpty());
        assertTrue(actions.get(0) instanceof DeplacerJardinier, "Le bot devrait déplacer le jardinier pour faire pousser");
    }
}