package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifJardinier;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.PiocheObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.*;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BotJardinierTest {

    BotJardinier bot;
    GameState gameStateMock;
    Plateau plateauMock;
    Jardinier jardinierMock;
    Panda pandaMock;
    PiocheParcelle piocheParcelleMock;
    PiocheObjectif piocheObjectifMock;

    @BeforeEach
    void setUp() {
        // 1. On crée le Bot (le vrai sujet du test.)
        bot = new BotJardinier("BotJardinierTest");

        // 2. On Mock tout l'environnement (le monde faux)
        gameStateMock = mock(GameState.class);
        plateauMock = mock(Plateau.class);
        jardinierMock = mock(Jardinier.class);
        pandaMock = mock(Panda.class);
        piocheParcelleMock = mock(PiocheParcelle.class);
        piocheObjectifMock = mock(PiocheObjectif.class);

        // 3. On connecte les mocks entre eux
        when(gameStateMock.getPlateau()).thenReturn(plateauMock);
        when(gameStateMock.getJardinier()).thenReturn(jardinierMock);
        when(gameStateMock.getPanda()).thenReturn(pandaMock);
        when(gameStateMock.getPioche()).thenReturn(piocheParcelleMock);

        // Configuration par défaut pour éviter les NullPointer
        when(plateauMock.getEmplacementsDisponibles()).thenReturn(Collections.emptyList());
        when(plateauMock.getTrajetsLigneDroite(any())).thenReturn(Collections.emptyList());
    }

    @Test
    void testStrat1_PrioriteDeplacerJardinier() {
        // SCÉNARIO : Le bot a un objectif VERT. Une parcelle VERTE irriguée est accessible.
        // ATTENDU : Il doit déplacer le jardinier dessus.

        // 1. On donne un objectif au bot
        ObjectifJardinier objVert = new ObjectifJardinier(Couleur.VERT, 4, 5);
        bot.getInventaire().ajouterObjectif(objVert);

        // 2. On prépare le terrain (Plateau Mocké)
        Position posJardinier = new Position(0,0);
        Position posCible = new Position(1,0); // La case où il doit aller

        when(jardinierMock.getPosition()).thenReturn(posJardinier);
        when(plateauMock.getTrajetsLigneDroite(posJardinier)).thenReturn(List.of(posCible));

        // La parcelle cible est VERTE, IRRIGUÉE et a BAMBOU < 4.
        Parcelle parcelleCible = mock(Parcelle.class);
        when(parcelleCible.getCouleur()).thenReturn(Couleur.VERT);
        when(parcelleCible.estIrriguee()).thenReturn(true);
        when(parcelleCible.getNbSectionsSurParcelle()).thenReturn(1); // Il y a de la place pour pousser
        when(plateauMock.getParcelle(posCible)).thenReturn(parcelleCible);

        // 3. Action
        bot.jouer(gameStateMock);

        // 4. Vérification
        // On vérifie que le jardinier a bien reçu l'ordre de bouger vers (1,0)
        verify(jardinierMock, times(1)).setPosition(posCible);

        // On vérifie qu'il n'a pas essayé de piocher (Plan B).
        verify(piocheParcelleMock, never()).piocherParcelle();
    }

    @Test
    void testStrat2_PlanB_PoserParcelle() {
        // SCÉNARIO : Objectif VERT. Aucune parcelle verte sur le plateau.
        // ATTENDU : Il doit piocher une parcelle et la poser.

        // 1. Objectif Vert
        bot.getInventaire().ajouterObjectif(new ObjectifJardinier(Couleur.VERT, 4, 5));

        // 2. Pas de trajet utile (Plan A échoue).
        when(plateauMock.getTrajetsLigneDroite(any())).thenReturn(Collections.emptyList());

        // 3. La pioche contient une parcelle VERTE (Plan B possible).
        Parcelle parcellePiochee = new Parcelle(Couleur.VERT);
        when(piocheParcelleMock.getSize()).thenReturn(10);
        when(piocheParcelleMock.piocherParcelle()).thenReturn(parcellePiochee);

        // Il y a de la place pour poser
        Position posDispo = new Position(1, 0);
        when(plateauMock.getEmplacementsDisponibles()).thenReturn(List.of(posDispo));

        // 4. Action
        bot.jouer(gameStateMock);

        // 5. Vérification
        // On vérifie qu'il a pioché
        verify(piocheParcelleMock, times(1)).piocherParcelle();
        // On vérifie qu'il a posé la parcelle sur le plateau
        verify(plateauMock, times(1)).placerParcelle(any(Parcelle.class), eq(posDispo));
    }

    @Test
    void testStrat3_RienEnMain_PiocherObjectif() {
        // SCÉNARIO : Le bot n'a aucun objectif en main.
        // ATTENDU : Il doit piocher un objectif Jardinier.

        // 1. Inventaire vide (pas d'objectifs)
        // (C'est le cas par défaut à l'initialisation)

        // 2. Mock de la pioche objectifs
        when(gameStateMock.getPiocheJardinier()).thenReturn(piocheObjectifMock);

        // 3. Action
        bot.jouer(gameStateMock);

        // 4. vérification,
        // il doit piocher un objectif, mais attention :
        // Dans votre code actuel, PiocherObjectif appelle 'pioche.piocher()' puis l'ajoute à l'inventaire du bot.
        // Comme 'piocheObjectifMock' est un mock, piocher() renvoie null par défaut.
        // Il faut dire au mock de renvoyer un vrai objectif pour éviter un crash si votre code l'utilise.
        when(piocheObjectifMock.piocher()).thenReturn(Optional.of(new ObjectifJardinier(Couleur.VERT, 4, 5)));

        // On relance pour être sûr
        bot.jouer(gameStateMock);

        verify(piocheObjectifMock, atLeastOnce()).piocher();
    }
}