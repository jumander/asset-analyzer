package GUI.components;

import GUI.Insets;
import GUI.layout.Section;
import graphics.Color;
import graphics.Renderer;
import static GUI.components.DefaultLook.*;

/**
 * Created by johannes on 17/06/24.
 */
public class TextInput extends Component {
    private String text;
    private String input = "";
    private int font;

    private int status;



    public TextInput(String text)
    {
        this.text = text;
        padding = new Insets(12, 12, 8, 8);
    }

    @Override
    public void init(Renderer render)
    {
        super.init(render);
        font = render.loadFont("small_arial.fnt");
        setWidth(render.textWidth(font, text));
        setHeight(render.textHeight(font, text));

        calculateSize();

    }

    public String getInput()
    {
        return input;
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

        String drawableText = input.length() == 0 ? text : input;


        float inputWidth = status == NORMAL ? section.width : (render.textWidth(font, drawableText)    + borderWidth * 2 + padding.getLeft() + padding.getRight()) / windowWidth;
        inputWidth = Math.max(section.width, inputWidth);

        // draw button
        render.drawRectangleFilled(section.x, section.y,
                Renderer.Level.FOREGROUND, inputWidth, section.height,
                componentColor[status]);

        // draw border
        render.drawRectangle(section.x, section.y, Renderer.Level.FOREGROUND, inputWidth, section.height, borderWidth, borderColor[status]);


        if(status == NORMAL)
        {
            boolean updated = false;
            while(drawableText.length() != 0 && render.textWidth(font, drawableText) > render.textWidth(font, text))
            {
                drawableText = drawableText.substring(0, drawableText.length()-1);
                updated = true;
            }
            if(updated)
                drawableText = drawableText + "..";
        }

        render.drawText(font, drawableText,
                section.x + (padding.getLeft() + borderWidth)/windowWidth,
                section.y + (padding.getTop() + borderWidth)/windowHeight, Renderer.Level.FOREGROUND, textColor[status]);
    }



    @Override
    public boolean mouseEvent(MouseEvent event, int x, int y, double z, MouseButton button)
    {
        if(status < HOVER)
            status = HOVER;

        boolean onComponent = true;
        if((x < 0 || x > currWidth) || (y < 0 || y > currHeight)) {
            onComponent = false;
        }

        if(!onComponent && status == HOVER)
            status = NORMAL;

        if(event == MouseEvent.PRESS && button == MouseButton.LEFT)
        {
            if(onComponent && status == HOVER)
                status = PRESSED;

            if(!onComponent)
                status = NORMAL;

        }
        if(event == MouseEvent.RELEASE && button == MouseButton.LEFT)
        {
            if(onComponent && status == PRESSED)
                status = ACTIVE;
        }

        return status >= HOVER;
    }

    @Override
    public boolean keyboardEvent(KeyEvent event, int key){
        if(event == KeyEvent.CHAR)
            input += (char) key;


        if(event == KeyEvent.RELEASE || event == KeyEvent.REPEAT) {

            if(key == 259 && input.length() > 0) // backspace
                input = input.substring(0, input.length()-1);
        }

        return true;
    }
}
