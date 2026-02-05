package fr.cotedazur.univ.polytech.startingpoint.joueurs;


import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.Strategies.StrategieJardinierUnCoup;
import fr.cotedazur.univ.polytech.startingpoint.Strategies.StrategiePandaUnCoup;
import fr.cotedazur.univ.polytech.startingpoint.Strategies.StrategieSabotage;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.*;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Arrangement;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;


import java.util.*;


public class BotTeacher extends Bot {
    private boolean estPremierTour;
    private final Random random;
    private StrategieSabotage strategieSabotage = new StrategieSabotage();
    private StrategiePandaUnCoup strategiePanda = new StrategiePandaUnCoup();
    private StrategieJardinierUnCoup strategieJardinier = new StrategieJardinierUnCoup();


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
                    if(actionJardinier != null) {return actionJardinier;}
            }
            }


        }


        // 5. STRATÉGIE PANDA GLOUTON (Intelligente puis par défaut)
        if (!typesInterdits.contains(TypeAction.DEPLACER_PANDA)) {


            // ETAPE 1 : Identifier les couleurs dont on a besoin pour nos objectifs
            Set<Couleur> couleursRecherchees = new HashSet<>();
            Map<Couleur, Integer> bambousPossedes = getInventaire().getBambous();


            for (Objectif obj : getInventaire().getObjectifs()) {
                if (obj.getType() == TypeObjectif.PANDA) {
                    Map<Couleur, Integer> requis = obj.getObjMap();
                    for (Map.Entry<Couleur, Integer> entry : requis.entrySet()) {
                        Couleur c = entry.getKey();
                        int qteRequise = entry.getValue();
                        int qtePossedee = bambousPossedes.getOrDefault(c, 0);
                        if (qtePossedee < qteRequise) {
                            couleursRecherchees.add(c);
                        }
                    }
                }
            }


            // ETAPE 2 : Chercher d'abord les bambous "utiles"
            if (!couleursRecherchees.isEmpty()) {
                for (Position pos : plateau.getPositionOccupees()) {
                    if (plateau.getNombreDeSectionsAPosition(pos) > 0) {
                        Couleur couleurBambou = plateau.getParcelle(pos).getCouleur();
                        if (couleursRecherchees.contains(couleurBambou)) {
                            if (gameState.getPanda().accessibleEnUnCoupParPanda(gameState, pos)) {
                                return new DeplacerPanda(gameState.getPanda(), pos);
                            }
                        }
                    }
                }
            }


            // ETAPE 3 : Sinon, manger n'importe quoi
            for (Position pos : plateau.getPositionOccupees()) {
                if (plateau.getNombreDeSectionsAPosition(pos) > 0) {
                    if (gameState.getPanda().accessibleEnUnCoupParPanda(gameState, pos)) {
                        return new DeplacerPanda(gameState.getPanda(), pos);
                    }
                }
            }
        }


        // --- 6. CHOIX ALÉATOIRE (Dernier recours) ---
        return choisirActionAleatoire(gameState, typesInterdits);
    }


    // ==========================================================


    private Action choisirActionAleatoire(GameState gameState, Set<TypeAction> typesInterdits) {
        List<TypeAction> actionsPossibles = new ArrayList<>();


        if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF))
            actionsPossibles.add(TypeAction.PIOCHER_OBJECTIF);
        if (!typesInterdits.contains(TypeAction.POSER_PARCELLE) && !gameState.getPiocheParcelle().estVide())
            actionsPossibles.add(TypeAction.POSER_PARCELLE);
        if (!typesInterdits.contains(TypeAction.DEPLACER_JARDINIER))
            actionsPossibles.add(TypeAction.DEPLACER_JARDINIER);
        if (!typesInterdits.contains(TypeAction.DEPLACER_PANDA))
            actionsPossibles.add(TypeAction.DEPLACER_PANDA);


        if (actionsPossibles.isEmpty())
            return null;


        Collections.shuffle(actionsPossibles, random);


        for (TypeAction type : actionsPossibles) {
            switch (type) {
                case PIOCHER_OBJECTIF:
                    int randType = random.nextInt(3);
                    if (randType == 0 && gameState.getPiochePanda().getTaille() > 0)
                        return new PiocherObjectif(TypeObjectif.PANDA);
                    if (randType == 1 && gameState.getPiocheJardinier().getTaille() > 0)
                        return new PiocherObjectif(TypeObjectif.JARDINIER);
                    if (gameState.getPiocheObjectifParcelle().getTaille() > 0)
                        return new PiocherObjectif(TypeObjectif.PARCELLE);
                    break;


                case POSER_PARCELLE:
                    return new PoserParcelle();


                case DEPLACER_JARDINIER:
                    List<Position> posJ = gameState.getPlateau()
                            .getTrajetsLigneDroite(gameState.getJardinier().getPosition());
                    posJ.remove(gameState.getJardinier().getPosition());
                    if (!posJ.isEmpty())
                        return new DeplacerJardinier(gameState.getJardinier(), posJ.get(random.nextInt(posJ.size())));
                    break;


                case DEPLACER_PANDA:
                    List<Position> posP = gameState.getPlateau()
                            .getTrajetsLigneDroite(gameState.getPanda().getPositionPanda());
                    posP.remove(gameState.getPanda().getPositionPanda());
                    if (!posP.isEmpty())
                        return new DeplacerPanda(gameState.getPanda(), posP.get(random.nextInt(posP.size())));
                    break;
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
