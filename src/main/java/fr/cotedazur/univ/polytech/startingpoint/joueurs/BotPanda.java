package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Strategies.StrategiePandaGenerale;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.GrillePlateau;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.elements.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;

import java.util.*;

public class BotPanda extends Bot {
    private final Random random;
    private final StrategiePandaGenerale strategiePanda = new StrategiePandaGenerale();

    public BotPanda(String nom) {
        super(nom);
        this.random = new Random();
    }

    @Override
    protected Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {
        List<Couleur> couleursCibles = identifierCouleursCibles();

        Action actionUrgence = gererUrgenceObjectif(gameState, typesInterdits);
        if (actionUrgence != null) return actionUrgence;

        Action actionIrrigation = gererPoseIrrigation(gameState, typesInterdits, couleursCibles);
        if (actionIrrigation != null) return actionIrrigation;

        Action actionManger = gererActionManger(gameState, typesInterdits);
        if (actionManger != null) return actionManger;

        Action actionJardinier = gererActionJardinier(gameState, typesInterdits, couleursCibles);
        if (actionJardinier != null) return actionJardinier;

        Action actionExtension = gererExtensionPlateau(gameState, typesInterdits);
        if (actionExtension != null) return actionExtension;

        return gererReplisStrategiques(gameState, typesInterdits, couleursCibles);
    }

    // --- SOUS-METHODES DE DECISION (Complexité réduite) ---

    private List<Couleur> identifierCouleursCibles() {
        List<Couleur> couleurs = new ArrayList<>();
        for (Objectif objectif : getInventaire().getObjectifs()) {
            couleurs.addAll(objectif.getCouleurs());
        }
        return couleurs;
    }

    private Action gererUrgenceObjectif(GameState gameState, Set<TypeAction> typesInterdits) {
        if (getInventaire().getObjectifs().isEmpty() && !typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {

            if (gameState.getPiochePanda().getTaille() > 0)
                return new PiocherObjectif(TypeObjectif.PANDA);

            if (gameState.getPiocheJardinier().getTaille() > 0)
                return new PiocherObjectif(TypeObjectif.JARDINIER);
        }
        return null;
    }

    private Action gererPoseIrrigation(GameState gameState, Set<TypeAction> typesInterdits, List<Couleur> couleursCibles) {
        if (getInventaire().getNombreCanauxDisponibles() > 0 && !typesInterdits.contains(TypeAction.POSER_IRRIGATION)) {
            return tenterPoserIrrigationUtile(gameState, couleursCibles);
        }
        return null;
    }

    private Action gererActionManger(GameState gameState, Set<TypeAction> typesInterdits) {
        if (!typesInterdits.contains(TypeAction.DEPLACER_PANDA)) {
            return strategiePanda.getStrategiePandaGenerale(gameState, this);
        }
        return null;
    }

    private Action gererActionJardinier(GameState gameState, Set<TypeAction> typesInterdits, List<Couleur> couleursCibles) {
        if (!typesInterdits.contains(TypeAction.DEPLACER_JARDINIER)) {
            return tenterFairePousserPourPanda(gameState, couleursCibles);
        }
        return null;
    }

    private Action gererExtensionPlateau(GameState gameState, Set<TypeAction> typesInterdits) {
        if (!typesInterdits.contains(TypeAction.POSER_PARCELLE)) {
            return tenterPoserParcelle(gameState);
        }
        return null;
    }

    private Action gererReplisStrategiques(GameState gameState, Set<TypeAction> typesInterdits, List<Couleur> couleursCibles) {
        // Option 1 : Prendre une irrigation si on est bloqué par l'eau
        if (!typesInterdits.contains(TypeAction.PRENDRE_IRRIGATION)
                && getInventaire().getNombreCanauxDisponibles() == 0
                && aBesoinDIrrigation(gameState, couleursCibles)) {
            return new ObtenirCanalDirrigation();
        }

        // Option 2 : Piocher des objectifs pour se refaire une main
        if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)
                && getInventaire().getObjectifs().size() < 3
                && gameState.getPiochePanda().getTaille() > 0) {
            return new PiocherObjectif(TypeObjectif.PANDA);
        }
        return null;
    }

    // --- UTILITAIRES TACTIQUES ---

    private Action tenterPoserIrrigationUtile(GameState gameState, List<Couleur> couleursVisees) {
        Plateau plateau = gameState.getPlateau();
        Map<Position, Parcelle> parcelles = plateau.getParcellesMap();

        for (Map.Entry<Position, Parcelle> entry : parcelles.entrySet()) {
            Position pos = entry.getKey();
            Parcelle parcelle = entry.getValue();

            if (!parcelle.estIrriguee() && couleursVisees.contains(parcelle.getCouleur())) {
                for (PositionsRelatives dir : PositionsRelatives.values()) {
                    if (dir == PositionsRelatives.ZERO) continue;
                    Position voisin = pos.add(dir.getPosition());

                    if (plateau.peutPlacerCanal(pos, voisin) && !plateau.aCanalEntre(pos, voisin)) {
                        return new PoserCanalDirrigation(pos, voisin);
                    }
                }
            }
        }
        return null;
    }

    private boolean aBesoinDIrrigation(GameState gameState, List<Couleur> couleursVisees) {
        for (Parcelle p : gameState.getPlateau().getParcellesMap().values()) {
            if (couleursVisees.contains(p.getCouleur()) && !p.estIrriguee()) {
                return true;
            }
        }
        return false;
    }

    private Action tenterFairePousserPourPanda(GameState gameState, List<Couleur> couleursVisees) {
        Plateau plateau = gameState.getPlateau();
        Position posJardinier = gameState.getJardinier().getPosition();
        List<Position> destinations = plateau.getTrajetsLigneDroite(posJardinier);

        for (Position dest : destinations) {
            if (dest.equals(posJardinier)) continue;
            for (Couleur couleurVisee : couleursVisees) {
                Parcelle parcelle = plateau.getParcelle(dest);
                // Condition : Bonne couleur, irriguée, et pas pleine (max 4)
                if (parcelle.getCouleur() == couleurVisee && parcelle.estIrriguee()
                        && parcelle.getNbSectionsSurParcelle() < 4) {
                    return new DeplacerJardinier(gameState.getJardinier(), dest);
                }
            }
        }
        return null;
    }

    private Action tenterPoserParcelle(GameState gameState) {
        PiocheParcelle pioche = gameState.getPiocheParcelle();
        Plateau plateau = gameState.getPlateau();

        if (pioche.getSize() > 0 && !plateau.getEmplacementsDisponibles().isEmpty()) {
            for (Position pos : plateau.getEmplacementsDisponibles()) {
                if (pos.estAdjacent(GrillePlateau.POSITION_ORIGINE))
                    return new PoserParcelle();
            }
            return new PoserParcelle();
        }
        return null;
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