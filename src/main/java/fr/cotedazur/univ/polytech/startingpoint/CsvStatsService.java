package fr.cotedazur.univ.polytech.startingpoint;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.cotedazur.univ.polytech.startingpoint.GameEngine.LOGGER;

public class CsvStatsService {

    private static final Path DOSSIER_STATS = Paths.get("stats");
    private static final Path FICHIER_STATS = DOSSIER_STATS.resolve("gamestats.csv");

    // NOUVEL ENTÊTE AVEC LES NOUVELLES STATS
    private static final String[] HEADER = {
            "Bot", "Parties", "Victoires", "Egalites",
            "TotalPoints", "TotalObjectifs", // On stocke les totaux pour pouvoir recalculer les moyennes
            "%Victoire", "ScoreMoyen", "ObjMoyen" // Colonnes d'affichage
    };

    /**
     * Met à jour les stats avec Score et Objectifs
     */
    public void updateStats(String nomBot, int nbParties, int nbVictoires, int nbEgalites, long cumulScore, long cumulObjectifs) {
        Map<String, String[]> statsMap = lireFichierExistant();

        // [0]Nom, [1]Parties, [2]Victoires, [3]Egalites, [4]TotalPts, [5]TotalObj, [6]%, [7]ScoreMoy, [8]ObjMoy
        // Valeurs par défaut "0"
        String[] currentStats = statsMap.getOrDefault(nomBot, new String[]{
                nomBot, "0", "0", "0", "0", "0", "0%", "0", "0"
        });

        // 1. CALCUL DES NOUVEAUX TOTAUX (On additionne ce qu'il y avait + ce qu'on vient de jouer)
        int totalParties = Integer.parseInt(currentStats[1]) + nbParties;
        int totalVictoires = Integer.parseInt(currentStats[2]) + nbVictoires;
        int totalEgalites = Integer.parseInt(currentStats[3]) + nbEgalites;
        long totalPoints = Long.parseLong(currentStats[4]) + cumulScore;
        long totalObj = Long.parseLong(currentStats[5]) + cumulObjectifs;

        // 2. CALCUL DES MOYENNES
        double pourcent = (totalParties == 0) ? 0.0 : ((double) totalVictoires / totalParties) * 100;
        double scoreMoyen = (totalParties == 0) ? 0.0 : ((double) totalPoints / totalParties);
        double objMoyen = (totalParties == 0) ? 0.0 : ((double) totalObj / totalParties);

        // 3. MISE À JOUR DU TABLEAU (On remet tout en String)
        currentStats[1] = String.valueOf(totalParties);
        currentStats[2] = String.valueOf(totalVictoires);
        currentStats[3] = String.valueOf(totalEgalites);
        currentStats[4] = String.valueOf(totalPoints);
        currentStats[5] = String.valueOf(totalObj);

        // Formatage joli
        currentStats[6] = String.format("%.2f%%", pourcent);
        currentStats[7] = String.format("%.2f", scoreMoyen);
        currentStats[8] = String.format("%.2f", objMoyen);

        statsMap.put(nomBot, currentStats);
        ecrireFichier(statsMap);
    }

    private Map<String, String[]> lireFichierExistant() {
        Map<String, String[]> map = new HashMap<>();
        if (!Files.exists(FICHIER_STATS)) return map;

        try (CSVReader reader = new CSVReader(new FileReader(FICHIER_STATS.toFile()))) {
            List<String[]> lignes = reader.readAll();
            for (int i = 1; i < lignes.size(); i++) {
                String[] ligne = lignes.get(i);
                // On vérifie qu'on a bien toutes les colonnes
                if (ligne.length >= HEADER.length) {
                    map.put(ligne[0], ligne);
                }
            }
        } catch (IOException | CsvException e) {
            LOGGER.warning("Erreur critique lors de la simulation : " + e.getMessage());
        }
        return map;
    }

    private void ecrireFichier(Map<String, String[]> statsMap) {
        try {
            if (!Files.exists(DOSSIER_STATS)) Files.createDirectories(DOSSIER_STATS);
            try (CSVWriter writer = new CSVWriter(new FileWriter(FICHIER_STATS.toFile()))) {
                writer.writeNext(HEADER);
                for (String[] ligne : statsMap.values()) {
                    writer.writeNext(ligne);
                }
            }
        } catch (IOException e) {
            LOGGER.warning("Erreur critique lors de la simulation : " + e.getMessage());
        }
    }
}