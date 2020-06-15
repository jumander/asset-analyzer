package GUI.components;

import graphics.Color;

/**
 * Created by johannes on 17/05/14.
 */
public class HorizontalLine extends Component {

    public HorizontalLine() {
        setHeight(2);
        setBackgroundColor(new Color(60));
    }

    @Override
    public boolean keyboardEvent(KeyEvent event, int key) {
        return false;
    }
}
