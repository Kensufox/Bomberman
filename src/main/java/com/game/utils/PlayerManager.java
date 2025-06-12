package com.game.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages player profiles including creation, deletion, loading, saving,
 * and maintaining statistics like games played and won.
 */
public class PlayerManager {

    /** Chemin du fichier de sauvegarde des profils joueurs */
    private static final String PLAYERS_FILE = "profile/players.dat";

    /** Liste des joueurs chargés en mémoire */
    private static List<Player> players = new ArrayList<>();

    /** Joueur actuellement actif/sélectionné */
    private static Player currentPlayer = null;

    static {
        loadPlayers();
    }

    /**
     * Represents a player profile with personal information and game statistics.
     */
    public static class Player implements Serializable {
        private static final long serialVersionUID = 1L;

        /** Nom du joueur */
        private String nom;

        /** Prénom du joueur */
        private String prenom;

        /** Identifiant ou nom du fichier de l'avatar du joueur */
        private String avatar;

        /** Nombre de parties jouées par le joueur */
        private int partiesJouees;

        /** Nombre de parties gagnées par le joueur */
        private int partiesGagnees;

        /**
         * Constructs a Player instance.
         * @param nom last name of the player
         * @param prenom first name of the player
         * @param avatar avatar identifier or filename
         */
        public Player(String nom, String prenom, String avatar) {
            this.nom = nom;
            this.prenom = prenom;
            this.avatar = avatar != null ? avatar : "default";
            this.partiesJouees = 0;
            this.partiesGagnees = 0;
        }

        // Getters
        /** @return player's last name */
        public String getNom() { return nom; }
        /** @return player's first name */
        public String getPrenom() { return prenom; }
        /** @return player's avatar */
        public String getAvatar() { return avatar; }
        /** @return number of games played */
        public int getPartiesJouees() { return partiesJouees; }
        /** @return number of games won */
        public int getPartiesGagnees() { return partiesGagnees; }

        // Setters
        /**
         * Sets the player's last name and saves the updated profile.
         * @param nom new last name
         */
        public void setNom(String nom) {
            this.nom = nom;
            PlayerManager.savePlayersToFile();
        }

        /**
         * Sets the player's first name and saves the updated profile.
         * @param prenom new first name
         */
        public void setPrenom(String prenom) {
            this.prenom = prenom;
            PlayerManager.savePlayersToFile();
        }

        /**
         * Sets the player's avatar and saves the updated profile.
         * @param avatar new avatar identifier
         */
        public void setAvatar(String avatar) {
            this.avatar = avatar;
            PlayerManager.savePlayersToFile();
        }

        // Méthodes statistiques

        /**
         * Increments the count of games played and saves the profile.
         */
        public void incrementPartiesJouees() {
            this.partiesJouees++;
            PlayerManager.savePlayersToFile();
        }

        /**
         * Increments the count of games won and saves the profile.
         */
        public void incrementPartiesGagnees() {
            this.partiesGagnees++;
            PlayerManager.savePlayersToFile();
        }

        /**
         * Calculates the win ratio as a percentage.
         * @return win percentage (0 if no games played)
         */
        public double getRatioVictoires() {
            return partiesJouees > 0 ? (double) partiesGagnees / partiesJouees * 100 : 0;
        }

        @Override
        public String toString() {
            return prenom + " " + nom;
        }
    }

    /**
     * Returns a copy of the list of all players.
     * @return list of players
     */
    public static List<Player> getAllPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Gets the currently active player.
     * @return current player or null if none selected
     */
    public static Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Sets the currently active player.
     * @param player the player to set as current
     */
    public static void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

    /**
     * Creates a new player profile and saves it.
     * @param nom last name
     * @param prenom first name
     * @param avatar avatar identifier
     * @return the newly created Player instance
     */
    public static Player createPlayer(String nom, String prenom, String avatar) {
        Player newPlayer = new Player(nom, prenom, avatar);
        players.add(newPlayer);
        savePlayersToFile(); // Save immediately
        System.out.println("Profil créé et sauvegardé: " + newPlayer.toString());
        return newPlayer;
    }

    /**
     * Deletes a player profile and saves changes.
     * @param player the player to delete
     * @return true if the player was found and removed, false otherwise
     */
    public static boolean deletePlayer(Player player) {
        boolean removed = players.remove(player);
        if (removed) {
            if (currentPlayer == player) {
                currentPlayer = null;
            }
            savePlayersToFile(); // Save immediately
            System.out.println("Profil supprimé: " + player.toString());
        }
        return removed;
    }

    /**
     * Finds a player by name.
     * @param nom last name
     * @param prenom first name
     * @return the Player if found, else null
     */
    public static Player findPlayer(String nom, String prenom) {
        return players.stream()
                .filter(p -> p.getNom().equalsIgnoreCase(nom) && p.getPrenom().equalsIgnoreCase(prenom))
                .findFirst()
                .orElse(null);
    }

    /**
     * Records that the current player has played a game.
     */
    public static void recordGamePlayed() {
        if (currentPlayer != null) {
            currentPlayer.incrementPartiesJouees();
            System.out.println("Partie enregistrée pour: " + currentPlayer.toString());
        }
    }

    /**
     * Records that the current player has won a game.
     */
    public static void recordGameWon() {
        if (currentPlayer != null) {
            currentPlayer.incrementPartiesGagnees();
            System.out.println("Victoire enregistrée pour: " + currentPlayer.toString());
        }
    }

    /**
     * Loads players from the file, creating a new list if file is absent.
     */
    private static void loadPlayers() {
        File file = new File(PLAYERS_FILE);
        if (!file.exists()) {
            System.out.println("Fichier de profils inexistant, création d'une nouvelle liste et fichier.");
            try {
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                players = new ArrayList<>();
                savePlayersToFile();
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

    /**
     * Saves the current player list to the file.
     */
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

    /**
     * Public method to save players list to file.
     */
    public static void savePlayers() {
        savePlayersToFile();
    }

    /**
     * Checks if there is a current player set.
     * @return true if a current player is set, false otherwise
     */
    public static boolean hasCurrentPlayer() {
        return currentPlayer != null;
    }

    /**
     * Returns the name of the current player or a default message if none.
     * @return current player name or "Aucun profil"
     */
    public static String getCurrentPlayerName() {
        return currentPlayer != null ? currentPlayer.toString() : "Aucun profil";
    }

    /**
     * Returns the total number of player profiles.
     * @return number of players
     */
    public static int getPlayerCount() {
        return players.size();
    }

    /**
     * Forces reloading of the player list from the file.
     */
    public static void reloadPlayers() {
        loadPlayers();
    }
}