package com.game.utils;

import java.io.*;
import java.util.*;

/**
 * Manages player profiles including creation, loading, saving and statistics.
 */
public class PlayerManager {

    private static final String PLAYERS_FILE = "profile/players.dat";
    private static List<Player> players = new ArrayList<>();
    private static Player currentPlayer = null;

    static {
        loadPlayers();
    }

    // Classe Player sérialisable
    public static class Player implements Serializable {
        private static final long serialVersionUID = 1L;

        private String nom;
        private String prenom;
        private String avatar;
        private int partiesJouees;
        private int partiesGagnees;

        public Player(String nom, String prenom, String avatar) {
            this.nom = nom;
            this.prenom = prenom;
            this.avatar = avatar != null ? avatar : "default";
            this.partiesJouees = 0;
            this.partiesGagnees = 0;
        }

        // Getters
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }
        public String getAvatar() { return avatar; }
        public int getPartiesJouees() { return partiesJouees; }
        public int getPartiesGagnees() { return partiesGagnees; }

        // Setters
        public void setNom(String nom) {
            this.nom = nom;
            PlayerManager.savePlayersToFile();
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
            PlayerManager.savePlayersToFile();
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
            PlayerManager.savePlayersToFile();
        }

        // Méthodes statistiques
        public void incrementPartiesJouees() {
            this.partiesJouees++;
            PlayerManager.savePlayersToFile();
        }

        public void incrementPartiesGagnees() {
            this.partiesGagnees++;
            PlayerManager.savePlayersToFile();
        }

        public double getRatioVictoires() {
            return partiesJouees > 0 ? (double) partiesGagnees / partiesJouees * 100 : 0;
        }

        @Override
        public String toString() {
            return prenom + " " + nom;
        }
    }

    // Méthodes de gestion des profils
    public static List<Player> getAllPlayers() {
        return new ArrayList<>(players);
    }

    public static Player getCurrentPlayer() {
        return currentPlayer;
    }

    public static void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

    public static Player createPlayer(String nom, String prenom, String avatar) {
        Player newPlayer = new Player(nom, prenom, avatar);
        players.add(newPlayer);
        savePlayersToFile(); // Sauvegarde immédiate
        System.out.println("Profil créé et sauvegardé: " + newPlayer.toString());
        return newPlayer;
    }

    public static boolean deletePlayer(Player player) {
        boolean removed = players.remove(player);
        if (removed) {
            if (currentPlayer == player) {
                currentPlayer = null;
            }
            savePlayersToFile(); // Sauvegarde immédiate
            System.out.println("Profil supprimé: " + player.toString());
        }
        return removed;
    }

    public static Player findPlayer(String nom, String prenom) {
        return players.stream()
                .filter(p -> p.getNom().equalsIgnoreCase(nom) && p.getPrenom().equalsIgnoreCase(prenom))
                .findFirst()
                .orElse(null);
    }

    // Méthodes de statistiques
    public static void recordGamePlayed() {
        if (currentPlayer != null) {
            currentPlayer.incrementPartiesJouees();
            System.out.println("Partie enregistrée pour: " + currentPlayer.toString());
        }
    }

    public static void recordGameWon() {
        if (currentPlayer != null) {
            currentPlayer.incrementPartiesGagnees();
            System.out.println("Victoire enregistrée pour: " + currentPlayer.toString());
        }
    }

    // Sauvegarde et chargement
    private static void loadPlayers() {
        File file = new File(PLAYERS_FILE);
        if (!file.exists()) {
            System.out.println("Fichier de profils inexistant, création d'une nouvelle liste et fichier.");
            try {
                // Crée les dossiers parents si nécessaire
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }

                // Initialise la liste vide
                players = new ArrayList<>();
                savePlayersToFile();  // Cette méthode écrira dans "profile/players.dat"
            } catch (Exception e) {
                System.err.println("Erreur lors de la création du fichier de profils: " + e.getMessage());
                e.printStackTrace();
            }
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                players = (List<Player>) obj;
                System.out.println("Profils chargés: " + players.size() + " joueur(s)");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement des profils: " + e.getMessage());
            players = new ArrayList<>();
        }
    }

    private static void savePlayersToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PLAYERS_FILE))) {
            oos.writeObject(players);
            oos.flush();
            System.out.println("Profils sauvegardés: " + players.size() + " joueur(s)");
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des profils: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void savePlayers() {
        savePlayersToFile();
    }

    // Méthodes utilitaires
    public static boolean hasCurrentPlayer() {
        return currentPlayer != null;
    }

    public static String getCurrentPlayerName() {
        return currentPlayer != null ? currentPlayer.toString() : "Aucun profil";
    }

    public static int getPlayerCount() {
        return players.size();
    }

    // Méthode pour forcer le rechargement depuis le fichier
    public static void reloadPlayers() {
        loadPlayers();
    }
}