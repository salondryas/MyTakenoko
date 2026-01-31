package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifJardinier;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class BotJardinier extends Bot {

    public BotJardinier(String nom) {
        super(nom);
    }

    @Override
    protected Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {

        // 1. ANALYSE : Quel est mon objectif prioritaire ?
        ObjectifJardinier objectifPrioritaire = choisirMeilleurObjectif();

        // 2. STRATEGIE "URGENCE" : Si je n'ai pas d'objectif, je dois en piocher un
        if (objectifPrioritaire == null) {
            // Je vérifie si j'ai le droit de piocher (si je ne l'ai pas déjà fait)
            if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
                return new PiocherObjectif(TypeObjectif.JARDINIER);
            }
            // Si j'ai déjà pioché, tant pis, je continue pour essayer de faire autre chose...
        }

        // On détermine la couleur visée (soit celle de l'objectif, soit VERT par défaut si null)
        Couleur couleurCible = (objectifPrioritaire != null) ? objectifPrioritaire.getCouleur() : Couleur.VERT;

        // 3. STRATEGIE PRINCIPALE : Faire pousser du bambou (Déplacer Jardinier)
        if (!typesInterdits.contains(TypeAction.DEPLACER_JARDINIER)) {
            Action actionJardinier = tenterDeplacerJardinier(gameState, couleurCible);
            if (actionJardinier != null) {
                return actionJardinier;
            }
        }

        // 4. STRATEGIE SECONDAIRE : Poser une parcelle pour agrandir le terrain
        if (!typesInterdits.contains(TypeAction.POSER_PARCELLE)) {
            Action actionPose = tenterPoserParcelle(gameState, couleurCible);
            if (actionPose != null) {
                return actionPose;
            }
        }

        // 5. REPLI STRATEGIQUE (Si les actions principales sont impossibles ou interdites)

        // A. Piocher un objectif (pour en avoir d'avance)
        if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
            return new PiocherObjectif(TypeObjectif.JARDINIER);
        }

        // B. Déplacer le Panda (au moins ça bloque les adversaires ou mange du bambou gênant)
        if (!typesInterdits.contains(TypeAction.DEPLACER_PANDA)) {
            return tenterDeplacerPandaParDefaut(gameState);
        }

        // 6. ECHEC TOTAL : Aucune action valide trouvée
        return null;
    }

    private ObjectifJardinier choisirMeilleurObjectif() {
        for (Objectif obj : this.getInventaire().getObjectifs()) {
            if (obj instanceof ObjectifJardinier) {
                return (ObjectifJardinier) obj;
            }
        }
        return null;
    }

    private Action tenterDeplacerJardinier(GameState gameState, Couleur couleur) {
        Plateau plateau = gameState.getPlateau();
        Position posJardinier = gameState.getJardinier().getPosition();
        List<Position> destinations = plateau.getTrajetsLigneDroite(posJardinier);

        for (Position dest : destinations) {
            Parcelle parcelle = plateau.getParcelle(dest);
            // On cherche une parcelle de la bonne couleur qui peut encore grandir
            if (parcelle.getCouleur() == couleur
                    && parcelle.estIrriguee()
                    && parcelle.getNbSectionsSurParcelle() < 4) {
                return new DeplacerJardinier(gameState.getJardinier(), dest);
            }
        }
        return null;
    }

    private Action tenterPoserParcelle(GameState gameState, Couleur couleurVisee) {
        PiocheParcelle pioche = gameState.getPioche();
        Plateau plateau = gameState.getPlateau();

        // Vérification de base
        if (pioche.getSize() > 0 && !plateau.getEmplacementsDisponibles().isEmpty()) {

            // Dans une version plus avancée, on devrait piocher 3 cartes et choisir.
            Parcelle parcellePiochee = pioche.piocherParcelle();

            if (parcellePiochee != null) {
                List<Position> emplacements = plateau.getEmplacementsDisponibles();

                // Stratégie simple : on prend le premier emplacement disponible
                // Amélioration possible : chercher un emplacement adjacent à la même couleur
                Position meilleurEmplacement = emplacements.get(0);

                return new PoserParcelle(new Parcelle(meilleurEmplacement, parcellePiochee.getCouleur()), meilleurEmplacement);
            }
        }
        return null;
    }

    private Action tenterDeplacerPandaParDefaut(GameState gameState) {
        List<Position> destinations = gameState.getPlateau().getTrajetsLigneDroite(gameState.getPanda().getPositionPanda());
        if (!destinations.isEmpty()) {
            // On va à la première destination possible, juste pour ne pas passer son tour
            return new DeplacerPanda(gameState.getPanda(), destinations.get(0));
        }
        return null;
    }

//    private void jouerAleatoirement(GameState gameState) {
//        Random random = new Random();
//
//        // Petit changement : on évite de piocher JARDINIER si on sait que c'est vide
//        boolean piocheJardinierVide = gameState.getPiocheJardinier().estVide();
//
//        if (random.nextBoolean() && !piocheJardinierVide) {
//            Action action = new PiocherObjectif(TypeObjectif.JARDINIER);
//            System.out.println(getNom() + " (Aléatoire) joue : " + action.toString());
//            action.appliquer(gameState, this);
//        } else {
//            List<Position> dep = gameState.getPlateau().getTrajetsLigneDroite(gameState.getPanda().getPositionPanda());
//            if (!dep.isEmpty()) {
//                Action action = new DeplacerPanda(gameState.getPanda(), dep.get(0));
//                System.out.println(getNom() + " (Aléatoire) joue : " + action.toString());
//                action.appliquer(gameState, this);
//            } else {
//                // Vraiment bloqué ? On pioche un objectif Panda
//                Action action = new PiocherObjectif(TypeObjectif.PANDA);
//                System.out.println(getNom() + " (Aléatoire) joue : " + action.toString());
//                action.appliquer(gameState, this);
//            }
//        }
//    }
}