package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.PiocheParcelle; // <--- Import ajouté
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.SelectionParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BotExpertTest {

    BotExpert bot;
    GameState gameState;

    @BeforeEach
    void setUp() {
        bot = new BotExpert("MasterBot");
        gameState = new GameState(List.of(bot));
    }

    @Test
    void testIntegration_ChoisirAction_AppelleStrategie() {
        // On vérifie que la méthode choisirUneAction ne plante pas et renvoie un résultat
        Action action = bot.choisirUneAction(gameState, new HashSet<>());
        // Note: L'action peut être null si rien n'est possible, mais l'appel ne doit pas crasher
        // Ici avec un jeu vide, il devrait vouloir piocher un objectif
        assertNotNull(action, "Le Bot Expert doit choisir une action via sa stratégie");
    }

    @Test
    void testDelegationMeteo_AppelleExpertMeteo() {
        Meteo m = bot.choisirMeteo();
        assertNotNull(m, "Le bot doit déléguer le choix météo");

        Meteo mAlt = bot.choisirMeteoAlternative();
        assertNotNull(mAlt, "Le bot doit déléguer le choix météo alternatif");
    }

    @Test
    void testDelegationChoixParcelle() {
        // Simulation : On pioche 3 parcelles
        List<Parcelle> choix = new ArrayList<>();
        choix.add(new Parcelle(Couleur.VERT));
        choix.add(new Parcelle(Couleur.ROSE));
        choix.add(new Parcelle(Couleur.JAUNE));

        // CORRECTION ICI : On doit fournir une pioche (même vide ou bidon) au constructeur
        PiocheParcelle piocheDummy = new PiocheParcelle();
        SelectionParcelle session = new SelectionParcelle(choix, piocheDummy);

        // Le bot doit en choisir une via ExpertStrategie
        Parcelle p = bot.choisirParcelle(session, gameState.getPlateau());

        assertNotNull(p, "Le bot doit choisir une parcelle");
        assertTrue(choix.contains(p), "La parcelle choisie doit être valide");
    }

    @Test
    void testDelegationChoixPosition() {
        Parcelle p = new Parcelle(Couleur.VERT);
        Position pos = bot.choisirPosition(p, gameState.getPlateau());

        // Au début du jeu, il y a des places libres autour de l'étang
        assertNotNull(pos, "Le bot doit trouver une position pour poser sa tuile");
    }
}