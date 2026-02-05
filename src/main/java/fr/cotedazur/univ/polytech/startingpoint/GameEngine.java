package fr.cotedazur.univ.polytech.startingpoint;

import com.beust.jcommander.JCommander;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class GameEngine {

    private static final CsvStatsService csvService = new CsvStatsService();
    private static final Logger LOGGER = Logger.getLogger("");

    public static void main(String[] args) {
        MainArgs arguments = new MainArgs();
        JCommander.newBuilder().addObject(arguments).build().parse(args);

        // On coupe le son si on est en mode 2000 OU en mode CSV
        boolean modeSilence = arguments.twoThousands || arguments.csv;
        configurerLogger(modeSilence);

        // LOGIQUE DE SÉLECTION DU MODE
        if (arguments.csv) {
            System.out.println(">>> MODE CSV ACTIVÉ : Les stats seront enregistrées.");
            lancerSimulation(true); // true = ON Sauvegarde
        } else if (arguments.twoThousands) {
            System.out.println(">>> MODE 2000 (Console uniquement) : Pas d'enregistrement CSV.");
            lancerSimulation(false); // false = ON NE Sauvegarde PAS
        } else {
            lancerPartieDemo();
        }
    }

    private static void lancerPartieDemo() {
        List<Bot> bots = new ArrayList<>();
        bots.add(new BotJardinier("Jardinier Demo"));
        bots.add(new BotPanda("Panda Demo"));

        Partie partie = new Partie(bots);
        partie.jouer();
    }

    // J'ai renommé en "lancerSimulation" et ajouté le paramètre boolean
    private static void lancerSimulation(boolean sauvegardeCsv) {
        System.out.println(" Calcul des statistiques en cours (2x1000 parties)...");

        // SERIE 1 : Panda vs Jardinier
        jouerSerie(BotJardinier.class, BotPanda.class, 1000, sauvegardeCsv);

        // SERIE 2 : Panda vs Panda
        jouerSerie(BotPanda.class, BotPanda.class, 1000, sauvegardeCsv);
    }

    // Ajout du paramètre boolean "sauvegarderCsv" ici aussi
    private static void jouerSerie(Class<? extends Bot> classeBot1, Class<? extends Bot> classeBot2, int n, boolean sauvegardeCsv) {
        int victoires1 = 0;
        int victoires2 = 0;
        int egalites = 0;

        long cumulScore1 = 0;
        long cumulScore2 = 0;
        long cumulObj1 = 0;
        long cumulObj2 = 0;

        for (int i = 0; i < n; i++) {
            try {
                Bot b1 = classeBot1.getConstructor(String.class).newInstance("Bot A");
                Bot b2 = classeBot2.getConstructor(String.class).newInstance("Bot B");

                Partie p = new Partie(List.of(b1, b2));
                p.jouer();

                Bot gagnant = p.getGagnant();
                if (gagnant == b1) victoires1++;
                else if (gagnant == b2) victoires2++;
                else egalites++;

                cumulScore1 += b1.getScore();
                cumulScore2 += b2.getScore();
                cumulObj1 += b1.getNombreObjectifsValides();
                cumulObj2 += b2.getNombreObjectifsValides();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // On ne sauvegarde que si demandé
        if (sauvegardeCsv) {
            csvService.updateStats(classeBot1.getSimpleName(), n, victoires1, egalites, cumulScore1, cumulObj1);
            csvService.updateStats(classeBot2.getSimpleName(), n, victoires2, egalites, cumulScore2, cumulObj2);
            System.out.println(">> Statistiques mises à jour dans " + Paths.get("stats/gamestats.csv").toAbsolutePath());
        }

        // Affichage console (Toujours affiché)
        System.out.println("\n--------------------------------------------------");
        System.out.println("Stats : " + classeBot1.getSimpleName() + " vs " + classeBot2.getSimpleName());
        System.out.println("--------------------------------------------------");
        // J'ajoute aussi l'affichage du score moyen car c'est utile
        System.out.printf("Bot A (%s) : %.2f%% vict. | Score Moy: %.1f%n",
                classeBot1.getSimpleName(), (victoires1 / (float) n) * 100, (double)cumulScore1/n);
        System.out.printf("Bot B (%s) : %.2f%% vict. | Score Moy: %.1f%n",
                classeBot2.getSimpleName(), (victoires2 / (float) n) * 100, (double)cumulScore2/n);
        System.out.println("Egalites; " + String.format("%.2f", (egalites / (float) n) * 100) + "%");
    }

    private static void configurerLogger(boolean modeSilence) {
        LogManager.getLogManager().reset();
        Logger rootLogger = Logger.getLogger("");

        if (modeSilence) {
            rootLogger.setLevel(Level.OFF);
        } else {
            ConsoleHandler ch = new ConsoleHandler();
            ch.setLevel(Level.INFO);
            ch.setFormatter(new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord lr) {
                    return lr.getMessage() + "\n";
                }
            });
            rootLogger.addHandler(ch);
            rootLogger.setLevel(Level.INFO);
        }
    }
}