package com.game.utils;

/**
 * Utility class containing constant paths to image resources used in the game.
 * <p>
 * This class should not be instantiated.
 * Each constant represents the path to an image located in the <code>/images/</code> directory
 * within the resource folder.
 * </p>
 * Usage example:
 * <pre>
 * Image img = new Image(getClass().getResourceAsStream(ImageLibrary.Bomb));
 * </pre>
 */
public class ImageLibrary {

    /**
     * Path to the bomb image.
     */
    public static final String Bomb          = "/images/bomb.png";

    /**
     * Path to the Bomberman menu background image.
     */
    public static final String BombermanMenu = "/images/BombermanMenu.jpg";

    /**
     * Path to the central fire/explosion image.
     */
    public static final String CenterFire    = "/images/center-fire.png";

    /**
     * Path to the first segment of downward fire/explosion.
     */
    public static final String Down1Fire     = "/images/down-1-fire.png";

    /**
     * Path to the second segment of downward fire/explosion.
     */
    public static final String Down2Fire     = "/images/down-2-fire.png";

    /**
     * Path to the empty tile image.
     */
    public static final String Empty         = "/images/empty.png";

    /**
     * Path to the indestructible wall image.
     */
    public static final String InfWall       = "/images/inf_wall.png";

    /**
     * Path to the first segment of leftward fire/explosion.
     */
    public static final String Left1Fire     = "/images/left-1-fire.png";

    /**
     * Path to the second segment of leftward fire/explosion.
     */
    public static final String Left2Fire     = "/images/left-2-fire.png";

    /**
     * Path to the generic player image.
     */
    public static final String Player        = "/images/player.png";

    /**
     * Path to the player 1 image.
     */
    public static final String Player1       = "/images/player1.png";

    /**
     * Path to the player 2 image.
     */
    public static final String Player2       = "/images/player2.png";

    /**
     * Path to the AI-controlled robot player image.
     */
    public static final String RobotPlayer   = "/images/robot-player.png";

    /**
     * Path to the power-up that increases bomb amount.
     */
    public static final String PowerAmount   = "/images/power-up-amount.png";

    /**
     * Path to the power-up that increases explosion range.
     */
    public static final String PowerRange    = "/images/power-up-range.png";

    /**
     * Path to the power-up that increases player speed.
     */
    public static final String PowerSpeed    = "/images/power-up-speed.png";

    /**
     * Path to a generic power-up image.
     */
    public static final String Power         = "/images/power-up.png";

    /**
     * Path to the first segment of rightward fire/explosion.
     */
    public static final String Right1Fire    = "/images/right-1-fire.png";

    /**
     * Path to the second segment of rightward fire/explosion.
     */
    public static final String Right2Fire    = "/images/right-2-fire.png";

    /**
     * Path to the first segment of upward fire/explosion.
     */
    public static final String Up1Fire       = "/images/up-1-fire.png";

    /**
     * Path to the second segment of upward fire/explosion.
     */
    public static final String Up2Fire       = "/images/up-2-fire.png";

    /**
     * Path to the combined vertical (up-down) center fire image.
     */
    public static final String CenterUDFire  = "/images/center-ud-fire.png";

    /**
     * Path to the combined horizontal (left-right) center fire image.
     */
    public static final String CenterLRFire  = "/images/center-lr-fire.png";

    /**
     * Path to the combined center fire image (up-left).
     */
    public static final String CenterULFire  = "/images/center-ul-fire.png";

    /**
     * Path to the combined center fire image (up-right).
     */
    public static final String CenterURFire  = "/images/center-ur-fire.png";

    /**
     * Path to the combined center fire image (down-left).
     */
    public static final String CenterDLFire  = "/images/center-dl-fire.png";

    /**
     * Path to the combined center fire image (down-right).
     */
    public static final String CenterDRFire  = "/images/center-dr-fire.png";

    /**
     * Path to the combined center fire image (up-down-left).
     */
    public static final String CenterUDLFire = "/images/center-udl-fire.png";

    /**
     * Path to the combined center fire image (up-down-right).
     */
    public static final String CenterUDRFire = "/images/center-udr-fire.png";

    /**
     * Path to the combined center fire image (up-left-right).
     */
    public static final String CenterULRFire = "/images/center-ulr-fire.png";

    /**
     * Path to the combined center fire image (down-left-right).
     */
    public static final String CenterDLRFire = "/images/center-dlr-fire.png";

    /**
     * Path to the destructible (weak) wall image.
     */
    public static final String WeakWall      = "/images/weak_wall.png";

    /**
     * Path to the first flag image (e.g., for objectives or scoring).
     */
    public static final String Flag1         = "/images/Flag2.png";

    /**
     * Path to the second flag image (e.g., for objectives or scoring).
     */
    public static final String Flag2         = "/images/Flag1.png";


    /**
     * Private constructor to prevent instantiation.
     * This class is intended to be used statically.
     */
    private ImageLibrary() {
        // Prevent instantiation
    }
}
