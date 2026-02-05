package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.jardinier.ObjectifJardinier;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class BotJardinier extends Bot {
    private final Random random;

    public BotJardinier(String nom) {
        super(nom);
        this.random = new Random();
    }

    @Override
    protected Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {
        // Chaque méthode ci-dessous gère une stratégie spécifique et retourne null si l'action n'est pas possible/souhaitée

        Action actionUrgente = gererUrgenceObjectif(gameState, typesInterdits);
        if (actionUrgente != null) return actionUrgente;

        Action actionIrrigation = gererPoseIrrigation(gameState, typesInterdits);
        if (actionIrrigation != null) return actionIrrigation;

        Action actionJardinage = gererAvancementObjectif(gameState, typesInterdits);
        if (actionJardinage != null) return actionJardinage;

        Action actionExtension = gererExtensionPlateau(gameState, typesInterdits);
        if (actionExtension != null) return actionExtension;

        Action actionStockage = gererStockageIrrigation(typesInterdits);
        if (actionStockage != null) return actionStockage;

        Action actionRecharge = gererRechargeObjectifs(gameState, typesInterdits);
        if (actionRecharge != null) return actionRecharge;

        return gererActionParDefaut(gameState, typesInterdits);
    }

    // --- SOUS-METHODES DE DECISION (Complexité divisée) ---

    private Action gererUrgenceObjectif(GameState gameState, Set<TypeAction> typesInterdits) {
        if (!getInventaire().getObjectifs().isEmpty() || typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
            return null;
        }
        if (gameState.getPiocheJardinier().getTaille() > 0) {
            return new PiocherObjectif(TypeObjectif.JARDINIER);
        } else if (gameState.getPiochePanda().getTaille() > 0) {
            return new PiocherObjectif(TypeObjectif.PANDA);
        }
        return null;
    }

    private Action gererPoseIrrigation(GameState gameState, Set<TypeAction> typesInterdits) {
        if (getInventaire().getNombreCanauxDisponibles() > 0 && !typesInterdits.contains(TypeAction.POSER_IRRIGATION)) {
            return tenterPoserIrrigation(gameState);
        }
        return null;
    }

    private Action gererAvancementObjectif(GameState gameState, Set<TypeAction> typesInterdits) {
        ObjectifJardinier obj = getMeilleurObjectifJardinier();
        if (obj != null && !typesInterdits.contains(TypeAction.DEPLACER_JARDINIER)) {
            return tenterFairePousser(gameState, obj.getCouleurs().getFirst());
        }
        return null;
    }

    private Action gererExtensionPlateau(GameState gameState, Set<TypeAction> typesInterdits) {
        if (!typesInterdits.contains(TypeAction.POSER_PARCELLE) && !gameState.getPiocheParcelle().estVide()) {
            return new PoserParcelle();
        }
        return null;
    }

    private Action gererStockageIrrigation(Set<TypeAction> typesInterdits) {
        if (!typesInterdits.contains(TypeAction.PRENDRE_IRRIGATION)
                && getInventaire().getNombreCanauxDisponibles() == 0) {
            return new ObtenirCanalDirrigation();
        }
        return null;
    }

    private Action gererRechargeObjectifs(GameState gameState, Set<TypeAction> typesInterdits) {
        if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF) && gameState.getPiocheJardinier().getTaille() > 0) {
            return new PiocherObjectif(TypeObjectif.JARDINIER);
        }
        return null;
    }

    private Action gererActionParDefaut(GameState gameState, Set<TypeAction> typesInterdits) {
        if (!typesInterdits.contains(TypeAction.DEPLACER_PANDA)) {
            List<Position> depts = gameState.getPlateau()
                    .getTrajetsLigneDroite(gameState.getPanda().getPositionPanda());
            if (!depts.isEmpty()) {
                return new DeplacerPanda(gameState.getPanda(), depts.getFirst());
            }
        }
        return null;
    }

    // --- UTILITAIRES LOGIQUES ---

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

    private Action tenterPoserIrrigation(GameState gs) {
        Plateau plateau = gs.getPlateau();
        Set<Position> positionsOccupees = plateau.getParcellesMap().keySet();

        for (Position pos : positionsOccupees) {
            for (PositionsRelatives dir : PositionsRelatives.values()) {
                if (dir == PositionsRelatives.ZERO) continue;

                Position voisin = pos.add(dir.getPosition());

                if (plateau.peutPlacerCanal(pos, voisin) && !plateau.aCanalEntre(pos, voisin)) {
                    return new PoserCanalDirrigation(pos, voisin);
                }
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

    // =================== METEO ===================

    @Override
    public Parcelle choisirParcelleMeteo(List<Parcelle> parcellesIrriguees) {
        if (parcellesIrriguees.isEmpty()) {
            return null;
        }
        return parcellesIrriguees.get(random.nextInt(parcellesIrriguees.size()));
    }

    @Override
    public Parcelle choisirDestinationPanda(List<Parcelle> parcelles) {
        if (parcelles.isEmpty()) {
            return null;
        }
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