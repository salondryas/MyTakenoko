package fr.cotedazur.univ.polytech.startingpoint;

import com.beust.jcommander.Parameter;

public class MainArgs {
    @Parameter(names = "--demo", description = "Lance une unique partie avec affichage complet")
    public boolean demo = false;

    @Parameter(names = "--2thousands", description = "Lance 2x1000 parties sans affichage")
    public boolean twoThousands = false;

    @Parameter(names = "--csv", description = "Lance 2x1000 parties ET sauvegarde les statistiques dans stats/gamestats.csv")
    public boolean csv = false;
}