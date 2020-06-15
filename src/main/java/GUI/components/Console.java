package GUI.components;

import GUI.Insets;
import GUI.layout.Section;
import graphics.Color;
import graphics.Renderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johannes on 17/05/14.
 */
public class Console extends Component {
    private static List<String> lines = new ArrayList<>();

    private int font;
    private int scrollWidth = 8;

    public static void println(String line)
    {
        lines.add(line);
    }


    public Console() {
        padding = new Insets(10);
    }

    @Override
    public void init(Renderer render)
    {
        this.render = render;
        font = render.loadFont("small_arial.fnt");
    }

    @Override
    public void draw(Section s)
    {
        float windowWidth = render.getWindowWidth();
        float windowHeight = render.getWindowHeight();

        float textHeight = render.textHeight(font, "Å")/windowHeight;


        float xOffset = padding.getLeft()/windowWidth;
        float yOffset = padding.getTop()/windowHeight;

        int linesDisplayed = (int)Math.floor((s.height - yOffset) / (textHeight *1.5));
        linesDisplayed = Math.min(linesDisplayed, lines.size());

        //System.out.println(lines.size() + " " + linesDisplayed);

        for(int i = lines.size()-linesDisplayed; i < lines.size(); i++) {
            render.drawText(font, lines.get(i), s.x + xOffset, s.y + yOffset, Renderer.Level.FOREGROUND, new Color(255));
            yOffset += textHeight*1.5;
        }

        // draw slider
        float scrollHeight = s.height * (linesDisplayed/(float)lines.size());
        scrollHeight = Math.max(scrollHeight, 1/40f);
        float scrollStart = (s.height-scrollHeight) * ((lines.size()-linesDisplayed)/(float)(lines.size()-linesDisplayed));


        if(linesDisplayed != lines.size()) {
            render.drawRectangleFilled(
                    s.x + (s.width - scrollWidth / windowWidth) ,
                    s.y + scrollStart , Renderer.Level.POPUP, scrollWidth / windowWidth, scrollHeight, new Color(200));
        }

        //render.drawRectangleFilled(section.x, section.y, section.width, section.height, new Color(255));

    }

}
