package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpertMeteoTest {

    ExpertMeteo expertMeteo;

    @BeforeEach
    void setUp() {
        expertMeteo = new ExpertMeteo();
    }

    @Test
    void testChoisirMeteo_NeRenvoiePasNull() {
        Meteo m = expertMeteo.choisirMeteoStrategy();
        assertNotNull(m, "Le choix de la météo ne doit pas être null");
    }

    @Test
    void testChoisirParcellePluie_RenvoieElementDeLaListe() {
        List<Parcelle> parcelles = new ArrayList<>();
        parcelles.add(new Parcelle(Couleur.VERT));
        parcelles.add(new Parcelle(Couleur.ROSE));

        Parcelle choisie = expertMeteo.choisirParcellePourPluie(parcelles);

        assertNotNull(choisie, "L'expert doit choisir une parcelle s'il y en a");
        assertTrue(parcelles.contains(choisie), "La parcelle choisie doit appartenir à la liste proposée");
    }

    @Test
    void testChoisirParcellePluie_GereListeVide() {
        Parcelle choisie = expertMeteo.choisirParcellePourPluie(new ArrayList<>());
        assertNull(choisie, "Si aucune parcelle n'est disponible, l'expert doit renvoyer null");
    }
}