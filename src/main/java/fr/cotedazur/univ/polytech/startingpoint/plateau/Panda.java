package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import java.util.List;
import static fr.cotedazur.univ.polytech.startingpoint.plateau.GrillePlateau.POSITION_ORIGINE;

public class Panda {
    private Position positionActuellePanda;
    private final Plateau plateau;
    private boolean canEAT;

    // CORRECTION : Constructeur unique et complet
    public Panda(Plateau plateau) {
        this.plateau = plateau;
        this.positionActuellePanda = GrillePlateau.POSITION_ORIGINE;
        this.canEAT = true;
    }

    public Position getPositionPanda() {
        return positionActuellePanda;
    }

    public Position getPosition() {
        return positionActuellePanda;
    } // Alias

    public void setPositionPanda(Position position) {
        this.positionActuellePanda = position;
        this.canEAT = true;
    }

    public boolean mangerBambou(Position destination, Plateau plateauPasse) {
        Plateau p = (plateauPasse != null) ? plateauPasse : this.plateau;
        Parcelle parcelle = p.getParcelle(positionActuellePanda);

        if (parcelle != null && parcelle.getNbSectionsSurParcelle() > 1 && canEAT) { // on met 1 pour eviter de manger
                                                                                     // le socle sans quoi le bambou ne
                                                                                     // pousserait plus
            parcelle.getBambou().retirerSection();
            return true;
        }
        return false;
    }

    // CORRECTION : Méthode requise par BotTeacher
    public boolean accessibleEnUnCoupParPanda(GameState gameState, Position cible) {
        if (this.positionActuellePanda.equals(cible))
            return false;
        List<Position> possibles = plateau.getTrajetsLigneDroite(this.positionActuellePanda);
        return possibles.contains(cible);
    }

    public void cannotEat() {
        canEAT = false;
    }
}