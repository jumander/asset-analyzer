package GUI.components;

import GUI.Insets;
import GUI.layout.Section;
import graphics.Color;
import graphics.Renderer;

/**
 * Created by johannes on 16/12/18.
 */
public abstract class Component implements InputEventListener {

    protected Renderer render;
    protected Color backgroundColor;
    protected int fixWidth;
    protected int fixHeight;
    protected Insets margin;
    protected Insets padding;

    protected int currXpos;
    protected int currYpos;
    protected int currWidth;
    protected int currHeight;



    public Component()
    {
        backgroundColor = null;
        fixWidth = 0;
        fixHeight = 0;
        margin =    new Insets(0, 0, 0, 0);
        padding =   new Insets(0, 0, 0, 0);
        currXpos    = 0;
        currYpos    = 0;
        currWidth   = 0;
        currHeight  = 0;
    }

    public void init(Renderer render)
    {
        this.render = render;
    }

    public void draw(Section section)
    {
        float windowWidth = render.getWindowWidth();
        float windowHeight = render.getWindowHeight();
        currXpos    = (int)(section.x * windowWidth);
        currYpos    = (int)(section.y * windowHeight);
        currWidth   = (int)(section.width * windowWidth);
        currHeight  = (int)(section.height * windowHeight);

        if(backgroundColor != null)
        {
            render.drawRectangleFilled(section.x, section.y, Renderer.Level.BACKGROUND, section.width, section.height, backgroundColor);
        }
    }

    public void setMargin(Insets margin)
    {
        this.margin = margin;
    }

    public void setPadding(Insets padding)
    {
        this.padding = padding;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setWidth(int width) {
        this.fixWidth = width;
    }

    public void setHeight(int height) {
        this.fixHeight = height;
    }

    public Insets getMargin()
    {
        return margin;
    }

    public Insets getPadding()
    {
        return padding;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public int getWidth() {
        return fixWidth;
    }

    public int getHeight() {
        return fixHeight;
    }

    @Override
    public boolean mouseEvent(MouseEvent event, int x, int y, double z, MouseButton button){return false;}

    @Override
    public boolean keyboardEvent(KeyEvent event, int key){return false;}

    public int getCurrentXpos(){ return currXpos;}
    public int getCurrentYpos(){ return currYpos;}

    public void close(){}

}
