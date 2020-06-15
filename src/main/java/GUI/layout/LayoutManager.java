package GUI.layout;

import GUI.components.Component;
import GUI.components.Container;
import graphics.Renderer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by johannes on 16/12/19.
 */
public abstract class LayoutManager{

    public enum Align{LEFT, CENTER, RIGHT, TOP, BOTTOM};

    public abstract <T> T layout(List<Component> components, Renderer render, Section section, LayoutFunction<T> function);


    public void draw(LinkedList<Component> components, Section section, Renderer render) {
        layout(components, render, section, (Component c, Section s) -> {
            c.draw(s);
            return null; // dummy return
        });
    }

    public Component getComponent(int x, int y, LinkedList<Component> components, Section section, Renderer render) {
        float windowWidth = render.getWindowWidth();
        float windowHeight = render.getWindowHeight();

        return layout(components, render, section, (Component c, Section s) -> {
            if (LayoutManager.contains(x/windowWidth, y/windowHeight, s))
            {
                if(c instanceof Container)
                    return ((Container) c).getComponent(x, y, s);
                else
                    return c;
            }
            return null;
        });
    }

    static boolean contains(float x, float y, Section section)
    {
        if(x > section.x && x < section.x + section.width)
        {
            if(y > section.y && y < section.y + section.height)
                return true;
        }
        return false;
    }
}
