package GUI.components;

/**
 * Created by johannes on 16/12/20.
 */
public interface InputEventListener {

    enum MouseButton{NONE, LEFT, RIGHT, MIDDLE}
    enum MouseEvent{MOVE, PRESS, RELEASE, SCROLL}

    enum KeyEvent{CHAR, PRESS, RELEASE, REPEAT}


    /**
     * Method that is called when the mouse moves or a mouse button is pressed. The callee can be notified if the caller
     * wants to be prioritised via the return parameter
     * @param event -   the mouse event
     * @param x -       window relative x pixel
     * @param y -       window relative y pixel
     * @param z -       scroll movement
     * @param button -  eventual button
     * @return true if wants to be prioritized false otherwise.
     */
    boolean mouseEvent(MouseEvent event, int x, int y, double z, MouseButton button);

    boolean keyboardEvent(KeyEvent event, int key);
}
