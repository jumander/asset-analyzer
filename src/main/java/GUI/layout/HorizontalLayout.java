package GUI.layout;

import GUI.components.Component;
import GUI.components.Container;
import graphics.Renderer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by johannes on 16/12/19.
 */
public class HorizontalLayout extends LayoutManager{

    private Align align = Align.TOP;

    public HorizontalLayout(){}

    public HorizontalLayout(Align align)
    {
        this.align = align;
    }

    public <T> T layout(List<Component> components, Renderer render, Section section, LayoutFunction<T> function) {
        float windowWidth = render.getWindowWidth();
        float windowHeight = render.getWindowHeight();

        float distance = 0;
        float xOffset = 0;
        float yOffset = 0;

        // Calculate component total width and unsized components
        float totalWidth = 0;
        int numUnsized = 0;
        for(Component c : components)
        {
            distance = Math.max(distance, c.getMargin().getLeft()/windowWidth);
            totalWidth += distance + (c.getWidth() + c.getMargin().getRight())/windowWidth;
            distance = c.getMargin().getRight()/windowWidth;

            if(c.getWidth() == 0)
                numUnsized++;
        }
        float remainingWidth = section.width - totalWidth;

        distance = 0;
        for(Component c : components)
        {
            distance = Math.max(distance, c.getMargin().getLeft()/windowWidth);
            xOffset += distance;

            // determine width and height
            float cWidth = c.getWidth()/windowWidth;
            float cHeight = c.getHeight()/windowHeight;

            if(cHeight == 0)
                cHeight = section.height;

            if(cWidth == 0 && remainingWidth > 0)
                cWidth = remainingWidth / numUnsized;

            // determine y offset
            if(align == Align.CENTER)
            {
                yOffset = (section.height - cHeight)/2.0f;
            }
            else if(align == Align.BOTTOM)
            {
                yOffset = (section.height - cHeight) - c.getMargin().getBottom()/windowHeight;
            }
            else if(align == Align.TOP)
            {
                yOffset = c.getMargin().getTop()/windowHeight;
            }
            Section s = new Section(section.x + xOffset, section.y + yOffset, cWidth, cHeight);

            T ret = function.action(c, s);
            if(ret != null)
                return ret;

            xOffset += cWidth;

            distance = c.getMargin().getRight()/windowWidth;
        }
        return null;
    }
}
