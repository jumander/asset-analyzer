package GUI.components;

import GUI.layout.LayoutManager;
import GUI.layout.Section;
import GUI.layout.VerticalLayout;
import graphics.Renderer;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by johannes on 16/12/18.
 */
public class Container extends Component{
    LinkedList<Component> components;
    LayoutManager layoutManager;

    public Container()
    {
        super();

        components = new LinkedList<>();
        // Set layout as default to vertical layout
        layoutManager = new VerticalLayout();
    }

    public void add(Component c)
    {
        if(render != null)
            c.init(render);
        components.add(c);
    }

    @Override
    public void init(Renderer render)
    {
        super.init(render);
        for(Component c : components)
        {
            c.init(render);
        }
    }

    public void remove(Component c)
    {
        components.remove(c);
    }

    public void setLayout(LayoutManager layout)
    {
        layoutManager = layout;
    }


    @Override
    public int getHeight() {
        if (super.getHeight() != 0) {
            return super.getHeight();
        } else {

            AtomicInteger maxHeight = new AtomicInteger();
            maxHeight.set(0);
            layoutManager.layout(components, render, new Section(0, 0, 1, 1), (Component c, Section s) -> {
                int height = (int) ((s.y + s.height) * render.getWindowHeight() + c.getMargin().getBottom());
                if (height > maxHeight.get())
                    maxHeight.set(height);

                return null; // dummy return
            });
            int result = maxHeight.get();
            return result == render.getWindowHeight() ? 0 : result;
        }
    }

    @Override
    public int getWidth() {
        if(super.getWidth() != 0) {
            return super.getWidth();
        } else {
            AtomicInteger maxWidth = new AtomicInteger();
            maxWidth.set(0);
            layoutManager.layout(components, render, new Section(0, 0, 1, 1), (Component c, Section s) -> {
                int width = (int) ((s.x + s.width) * render.getWindowWidth() + c.getMargin().getRight());
                if (width > maxWidth.get())
                    maxWidth.set(width);

                return null; // dummy return
            });
            int result = maxWidth.get();
            return result == render.getWindowWidth() ? 0 : result;
        }
    }


    @Override
    public void draw(Section section)
    {
        super.draw(section);
        layoutManager.draw(components, section, render);
    }

    public Component getComponent(int x, int y, Section section)
    {
        Component c = layoutManager.getComponent(x, y, components, section, render);
        if(c != null)
            return c;
        else
            return this;
    }

    @Override
    public boolean mouseEvent(MouseEvent event, int x, int y, double z, MouseButton button){
        return false;
    }

    @Override
    public boolean keyboardEvent(KeyEvent event, int key) {
        return false;
    }

    @Override
    public void close(){
        for(Component c : components)
            c.close();
    }
}
