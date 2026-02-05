package fr.cotedazur.univ.polytech.startingpoint.actions;

public class ActionJouableContext {
    private static final int regularTokenCount = 2; // nombre de jetons regulier par défaut
    private int tokenCount; // nombre de jetons variable

    public ActionJouableContext() {
        this.tokenCount = regularTokenCount;
    }

    // ======= getters ============

    public int getTokenCount() {
        return tokenCount;
    }

    // ======= setters ============

    public void setTokenCount(int tokenCount) {
        this.tokenCount = tokenCount;
    }

    public void resetTokenCount() {
        this.tokenCount = regularTokenCount;
    }

    // ======== autres methodes =========

    public void consumeOneToken(Action action) {
        if (action.getType().getIsActionJeton())
            this.tokenCount--;
    }

}
