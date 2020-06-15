package GUI.components;

import GUI.layout.Section;
import graphics.Color;
import graphics.Renderer;

/**
 * Created by johannes on 17/05/03.
 */
public class Label extends Component {

    String label;
    int font;

    public Label(String text, int font)
    {
        this.label = text;
        this.font = font;
    }

    @Override
    public void init(Renderer render)
    {
        super.init(render);
        setWidth(render.textWidth(font, label));
        setHeight(render.textHeight(font, label));

    }

    @Override
    public void draw(Section section)
    {
        render.drawText(font, label, section.x, section.y, Renderer.Level.FOREGROUND, new Color(128, 125, 110));
    }
}
