package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.PiocherObjectif;
import fr.cotedazur.univ.polytech.startingpoint.actions.PoserParcelle;
import fr.cotedazur.univ.polytech.startingpoint.actions.TypeAction;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.GrillePlateau;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.elements.pioche.SelectionParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static fr.cotedazur.univ.polytech.startingpoint.GameEngine.LOGGER;

public class BotParcelle extends Bot {
    private final Random random;
    private Position positionMemoriseePourLeTour = null;

    public BotParcelle(String nom) {
        super(nom);
        this.random = new Random();
    }

    @Override
    protected Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {
        // Priorité 1 : Urgence d'objectifs (si main vide)
        if (getNombreObjectifsParcelleEnMain() == 0 && !typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
            return new PiocherObjectif(TypeObjectif.PARCELLE);
        }

        // Priorité 2 : Action principale (Poser Parcelle)
        if (!typesInterdits.contains(TypeAction.POSER_PARCELLE) && !gameState.getPiocheParcelle().estVide()) {
            return new PoserParcelle();
        }

        // Priorité 3 : Remplissage (Piocher encore)
        if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
            return new PiocherObjectif(TypeObjectif.PARCELLE);
        }

        return null;
    }

    @Override
    public Parcelle choisirParcelle(SelectionParcelle selection, Plateau plateau) {
        List<Parcelle> tuilesCandidates = selection.getParcellesAChoisir();
        List<Position> positionsLegales = plateau.getEmplacementsDisponibles();

        Parcelle meilleurTuile = tuilesCandidates.getFirst();
        Position meilleurPos = positionsLegales.isEmpty() ? null : positionsLegales.getFirst();
        int meilleurScore = Integer.MIN_VALUE;

        // Algorithme de recherche exhaustif : on teste chaque combinaison Tuile/Position
        for (Parcelle tuile : tuilesCandidates) {
            for (Position pos : positionsLegales) {
                int score = evaluerCoup(tuile, pos, plateau);

                if (score > meilleurScore) {
                    meilleurScore = score;
                    meilleurTuile = tuile;
                    meilleurPos = pos;
                }
            }
        }

        this.positionMemoriseePourLeTour = meilleurPos;
        LOGGER.info(getNom() + " a choisi : " + meilleurTuile.getCouleur() + " en " + meilleurPos + " (Score: " + meilleurScore + ")");

        selection.validerChoix(meilleurTuile);
        return meilleurTuile;
    }

    @Override
    public Position choisirPosition(Parcelle parcelleChoisie, Plateau plateau) {
        // Utilisation de la mémoire tampon calculée lors de choisirParcelle
        if (positionMemoriseePourLeTour != null && plateau.isPositionDisponible(positionMemoriseePourLeTour)) {
            Position pos = positionMemoriseePourLeTour;
            positionMemoriseePourLeTour = null;
            return pos;
        }

        List<Position> dispos = plateau.getEmplacementsDisponibles();
        return dispos.isEmpty() ? null : dispos.getFirst();
    }

    // =================================================================================
    // SCORE
    // =================================================================================

    /**
     * Calcule un score pour un coup donné.
     * Le score est la somme de trois critères : Utilité pour objectifs, Adjacence couleur, Accès eau.
     */
    private int evaluerCoup(Parcelle tuile, Position pos, Plateau plateau) {
        int score = 0;
        Couleur couleur = tuile.getCouleur();

        score += calculerScoreObjectifs(couleur);
        score += calculerScoreAdjacence(couleur, pos, plateau);
        score += calculerScoreIrrigation(pos, plateau);

        return score;
    }

    private int calculerScoreObjectifs(Couleur couleur) {
        for (Objectif obj : getInventaire().getObjectifs()) {
            if (obj.getType() == TypeObjectif.PARCELLE && obj.getCouleurs().contains(couleur)) {
                return 20; // Bonus fort si la couleur aide un objectif
            }
        }
        return -10; // Malus si couleur inutile
    }

    private int calculerScoreAdjacence(Couleur couleur, Position pos, Plateau plateau) {
        int voisinsMemeCouleur = 0;

        for (PositionsRelatives dir : PositionsRelatives.values()) {
            if (dir == PositionsRelatives.ZERO) continue;

            Position voisinPos = pos.add(dir.getPosition());
            Parcelle voisin = plateau.getParcelle(voisinPos);

            if (voisin != null && voisin.getCouleur() == couleur) {
                voisinsMemeCouleur++;
            }
        }

        // 10 pts par voisin + Bonus de motif (Ligne/Triangle) si >= 2 voisins
        int score = voisinsMemeCouleur * 10;
        if (voisinsMemeCouleur >= 1) score += 5;
        if (voisinsMemeCouleur >= 2) score += 20;

        return score;
    }

    private int calculerScoreIrrigation(Position pos, Plateau plateau) {
        boolean accesEau = pos.estAdjacent(GrillePlateau.POSITION_ORIGINE) || aCanalAdjacent(plateau, pos);
        // Une parcelle irriguée (ou proche de l'eau) a plus de valeur
        return accesEau ? 15 : -5;
    }

    // --- UTILITAIRES ---

    private int getNombreObjectifsParcelleEnMain() {
        return (int) getInventaire().getObjectifs().stream()
                .filter(o -> o.getType() == TypeObjectif.PARCELLE)
                .count();
    }

    private boolean aCanalAdjacent(Plateau plateau, Position pos) {
        // TODO: Implémenter une vérification réelle via le Plateau si nécessaire
        return false;
    }

    // =================== METEO ===================

    @Override
    public Parcelle choisirParcelleMeteo(List<Parcelle> parcellesIrriguees) {
        if (parcellesIrriguees.isEmpty()) return null;
        return parcellesIrriguees.get(random.nextInt(parcellesIrriguees.size()));
    }

    @Override
    public Parcelle choisirDestinationPanda(List<Parcelle> parcelles) {
        if (parcelles.isEmpty()) return null;
        return parcelles.get(random.nextInt(parcelles.size()));
    }

    @Override
    public Meteo choisirMeteo() {
        Meteo[] options = { Meteo.SOLEIL, Meteo.PLUIE, Meteo.VENT, Meteo.ORAGE, Meteo.NUAGES };
        return options[random.nextInt(options.length)];
    }

    @Override
    public Meteo choisirMeteoAlternative() {
        Meteo[] options = { Meteo.SOLEIL, Meteo.PLUIE, Meteo.VENT, Meteo.ORAGE };
        return options[random.nextInt(options.length)];
    }
}