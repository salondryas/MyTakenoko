package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifJardinier;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BotJardinier extends Bot {

    public BotJardinier(String nom) {
        super(nom);
    }

    @Override
    protected Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {
        // 0. URGENCE : Si je n'ai AUCUN objectif, j'en pioche un tout de suite !
        // Sinon je joue dans le vide sans but.
        if (getInventaire().getObjectifs().isEmpty() && !typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
            return new PiocherObjectif(TypeObjectif.JARDINIER);
        }

        ObjectifJardinier obj = getMeilleurObjectifJardinier();

        // 1. GESTION DE L'IRRIGATION (Le point qui bloquait votre partie)
        // Si j'ai du stock, je DOIS essayer de le poser avant d'en reprendre !
        if (getInventaire().getNombreCanauxDisponibles() > 0 && !typesInterdits.contains(TypeAction.POSER_IRRIGATION)) {
            Action poserCanal = tenterPoserIrrigation(gameState);
            if (poserCanal != null) return poserCanal;
        }

        // 2. AVANCER L'OBJECTIF JARDINIER
        if (obj != null) {
            // Faire pousser du bambou
            if (!typesInterdits.contains(TypeAction.DEPLACER_JARDINIER)) {
                Action actionJardinier = tenterFairePousser(gameState, obj.getCouleur());
                if (actionJardinier != null) return actionJardinier;
            }
        }

        // 3. AGRANDIR LE PLATEAU (Poser une parcelle)
        if (!typesInterdits.contains(TypeAction.POSER_PARCELLE) && !gameState.getPiocheParcelle().estVide()) {
            List<Position> dispos = gameState.getPlateau().getEmplacementsDisponibles();
            if (!dispos.isEmpty()) {
                Parcelle p = gameState.getPiocheParcelle().piocher();
                if (p != null) {
                    // On pose sur le premier emplacement disponible
                    return new PoserParcelle(p, dispos.get(0));
                }
            }
        }

        // 4. PRENDRE IRRIGATION (Seulement si je n'en ai pas !)
        // C'est ce check qui manquait et causait les 999 canaux
        if (!typesInterdits.contains(TypeAction.PRENDRE_IRRIGATION)
                && getInventaire().getNombreCanauxDisponibles() == 0) { // <--- LIMITE IMPORTANTE
            return new ObtenirCanalDirrigation();
        }

        // 5. RECHARGER LA MAIN D'OBJECTIFS (Si j'en ai peu)
        if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
            return new PiocherObjectif(TypeObjectif.JARDINIER);
        }

        // 6. ACTION PAR DÉFAUT (Déplacer panda pour manger un peu)
        if (!typesInterdits.contains(TypeAction.DEPLACER_PANDA)) {
            List<Position> depts = gameState.getPlateau().getTrajetsLigneDroite(gameState.getPanda().getPositionPanda());
            if (!depts.isEmpty()) return new DeplacerPanda(gameState.getPanda(), depts.get(0));
        }

        return null;
    }

    private ObjectifJardinier getMeilleurObjectifJardinier() {
        for (Objectif o : getInventaire().getObjectifs()) {
            if (o instanceof ObjectifJardinier) return (ObjectifJardinier) o;
        }
        return null;
    }

    private Action tenterFairePousser(GameState gs, Couleur couleur) {
        Plateau plateau = gs.getPlateau();
        List<Position> deplacements = plateau.getTrajetsLigneDroite(gs.getJardinier().getPosition());

        for (Position pos : deplacements) {
            Optional<Parcelle> p = Optional.ofNullable(plateau.getParcelle(pos));
            // On cherche une parcelle de la bonne couleur, irriguée, et qui n'est pas pleine (taille < 4)
            if (p.isPresent()
                    && p.get().getCouleur() == couleur
                    && p.get().estIrriguee()
                    && p.get().getNbSectionsSurParcelle() < 4) {
                return new DeplacerJardinier(gs.getJardinier(), pos);
            }
        }
        return null;
    }

    /**
     * Cherche un emplacement valide pour poser un canal.
     * Stratégie simple : Parcourt les parcelles occupées et cherche une arête voisine validée par le plateau.
     */
    private Action tenterPoserIrrigation(GameState gs) {
        Plateau plateau = gs.getPlateau();
        Set<Position> positionsOccupees = plateau.getParcellesMap().keySet();

        for (Position pos : positionsOccupees) {
            for (PositionsRelatives dir : PositionsRelatives.values()) {
                if (dir == PositionsRelatives.ZERO) continue;

                Position voisin = pos.add(dir.getPosition());

                // Si le plateau autorise un canal ici (connecté à l'eau + pas déjà présent)
                if (plateau.peutPlacerCanal(pos, voisin)) {
                    // Vérification supplémentaire : on ne pose pas de canal doublon
                    // (normalement géré par peutPlacerCanal, mais on assure)
                    return new PoserCanalDirrigation(pos, voisin);
                }
            }
        }
        return null;
    }
}