package fr.cotedazur.univ.polytech.startingpoint;

import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.affichage.AffichageConsole;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import static fr.cotedazur.univ.polytech.startingpoint.GameEngine.LOGGER;

/**
 * Cette classe est responsable de l'exécution des campagnes de tests (Simulations).
 * Elle gère les séries de matchs, le calcul des statistiques et le rapport.
 */
public class SimulationRunner {

    private final CsvStatsService csvService;

    public SimulationRunner() {
        this.csvService = new CsvStatsService();
    }

    public void lancerToutesLesSimulations(boolean sauvegardeCsv, Pair<Class<? extends Bot>,Class<? extends Bot>> joueursPartie1, Pair<Class<? extends Bot>,Class<? extends Bot>> joueursPartie2) {
        System.out.println(">>> Démarrage des simulations (2 séries de 1000 parties)...");

        // SERIE 1 : Panda vs Jardinier
        String resultats1 = jouerSerie(joueursPartie1.getLeft(), joueursPartie1.getRight(), 1000, sauvegardeCsv);

        // SERIE 2 : Panda vs Panda
        String resultats2 = jouerSerie(joueursPartie2.getLeft(), joueursPartie2.getRight(), 1000, sauvegardeCsv);

        System.out.println(resultats1 + resultats2);
    }

    private String jouerSerie(Class<? extends Bot> classeBot1, Class<? extends Bot> classeBot2, int nombreDeParties, boolean sauvegardeCsv) {
        int victoires1 = 0;
        int victoires2 = 0;
        int egalites = 0;
        long cumulScore1 = 0;
        long cumulScore2 = 0;
        long cumulObj1 = 0;
        long cumulObj2 = 0;

        // --- 1. EXÉCUTION DES PARTIES ---
        for (int i = 0; i < nombreDeParties; i++) {
            try {
                // Instanciation dynamique des bots
                Bot b1 = classeBot1.getConstructor(String.class).newInstance("Bot A");
                Bot b2 = classeBot2.getConstructor(String.class).newInstance("Bot B");

                Partie p = new Partie(List.of(b1, b2));
                p.jouer();

                // Collecte des résultats
                Bot gagnant = p.getGagnant();
                if (gagnant == b1) victoires1++;
                else if (gagnant == b2) victoires2++;
                else egalites++;

                cumulScore1 += b1.getScore();
                cumulScore2 += b2.getScore();
                cumulObj1 += b1.getNombreObjectifsValides();
                cumulObj2 += b2.getNombreObjectifsValides();

            } catch (Exception e) {
                LOGGER.warning("Erreur critique lors de la simulation : " + e.getMessage());
            }
        }

        if (sauvegardeCsv) {
            csvService.updateStats(classeBot1.getSimpleName(), nombreDeParties, victoires1, egalites, cumulScore1, cumulObj1);
            csvService.updateStats(classeBot2.getSimpleName(), nombreDeParties, victoires2, egalites, cumulScore2, cumulObj2);
        }

        // --- RAPPORT CONSOLE ---
        AffichageConsole affichage = new AffichageConsole(sauvegardeCsv, classeBot1, classeBot2, nombreDeParties, victoires1, victoires2, egalites, cumulScore1, cumulScore2);
        return affichage.afficher();
    }
}