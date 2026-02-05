package fr.cotedazur.univ.polytech.startingpoint.actions;

public class ActionJouableContext {
    private static final int REGULAR_TOKEN_COUNT = 2; // nombre de jetons regulier par défaut
    private int tokenCount; // nombre de jetons variable

    public ActionJouableContext() {
        this.tokenCount = REGULAR_TOKEN_COUNT;
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
        this.tokenCount = REGULAR_TOKEN_COUNT;
    }

    // ======== autres methodes =========

    public void consumeOneToken(Action action) {
        if (action.getType().getIsActionJeton())
            this.tokenCount--;
    }

}
