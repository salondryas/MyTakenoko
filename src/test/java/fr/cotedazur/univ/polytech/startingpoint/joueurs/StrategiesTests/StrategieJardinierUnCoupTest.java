package fr.cotedazur.univ.polytech.startingpoint.joueurs.StrategiesTests;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.DeplacerJardinier;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.AmenagmentAttribuable;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Strategies.StrategieJardinierUnCoup;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.jardinier.ObjectifJardinier;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.elements.movables.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StrategieJardinierUnCoupTest {

    StrategieJardinierUnCoup strategie;

    @Mock GameState gameState;
    @Mock Bot bot;
    @Mock Plateau plateau;
    @Mock Jardinier jardinier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        strategie = new StrategieJardinierUnCoup();

        when(gameState.getPlateau()).thenReturn(plateau);
        when(gameState.getJardinier()).thenReturn(jardinier);
    }

    @Test
    void testIgnorerMauvaisTypeObjectif() {
        // On mock un objectif générique qui se fait passer pour un PANDA
        Objectif objPanda = mock(Objectif.class);
        when(objPanda.getType()).thenReturn(TypeObjectif.PANDA);

        Action action = strategie.getStrategieJardinierUnCoup(gameState, bot, objPanda);

        assertNull(action, "La stratégie doit renvoyer null si l'objectif n'est pas JARDINIER");
    }

    @Test
    void testCoupGagnantDirect() {
        // SCENARIO : Objectif VERT, Taille 3.
        // Constructeur : (Couleur, Taille, Points, Arrangement, NombreRequis)
        // On veut 1 bambou validé à la fin, et on en a 0 actuellement validés -> donc nombreRequis = 1
        ObjectifJardinier obj = new ObjectifJardinier(Couleur.VERT, 3, 10, AmenagmentAttribuable.AUCUN, 1);

        // Parcelle cible : Verte, Taille 2 (donc il manque 1 section pour arriver à 3), Irriguée
        Position posCible = new Position(1, 0);
        Parcelle parcelleCible = mock(Parcelle.class);
        when(parcelleCible.getCouleur()).thenReturn(Couleur.VERT);
        when(parcelleCible.getNbSectionsSurParcelle()).thenReturn(2);
        when(parcelleCible.estIrriguee()).thenReturn(true);
        when(parcelleCible.arrangementValide(any())).thenReturn(true);

        // Configuration Plateau
        when(plateau.getPositionOccupees()).thenReturn(Set.of(posCible));
        when(plateau.getParcelle(posCible)).thenReturn(parcelleCible);

        // Accessibilité directe
        when(jardinier.accessibleEnUnCoupParJardinier(gameState, posCible)).thenReturn(true);

        // TEST
        Action action = strategie.getStrategieJardinierUnCoup(gameState, bot, obj);

        assertInstanceOf(DeplacerJardinier.class, action);
        assertEquals(posCible, ((DeplacerJardinier) action).getDestination());
    }

    @Test
    void testEchecSiPasIrriguee() {
        // Constructeur : (Couleur, Taille, Points, Arrangement, NombreRequis)
        ObjectifJardinier obj = new ObjectifJardinier(Couleur.VERT, 3, 10, AmenagmentAttribuable.AUCUN, 1);

        Position posCible = new Position(1, 0);
        Parcelle parcelleCible = mock(Parcelle.class);
        when(parcelleCible.getCouleur()).thenReturn(Couleur.VERT);
        when(parcelleCible.getNbSectionsSurParcelle()).thenReturn(2);
        when(parcelleCible.estIrriguee()).thenReturn(false); // <--- NON IRRIGUÉE
        when(parcelleCible.arrangementValide(any())).thenReturn(true);

        when(plateau.getPositionOccupees()).thenReturn(Set.of(posCible));
        when(plateau.getParcelle(posCible)).thenReturn(parcelleCible);
        when(jardinier.accessibleEnUnCoupParJardinier(gameState, posCible)).thenReturn(true);

        Action action = strategie.getStrategieJardinierUnCoup(gameState, bot, obj);

        assertNull(action, "On ne peut pas faire pousser sur une parcelle non irriguée.");
    }

    @Test
    void testEchecSiManqueTropDeSections() {
        // Constructeur : (Couleur, Taille, Points, Arrangement, NombreRequis)
        ObjectifJardinier obj = new ObjectifJardinier(Couleur.VERT, 3, 10, AmenagmentAttribuable.AUCUN, 1);

        Position posCible = new Position(1, 0);
        Parcelle parcelleCible = mock(Parcelle.class);
        when(parcelleCible.getCouleur()).thenReturn(Couleur.VERT);
        when(parcelleCible.getNbSectionsSurParcelle()).thenReturn(1); // <--- TAILLE 1 (Il en manque 2 pour arriver à 3)
        when(parcelleCible.estIrriguee()).thenReturn(true);
        when(parcelleCible.arrangementValide(any())).thenReturn(true);

        when(plateau.getPositionOccupees()).thenReturn(Set.of(posCible));
        when(plateau.getParcelle(posCible)).thenReturn(parcelleCible);

        Action action = strategie.getStrategieJardinierUnCoup(gameState, bot, obj);

        assertNull(action, "La stratégie 'Un Coup' ne doit réagir que s'il manque exactement 1 section.");
    }

    @Test
    void testCoupIndirectViaVoisin() {
        // Constructeur : (Couleur, Taille, Points, Arrangement, NombreRequis)
        ObjectifJardinier obj = new ObjectifJardinier(Couleur.ROSE, 3, 10, AmenagmentAttribuable.AUCUN, 1);

        // Parcelle Cible (Rose, Taille 2) en (0,1)
        Position posCible = new Position(0, 1);
        Parcelle parcelleCible = mock(Parcelle.class);
        when(parcelleCible.getCouleur()).thenReturn(Couleur.ROSE);
        when(parcelleCible.getNbSectionsSurParcelle()).thenReturn(2);
        when(parcelleCible.estIrriguee()).thenReturn(true);
        when(parcelleCible.arrangementValide(any())).thenReturn(true);

        // Parcelle Voisine (Rose) en (0,0) - C'est un voisin de (0,1)
        Position posVoisin = new Position(0, 0);
        Parcelle parcelleVoisine = mock(Parcelle.class);
        when(parcelleVoisine.getCouleur()).thenReturn(Couleur.ROSE); // Même couleur !

        // Configuration Plateau : Les deux parcelles existent
        when(plateau.getPositionOccupees()).thenReturn(Set.of(posCible, posVoisin));
        when(plateau.getParcelle(posCible)).thenReturn(parcelleCible);
        when(plateau.getParcelle(posVoisin)).thenReturn(parcelleVoisine);

        // Accessibilité
        when(jardinier.accessibleEnUnCoupParJardinier(gameState, posCible)).thenReturn(false); // Cible bloquée
        when(jardinier.accessibleEnUnCoupParJardinier(gameState, posVoisin)).thenReturn(true); // Voisin accessible

        // Simulation du voisinage : (0,1) a pour voisin (0,0)
        // ATTENTION : Cela dépend de ta classe Position. Si getVoisins() n'est pas mockable,
        // assure-toi que new Position(0,1).getVoisins() contient bien (0,0).
        // Sinon, il faudrait mocker Position, mais c'est complexe pour une classe utilitaire.

        Action action = strategie.getStrategieJardinierUnCoup(gameState, bot, obj);

        assertInstanceOf(DeplacerJardinier.class, action);
        // L'action doit être d'aller sur le VOISIN, pas la cible
        assertEquals(posVoisin, ((DeplacerJardinier) action).getDestination());
    }
}