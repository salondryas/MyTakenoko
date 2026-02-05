package fr.cotedazur.univ.polytech.startingpoint;

import com.beust.jcommander.JCommander;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class GameEngine {

    private static final CsvStatsService csvService = new CsvStatsService();
    public static final Logger LOGGER = Logger.getLogger("");

    private static Class<? extends Bot> JOUEUR_1_PARTIE_1 = BotParcelle.class;
    private static Class<? extends Bot> JOUEUR_2_PARTIE_1 = BotParcelle.class;

    private static Class<? extends Bot> JOUEUR_1_PARTIE_2 = BotRandom.class;
    private static Class<? extends Bot> JOUEUR_2_PARTIE_2 = BotParcelle.class;

    public static void main(String[] args) {
        MainArgs arguments = new MainArgs();
        JCommander.newBuilder().addObject(arguments).build().parse(args);

        // On coupe le son si on est en mode 2000 OU en mode CSV
        boolean modeSilence = arguments.twoThousands || arguments.csv;
        configurerLogger(modeSilence);

        // LOGIQUE DE SÉLECTION DU MODE
        if (modeSilence) {
            boolean modeSauvegarde = arguments.csv;

            if (modeSauvegarde) System.out.println(">>> MODE CSV ACTIVÉ : Les stats seront enregistrées.");
            else System.out.println(">>> MODE 2000 (Console uniquement) : Pas d'enregistrement CSV.");

            SimulationRunner sr = new SimulationRunner();
            sr.lancerToutesLesSimulations(modeSauvegarde, Pair.of(JOUEUR_1_PARTIE_1, JOUEUR_2_PARTIE_1), Pair.of(JOUEUR_1_PARTIE_2, JOUEUR_2_PARTIE_2));
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