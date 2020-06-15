package GUI.components;

import GUI.Insets;
import GUI.components.Listners.ListListner;
import GUI.layout.Section;
import graphics.Color;
import graphics.Renderer;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * Created by johannes on 16/12/22.
 */
public class ListSelect extends Component {

    private final int NORMAL    = 0;
    private final int HOVER     = 1;
    private final int PRESSED   = 2;
    private final int ACTIVE    = 3;

    private List<Object> items;
    private List<Integer> filteredItems;
    private String text;
    private String searchText;

    private Color[] borderColor;
    private Color[] listColor;
    private Color[] textColor;
    private Color[] listTextColor;

    private int font;
    private int smallFont;
    private int borderWidth = 1;
    private int scrollWidth = 10;
    private int triangleWidth = 6;

    private int status;
    private int selectedIndex;
    private int scrollStatus;
    private int scrollYPos;
    private int scrollPos;

    private Object selectedItem;

    private ListListner listner;

    private int startIndex = 0;
    private int itemsShown = 0;

    public ListSelect(String text)
    {
        this.text = text;
        searchText = "";
        items = new ArrayList<>();
        filteredItems = new ArrayList<>();

        borderColor     = new Color[4];
        listColor       = new Color[4];
        textColor       = new Color[4];
        listTextColor   = new Color[4];

        borderColor     [NORMAL] = new Color(0);//new Color(40);
        listColor       [NORMAL] = new Color(0);
        textColor       [NORMAL] = new Color(200);
        listTextColor   [NORMAL] = new Color(200);

        borderColor     [HOVER] = new Color(60);
        listColor       [HOVER] = new Color(0);
        textColor       [HOVER] = new Color(255);


        borderColor     [PRESSED] = new Color(64, 128, 255);
        listColor       [PRESSED] = new Color(0, 0, 0);
        textColor       [PRESSED] = new Color(255);

        borderColor     [ACTIVE] = new Color(64, 128, 255);
        listColor       [ACTIVE] = new Color(0, 0, 0);
        textColor       [ACTIVE] = new Color(255);
        listTextColor   [ACTIVE] = new Color(0);

        padding = new Insets(12, 12, 8, 8);
        status = NORMAL;
        scrollStatus = NORMAL;
        selectedIndex = 0;
        listner = null;
    }

    public void setItems(List<Object> items)
    {
        this.items = items;
        filterItems();
    }

    public List<Object> getItems() { return items; }

