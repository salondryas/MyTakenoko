package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.GrillePlateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives; // IMPORTANT
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Random;

public class BotPanda extends Bot {
    private final Random random; // pour les choix de meteo

    public BotPanda(String nom) {
        super(nom);
        this.random = new Random();
    }

    @Override
    protected Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {

        // 0. URGENCE : Si je n'ai AUCUN objectif
        if (getInventaire().getObjectifs().isEmpty() && !typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
            if (gameState.getPiochePanda().getTaille() > 0) {
                return new PiocherObjectif(TypeObjectif.PANDA);
            } else if (gameState.getPiocheJardinier().getTaille() > 0) {
                return new PiocherObjectif(TypeObjectif.JARDINIER);
            }
        }

        // 1. ANALYSE : Lister les couleurs dont j'ai besoin
        List<Couleur> couleursCibles = new ArrayList<>();
        for (Objectif objectif : getInventaire().getObjectifs()) {
            couleursCibles.addAll(objectif.getCouleurs());
        }

        // 2. LOGISTIQUE : Poser une irrigation si j'en ai une en stock (Action Gratuite
        // ou prioritaire)
        // Cela permet de débloquer la pousse du bambou immédiatement après
        if (getInventaire().getNombreCanauxDisponibles() > 0 && !typesInterdits.contains(TypeAction.POSER_IRRIGATION)) {
            Action actionIrriguer = tenterPoserIrrigationUtile(gameState, couleursCibles);
            if (actionIrriguer != null)
                return actionIrriguer;
        }

        // 3. STRATEGIE PRINCIPALE; PLAN A: Manger du bambou
        if (!typesInterdits.contains(TypeAction.DEPLACER_PANDA)) {
            Action actionManger = tenterMangerBambou(gameState, couleursCibles);
            if (actionManger != null)
                return actionManger;
        }

        // 4. PLAN B : Faire pousser du bambou (nécessite de l'eau)
        if (!typesInterdits.contains(TypeAction.DEPLACER_JARDINIER)) {
            Action actionJardinier = tenterFairePousserPourPanda(gameState, couleursCibles);
            if (actionJardinier != null)
                return actionJardinier;
        }

        // 5. PLAN C : Poser une parcelle (Extension du terrain)
        if (!typesInterdits.contains(TypeAction.POSER_PARCELLE)) {
            Action actionPose = tenterPoserParcelle(gameState, couleursCibles);
            if (actionPose != null)
                return actionPose;
        }

        // 6. DERNIER RECOURS (Option 1) : Prendre une irrigation si on est bloqué par
        // le manque d'eau
        if (!typesInterdits.contains(TypeAction.PRENDRE_IRRIGATION)
                && getInventaire().getNombreCanauxDisponibles() == 0) {
            if (aBesoinDIrrigation(gameState, couleursCibles)) {
                return new ObtenirCanalDirrigation();
            }
        }

        // 7. DERNIER RECOURS (Option 2) : Piocher objectifs
        if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF) && getInventaire().getObjectifs().size() < 3) {
            if (gameState.getPiochePanda().getTaille() > 0)
                return new PiocherObjectif(TypeObjectif.PANDA);
        }

        return null;
    }

    /**
     * Cherche à poser un canal de l'inventaire pour irriguer une parcelle de la
     * bonne couleur.
     */
    private Action tenterPoserIrrigationUtile(GameState gameState, List<Couleur> couleursVisees) {
        Plateau plateau = gameState.getPlateau();
        Map<Position, Parcelle> parcelles = plateau.getParcellesMap();

        // On parcourt toutes les parcelles du plateau
        for (Map.Entry<Position, Parcelle> entry : parcelles.entrySet()) {
            Position pos = entry.getKey();
            Parcelle parcelle = entry.getValue();

            // Si c'est une parcelle sèche de la couleur qu'on veut
            if (!parcelle.estIrriguee() && couleursVisees.contains(parcelle.getCouleur())) {

                // On regarde autour d'elle si on peut placer un canal
                for (PositionsRelatives dir : PositionsRelatives.values()) {
                    if (dir == PositionsRelatives.ZERO)
                        continue;

                    Position voisin = pos.add(dir.getPosition());

                    // Si on peut mettre un canal ici et qu'il n'y en a pas déjà
                    if (plateau.peutPlacerCanal(pos, voisin) && !plateau.aCanalEntre(pos, voisin)) {
                        return new PoserCanalDirrigation(pos, voisin);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Vérifie s'il existe des parcelles de la bonne couleur sur le plateau mais
     * qu'elles sont sèches.
     * Si oui, cela vaut le coup de prendre une irrigation.
     */
    private boolean aBesoinDIrrigation(GameState gameState, List<Couleur> couleursVisees) {
        for (Parcelle p : gameState.getPlateau().getParcellesMap().values()) {
            if (couleursVisees.contains(p.getCouleur()) && !p.estIrriguee()) {
                return true; // On a trouvé une parcelle utile mais sèche -> Besoin d'eau !
            }
        }
        return false;
    }

    private Action tenterMangerBambou(GameState gameState, List<Couleur> couleursVisees) {
        Plateau plateau = gameState.getPlateau();
        Position posPanda = gameState.getPanda().getPositionPanda();
        List<Position> destinations = plateau.getTrajetsLigneDroite(posPanda);

        for (Position dest : destinations) {
            if (dest.equals(posPanda))
                continue;
            for (Couleur couleur : couleursVisees) {
                Parcelle parcelle = plateau.getParcelle(dest);
                if (parcelle.getCouleur() == couleur && plateau.getNombreDeSectionsAPosition(dest) > 0) {
                    return new DeplacerPanda(gameState.getPanda(), dest);
                }
            }
        }
        return null;
    }

    private Action tenterFairePousserPourPanda(GameState gameState, List<Couleur> couleursVisees) {
        Plateau plateau = gameState.getPlateau();
        Position posJardinier = gameState.getJardinier().getPosition();
        List<Position> destinations = plateau.getTrajetsLigneDroite(posJardinier);

        for (Position dest : destinations) {
            if (dest.equals(posJardinier))
                continue;
            for (Couleur couleurVisee : couleursVisees) {
                Parcelle parcelle = plateau.getParcelle(dest);
                if (parcelle.getCouleur() == couleurVisee && parcelle.estIrriguee()
                        && parcelle.getNbSectionsSurParcelle() < 4) {
                    return new DeplacerJardinier(gameState.getJardinier(), dest);
                }
            }
        }
        return null;
    }

    private Action tenterPoserParcelle(GameState gameState, List<Couleur> couleursVisees) {
        PiocheParcelle pioche = gameState.getPiocheParcelle();
        Plateau plateau = gameState.getPlateau();

        if (pioche.getSize() > 0 && !plateau.getEmplacementsDisponibles().isEmpty()) {
            Parcelle parcellePiochee = pioche.piocherParcelle();
            if (parcellePiochee != null) {
                List<Position> emplacements = plateau.getEmplacementsDisponibles();

                // On essaie de poser près de l'étang (0,0) pour irriguer auto
                for (Position pos : emplacements) {
                    if (pos.estAdjacent(GrillePlateau.POSITION_ORIGINE)) {
                        return new PoserParcelle();
                    }
                }

                return new PoserParcelle();
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