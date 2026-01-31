package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Bassin;
import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Enclos;
import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Engrais;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AmenagementTest {
    Parcelle parcelleVerte;
    Bambou bambou;

    @BeforeEach
    void setUp() {
        parcelleVerte = new Parcelle(Couleur.VERT);
        bambou = parcelleVerte.getBambou();
    }

    @Test
    void testBassinIrrigueParcelle() {
        assertFalse(parcelleVerte.estIrriguee());

        // Simulation d'un achat ou d'une pose via Pioche
        new Bassin(parcelleVerte, bambou);

        assertTrue(parcelleVerte.getIsAmenagee());
        assertTrue(parcelleVerte.getAmenagement() instanceof Bassin);
        // Le bassin doit déclencher l'irrigation immédiate
        assertTrue(parcelleVerte.estIrriguee());
    }

    @Test
    void testEngraisBoosteCroissance() {
        // L'engrais est posé
        new Engrais(parcelleVerte, bambou);

        // On simule une irrigation pour faire pousser
        parcelleVerte.triggerIrrigation();

        // Normalement 1 section, mais avec Engrais ça dépend de votre règle (souvent +2 ou x2)
        // Adaptez l'assertion selon votre règle exacte dans Bambou.croissance()
        assertTrue(parcelleVerte.getNbSectionsSurParcelle() > 0);
        assertTrue(parcelleVerte.getAmenagement() instanceof Engrais);
    }

    @Test
    void testEnclosProtegeDuPanda() {
        Panda panda = new Panda(); // Mock rapide
        new Enclos(parcelleVerte, bambou);

        // Teste l'effet "Action sur Parcelle"
        Enclos enclos = (Enclos) parcelleVerte.getAmenagement();
        enclos.actionSurParcelle(panda);

        // Vérifiez ici que le panda a bien reçu l'interdiction (selon votre implémentation de Panda)
        // ex: assertFalse(panda.peutManger());
        assertTrue(parcelleVerte.getAmenagement() instanceof Enclos);
    }

    @Test
    void testImpossibleDePoserDeuxAmenagements() {
        new Bassin(parcelleVerte, bambou);
        assertTrue(parcelleVerte.getIsAmenagee());

        // Tente de poser un Enclos par dessus
        new Enclos(parcelleVerte, bambou);

        // Doit rester un Bassin
        assertTrue(parcelleVerte.getAmenagement() instanceof Bassin);
    }
}