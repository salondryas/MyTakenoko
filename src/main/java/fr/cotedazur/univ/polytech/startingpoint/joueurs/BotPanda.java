package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifPanda;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.List;
import java.util.Set;

public class BotPanda extends Bot {

    public BotPanda(String nom) {
        super(nom);
    }

    @Override
    protected Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {

        // 1. ANALYSE : Quel est mon objectif prioritaire ?
        ObjectifPanda objectifPrioritaire = choisirMeilleurObjectif();

        // 2. URGENCE : Si je n'ai rien en main, je pioche
        if (objectifPrioritaire == null) {
            if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
                return new PiocherObjectif(TypeObjectif.PANDA);
            }
        }

        // Couleur cible (Vert par défaut si pas d'objectif)
        Couleur couleurCible = (objectifPrioritaire != null) ? objectifPrioritaire.getCouleur() : Couleur.VERT;

        // 3. STRATEGIE PRINCIPALE : Manger du bambou (Déplacer Panda)
        if (!typesInterdits.contains(TypeAction.DEPLACER_PANDA)) {
            // A. Essayer de manger la BONNE couleur
            Action actionMangerCible = tenterMangerBambou(gameState, couleurCible);
            if (actionMangerCible != null) return actionMangerCible;

            // B. (Optionnel) Sinon, manger n'importe quoi (pour stocker ou embêter les autres)
            Action actionMangerAutre = tenterMangerNimporteQuoi(gameState);
            if (actionMangerAutre != null) return actionMangerAutre;
        }

        // 4. PLAN B : Faire pousser du bambou (Déplacer Jardinier) pour le manger au prochain tour
        if (!typesInterdits.contains(TypeAction.DEPLACER_JARDINIER)) {
            Action actionJardinier = tenterFairePousserPourPanda(gameState, couleurCible);
            if (actionJardinier != null) return actionJardinier;
        }

        // 5. PLAN C : Poser une parcelle (pour avoir plus de terrain de chasse)
        if (!typesInterdits.contains(TypeAction.POSER_PARCELLE)) {
            Action actionPose = tenterPoserParcelle(gameState, couleurCible);
            if (actionPose != null) return actionPose;
        }

        // 6. DERNIERS RECOURS
        if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
            return new PiocherObjectif(TypeObjectif.PANDA);
        }

        // Vraiment bloqué ? On bouge le panda n'importe où sans manger
        if (!typesInterdits.contains(TypeAction.DEPLACER_PANDA)) {
            List<Position> pos = gameState.getPlateau().getTrajetsLigneDroite(gameState.getPanda().getPositionPanda());
            if (!pos.isEmpty()) return new DeplacerPanda(gameState.getPanda(), pos.get(0));
        }

        return null;
    }

    // --- METHODES UTILITAIRES ---

    private ObjectifPanda choisirMeilleurObjectif() {
        for (Objectif obj : this.getInventaire().getObjectifs()) {
            if (obj instanceof ObjectifPanda) {
                return (ObjectifPanda) obj;
            }
        }
        return null;
    }

    private Action tenterMangerBambou(GameState gameState, Couleur couleurVisee) {
        Plateau plateau = gameState.getPlateau();
        Position posPanda = gameState.getPanda().getPositionPanda();
        List<Position> destinations = plateau.getTrajetsLigneDroite(posPanda);

        for (Position dest : destinations) {
            Parcelle parcelle = plateau.getParcelle(dest);
            // On veut : Bonne couleur ET il y a du bambou à manger (> 0)
            if (parcelle.getCouleur() == couleurVisee
                    && plateau.getNombreDeSectionsAPosition(dest) > 0) {
                return new DeplacerPanda(gameState.getPanda(), dest);
            }
        }
        return null;
    }

    private Action tenterMangerNimporteQuoi(GameState gameState) {
        Plateau plateau = gameState.getPlateau();
        List<Position> destinations = plateau.getTrajetsLigneDroite(gameState.getPanda().getPositionPanda());

        for (Position dest : destinations) {
            // On mange le premier truc qui vient
            if (plateau.getNombreDeSectionsAPosition(dest) > 0) {
                return new DeplacerPanda(gameState.getPanda(), dest);
            }
        }
        return null;
    }

    private Action tenterFairePousserPourPanda(GameState gameState, Couleur couleurVisee) {
        Plateau plateau = gameState.getPlateau();
        Position posJardinier = gameState.getJardinier().getPosition();
        List<Position> destinations = plateau.getTrajetsLigneDroite(posJardinier);

        for (Position dest : destinations) {
            Parcelle parcelle = plateau.getParcelle(dest);
            // On cherche à arroser une parcelle de la couleur du Panda
            // Elle doit être irriguée pour que ça pousse
            if (parcelle.getCouleur() == couleurVisee
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

        if (pioche.getSize() > 0 && !plateau.getEmplacementsDisponibles().isEmpty()) {
            Parcelle parcellePiochee = pioche.piocherParcelle();
            if (parcellePiochee != null) {
                List<Position> emplacements = plateau.getEmplacementsDisponibles();
                Position meilleurEmplacement = emplacements.get(0);
                return new PoserParcelle(new Parcelle(meilleurEmplacement, parcellePiochee.getCouleur()), meilleurEmplacement);
            }
        }
        return null;
    }
}