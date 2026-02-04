package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.PiocherObjectif;
import fr.cotedazur.univ.polytech.startingpoint.actions.TypeAction;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.SelectionParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Logger;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.*;

/**
 * Bot Équipe - Architecture "Vote Compétitif"
 *
 * Stratégie :
 * 1. Synchronisation : Le chef partage son inventaire avec ses 3 experts (Jardinier, Panda, Architecte).
 * 2. Analyse : Il évalue le contexte (nombre d'objectifs, état du plateau).
 * 3. Vote : Chaque expert propose son meilleur coup avec un score de confiance.
 * 4. Décision : Le chef applique des pondérations dynamiques et choisit l'action avec le meilleur score final.
 */
public class BotEquipe extends Bot {

    // === LES 3 EXPERTS ===
    private final BotJardinier expertJardinier;
    private final BotPanda expertPanda;
    private final BotParcelle expertParcelle;

    // === SYSTÈME DE PONDÉRATION ===
    private final Map<TypeObjectif, Double> multiplicateursConfiance;
    private final Random random = new Random();

    public BotEquipe(String nom) {
        super(nom);

        // Initialisation des sous-bots
        this.expertJardinier = new BotJardinier(nom + "_Jardinier");
        this.expertPanda = new BotPanda(nom + "_Panda");
        this.expertParcelle = new BotParcelle(nom + "_Architecte");

        this.multiplicateursConfiance = new HashMap<>();
        initialiserPonderations();

        // Synchronisation initiale
        synchroniserInventaires();
    }

    private void initialiserPonderations() {
        // Au départ, tout se vaut (1.0 = 100%)
        multiplicateursConfiance.put(TypeObjectif.JARDINIER, 1.0);
        multiplicateursConfiance.put(TypeObjectif.PANDA, 1.0);
        multiplicateursConfiance.put(TypeObjectif.PARCELLE, 1.0);
    }

    /**
     * Synchronise l'inventaire unique du BotEquipe avec ses sous-bots.
     * Ainsi, quand l'expert Panda regarde "son" inventaire, il voit les cartes du chef.
     */
    private void synchroniserInventaires() {
        InventaireJoueur inventairePartage = this.getInventaire();
        expertJardinier.setInventaire(inventairePartage);
        expertPanda.setInventaire(inventairePartage);
        expertParcelle.setInventaire(inventairePartage);
    }

    // =================================================================================
    // LE CŒUR DU SYSTÈME : CHOIX DE L'ACTION
    // =================================================================================

    @Override
    protected Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {
        // 0. Mise à jour des données partagées
        synchroniserInventaires();

        // 1. Mise à jour des priorités stratégiques (Le Chef analyse la situation)
        ajusterPonderationsDynamiques(gameState);

        // 2. Récolte des votes (Chaque expert propose son action)
        List<PropositionAction> propositions = collecterPropositions(gameState, typesInterdits);

        // 3. Sélection du vainqueur
        PropositionAction meilleureProposition = choisirMeilleureProposition(propositions);

        if (meilleureProposition != null) {
            Logger.print(String.format("[%s] Action choisie : %s (Expert: %s, Score: %.2f)",
                    getNom(),
                    meilleureProposition.action.getType(),
                    meilleureProposition.nomExpert,
                    meilleureProposition.scoreFinal));
            return meilleureProposition.action;
        }

        // 4. Filet de sécurité (Fallback) : Si personne ne sait quoi faire, on pioche
        if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
            return new PiocherObjectif(random.nextBoolean() ? TypeObjectif.JARDINIER : TypeObjectif.PANDA);
        }

