package graphics;

import text.Text;

/**
 * Created by johannes on 16/12/17.
 */
public interface Renderer {

    enum Level{BACKGROUND, FOREGROUND, POPUP}

    boolean windowShouldClose();

    int getWindowWidth();
    int getWindowHeight();

    void drawRectangle(float x, float y,        Level level, float width, float height, int lineWidth, Color color);
    void drawRectangle(int x, int y,            Level level, int width, int height, int lineWidth, Color color);
    void drawRectangleFilled(float x, float y,  Level level, float width, float height, Color color);
    void drawRectangleFilled(int x, int y,      Level level, int width, int height, Color color);

    int loadFont(String font);
    void drawText(int fontIndex, String text, float x, float y, Level level, Color color);
    void drawText(int fontIndex, String text, int x, int y, Level level, Color color);
    void drawText(int fontIndex, String text, float x, float y, Level level, Color color, Text.Align align);
    void drawText(int fontIndex, String text, int x, int y, Level level, Color color, Text.Align align);
    int textWidth(int fontIndex, String text);
    int textHeight(int fontIndex, String text);
    int textHeight(int fontIndex);

    void drawTriangle(float x, float y, Level level, float width, int angle, Color color);


    void begin();
    void present();

    //long getDevice();

}
