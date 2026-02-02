package fr.cotedazur.univ.polytech.startingpoint.plateau.pioche;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;

import java.util.ArrayList;
import java.util.List;

public class SelectionParcelle {
    private final List<Parcelle> parcellesAChoisir;
    private final PiocheParcelle pioche;
    private boolean choixEffectue = false;

    public SelectionParcelle(List<Parcelle> parcellesAChoisir, PiocheParcelle piocheRef) {
        this.parcellesAChoisir = new ArrayList<>(parcellesAChoisir);
        this.pioche = piocheRef;
    }

    public List<Parcelle> getParcellesAChoisir() {
        return parcellesAChoisir;
    }

    public Parcelle getFirst() {
        if (!parcellesAChoisir.isEmpty())
            return parcellesAChoisir.getFirst();
        return null;
    }

    public Parcelle validerChoix(Parcelle parcelleChoisie) {
        if (choixEffectue) throw new IllegalStateException("Le choix a déjà été fait");
        if (parcelleChoisie!=null && !parcellesAChoisir.contains(parcelleChoisie)) throw new IllegalArgumentException("Cette parcelle "+parcelleChoisie+" n'est pas dans les options proposées.");

        choixEffectue = true; // Le choix a été fait

        parcellesAChoisir.remove(parcelleChoisie);

        for (Parcelle p : parcellesAChoisir) {
            pioche.remettreEnDessous(p);
        }

        return parcelleChoisie;
    }
}
