package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.pioche.SelectionParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BotEquipeTest {

    BotEquipe bot;
    GameState gameState;

    @BeforeEach
    void setUp() {
        bot = new BotEquipe("DreamTeam");
        gameState = new GameState(List.of(bot));
    }

    @Test
    void testChoixAction_Delegation() {
        Action action = bot.choisirUneAction(gameState, new HashSet<>());
        // Le bot doit être capable de choisir une action via sa stratégie
        assertNotNull(action, "Le Bot Equipe doit être capable de jouer");
    }

    @Test
    void testMeteo_Delegation() {
        Meteo m = bot.choisirMeteo();
        assertNotNull(m, "Le choix météo doit être délégué");
    }

    @Test
    void testArchitecte_Delegation() {
        // Test que le choix de parcelle passe bien par la stratégie (et donc l'expert architecte)
        List<Parcelle> options = new ArrayList<>();
        options.add(new Parcelle(Couleur.VERT));
        options.add(new Parcelle(Couleur.ROSE));

        SelectionParcelle session = new SelectionParcelle(options, new PiocheParcelle());

        Parcelle choix = bot.choisirParcelle(session, gameState.getPlateau());
        assertNotNull(choix);
        assertTrue(options.contains(choix));
    }
}