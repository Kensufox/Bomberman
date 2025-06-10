package com.game.models.entities;

/**
 * Represents a flag in Capture the Flag game mode
 */
public class Flag {
    private int row;
    private int col;
    private final int homeRow;
    private final int homeCol;
    private boolean isAtHome;
    private boolean isCarried;
    private Player carrier;
    private final int teamId;

    /**
     * Creates a new flag at the specified home position
     * @param homeRow The home row position
     * @param homeCol The home column position
     * @param teamId The team ID this flag belongs to (0 for player 1, 1 for player 2)
     */
    public Flag(int homeRow, int homeCol, int teamId) {
        this.homeRow = homeRow;
        this.homeCol = homeCol;
        this.row = homeRow;
        this.col = homeCol;
        this.isAtHome = true;
        this.isCarried = false;
        this.carrier = null;
        this.teamId = teamId;
    }

    // Getters
    public int getRow() { 
        return row; 
    }

    public int getCol() { 
        return col; 
    }

    public int getHomeRow() { 
        return homeRow; 
    }

    public int getHomeCol() { 
        return homeCol; 
    }

    public boolean isAtHome() { 
        return isAtHome; 
    }

    public boolean isCarried() { 
        return isCarried; 
    }

    public Player getCarrier() { 
        return carrier; 
    }

    public int getTeamId() { 
        return teamId; 
    }

    /**
     * Sets the flag position and updates home status
     * @param row New row position
     * @param col New column position
     */
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
        this.isAtHome = (row == homeRow && col == homeCol);
    }

    /**
     * Makes a player pick up this flag
     * @param player The player picking up the flag
     * @return true if the flag was successfully picked up, false if already carried
     */
    public boolean pickUp(Player player) {
        if (isCarried) {
            return false; // Flag is already being carried
        }
        
        this.isCarried = true;
        this.carrier = player;
        this.isAtHome = false;
        return true;
    }

    /**
     * Drops the flag at the specified position
     * @param row Row to drop the flag at
     * @param col Column to drop the flag at
     */
    public void drop(int row, int col) {
        this.isCarried = false;
        this.carrier = null;
        setPosition(row, col);
    }

    /**
     * Returns the flag to its home position
     */
    public void returnHome() {
        this.isCarried = false;
        this.carrier = null;
        setPosition(homeRow, homeCol);
    }

    /**
     * Updates the flag position to match its carrier's position
     * Should be called when the carrier moves
     */
    public void updateCarrierPosition() {
        if (isCarried && carrier != null) {
            setPosition(carrier.getRow(), carrier.getCol());
        }
    }

    /**
     * Checks if this flag can be picked up by the specified player
     * @param player The player attempting to pick up the flag
     * @return true if the player can pick up this flag
     */
    public boolean canBePickedUpBy(Player player) {
        // A flag can be picked up if:
        // 1. It's not already being carried
        // 2. The player is at the flag's position
        // 3. It's not the player's own flag (assuming teamId matches player index)
        return !isCarried && 
               player.getRow() == this.row && 
               player.getCol() == this.col;
    }

    /**
     * Checks if the flag is at the specified position
     * @param row Row to check
     * @param col Column to check
     * @return true if the flag is at the specified position
     */
    public boolean isAtPosition(int row, int col) {
        return this.row == row && this.col == col;
    }

    /**
     * Gets a string representation of the flag's current state
     * @return String describing the flag's state
     */
    @Override
    public String toString() {
        return String.format("Flag[Team=%d, Pos=(%d,%d), Home=(%d,%d), Carried=%b, AtHome=%b]", 
                           teamId, row, col, homeRow, homeCol, isCarried, isAtHome);
    }
}