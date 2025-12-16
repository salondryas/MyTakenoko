package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifPoseur;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BotTest {
    Bot bot;
    Plateau plateau;

    @BeforeEach
    void setUp() {
        bot = new Bot("TestBot");
        plateau = new Plateau();
    }

    @Test
    void testJouerRenvoieUneAction() {
        PiocheParcelle pioche = new PiocheParcelle();
        // Le bot doit renvoyer une action non nulle s'il peut jouer
        Action action = bot.jouer(plateau, pioche);
        assertNotNull(action, "Le bot doit renvoyer une action");
    }

    @Test
    void testVerifierObjectifs() {
        // Le bot a un objectif : 1 tuile ROSE pour 100 points
        ObjectifPoseur obj = new ObjectifPoseur(100, Couleur.ROSE, 1);
        bot.getInventaire().ajouterObjectif(obj);

        assertEquals(0, bot.getScore(), "Score initial doit être 0");

        // On prépare le plateau pour valider l'objectif
        plateau.placerParcelle(new Parcelle(Couleur.ROSE), new Position(1, -1, 0));

        // Le bot vérifie ses objectifs
        bot.verifierObjectifs(plateau);

        // Il doit avoir gagné les points
        assertEquals(100, bot.getScore(), "Le bot aurait dû valider l'objectif et gagner 100 points");
        assertTrue(obj.isValide(), "L'objectif devrait être marqué comme validé");
    }
}