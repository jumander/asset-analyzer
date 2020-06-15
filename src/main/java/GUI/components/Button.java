package GUI.components;

import GUI.Insets;
import GUI.layout.Section;
import graphics.Color;
import graphics.Renderer;

/**
 * Created by johannes on 16/12/19.
 */
public class Button extends Component {

    private final int NORMAL = 0;
    private final int HOVER = 1;
    private final int PRESSED = 2;

    private String text;

    private Color[] borderColor;
    private Color[] buttonColor;
    private Color[] textColor;

    private int font;
    private int borderWidth = 1;

    private int status;

    private ButtonListner listner;

    public Button(String text)
    {
        this.text = text;
        /*borderColor     = new Color(32, 64, 128);
        buttonColor     = new Color(64, 128,255);
        textColor       = new Color(255, 255, 255);*/
        borderColor     = new Color[3];
        buttonColor     = new Color[3];
        textColor       = new Color[3];

        borderColor[NORMAL] = new Color(0);//new Color(40);
        buttonColor[NORMAL] = new Color(0);
        textColor  [NORMAL] = new Color(200);

        borderColor[HOVER] = new Color(60);
        buttonColor[HOVER] = new Color(0);
        textColor  [HOVER] = new Color(255);

        borderColor[PRESSED] = new Color(64, 128, 255);
        buttonColor[PRESSED] = new Color(0, 0, 0);
        textColor  [PRESSED] = new Color(255);

        padding = new Insets(12, 12, 8, 8);
        status = NORMAL;
        listner = null;
    }

    @Override
    public void init(Renderer render)
    {
        super.init(render);
        font = render.loadFont("small_arial.fnt");
        calculateSize();
    }

    @Override
    public void setPadding(Insets padding)
    {
        super.setPadding(padding);
        if(render != null)
            calculateSize();
    }

    public void setListner(ButtonListner listner)
    {
        this.listner = listner;
    }

    private void calculateSize()
    {
        fixWidth = render.textWidth(font, text)    + borderWidth * 2 + padding.getLeft() + padding.getRight();
        fixHeight = render.textHeight(font, text)  + borderWidth * 2 + padding.getTop()  + padding.getBottom();
    }

    @Override
    public void draw(Section section)
    {
        super.draw(section);

        float windowWidth = render.getWindowWidth();
        float windowHeight = render.getWindowHeight();

        // draw button
        render.drawRectangleFilled(section.x, section.y, Renderer.Level.FOREGROUND, section.width, section.height, buttonColor[status]);

        // draw border
        render.drawRectangle(section.x, section.y, Renderer.Level.FOREGROUND, section.width, section.height, borderWidth, borderColor[status]);




        /*if(hover)
        {
            render.drawRectangle(x+borderWidth/windowWidth, y+borderWidth/windowHeight,
                    width-(borderWidth/windowWidth)*2, height-(borderWidth/windowHeight)*2, borderWidth/2, highlightColor);
        }*/

        render.drawText(font, text,
                section.x + (padding.getLeft() + borderWidth)/windowWidth,
                section.y + (padding.getTop() + borderWidth)/windowHeight, Renderer.Level.FOREGROUND, textColor[status]);

    }

    @Override
    public boolean mouseEvent(MouseEvent event, int x, int y, double z, MouseButton button)
    {
        if(status != PRESSED)
            status = HOVER;

        if((x < 0 || x > currWidth) || (y < 0 || y > currHeight)) {
            status = NORMAL;
        }

        if(status != PRESSED && event == MouseEvent.PRESS) {
            status = PRESSED;
            return true;
        }
        if(status == PRESSED && event == MouseEvent.RELEASE) {
            status = HOVER;
            if(listner != null)
                listner.onClick();

            return false;
        }

        return status >= HOVER;
    }
}
