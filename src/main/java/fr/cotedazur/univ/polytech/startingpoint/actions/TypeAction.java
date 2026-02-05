package fr.cotedazur.univ.polytech.startingpoint.actions;

public enum TypeAction {
    DEPLACER_PANDA(true),
    DEPLACER_JARDINIER(true),
    POSER_PARCELLE(true),
    PIOCHER_OBJECTIF(true),
    PRENDRE_IRRIGATION(true),
    POSER_IRRIGATION(false);

    private final boolean isActionJeton;

    TypeAction(boolean isActionJeton) {
        this.isActionJeton = isActionJeton;
    }

    public boolean getIsActionJeton() {
        return isActionJeton;
    }
}