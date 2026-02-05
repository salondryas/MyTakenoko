package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
// Correct Import for ObjectifJardinier
import fr.cotedazur.univ.polytech.startingpoint.objectifs.jardinier.ObjectifJardinier;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.PiocheObjectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
// Correct Imports for Plateau classes
import fr.cotedazur.univ.polytech.startingpoint.elements.movables.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.elements.movables.Panda;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
// Correct Import for PiocheParcelle
import fr.cotedazur.univ.polytech.startingpoint.elements.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BotJardinierTest {

    BotJardinier bot;

    @Mock GameState gameState;
    @Mock Plateau plateau;
    @Mock Jardinier jardinier;
    @Mock Panda panda;
    @Mock PiocheParcelle piocheParcelle;
    @Mock PiocheObjectif piocheJardinier;
    @Mock PiocheObjectif piochePanda;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bot = new BotJardinier("JardinierTest");

        // Configuration standard
        when(gameState.getPlateau()).thenReturn(plateau);
        when(gameState.getJardinier()).thenReturn(jardinier);
        when(gameState.getPanda()).thenReturn(panda);
        when(gameState.getPiocheParcelle()).thenReturn(piocheParcelle);
        when(gameState.getPiocheJardinier()).thenReturn(piocheJardinier);
        when(gameState.getPiochePanda()).thenReturn(piochePanda);

        // Par défaut, les pioches sont pleines
        when(piocheJardinier.getTaille()).thenReturn(5);
        when(piochePanda.getTaille()).thenReturn(5);
        when(piocheParcelle.estVide()).thenReturn(false);
    }

    // =========================================================================
    // 0. URGENCE : PIOCHE VIDE
    // =========================================================================

    @Test
    void test0_Urgence_FallbackSurPandaSiPlusDeJardinier() {
        // SCENARIO : Inventaire vide. Pioche Jardinier VIDE. Pioche Panda PLEINE.
        bot.getInventaire().getObjectifs().clear();

        when(piocheJardinier.getTaille()).thenReturn(0); // Vide !
        when(piochePanda.getTaille()).thenReturn(5);     // Pleine

        Action action = bot.choisirUneAction(gameState, Collections.emptySet());

        assertInstanceOf(PiocherObjectif.class, action);
        assertEquals(TypeObjectif.PANDA, ((PiocherObjectif)action).getTypeObjectif());
    }

    // =========================================================================
    // 2. FAIRE POUSSER (Action Principale)
    // =========================================================================

    @Test
    void test2_FairePousser_SiParcelleValide() {
        // 1. Objectif (Utilisation du bon constructeur mocké ou réel)
        // Note: Adaptez les arguments du constructeur si nécessaire selon votre code
        ObjectifJardinier obj = mock(ObjectifJardinier.class);
        when(obj.getCouleurs()).thenReturn(List.of(Couleur.VERT));
        bot.getInventaire().ajouterObjectif(obj);

        // 2. Plateau : Parcelle Verte en (0,1)
        Position posVert = new Position(0,1);
        Parcelle pVert = mock(Parcelle.class);
        when(pVert.getCouleur()).thenReturn(Couleur.VERT);
        when(pVert.estIrriguee()).thenReturn(true);
        when(pVert.getNbSectionsSurParcelle()).thenReturn(1); // < 4, donc ça pousse

        when(plateau.getParcelle(posVert)).thenReturn(pVert);

        // 3. Jardinier
        when(jardinier.getPosition()).thenReturn(new Position(0,0));
        when(plateau.getTrajetsLigneDroite(any())).thenReturn(List.of(posVert)); // Accessible

        Action action = bot.choisirUneAction(gameState, Collections.emptySet());

        assertInstanceOf(DeplacerJardinier.class, action);
        assertEquals(posVert, ((DeplacerJardinier)action).getDestination());
    }

    @Test
    void test2_FairePousser_IgnoreParcellePleine() {
        ObjectifJardinier obj = mock(ObjectifJardinier.class);
        when(obj.getCouleurs()).thenReturn(List.of(Couleur.VERT));
        bot.getInventaire().ajouterObjectif(obj);

        Position posVert = new Position(0,1);
        Parcelle pVert = mock(Parcelle.class);
        when(pVert.getCouleur()).thenReturn(Couleur.VERT);
        when(pVert.estIrriguee()).thenReturn(true);
        when(pVert.getNbSectionsSurParcelle()).thenReturn(4); // <--- PLEINE !

        when(plateau.getParcelle(posVert)).thenReturn(pVert);
        when(plateau.getTrajetsLigneDroite(any())).thenReturn(List.of(posVert));

        // On bloque les autres actions pour forcer la vérification
        when(piocheParcelle.estVide()).thenReturn(true); // Pas de pose parcelle

        Action action = bot.choisirUneAction(gameState, Collections.emptySet());

        // Il ne doit pas choisir DeplacerJardinier vers cette case
        if (action instanceof DeplacerJardinier) {
            assertNotEquals(posVert, ((DeplacerJardinier)action).getDestination());
        }
    }

    // =========================================================================
    // 4. PRENDRE IRRIGATION (Stockage)
    // =========================================================================

    @Test
    void test4_PrendreIrrigation_SiRienDAutreEtStockVide() {
        ObjectifJardinier obj = mock(ObjectifJardinier.class);
        when(obj.getCouleurs()).thenReturn(List.of(Couleur.JAUNE));
        bot.getInventaire().ajouterObjectif(obj);

        // On vide l'inventaire de canaux (via mock si possible, sinon boucle)
        // Ici on suppose que l'inventaire est réel, donc on ne peut pas mocker getNombreCanauxDisponibles
        // Si c'est un mock : when(inventaire.getNombreCanauxDisponibles()).thenReturn(0);
        while(bot.getInventaire().getNombreCanauxDisponibles() > 0) bot.getInventaire().retirerIrrigation();

        // On bloque l'étape 2 (Faire pousser)
        when(plateau.getTrajetsLigneDroite(any())).thenReturn(Collections.emptyList());

        // On bloque l'étape 3 (Poser parcelle)
        when(piocheParcelle.estVide()).thenReturn(true);

        Action action = bot.choisirUneAction(gameState, Collections.emptySet());

        assertInstanceOf(ObtenirCanalDirrigation.class, action, "Le bot doit stocker de l'irrigation s'il est bloqué");
    }

    // =========================================================================
    // 5. RECHARGER LA MAIN
    // =========================================================================

    @Test
    void test5_RechargerMain_SiPasVide() {
        ObjectifJardinier obj = mock(ObjectifJardinier.class);
        when(obj.getCouleurs()).thenReturn(List.of(Couleur.ROSE));
        bot.getInventaire().ajouterObjectif(obj);

        when(plateau.getTrajetsLigneDroite(any())).thenReturn(Collections.emptyList());
        when(piocheParcelle.estVide()).thenReturn(true);
        // On lui donne un canal pour qu'il ne tente pas d'en prendre un autre (étape 4)
        bot.getInventaire().ajouterIrrigation();

        // Mais on bloque l'étape 1 (Poser Irrigation) en disant qu'aucune parcelle n'est éligible
        when(plateau.getParcellesMap()).thenReturn(Collections.emptyMap());

        Action action = bot.choisirUneAction(gameState, Collections.emptySet());

        assertInstanceOf(PiocherObjectif.class, action);
        assertEquals(TypeObjectif.JARDINIER, ((PiocherObjectif)action).getTypeObjectif());
    }

    // =========================================================================
    // 6. ACTION PAR DÉFAUT (Panda)
    // =========================================================================

    @Test
    void test6_Defaut_DeplacerPanda() {
        ObjectifJardinier obj = mock(ObjectifJardinier.class);
        when(obj.getCouleurs()).thenReturn(List.of(Couleur.ROSE));
        bot.getInventaire().ajouterObjectif(obj);

        bot.getInventaire().ajouterIrrigation(); // Bloque étape 4

        when(plateau.getTrajetsLigneDroite(any())).thenReturn(Collections.emptyList()); // Bloque étapes 2 & 6a (Jardinier)
        when(piocheParcelle.estVide()).thenReturn(true); // Bloque étape 3
        when(piocheJardinier.getTaille()).thenReturn(0); // Bloque étape 5
        when(plateau.getParcellesMap()).thenReturn(Collections.emptyMap()); // Bloque étape 1

        // Configuration pour le Panda (Etape 6)
        Position posPanda = new Position(0,0);
        Position destPanda = new Position(1,1);
        when(panda.getPositionPanda()).thenReturn(posPanda);
        // ATTENTION : Ici on mock le trajet DU PANDA, pas du jardinier
        when(plateau.getTrajetsLigneDroite(posPanda)).thenReturn(List.of(destPanda));

        Action action = bot.choisirUneAction(gameState, Collections.emptySet());

        assertInstanceOf(DeplacerPanda.class, action);
        assertEquals(destPanda, ((DeplacerPanda)action).getDestination());
    }
}