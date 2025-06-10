package com.game.models.entities;

/**
 * Represents a flag in Capture the Flag game mode.
 * A flag has a fixed home position and can be picked up, carried, dropped, or returned.
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
     * Constructs a Flag object with a specified home location and team.
     *
     * @param homeRow The row where the flag's base is located.
     * @param homeCol The column where the flag's base is located.
     * @param teamId  The ID of the team the flag belongs to (e.g., 0 or 1).
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

    /**
     * Gets the current row position of the flag.
     *
     * @return The row index.
     */
    public int getRow() { 
        return row; 
    }

    /**
     * Gets the current column position of the flag.
     *
     * @return The column index.
     */
    public int getCol() { 
        return col; 
    }

    /**
     * Gets the home row position of the flag.
     *
     * @return The flag's home row.
     */
    public int getHomeRow() { 
        return homeRow; 
    }

    /**
     * Gets the home column position of the flag.
     *
     * @return The flag's home column.
     */
    public int getHomeCol() { 
        return homeCol; 
    }

    /**
     * Checks if the flag is currently at its home position.
     *
     * @return true if the flag is at home; false otherwise.
     */
    public boolean isAtHome() { 
        return isAtHome; 
    }

    /**
     * Checks if the flag is currently being carried by a player.
     *
     * @return true if the flag is carried; false otherwise.
     */
    public boolean isCarried() { 
        return isCarried; 
    }

    /**
     * Gets the player currently carrying the flag.
     *
     * @return The player carrying the flag, or null if not carried.
     */
    public Player getCarrier() { 
        return carrier; 
    }

    /**
     * Gets the team ID of the flag.
     *
     * @return The team ID (e.g., 0 or 1).
     */
    public int getTeamId() { 
        return teamId; 
    }

    /**
     * Updates the flag's position and its home status.
     *
     * @param row The new row position.
     * @param col The new column position.
     */
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
        this.isAtHome = (row == homeRow && col == homeCol);
    }

    /**
     * Attempts to pick up the flag with the specified player.
     *
     * @param player The player trying to pick up the flag.
     * @return true if the flag was picked up; false if already carried.
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
     * Drops the flag at the specified position.
     *
     * @param row The row where the flag is dropped.
     * @param col The column where the flag is dropped.
     */
    public void drop(int row, int col) {
        this.isCarried = false;
        this.carrier = null;
        setPosition(row, col);
    }

    /**
     * Returns the flag to its home position, resetting its state.
     */
    public void returnHome() {
        this.isCarried = false;
        this.carrier = null;
        setPosition(homeRow, homeCol);
    }

    /**
     * Updates the flag's position to match its carrier.
     * Should be called after the carrier moves.
     */
    public void updateCarrierPosition() {
        if (isCarried && carrier != null) {
            setPosition(carrier.getRow(), carrier.getCol());
        }
    }

    /**
     * Checks if a player can pick up this flag.
     *
     * @param player The player attempting pickup.
     * @return true if pickup is possible (same position and not already carried).
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
     * Checks if the flag is located at a specific position.
     *
     * @param row The row to check.
     * @param col The column to check.
     * @return true if the flag is at the specified coordinates.
     */
    public boolean isAtPosition(int row, int col) {
        return this.row == row && this.col == col;
    }

    /**
     * Returns a string representation of the flag's current state.
     *
     * @return A formatted string describing the flag.
     */
    @Override
    public String toString() {
        return String.format("Flag[Team=%d, Pos=(%d,%d), Home=(%d,%d), Carried=%b, AtHome=%b]", 
                           teamId, row, col, homeRow, homeCol, isCarried, isAtHome);
    }
}