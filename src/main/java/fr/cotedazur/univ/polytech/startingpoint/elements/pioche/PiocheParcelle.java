package fr.cotedazur.univ.polytech.startingpoint.elements.pioche;

import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.TuileType;
import fr.cotedazur.univ.polytech.startingpoint.elements.amenagements.Amenagement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PiocheParcelle {
    private final List<Parcelle> piocheDeParcelle;

    public PiocheParcelle() {
        this.piocheDeParcelle = new ArrayList<>();
        remplirPioche();
        melangerPioche();
    }

    public void remplirPioche() {
        for (TuileType definition : TuileType.values()) {
            for (int i = 0; i < definition.getNombreExemplaires(); i++) {
                // 1. On utilise le constructeur existant de Parcelle
                Parcelle parcelle = new Parcelle(definition.getCouleur());

                // 2. Si la carte doit avoir un aménagement (ex: Bassin)
                if (definition.getGenerateurAmenagement() != null) {
                    // On crée l'aménagement en lui passant la parcelle (le constructeur de Bassin l'attend)
                    Amenagement amenagement = definition.getGenerateurAmenagement().apply(parcelle);

                    // On l'attache à la parcelle (votre méthode existante dans Parcelle.java)
                    parcelle.fetchAmenagementAcqui(amenagement);
                }

                piocheDeParcelle.add(parcelle);
            }
        }
    }

    public void melangerPioche() {
        Collections.shuffle(piocheDeParcelle);
    }

    public void remettreEnDessous(Parcelle parcelle) {
        piocheDeParcelle.addLast(parcelle);
    }

    public Parcelle piocher() {
        if (estVide()) return null;
        return piocheDeParcelle.removeFirst();
    }

    // Alias pour compatibilité
    public Parcelle piocherParcelle() { return piocher(); }
    public void ajouter(Parcelle p) { piocheDeParcelle.add(p); }
    public boolean estVide() { return piocheDeParcelle.isEmpty(); }
    public int size() { return piocheDeParcelle.size(); }
    public int getSize() { return size(); }

    @Override
    public String toString() { return "Pioche: " + size() + " cartes"; }
}