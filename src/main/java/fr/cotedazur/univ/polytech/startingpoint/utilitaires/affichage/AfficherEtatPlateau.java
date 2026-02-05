package fr.cotedazur.univ.polytech.startingpoint.utilitaires.affichage;

import fr.cotedazur.univ.polytech.startingpoint.plateau.GrillePlateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.Map;

public class AfficherEtatPlateau implements Afficher {
    static final String HEADER = " ÉTAT FINAL DU PLATEAU \n";
    static final String SEPARATOR = "----------------------------------------------------\n";
    static final String LINE_FORMAT   = "| %-15s | %-10s | %-10s | %-5s |\n";

    Plateau plateau;
    Map<Position, Parcelle> parcelles;

    public AfficherEtatPlateau(Plateau plateau) {
        this.plateau=plateau;
        parcelles = plateau.getParcellesMap();
    }

    public String afficher() {
        StringBuilder sb = new StringBuilder();

        sb.append(HEADER);
        sb.append(SEPARATOR);
        sb.append(String.format(LINE_FORMAT, "Position", "Couleur", "Irriguée?", "Bambou"));
        sb.append(SEPARATOR);

        for (Map.Entry<Position, Parcelle> entry : parcelles.entrySet()) {
            Position pos = entry.getKey();
            Parcelle p = entry.getValue();

            // On ignore la parcelle origine (Etang) pour l'affichage si on veut,
            // ou on l'affiche différemment.
            if (pos.equals(GrillePlateau.POSITION_ORIGINE)) {
                sb.append(String.format(LINE_FORMAT,
                        pos.toString(), "ETANG", "OUI", "-"));
            } else {
                sb.append(String.format(LINE_FORMAT,
                        pos.toString(),
                        p.getCouleur(),
                        (p.estIrriguee() ? "OUI" : "NON"),
                        p.getNbSectionsSurParcelle()));
            }
        }

        sb.append(SEPARATOR);

        return sb.toString();
    }
}
