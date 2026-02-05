package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.TypeAction;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.elements.pioche.SelectionParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.*;

public class EquipeStrategie {

    private final Bot botChef;

    // Les spécialistes
    private final BotPanda expertPanda;
    private final BotJardinier expertJardinier;
    private final BotParcelle expertArchitecte;

    // Pondérations (Confiance accordée à chaque expert)
    private static final double POIDS_PANDA = 1.0;
    private static final double POIDS_JARDINIER = 0.9;
    private static final double POIDS_ARCHITECTE = 0.8;

    public EquipeStrategie(Bot botChef) {
        this.botChef = botChef;
        // On initialise les sous-bots avec des noms spécifiques pour le debug
        this.expertPanda = new BotPanda("ExpertPanda");
        this.expertJardinier = new BotJardinier("ExpertJardinier");
        this.expertArchitecte = new BotParcelle("ExpertArchitecte");
    }

    /**
     * Synchronise les inventaires des sous-bots avec celui du chef.
     * C'est CRUCIAL car les sous-bots doivent savoir ce que l'équipe possède.
     */
    public void synchroniserInventaires() {
        InventaireJoueur inventairePartage = botChef.getInventaire();
        expertPanda.setInventaire(inventairePartage);
        expertJardinier.setInventaire(inventairePartage);
        expertArchitecte.setInventaire(inventairePartage);
    }

    public Action arbitrerLesPropositions(GameState gameState, Set<TypeAction> typesInterdits) {
        synchroniserInventaires();
        List<PropositionAction> votes = new ArrayList<>();

        // 1. Demander l'avis de chaque expert
        votes.add(consulterExpert(expertPanda, POIDS_PANDA, gameState, typesInterdits));
        votes.add(consulterExpert(expertJardinier, POIDS_JARDINIER, gameState, typesInterdits));
        votes.add(consulterExpert(expertArchitecte, POIDS_ARCHITECTE, gameState, typesInterdits));

        // 2. Trier pour trouver le meilleur score
        votes.sort(Comparator.comparingDouble(PropositionAction::getScoreFinal).reversed());

        // 3. Retourner la meilleure action non nulle
        for (PropositionAction prop : votes) {
            if (prop.action != null) {
                return prop.action;
            }
        }
        return null;
    }

    private PropositionAction consulterExpert(Bot expert, double poids, GameState gs, Set<TypeAction> interdits) {
        // Pour l'instant, on utilise une heuristique simple via choisirUneAction
        // Dans une version très avancée, les bots experts retourneraient un score.
        // Ici, on simule un score basé sur le type de bot (Panda priorise Panda, etc.)

        // On utilise la réflexion ou une méthode protected exposée pour appeler choisirUneAction
        // NOTE : Comme choisirUneAction est protected, BotEquipe (dans le même package) peut l'appeler sur les instances.
        // Mais ici nous sommes dans une classe helper. L'astuce est de passer par le BotEquipe ou de rendre la méthode package-private.
        // Solution propre : Les Bots Spécialisés doivent avoir une méthode publique pour "proposer".
        // Pour ce refactor, on suppose que choisirUneAction est accessible (package-protected).

        Action action = expert.choisirUneAction(gs, interdits);

        // Calcul du score (Simplifié)
        double scoreBrut = (action != null) ? 10.0 : 0.0;

        return new PropositionAction(action, expert.getNom(), scoreBrut, poids);
    }

    // --- Délégations Spécifiques ---

    public Parcelle choisirParcelle(SelectionParcelle session, Plateau plateau) {
        // On laisse l'architecte décider
        return expertArchitecte.choisirParcelle(session, plateau);
    }

    public Position choisirPosition(Parcelle parcelle, Plateau plateau) {
        // On laisse l'architecte décider
        return expertArchitecte.choisirPosition(parcelle, plateau);
    }

    // --- Classe Interne pour le Vote ---

    public static class PropositionAction {
        Action action;
        String nomExpert;
        double scoreFinal;

        public PropositionAction(Action action, String nomExpert, double scoreBrut, double ponderation) {
            this.action = action;
            this.nomExpert = nomExpert;
            this.scoreFinal = scoreBrut * ponderation;
        }

        public double getScoreFinal() { return scoreFinal; }
    }
}