    public Object getSelectedItem() {
        return selectedItem;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    @Override
    public void init(Renderer render)
    {
        super.init(render);
        font = render.loadFont("small_arial.fnt");
        smallFont = render.loadFont("small_arial.fnt");
        calculateSize();
    }

    @Override
    public void setPadding(Insets padding)
    {
        super.setPadding(padding);
        if(render != null)
            calculateSize();
    }

    public void setListner(ListListner listner)
    {
        this.listner = listner;
    }

    public void setText(String text)
    {
        this.text = text;
        calculateSize();
    }

    private void calculateSize()
    {
        fixWidth = render.textWidth(font, text)    + borderWidth * 2 + padding.getLeft() + padding.getRight() + triangleWidth + Math.max(padding.getLeft(), padding.getRight());
        fixHeight = render.textHeight(font, text)  + borderWidth * 2 + padding.getTop()  + padding.getBottom();
    }

    private void filterItems()
    {
        filteredItems = new ArrayList<>();
        for(int i = 0; i < items.size(); i++)
        {
            String item = items.get(i).toString();
            if(item.toString().toLowerCase().contains(searchText.toLowerCase()))
                filteredItems.add(i);
        }
        startIndex = 0;
    }

    @Override
    public void draw(Section section)
    {
        super.draw(section);

        float windowWidth = render.getWindowWidth();
        float windowHeight = render.getWindowHeight();

        // draw button
        render.drawRectangleFilled(section.x, section.y, Renderer.Level.FOREGROUND, section.width, section.height, listColor[status]);

        // draw border
        render.drawRectangle(section.x, section.y, Renderer.Level.FOREGROUND, section.width, section.height, borderWidth, borderColor[status]);

        // draw text
        String displayText = text;
        if(searchText.length() > 0 && status == ACTIVE)
            displayText = searchText;

        render.drawText(font, displayText,
                section.x + (padding.getLeft() + borderWidth)/windowWidth,
                section.y + (padding.getTop() + borderWidth)/windowHeight, Renderer.Level.FOREGROUND, textColor[status]);

        // draw triangle
        float triangleX = section.x + (padding.getLeft() + borderWidth + render.textWidth(font, text) + Math.max(padding.getLeft(), padding.getRight()) + triangleWidth / 2)/windowWidth;
        float triangleY = section.y + (fixHeight/2)/windowHeight;
        render.drawTriangle(triangleX, triangleY, Renderer.Level.FOREGROUND, triangleWidth/windowHeight, 0, textColor[status]);

        int itemHeight = 0;
        int itemWidth = 0;
        if(status >= PRESSED) {
            // calculate height
            itemHeight = render.textHeight(smallFont) + padding.getTop() + padding.getBottom();
            itemsShown = (int) Math.floor((1 - (section.y + fixHeight / windowHeight)) / (itemHeight / windowHeight));
            itemsShown = Math.min(itemsShown, filteredItems.size());

            // calculate width

            for(int i = 0; i < items.size(); i++) {
                int width = render.textWidth(smallFont, items.get(i).toString()) + padding.getLeft() + padding.getRight();
                if(width > itemWidth)
                    itemWidth = width;
            }
            itemWidth = Math.max(itemWidth + scrollWidth, fixWidth);

            // draw background
            render.drawRectangleFilled(section.x, section.y + fixHeight / windowHeight, Renderer.Level.POPUP,
                    itemWidth / windowWidth, (itemsShown * itemHeight) / windowHeight, borderColor[NORMAL]);


            int shown = 0;
            for(int i = startIndex; i < filteredItems.size() && shown < itemsShown; i++) {
                int index = filteredItems.get(i);

                // draw selected item background
                if(selectedIndex == index)
                    render.drawRectangleFilled(section.x, section.y + (fixHeight + shown*itemHeight) / windowHeight, Renderer.Level.POPUP,
                            itemWidth / windowWidth, itemHeight / windowHeight, borderColor[ACTIVE]);

                // draw item
                render.drawText(smallFont, items.get(index).toString(),
                        section.x + padding.getLeft() / windowWidth,
                        section.y + (padding.getTop() + (fixHeight + shown*itemHeight)) / windowHeight, Renderer.Level.POPUP, selectedIndex == index ? listTextColor[ACTIVE] : listTextColor[NORMAL]);

                shown++;
            }

            // draw items border
            render.drawRectangle(section.x, section.y + fixHeight / windowHeight, Renderer.Level.POPUP,
                    itemWidth / windowWidth, (itemsShown * itemHeight) / windowHeight, 1, borderColor[HOVER]);
        }




        // draw scrollbar
        float scrollHeight = (itemsShown*itemHeight) * (itemsShown/(float)filteredItems.size());
        float scrollStart = (itemsShown*itemHeight-scrollHeight) * (startIndex/(float)(filteredItems.size()-itemsShown));

        if(itemsShown != filteredItems.size()) {
            render.drawRectangleFilled(
                    section.x + (itemWidth - scrollWidth) / windowWidth,
                    section.y + (fixHeight + scrollStart) / windowHeight, Renderer.Level.POPUP, scrollWidth / windowWidth, scrollHeight / windowHeight, textColor[scrollStatus]);
        }


    }



    @Override
    public boolean mouseEvent(MouseEvent event, int x, int y, double z, MouseButton button)
    {
        boolean onButton = false;
        boolean onListMenu = false;
        boolean onListItem = false;
        boolean onScrollBar = false;
        selectedIndex = -1;

        // set location booleans
        if(x > 0 && x < fixWidth && y > 0 && y < fixHeight)
            onButton = true;

        int itemHeight = render.textHeight(smallFont) + padding.getTop() + padding.getBottom();
        int scrollHeight = 0;
        int scrollStart = 0;
        if(status == ACTIVE) {
            // calculate height


            // calculate width
            int itemWidth = 0;
            for (int i = 0; i < items.size(); i++) {
                int width = render.textWidth(smallFont, items.get(i).toString()) + padding.getLeft() + padding.getRight();
                if (width > itemWidth)
                    itemWidth = width;
            }
            itemWidth = Math.max(itemWidth + scrollWidth, fixWidth);
            scrollHeight = (int)((itemsShown * itemHeight) * (itemsShown / (float) filteredItems.size()));
            scrollStart = (int)((itemsShown * itemHeight - scrollHeight) * (startIndex / (float) (filteredItems.size() - itemsShown)));

            if (x > 0 && x < itemWidth && y > fixHeight && y < fixHeight + itemHeight * itemsShown)
                onListMenu = true;

            if (onListMenu && x > itemWidth - scrollWidth && y > fixHeight + scrollStart && y < fixHeight + scrollStart + scrollHeight)
                onScrollBar = true;

            if (onListMenu && !onScrollBar)
                onListItem = true;

            if (onListItem)
                selectedIndex = filteredItems.get(startIndex + ((y - fixHeight) / itemHeight));
        }

        // set hover and normal states
        if(onButton && status == NORMAL)
            status = HOVER;
        if(onScrollBar && scrollStatus == NORMAL)
            scrollStatus = HOVER;
        if(!onScrollBar && scrollStatus == HOVER)
            scrollStatus = NORMAL;
        if(!onButton && !onListMenu && status == HOVER)
            status = NORMAL;

        // press events
        if(event == MouseEvent.PRESS && button == MouseButton.LEFT)
        {
            if(onButton && status == HOVER) {
                status = PRESSED;
                return true;
            }

            if(onListItem)
            {
                selectedItem = items.get(selectedIndex);
                if(listner != null)
                    listner.itemSelected(selectedIndex);
                status = HOVER;
            }

            if(onScrollBar && scrollStatus == HOVER)
            {
                scrollStatus = PRESSED;
                scrollYPos = y;
                scrollPos = scrollStart;
                return true;
            }
            if(!onButton && !onListMenu)
                status = NORMAL;
        }

        // release events
        if(event == MouseEvent.RELEASE && button == MouseButton.LEFT) {

            if (status == PRESSED)
                status = ACTIVE;

            if(scrollStatus == PRESSED)
                scrollStatus = NORMAL;
        }

        // slider
        int newStartIndex = startIndex;
        if(scrollStatus == PRESSED)
        {
            int newScrollPos = scrollPos + (y - scrollYPos);
            newStartIndex = (int)(newScrollPos * (filteredItems.size() - itemsShown) / (float)(itemsShown*itemHeight-scrollHeight)); // derived from scrollStart = ...
        }

        // scroll
        if(event == MouseEvent.SCROLL)
        {
            if(z > 0)
                newStartIndex--;
            else if(z < 0)
                newStartIndex++;
        }

        if(newStartIndex >= 0 && newStartIndex <= filteredItems.size() - itemsShown)
            startIndex = newStartIndex;


        return status >= HOVER;
    }

    @Override
    public boolean keyboardEvent(KeyEvent event, int key){
        if(event == KeyEvent.CHAR)
            searchText += (char) key;


        if(event == KeyEvent.RELEASE || event == KeyEvent.REPEAT) {

            if(key == 259 && searchText.length() > 0) // backspace
                searchText = searchText.substring(0, searchText.length()-1);
        }

        filterItems();

        return true;
    }

}
