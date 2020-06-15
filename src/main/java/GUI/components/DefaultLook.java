package GUI.components;

import graphics.Color;

/**
 * Created by johannes on 17/06/24.
 */
public class DefaultLook {
    public static final int NORMAL    = 0;
    public static final int HOVER     = 1;
    public static final int PRESSED   = 2;
    public static final int ACTIVE    = 3;
    //                                                  NORMAL                      HOVER                   PRESSED                ACTIVE
    // Background

    // Components
    public static final int borderWidth = 1;
    public static final Color[] borderColor     = {color(0),    color(60),              color(64, 128, 255),    color(64, 128, 255)};

    public static final Color[] componentColor  = {color(0),                color(0),               color(0),               color(0)};
    public static final Color[] textColor       = {color(200),              color(255),             color(255),             color(255)};
    public static final Color[] listTextColor   = {color(200),              color(-1),              color(-1),              color(0)};


    private static Color color(int w)
    {
        return new Color(w);
    }

    private static Color color(int r, int g, int b)
    {
        return new Color(r, g, b);
    }

}
