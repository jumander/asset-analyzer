package GUI;

import GUI.components.Component;
import GUI.components.Container;
import GUI.components.InputEventListener;
import GUI.layout.Section;
import graphics.Renderer;

/**
 * Created by johannes on 16/12/17.
 */
public class GUI implements InputEventListener {

    private Renderer render;
    private Container root;
    private Component focused;

    public GUI(Renderer render)
    {
        this.render = render;
        focused = null;
        root = new Container();


        root.init(render);
        root.draw(new Section(0, 0, 1, 1));
    }

    public void build()
    {


    }

    public void start()
    {
        loop();
    }

    private void loop()
    {
        while(!render.windowShouldClose())
        {
            render.begin();

            root.draw(new Section(0, 0, 1, 1));

            render.present();
        }

        root.close();
        //System.exit(0);
    }

    public Container getRoot()
    {
        return root;
    }

    public void add(Component c)
    {
        root.add(c);
    }

    @Override
    public boolean mouseEvent(MouseEvent event, int x, int y, double z, MouseButton button) {

        Component c;

        // Check if a component is in focus, or if the component has to be found
        if(focused != null)
            c = focused;
        else
            c = root.getComponent(x, y, new Section(0, 0, 1, 1));

        boolean r = c.mouseEvent(event, x - c.getCurrentXpos(), y - c.getCurrentYpos(), z, button);

        // Check if component wants to be in focus
        if(r)
            focused = c;
        else
            focused = null;

        return r;
    }

    @Override
    public boolean keyboardEvent(KeyEvent event, int key) {
        // Check if any component can receive the keyboard inputs
        if(focused != null)
        {
            return focused.keyboardEvent(event, key);
        }
        return false;
    }

}
