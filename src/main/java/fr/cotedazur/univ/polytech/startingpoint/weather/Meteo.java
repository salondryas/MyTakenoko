package fr.cotedazur.univ.polytech.startingpoint.weather;

import java.util.List;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.DeplacerPanda;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.AmenagmentAttribuable;
import fr.cotedazur.univ.polytech.startingpoint.elements.movables.Panda;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.QuantityException;

import java.util.ArrayList;

public enum Meteo {
    SOLEIL {
        @Override
        public void apply(GameState gameState, Bot joueur, int tour) {
            // Donne 1 action supplémentaire (3 au lieu de 2)
            joueur.getActionJouableContext().setTokenCount(3);
        }
    },

    PLUIE {
        @Override
        public void apply(GameState gameState, Bot joueur, int tour) {
            // Fait pousser un bambou sur une parcelle irriguée choisie
            Plateau plateau = gameState.getPlateau();
            List<Parcelle> parcellesIrriguees = getParcellesIrrigueesAsList(plateau);

            if (!parcellesIrriguees.isEmpty()) {
                Parcelle choix = joueur.choisirParcelleMeteo(parcellesIrriguees);
                if (choix != null && choix.getNbSectionsSurParcelle() < 4) {
                    choix.pousserBambou();
                }
            }
        }

        private List<Parcelle> getParcellesIrrigueesAsList(Plateau plateau) {
            List<Parcelle> result = new ArrayList<>();
            for (Position pos : plateau.getParcellesIrriguees()) {
                Parcelle p = plateau.getParcelle(pos);
                if (p != null && p.getCouleur() != Couleur.AUCUNE) {
                    result.add(p);
                }
            }
            return result;
        }
    },

    VENT {
        @Override
        public void apply(GameState gameState, Bot joueur, int tour) {
            // AJOUT METEO: Permet de jouer 2 actions identiques
            gameState.setVentActif(true);
        }
    },

    ORAGE {
        @Override
        public void apply(GameState gameState, Bot joueur, int tour) {
            // Déplace le panda et fait manger un bambou
            Panda panda = gameState.getPanda();
            Plateau plateau = gameState.getPlateau();

            List<Parcelle> parcelles = new ArrayList<>(plateau.getParcellesMap().values());
            Parcelle destination = joueur.choisirDestinationPanda(parcelles);

            if (destination != null && destination.getPosition() != null) {
                DeplacerPanda allerManger = new DeplacerPanda(panda, destination.getPosition());
                allerManger.appliquer(gameState, joueur);
            }
        }
    },

    NUAGES {
        @Override
        public void apply(GameState gameState, Bot joueur, int tour) {
            // Donne un aménagement ou permet de choisir une autre météo
            AmenagmentAttribuable amenagementDispo = trouverAmenagementDisponible();

            if (amenagementDispo != null) {
                try {
                    amenagementDispo.pickAmenagement();
                    joueur.recevoirAmenagement(amenagementDispo);
                } catch (QuantityException e) {
                    appliquerMeteoAlternative(gameState, joueur, tour);
                }
            } else {
                appliquerMeteoAlternative(gameState, joueur, tour);
            }
        }

        private AmenagmentAttribuable trouverAmenagementDisponible() {
            for (AmenagmentAttribuable amenagement : AmenagmentAttribuable.values()) {
                if (amenagement.getQuantite() > 0) {
                    return amenagement;
                }
            }
            return null;
        }

        private void appliquerMeteoAlternative(GameState gameState, Bot joueur, int tour) {
            Meteo meteoAlternative = joueur.choisirMeteoAlternative();
            if (meteoAlternative != null &&
                    meteoAlternative != NUAGES &&
                    meteoAlternative != CHOIX_LIBRE) {
                meteoAlternative.apply(gameState, joueur, tour);
            }
        }
    },

    CHOIX_LIBRE {
        @Override
        public void apply(GameState gameState, Bot joueur, int tour) {
            // Le joueur choisit la météo qu'il veut
            Meteo meteoChoisie = joueur.choisirMeteo();

            if (meteoChoisie != null && meteoChoisie != CHOIX_LIBRE) {
                meteoChoisie.apply(gameState, joueur, tour);
            }
        }
    };

    public abstract void apply(GameState gameState, Bot joueur, int tour);
}