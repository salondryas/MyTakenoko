package fr.cotedazur.univ.polytech.startingpoint.objectifs.parcelle;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;

import java.util.ArrayList;
import java.util.List;

import static fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives.*;

public enum Forme {
    LIGNE( List.of(TROIS, ZERO, QUATRE) ),
    C( List.of(DEUX, ZERO, SIX) ),
    TRIANGLE( List.of(ZERO, UN, DEUX) ),
    LOSANGE( List.of(UN, DEUX, TROIS, ZERO) ),
    MIXTE1( List.of(DEUX,QUATRE) ),
    MIXTE2( List.of(ZERO,SIX) ),
    ;

    List<PositionsRelatives> positions;

    Forme(List<PositionsRelatives> positions) {
        this.positions = new ArrayList<>();
        this.positions.addAll(positions);
    }

    public List<PositionsRelatives> getPositions(){
        return positions;
    }
}
