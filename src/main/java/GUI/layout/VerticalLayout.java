package GUI.layout;

import GUI.components.Component;
import GUI.components.Container;
import graphics.Renderer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by johannes on 16/12/19.
 */
public class VerticalLayout extends LayoutManager{

    private Align align = Align.LEFT;

    public VerticalLayout(){}

    public VerticalLayout(Align align)
    {
        this.align = align;
    }

    public <T> T layout(List<Component> components, Renderer render, Section section, LayoutFunction<T> function) {
        float windowWidth = render.getWindowWidth();
        float windowHeight = render.getWindowHeight();

        float distance = 0;
        float xOffset = 0;
        float yOffset = 0;
        if(components.size() == 2)
            distance = 0;

        // Calculate component total height and unsized components
        float totalHeight = 0;
        int numUnsized = 0;
        for(Component c : components)
        {
            distance = Math.max(distance, c.getMargin().getTop()/windowHeight);
            totalHeight += distance + (c.getHeight() + c.getMargin().getBottom())/windowHeight;
            distance = c.getMargin().getBottom()/windowHeight;

            if(c.getHeight() == 0)
                numUnsized++;
        }
        float remainingHeight = section.height - totalHeight;

        distance = 0;
        for(Component c : components)
        {
            distance = Math.max(distance, c.getMargin().getTop()/windowHeight);
            yOffset += distance;

            // determine width and height
            float cWidth = c.getWidth()/windowWidth;
            float cHeight = c.getHeight()/windowHeight;

            if(cWidth == 0)
                cWidth = section.width;

            if(cHeight == 0 && remainingHeight > 0)
                cHeight = remainingHeight / numUnsized;

            // determine x offset
            if(align == Align.CENTER)
            {
                xOffset = (section.width - cWidth)/2.0f;
            }
            else if(align == Align.RIGHT)
            {
                xOffset = (section.width - cWidth) - c.getMargin().getRight()/windowWidth;
            }
            else if(align == Align.LEFT)
            {
                xOffset = c.getMargin().getLeft()/windowWidth;
            }
            Section s = new Section(section.x + xOffset, section.y + yOffset, cWidth, cHeight);

            T ret = function.action(c, s);
            if(ret != null)
                return ret;

            yOffset += cHeight;

            distance = c.getMargin().getBottom()/windowHeight;
        }
        return null;
    }
}
