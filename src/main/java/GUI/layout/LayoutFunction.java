package GUI.layout;

import GUI.components.Component;

/**
 * Created by johannes on 17/05/13.
 */
public interface LayoutFunction<T> {

    T action(Component c, Section section);

}
