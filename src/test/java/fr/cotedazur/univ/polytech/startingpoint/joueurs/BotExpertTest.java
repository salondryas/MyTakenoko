package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.DeplacerPanda;
import fr.cotedazur.univ.polytech.startingpoint.actions.PiocherObjectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifPanda;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Jardinier; // IMPORT AJOUTÉ
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Panda;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class BotExpertTest {

    BotExpert bot;
    @Mock GameState gameState;
    @Mock Plateau plateau;
    @Mock Panda panda;
    @Mock Jardinier jardinier; // MOCK JARDINIER AJOUTÉ
    @Mock PiocheParcelle piocheParcelle;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bot = new BotExpert("Einstein");

        when(gameState.getPlateau()).thenReturn(plateau);
        when(gameState.getPanda()).thenReturn(panda);
        when(gameState.getJardinier()).thenReturn(jardinier); // LIEN JARDINIER
        when(gameState.getPiocheParcelle()).thenReturn(piocheParcelle);

        // Comportements par défaut
        when(piocheParcelle.estVide()).thenReturn(false);
        when(jardinier.getPosition()).thenReturn(Plateau.POSITION_ORIGINE);
        when(panda.getPositionPanda()).thenReturn(Plateau.POSITION_ORIGINE);
    }

    @Test
    void testPrioriteMainVide() {
        // CAS : Le bot n'a aucun objectif.
        Action action = bot.jouer(gameState).get(0);
        assertTrue(action instanceof PiocherObjectif, "Main vide -> Doit piocher");
    }

    @Test
    void testPrioriteMangerPanda() {
        // CAS : Le bot a un objectif Panda VERT.
        bot.getInventaire().ajouterObjectif(new ObjectifPanda(2, List.of(Couleur.VERT)));

        Position posVerte = new Position(1, 0);
        Parcelle parcelleVerte = new Parcelle(posVerte, Couleur.VERT);
        parcelleVerte.pousserBambou();

        when(plateau.getParcelle(posVerte)).thenReturn(parcelleVerte);
        when(plateau.getTrajetsLigneDroite(any())).thenReturn(List.of(posVerte));

        Action action = bot.jouer(gameState).get(0);

        assertTrue(action instanceof DeplacerPanda, "Le bot devrait déplacer le Panda");
    }
}