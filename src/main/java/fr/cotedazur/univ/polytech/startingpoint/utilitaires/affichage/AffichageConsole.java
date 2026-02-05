package fr.cotedazur.univ.polytech.startingpoint.utilitaires.affichage;

import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;

import java.nio.file.Paths;

public class AffichageConsole implements Afficher{
    boolean isSauvegarde;
    Class<? extends Bot> classeBot1;
    Class<? extends Bot> classeBot2;
    int nombreDeParties;
    int victoires1;
    int victoires2;
    int egalites;
    long cumulScore1;
    long cumulScore2;

    public AffichageConsole(boolean csv, Class<? extends Bot> c1, Class<? extends Bot> c2, int n, int v1, int v2, int eq, long s1, long s2) {
        isSauvegarde = csv;
        classeBot1=c1;
        classeBot2=c2;
        nombreDeParties=n;
        victoires1=v1;
        victoires2=v2;
        egalites=eq;
        cumulScore1=s1;
        cumulScore2=s2;
    }

    @Override
    public String afficher() {
        String rapport = String.format(
                "%n--------------------------------------------------%n" +
                        "Stats : %s vs %s%n" +
                        "--------------------------------------------------%n" +
                        "Bot A (%s) : %.2f%% vict. | Score Moy: %.1f%n" +
                        "Bot B (%s) : %.2f%% vict. | Score Moy: %.1f%n" +
                        "Egalites     : %.2f%%",
                classeBot1.getSimpleName(), classeBot2.getSimpleName(),
                classeBot1.getSimpleName(), (victoires1 / (float) nombreDeParties) * 100, (double) cumulScore1 / nombreDeParties,
                classeBot2.getSimpleName(), (victoires2 / (float) nombreDeParties) * 100, (double) cumulScore2 / nombreDeParties,
                (egalites / (float) nombreDeParties) * 100
        );
        if (isSauvegarde) rapport = ">> Statistiques CSV mises à jour dans " + Paths.get("stats/gamestats.csv").toAbsolutePath()+ "\n" + rapport;
        return rapport;
    }
}