        Logger.print("[" + getNom() + "] Je suis bloqué et je passe mon tour.");
        return null;
    }

    // =================================================================================
    // COLLECTE ET ÉVALUATION DES PROPOSITIONS
    // =================================================================================

    private List<PropositionAction> collecterPropositions(GameState gameState, Set<TypeAction> typesInterdits) {
        List<PropositionAction> liste = new ArrayList<>();

        // --- AVIS DE L'EXPERT JARDINIER ---
        Action actionJardinier = expertJardinier.choisirUneAction(gameState, typesInterdits);
        if (actionJardinier != null) {
            double confiance = evaluerConfianceJardinier(gameState);
            double poids = multiplicateursConfiance.get(TypeObjectif.JARDINIER);
            liste.add(new PropositionAction(actionJardinier, "Jardinier", confiance, poids));
        }

        // --- AVIS DE L'EXPERT PANDA ---
        Action actionPanda = expertPanda.choisirUneAction(gameState, typesInterdits);
        if (actionPanda != null) {
            double confiance = evaluerConfiancePanda(gameState);
            double poids = multiplicateursConfiance.get(TypeObjectif.PANDA);
            liste.add(new PropositionAction(actionPanda, "Panda", confiance, poids));
        }

        // --- AVIS DE L'ARCHITECTE (PARCELLE) ---
        Action actionParcelle = expertParcelle.choisirUneAction(gameState, typesInterdits);
        if (actionParcelle != null) {
            double confiance = evaluerConfianceParcelle(gameState);
            double poids = multiplicateursConfiance.get(TypeObjectif.PARCELLE);
            liste.add(new PropositionAction(actionParcelle, "Architecte", confiance, poids));
        }

        return liste;
    }

    private PropositionAction choisirMeilleureProposition(List<PropositionAction> propositions) {
        return propositions.stream()
                .max(Comparator.comparingDouble(p -> p.scoreFinal))
                .orElse(null);
    }

    // =================================================================================
    // INTELLIGENCE CONTEXTUELLE (CALCUL DES SCORES DE CONFIANCE)
    // =================================================================================

    private double evaluerConfianceJardinier(GameState gs) {
        double score = 0.5; // Base neutre

        // Critère 1 : Nombre d'objectifs Jardinier en main
        int nbObj = compterObjectifs(TypeObjectif.JARDINIER);
        if (nbObj > 0) score += (0.2 * nbObj);

        // Critère 2 : Opportunités sur le plateau (Parcelles irriguées prêtes à pousser)
        long parcellesIrriguees = gs.getPlateau().getParcellesMap().values().stream()
                .filter(Parcelle::estIrriguee)
                .filter(p -> p.getNbSectionsSurParcelle() < 4) // On peut encore faire pousser
                .count();

        if (parcellesIrriguees > 3) score += 0.2;

        return bornes(score);
    }

    private double evaluerConfiancePanda(GameState gs) {
        double score = 0.5;

        // Critère 1 : Nombre d'objectifs Panda
        int nbObj = compterObjectifs(TypeObjectif.PANDA);
        if (nbObj > 0) score += (0.2 * nbObj);

        // Critère 2 : Nourriture disponible immédiatement (Bambou accessible)
        // On regarde si le panda est à côté de parcelles avec du bambou
        Position posPanda = gs.getPanda().getPositionPanda(); // Correction : getPosition() et non getPositionPanda()
        boolean aManger = false;

        // Scan rapide des lignes droites
        List<Position> destinations = gs.getPlateau().getTrajetsLigneDroite(posPanda);
        for(Position p : destinations) {
            if(!p.equals(posPanda)) {
                Parcelle parcelle = gs.getPlateau().getParcelle(p);
                if(parcelle != null && parcelle.getNbSectionsSurParcelle() > 0) {
                    aManger = true;
                    break;
                }
            }
        }

        if (aManger) score += 0.25; // Très motivant pour un panda !

        return bornes(score);
    }

    private double evaluerConfianceParcelle(GameState gs) {
        double score = 0.5;

        // Critère 1 : Objectifs Parcelles
        int nbObj = compterObjectifs(TypeObjectif.PARCELLE);
        if (nbObj > 0) score += (0.25 * nbObj); // Forte priorité car difficile à faire

        // Critère 2 : Début de partie (Expansion nécessaire)
        if (gs.getPlateau().getParcellesMap().size() < 7) {
            score += 0.3; // On doit construire le terrain !
        }

        return bornes(score);
    }

    private double bornes(double val) {
        return Math.min(2.0, Math.max(0.0, val)); // Cap à 2.0 (200%)
    }

    // =================================================================================
    // ADAPTATION DYNAMIQUE
    // =================================================================================

    private void ajusterPonderationsDynamiques(GameState gameState) {
        int nbJardinier = compterObjectifs(TypeObjectif.JARDINIER);
        int nbPanda = compterObjectifs(TypeObjectif.PANDA);
        int nbParcelle = compterObjectifs(TypeObjectif.PARCELLE);
        int total = nbJardinier + nbPanda + nbParcelle;

        if (total == 0) {
            // Équilibrage par défaut si main vide
            initialiserPonderations();
            return;
        }

        // Calcul des ratios (ex: 60% Jardinier, 20% Panda...)
        double poidsJ = (double) nbJardinier / total;
        double poidsP = (double) nbPanda / total;
        double poidsPa = (double) nbParcelle / total;

        // Application avec un facteur d'amplification (Base 0.8 + Bonus)
        multiplicateursConfiance.put(TypeObjectif.JARDINIER, 0.8 + (poidsJ * 1.5));
        multiplicateursConfiance.put(TypeObjectif.PANDA, 0.8 + (poidsP * 1.5));
        multiplicateursConfiance.put(TypeObjectif.PARCELLE, 0.8 + (poidsPa * 1.5));
    }

    // =================================================================================
    // DÉLÉGATION DES SOUS-TACHES
    // =================================================================================

    /**
     * Pour choisir une parcelle, on fait TOUJOURS confiance à l'Expert Architecte.
     * Son algorithme "Simuler et Noter" est le meilleur pour l'optimisation topologique.
     */
    @Override
    public Parcelle choisirParcelle(SelectionParcelle session, Plateau plateau) {
        return expertParcelle.choisirParcelle(session, plateau);
    }

    @Override
    public Position choisirPosition(Parcelle parcelleChoisie, Plateau plateau) {
        return expertParcelle.choisirPosition(parcelleChoisie, plateau);
    }

    // =================================================================================
    // CLASSES INTERNES ET UTILITAIRES
    // =================================================================================

    private int compterObjectifs(TypeObjectif type) {
        return (int) getInventaire().getObjectifs().stream()
                .filter(o -> o.getType() == type)
                .count();
    }

    /**
     * DTO (Data Transfer Object) pour stocker le résultat d'un vote
     */
    private static class PropositionAction {
        final Action action;
        final String nomExpert;
        final double scoreFinal; // Confiance * Pondération

        PropositionAction(Action action, String nomExpert, double confiance, double ponderation) {
            this.action = action;
            this.nomExpert = nomExpert;
            this.scoreFinal = confiance * ponderation;
        }
    }
}