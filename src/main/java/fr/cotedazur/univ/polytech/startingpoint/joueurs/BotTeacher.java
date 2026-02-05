package fr.cotedazur.univ.polytech.startingpoint.joueurs;


import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Strategies.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.*;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;


import java.util.*;


public class BotTeacher extends Bot {
    private boolean estPremierTour;
    private final Random random;
    private StrategieSabotage strategieSabotage = new StrategieSabotage();
    private StrategiePandaUnCoup strategiePanda = new StrategiePandaUnCoup();
    private StrategieJardinierUnCoup strategieJardinier = new StrategieJardinierUnCoup();
    private StrategiePandaGenerale strategiePandaGenerale = new StrategiePandaGenerale();
    private StrategieAleatoire strategieAleatoire = new StrategieAleatoire();

    public BotTeacher(String nom) {
        super(nom);
        this.estPremierTour = true;
        this.random = new Random();
    }

    @Override
    public Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {
        // 1. PREMIER TOUR
        if (estPremierTour) {
            if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
                if (gameState.getPiochePanda().getTaille() > 0) {
                    return new PiocherObjectif(TypeObjectif.PANDA);
                }
            }
            if (!typesInterdits.contains(TypeAction.PRENDRE_IRRIGATION)) {
                estPremierTour = false;
                return new ObtenirCanalDirrigation();
            }
        }

        Plateau plateau = gameState.getPlateau();
        // 2. SABOTAGE INTELLIGENT (Bloquer l'adversaire sans se tirer une balle dans le pied)
        Action actionSabotage = strategieSabotage.getActionSabotage(gameState, this);
        if (actionSabotage != null) {
            return actionSabotage;
        }

        // 3. REMPLISSAGE DE MAIN
        if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF) && getInventaire().getObjectifs().size() < 5) {
            if (gameState.getPiochePanda().getTaille() > 0)
                return new PiocherObjectif(TypeObjectif.PANDA);
            else if (gameState.getPiocheJardinier().getTaille() > 0) {
                return new PiocherObjectif(TypeObjectif.JARDINIER);
            } else
                return new PiocherObjectif(TypeObjectif.PARCELLE);
        }

        // 4. TENTATIVE DE RÉUSSITE D'OBJECTIFS
        for (Objectif objectifEnMain : getInventaire().getObjectifs()) {

            // A. OBJECTIF PANDA
            if (objectifEnMain.getType() == TypeObjectif.PANDA) {
                if (typesInterdits.contains(TypeAction.DEPLACER_PANDA))
                    continue;
                Action strategiePandaUnCoup = strategiePanda.getStrategiePandaUnCoup(gameState, this, objectifEnMain);
                if (strategiePandaUnCoup != null) {
                    return strategiePandaUnCoup;
                }
            }

            // B. OBJECTIF JARDINIER
            if (objectifEnMain.getType() == TypeObjectif.JARDINIER) {
                if (!typesInterdits.contains(TypeAction.DEPLACER_JARDINIER)) {
                    Action actionJardinier = strategieJardinier.getStrategieJardinierUnCoup(gameState, this, objectifEnMain);
                    if (actionJardinier != null) {
                        return actionJardinier;
                    }
                }
            }
        }

        // 5. STRATÉGIE PANDA GLOUTON (Intelligente puis par défaut)
        if (!typesInterdits.contains(TypeAction.DEPLACER_PANDA)) {
            Action actionPanda = strategiePandaGenerale.getStrategiePandaGenerale(gameState, this);
            if (actionPanda != null) {
                return actionPanda;
            }
        }

        // --- 6. CHOIX ALÉATOIRE (Dernier recours) ---
        return strategieAleatoire.getActionAleatoire(gameState, typesInterdits);
    }

    /// =================== METEO ===================

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
