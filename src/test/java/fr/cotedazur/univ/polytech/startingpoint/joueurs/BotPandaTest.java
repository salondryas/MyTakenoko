package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.elements.movables.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.elements.movables.Panda;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.panda.ObjectifPanda;
import fr.cotedazur.univ.polytech.startingpoint.elements.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BotPandaTest {

    BotPanda bot;

    @Mock GameState gameState;
    @Mock
    Plateau plateau;
    @Mock
    Panda panda;
    @Mock
    Jardinier jardinier;
    @Mock InventaireJoueur inventaire;
    @Mock PiocheParcelle piocheParcelle;
    // Mocks des pioches d'objectifs
    @Mock fr.cotedazur.univ.polytech.startingpoint.objectifs.PiocheObjectif piocheObjPanda;
    @Mock fr.cotedazur.univ.polytech.startingpoint.objectifs.PiocheObjectif piocheObjJardinier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bot = new BotPanda("PandaTester");

        // Injection "manuelle" du mock inventaire (via mockito spy si besoin, ou juste configuration du bot)
        // Comme on ne peut pas injecter l'inventaire facilement sans setter, on va mocker le comportement
        // Si ton Bot crée son inventaire avec "new", on ne peut pas le mocker directement sans Reflection.
        // SUPPOSITION : Tu as accès à getInventaire(). Pour ce test, on va supposer qu'on utilise un "Spy" ou que tu peux set l'inventaire.
        // Sinon, on remplit le vrai inventaire.

        // --- Configuration du GameState ---
        when(gameState.getPlateau()).thenReturn(plateau);
        when(gameState.getPanda()).thenReturn(panda);
        when(gameState.getJardinier()).thenReturn(jardinier);
        when(gameState.getPiochePanda()).thenReturn(piocheObjPanda);
        when(gameState.getPiocheJardinier()).thenReturn(piocheObjJardinier);
        when(gameState.getPiocheParcelle()).thenReturn(piocheParcelle);
    }

    // =========================================================================
    // 0. TEST URGENCE : PAS D'OBJECTIF
    // =========================================================================
    @Test
    void test0_Urgence_PiocherSiPasDObjectif() {
        // Setup : Inventaire vide
        bot.getInventaire().getObjectifs().clear();

        when(piocheObjPanda.getTaille()).thenReturn(5);

        Action action = bot.choisirUneAction(gameState, Collections.emptySet());

        assertInstanceOf(PiocherObjectif.class, action);
        // On vérifie qu'il pioche du PANDA en priorité
        // (Note: adapte le cast selon ta classe PiocherObjectif)
        assertEquals(TypeAction.PIOCHER_OBJECTIF, action.getType());
    }

    // =========================================================================
    // 3. TEST PLAN A : MANGER (Déjà fait, mais rappel pour complétude)
    // =========================================================================
    @Test
    void test3_MangerBambou_SiDisponible() {
        // Objectif : Vert
        ObjectifPanda obj = mock(ObjectifPanda.class);
        when(obj.getCouleurs()).thenReturn(List.of(Couleur.VERT));
        when(obj.getObjMap()).thenReturn(Map.of(Couleur.VERT, 1)); // Important pour StrategieGenerale
        bot.getInventaire().ajouterObjectif(obj);

        // Plateau : Vert dispo
        Position pos = new Position(1,0);
        Parcelle p = mock(Parcelle.class);
        when(p.getCouleur()).thenReturn(Couleur.VERT);

        when(plateau.getPositionOccupees()).thenReturn(Set.of(pos));
        when(plateau.getParcelle(pos)).thenReturn(p);
        when(plateau.getNombreDeSectionsAPosition(pos)).thenReturn(1);
        when(panda.accessibleEnUnCoupParPanda(gameState, pos)).thenReturn(true);

        Action action = bot.choisirUneAction(gameState, Collections.emptySet());

        assertInstanceOf(DeplacerPanda.class, action);
    }

    // =========================================================================
    // 4. TEST PLAN B : FAIRE POUSSER (Jardinier)
    // =========================================================================
    @Test
    void test4_Jardinier_SiPasDeBambouMaisParcellePrete() {
        // SCENARIO : Je veux du ROSE. Il n'y en a pas à manger (0 section).
        // MAIS il y a une parcelle ROSE, Irriguée, Accessible au Jardinier.

        // 1. Objectif Rose
        ObjectifPanda obj = mock(ObjectifPanda.class);
        when(obj.getCouleurs()).thenReturn(List.of(Couleur.ROSE));
        when(obj.getObjMap()).thenReturn(Map.of(Couleur.ROSE, 1));
        bot.getInventaire().ajouterObjectif(obj);

        // 2. Plateau : Parcelle Rose vide
        Position pos = new Position(0,1);
        Parcelle pRose = mock(Parcelle.class);
        when(pRose.getCouleur()).thenReturn(Couleur.ROSE);
        when(pRose.estIrriguee()).thenReturn(true);
        when(pRose.getNbSectionsSurParcelle()).thenReturn(0); // Vide, donc Panda ne peut pas manger

        // Configuration pour la StrategiePanda (qui va renvoyer null car pas de bambou)
        when(plateau.getPositionOccupees()).thenReturn(Set.of(pos));
        when(plateau.getParcelle(pos)).thenReturn(pRose);
        when(plateau.getNombreDeSectionsAPosition(pos)).thenReturn(0);

        // Configuration pour le Jardinier (Plan B)
        when(jardinier.getPosition()).thenReturn(new Position(0,0));
        when(plateau.getTrajetsLigneDroite(any())).thenReturn(List.of(pos)); // Jardinier peut y aller

        Action action = bot.choisirUneAction(gameState, Collections.emptySet());

        assertInstanceOf(DeplacerJardinier.class, action, "Le bot doit envoyer le jardinier faire pousser");
        assertEquals(pos, ((DeplacerJardinier)action).getDestination());
    }

    // =========================================================================
    // 5. TEST PLAN C : POSER PARCELLE
    // =========================================================================
    @Test
    void test5_PoserParcelle_SiRienDAutreAFaire() {
        // SCENARIO : Pas de bambou, pas de parcelle irriguée à faire pousser.
        // Il faut étendre le terrain.

        // Objectif bidon
        bot.getInventaire().ajouterObjectif(mock(ObjectifPanda.class));

        // Mocks pour que les étapes précédentes échouent
        when(plateau.getPositionOccupees()).thenReturn(Collections.emptySet()); // Pas de parcelles
        when(piocheParcelle.getSize()).thenReturn(1);
        when(plateau.getEmplacementsDisponibles()).thenReturn(List.of(new Position(1,0)));

        Action action = bot.choisirUneAction(gameState, Collections.emptySet());

        assertInstanceOf(PoserParcelle.class, action);
    }

    // =========================================================================
    // 2. TEST LOGISTIQUE : POSER IRRIGATION
    // =========================================================================
    @Test
    void test2_PoserIrrigation_SiInventaireEtUtile() {
        // SCENARIO : J'ai un canal en stock. J'ai un objectif JAUNE.
        // Il y a une parcelle JAUNE sèche. Je peux l'irriguer.

        // 1. Inventaire : 1 canal + Objectif Jaune
        bot.getInventaire().ajouterIrrigation();
        ObjectifPanda obj = mock(ObjectifPanda.class);
        when(obj.getCouleurs()).thenReturn(List.of(Couleur.JAUNE));
        bot.getInventaire().ajouterObjectif(obj);

        // 2. Plateau : Parcelle Jaune Sèche en (0,1)
        Position posJaune = new Position(0,1);
        Parcelle pJaune = mock(Parcelle.class);
        when(pJaune.getCouleur()).thenReturn(Couleur.JAUNE);
        when(pJaune.estIrriguee()).thenReturn(false); // Sèche !

        // Map des parcelles (pour l'itération)
        when(plateau.getParcellesMap()).thenReturn(Map.of(posJaune, pJaune));

        // 3. Logique de placement de canal
        // On simule qu'on peut placer un canal entre (0,1) et (1,1) par exemple
        Position voisin = posJaune.add(PositionsRelatives.SIX.getPosition());
        when(plateau.peutPlacerCanal(posJaune, voisin)).thenReturn(true);
        when(plateau.aCanalEntre(posJaune, voisin)).thenReturn(false);

        Action action = bot.choisirUneAction(gameState, Collections.emptySet());

        assertInstanceOf(PoserCanalDirrigation.class, action, "Le bot doit poser son canal pour irriguer la parcelle jaune");
    }

    // =========================================================================
    // 6. TEST DERNIER RECOURS : PRENDRE IRRIGATION
    // =========================================================================
    @Test
    void test6_PrendreIrrigation_SiBloqueParEau() {
        // SCENARIO : Rien à manger, rien à faire pousser (tout est sec).
        // J'ai besoin de VERT. Il y a du VERT sec.
        // Je n'ai pas de canal en stock. -> Je dois en prendre un.

        // Inventaire vide de canaux
        while(bot.getInventaire().getNombreCanauxDisponibles() > 0) bot.getInventaire().retirerIrrigation();

        ObjectifPanda obj = mock(ObjectifPanda.class);
        when(obj.getCouleurs()).thenReturn(List.of(Couleur.VERT));
        bot.getInventaire().ajouterObjectif(obj);

        // Plateau : Vert Sec
        Position pos = new Position(1,0);
        Parcelle pVert = mock(Parcelle.class);
        when(pVert.getCouleur()).thenReturn(Couleur.VERT);
        when(pVert.estIrriguee()).thenReturn(false);

        when(plateau.getParcellesMap()).thenReturn(Map.of(pos, pVert));
        // On s'assure que les autres plans échouent (pas de pioche parcelle dispo par exemple)
        when(piocheParcelle.getSize()).thenReturn(0);
        when(plateau.getPositionOccupees()).thenReturn(Collections.emptySet()); // Panda mange rien

        Action action = bot.choisirUneAction(gameState, Collections.emptySet());

        assertInstanceOf(ObtenirCanalDirrigation.class, action);
    }
}