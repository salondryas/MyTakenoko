package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.StockSectionBambou;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.QuantityException;

import java.util.HashMap; // N'oublie pas cet import !
import java.util.List;
import java.util.Map;

public class ObjectifPanda extends Objectif {
    // On garde la liste pour l'affichage ou des getters simples
    private final List<Couleur> couleurs;

    // Cette Map sert à stocker "2 VERTS" ou "1 VERT, 1 ROSE, 1 JAUNE"
    // CORRECTION 1 : On initialise la HashMap tout de suite pour éviter le
    // NullPointer
    private final Map<Couleur, Integer> objPanda = new HashMap<>();

    // --- CONSTRUCTEUR 1 : Jeu ---
    public ObjectifPanda(CartePanda carte) {
        super(carte.getPoints(), TypeObjectif.PANDA);
        this.couleurs = carte.getCouleurs();

        // Remplissage intelligent de la Map
        initialiserMapBesoins(this.couleurs);
    }

    // --- CONSTRUCTEUR 2 : Tests ---
    public ObjectifPanda(int points, List<Couleur> couleurs) {
        super(points, TypeObjectif.PANDA);
        this.couleurs = couleurs;

        // Remplissage intelligent de la Map
        initialiserMapBesoins(this.couleurs);
    }

    // Méthode utilitaire pour éviter de dupliquer le code dans les 2 constructeurs
    private void initialiserMapBesoins(List<Couleur> listeCouleurs) {
        for (Couleur c : listeCouleurs) {
            // CORRECTION : "merge" remplace ton bloc if/else de manière plus sûre
            // Si la couleur existe, on ajoute 1. Sinon on met 1.
            objPanda.merge(c, 1, Integer::sum);
        }
    }

    @Override
    public boolean valider(GameState gameState, Bot bot) {
        Map<Couleur, Integer> bambousDuBot = bot.getInventaire().getBambous();

        // 1. ÉTAPE DE VÉRIFICATION
        // On parcourt les BESOINS de l'objectif (ex: Vert -> 1, Rose -> 1)
        for (Map.Entry<Couleur, Integer> entry : objPanda.entrySet()) {
            Couleur couleurRequise = entry.getKey();
            int quantiteRequise = entry.getValue();

            // On regarde combien le bot en a (0 par défaut s'il n'en a pas)
            int quantitePossedee = bambousDuBot.getOrDefault(couleurRequise, 0);

            // Si pour une seule couleur, il n'en a pas assez, c'est perdu
            if (quantitePossedee < quantiteRequise) {
                return false;
            }
        }

        // 2. ÉTAPE DE CONSOMMATION (Si on arrive ici, c'est qu'on a tout ce qu'il faut)
        // CORRECTION 3 : On doit retirer les bambous de l'inventaire !
        for (Map.Entry<Couleur, Integer> entry : objPanda.entrySet()) {
            Couleur couleur = entry.getKey();
            int quantite = entry.getValue();

            for (int i = 0; i < quantite; i++) {
                replaceBambooSection(couleur);
                bot.getInventaire().retirerBambou(couleur);
            }
        }

        return true;
    }

    private void replaceBambooSection(Couleur section_colour) { // pour une seule couleur
        switch (section_colour) {
            case ROSE:
                try {
                    StockSectionBambou.ROSES.addToQuantity();
                } catch (QuantityException e) {
                    e.getMessage();
                }
                break;

            case VERT:
                try {
                    StockSectionBambou.VERTES.addToQuantity();
                } catch (QuantityException e) {
                    e.getMessage();
                }
                break;

            default:
                try {
                    StockSectionBambou.JAUNES.addToQuantity();
                } catch (QuantityException e) {
                    e.getMessage();
                }
                break;
        }
    }

    @Override
    public List<Couleur> getCouleurs() {
        return this.couleurs;
    }

    @Override
    public Map<Couleur, Integer> getObjMap() {
        return objPanda;
    }

    @Override
    public String toString() {
        return "Objectif Panda : Manger " + objPanda + " (" + super.getPoints() + "pts)";
    }
}