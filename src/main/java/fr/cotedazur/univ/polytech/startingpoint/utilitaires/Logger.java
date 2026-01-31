package fr.cotedazur.univ.polytech.startingpoint.utilitaires;

public class Logger {
    // C'est ici le "bouton on/off" magique
    // Mettez 'false' quand vous voulez du silence (pour les stats)
    // Mettez 'true' quand vous voulez voir le déroulé (débug)
    private static boolean estActif = true;

    public static void setActif(boolean actif) {
        estActif = actif;
    }

    // C'est la méthode qui remplace System.out.println
    public static void print(String message) {
        if (estActif) {
            System.out.println(message);
        }
    }

    // Petit bonus pour les lignes de séparation
    public static void separator() {
        if (estActif) {
            System.out.println("--------------------------------------------------");
        }
    }
}