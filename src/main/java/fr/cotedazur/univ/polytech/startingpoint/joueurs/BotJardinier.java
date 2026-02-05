package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifJardinier;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class BotJardinier extends Bot {
    private final Random random; // pour les choix de meteo

    public BotJardinier(String nom) {
        super(nom);
        this.random = new Random();
    }

    @Override
    protected Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {

        // 0. URGENCE : Si je n'ai AUCUN objectif
        if (getInventaire().getObjectifs().isEmpty() && !typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
            // CORRECTION 1 : On vérifie si la pioche est vide avant de demander !
            if (gameState.getPiocheJardinier().getTaille() > 0) {
                return new PiocherObjectif(TypeObjectif.JARDINIER);
            } else if (gameState.getPiochePanda().getTaille() > 0) {
                // Fallback : Si plus de cartes Jardinier, je prends Panda
                return new PiocherObjectif(TypeObjectif.PANDA);
            }
            // Si tout est vide, on continue vers d'autres actions (ne pas retourner null
            // ici)
        }

        ObjectifJardinier obj = getMeilleurObjectifJardinier();

        // 1. GESTION DE L'IRRIGATION
        if (getInventaire().getNombreCanauxDisponibles() > 0 && !typesInterdits.contains(TypeAction.POSER_IRRIGATION)) {
            Action poserCanal = tenterPoserIrrigation(gameState);
            if (poserCanal != null)
                return poserCanal;
        }

        // 2. AVANCER L'OBJECTIF JARDINIER
        if (obj != null) {
            if (!typesInterdits.contains(TypeAction.DEPLACER_JARDINIER)) {
                Action actionJardinier = tenterFairePousser(gameState, obj.getCouleurs().getFirst());
                if (actionJardinier != null)
                    return actionJardinier;
            }
        }

        // 3. AGRANDIR LE PLATEAU
        if (!typesInterdits.contains(TypeAction.POSER_PARCELLE) && !gameState.getPiocheParcelle().estVide()) {
            Parcelle p = gameState.getPiocheParcelle().piocher();
            if (p != null) {
                PoserParcelle action = new PoserParcelle();
                return new PoserParcelle();
            }
        }

        // 4. PRENDRE IRRIGATION (Stockage limité)
        if (!typesInterdits.contains(TypeAction.PRENDRE_IRRIGATION)
                && getInventaire().getNombreCanauxDisponibles() == 0) {
            return new ObtenirCanalDirrigation();
        }

        // 5. RECHARGER LA MAIN D'OBJECTIFS (Si pas vide)
        if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
            if (gameState.getPiocheJardinier().getTaille() > 0)
                return new PiocherObjectif(TypeObjectif.JARDINIER);
        }

        // 6. ACTION PAR DÉFAUT (Déplacer panda)
        if (!typesInterdits.contains(TypeAction.DEPLACER_PANDA)) {
            List<Position> depts = gameState.getPlateau()
                    .getTrajetsLigneDroite(gameState.getPanda().getPositionPanda());
            if (!depts.isEmpty())
                return new DeplacerPanda(gameState.getPanda(), depts.get(0));
        }

        return null;
    }

    private ObjectifJardinier getMeilleurObjectifJardinier() {
        for (Objectif o : getInventaire().getObjectifs()) {
            if (o instanceof ObjectifJardinier)
                return (ObjectifJardinier) o;
        }
        return null;
    }

    private Action tenterFairePousser(GameState gs, Couleur couleur) {
        Plateau plateau = gs.getPlateau();
        List<Position> deplacements = plateau.getTrajetsLigneDroite(gs.getJardinier().getPosition());

        for (Position pos : deplacements) {
            Optional<Parcelle> p = Optional.ofNullable(plateau.getParcelle(pos));
            if (p.isPresent()
                    && p.get().getCouleur() == couleur
                    && p.get().estIrriguee()
                    && p.get().getNbSectionsSurParcelle() < 4) {
                return new DeplacerJardinier(gs.getJardinier(), pos);
            }
        }
        return null;
    }

    @Override
    public Position choisirPosition(Parcelle parcelleChoisie, Plateau plateau) {
        List<Position> positionsDisponibles = plateau.getEmplacementsDisponibles();
        if (!positionsDisponibles.isEmpty()) {
            return positionsDisponibles.getFirst();
        }
        return null;
    }

    private Action tenterPoserIrrigation(GameState gs) {
        Plateau plateau = gs.getPlateau();
        Set<Position> positionsOccupees = plateau.getParcellesMap().keySet();

        for (Position pos : positionsOccupees) {
            for (PositionsRelatives dir : PositionsRelatives.values()) {
                if (dir == PositionsRelatives.ZERO)
                    continue;

                Position voisin = pos.add(dir.getPosition());

                // CORRECTION 2 : On utilise aCanalEntre() pour ne pas proposer un canal déjà
                // posé !
                if (plateau.peutPlacerCanal(pos, voisin) && !plateau.aCanalEntre(pos, voisin)) {
                    return new PoserCanalDirrigation(pos, voisin);
                }
            }
        }
        return null;
    }

    /// =================== METEO ===================
    ///

    // Implémentation pour la pluie
    @Override
    public Parcelle choisirParcelleMeteo(List<Parcelle> parcellesIrriguees) {
        if (parcellesIrriguees.isEmpty()) {
            return null;
        }
        // Choisit une parcelle aléatoire parmi les parcelles irriguées
        return parcellesIrriguees.get(random.nextInt(parcellesIrriguees.size()));
    }

    // Implémentation pour l'orage
    @Override
    public Parcelle choisirDestinationPanda(List<Parcelle> parcelles) {
        if (parcelles.isEmpty()) {
            return null;
        }
        // Choisit une parcelle aléatoire pour placer le panda
        return parcelles.get(random.nextInt(parcelles.size()));
    }

    // Implémentation pour le choix libre
    @Override
    public Meteo choisirMeteo() {
        Meteo[] options = { Meteo.SOLEIL, Meteo.PLUIE, Meteo.VENT, Meteo.ORAGE, Meteo.NUAGES };
        return options[random.nextInt(options.length)];
    }

    // Implémentation pour les nuages sans aménagement
    @Override
    public Meteo choisirMeteoAlternative() {
        Meteo[] options = { Meteo.SOLEIL, Meteo.PLUIE, Meteo.VENT, Meteo.ORAGE };
        return options[random.nextInt(options.length)];
    }
}