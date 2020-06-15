package assetAnalyzer;

import GUI.ComponentState;
import GUI.components.Component;
import GUI.components.InputEventListener;
import GUI.layout.Section;
import financial.Asset;
import financial.AssetValue;
import financial.Time;
import graphics.Color;
import graphics.Renderer;
import shader.Shader;
import text.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static GUI.ComponentState.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * Created by johannes on 16/12/17.
 */
public class AssetGraph extends Component {

    private final double MOUSE_THRESHOLD = 20;

    private Time left;
    private Time right;
    private float max = 300;
    private float min = 0;

    private Time anchor;
    private ComponentState anchorState = NORMAL;
    private boolean anchorHold = true;


    private int smallFont;

    List<DrawableAsset> assets = null;

    Shader assetShader;
    AssetGraphScale scale;

    @Override
    public void init(Renderer renderer)
    {
        super.init(renderer);
        smallFont = renderer.loadFont("small_arial.fnt");
        scale = new AssetGraphScale(renderer);
        left = new Time(scale.getMinTime());
        right = new Time(scale.getMaxTime());
        anchor = new Time((right.toInt() - left.toInt())/2 + left.toInt());

        assetShader = new Shader();
        assetShader.load("graphLineRelative.vs", "graphLine.fs");

        assets = new CopyOnWriteArrayList<>();
    }

    @Override
    synchronized public void draw(Section section)
    {
        super.draw(section);

        // initialize new assets
        for(DrawableAsset a : assets)
        {
            if(!a.isInitialized())
                a.init();
        }

        glLineWidth(1);
        scale.drawVertical(section.x, section.y, section.width, section.height);
        scale.drawHorizontal(section.x, section.y, section.width, section.height);
        scale.drawCurrentTime(section.x, section.y, section.width, section.height);
        drawAnchor(section);

        calculateVerticalScale();

        // draw assets
        int xPos = (int)(section.x * render.getWindowWidth());
        for(DrawableAsset line : assets) {
            line.draw(render, section, left, right, min, max);

            render.drawText(smallFont, line.getName(), xPos/(float)render.getWindowWidth(), section.y, Renderer.Level.FOREGROUND, line.getColor());
            xPos += render.textWidth(smallFont, line.getName() + ", ");
        }

        // draw info mouse
        drawInfo(section);
    }

    private void drawAnchor(Section s) {

        float r = 0.6f;
        if(anchorState != NORMAL)
            r = 0.8f;
        float g = 0.3f;
        float b = 0.2f;

        if(anchorHold && anchorState == NORMAL)
            return;

        float x = (anchor.toInt() - left.toInt()) / (float)(right.toInt()-left.toInt());
        if(anchorHold)
            x = 0;
        if(x < 0 || x > 1)
            return;

        float z = 0.75f;

        glUseProgram(0);
        glBegin(GL_LINES);
        glColor3f(r, g, b);
        glVertex3f(2 * (s.x + s.width * x) - 1, - 2 *(s.y) + 1, z);
        glColor3f(r, g, b);
        glVertex3f(2 * (s.x + s.width * x) - 1, - 2 *(s.y + s.height - s.height / 25.0f) + 1, z);
        glEnd();
    }

    private void drawInfo(Section section) {
        int width = right.toInt() - left.toInt();
        float height = max - min;
        for(DrawableAsset line : assets) {
            // find mouse cursor time
            int at = left.toInt() + (int)((lastXPos / (float)currWidth)*(float)width);

            // get closes value
            int index = line.find(new Time(at), Asset.choose.CLOSEST);
            if(index == -1)
                break;
            AssetValue val = line.getValues().get(index);

            float anchorValue = line.getAnchor();
            if(anchorValue == 0)
                continue;

            // get position in pixels of the closest value
            int x = (int)(((val.time.toInt() - left.toInt())/(float)width) * currWidth);
            int y = (int)((1-((val.value/anchorValue - min)/height)) * currHeight);

            double dist = Math.sqrt(Math.pow(x-lastXPos, 2) + Math.pow(y-lastYPos, 2));
            if(dist < MOUSE_THRESHOLD) {

                String text = String.format("%s: %.2f %.2f%%", line.getName(), val.value, ((val.value/anchorValue)*100-100));

                render.drawText(smallFont, text, (int) (x + section.x * render.getWindowWidth()) + 20, (int) (y + section.y * render.getWindowHeight()), Renderer.Level.FOREGROUND, line.getColor());
                if(val.info != null)
                {
                    render.drawText(smallFont, val.info, (int) (x + section.x * render.getWindowWidth()) + 20, (int) (y + section.y * render.getWindowHeight()) + 18, Renderer.Level.FOREGROUND, line.getColor());
                }
            }

            //System.out.println(x + " " + y + " : " + lastXPos + " " + lastYPos);
        }
    }



    synchronized public void addAsset(Asset newAsset) {

        assets.add(new DrawableAsset(newAsset, assetShader));

    }

    synchronized public void addAsset(Asset newAsset, Color color) {

        assets.add(new DrawableAsset(newAsset, color, assetShader));

    }

    synchronized public void removeAsset(Asset asset) {

        assets.remove(asset);
    }

    public void removeAllAssets() {
        assets.clear();
    }

    private void calculateVerticalScale() {
        // find min and max values
        if(assets.size() == 0)
            return;

        max = Float.MIN_VALUE;
        min = Float.MAX_VALUE;

        for(DrawableAsset line : assets) {
            int start = line.find(left, Asset.choose.CLOSEST);
            int end = line.find(right, Asset.choose.CLOSEST);
            int index = line.find(anchor, Asset.choose.CLOSEST);
            float anchorValue = 0;
            if(index != -1)
            {
                List<AssetValue> values = line.getValues();
                // try to find an anchor value that is not 0
                for(int i = index; i < values.size(); i++)
                {
                    if(values.get(i).value != 0)
                    {
                        anchorValue = values.get(i).value;
                        break;
                    }
                }
            }
            line.setAnchor(anchorValue);
            if(anchorValue == 0)
                continue;

            if(start == -1 || end == -1)
                break;

            for (int i = start; i <= end; i++) {
                float val = line.getValues().get(i).value/anchorValue;
                if (val > max)
                    max = val;
                if (val < min)
                    min = val;
            }

        }
        float span = max - min;
        max += 0.2f * span;
        min -= 0.2f * span;

        scale.setVertical(min, max);
    }

    /*private void calculateVerticalScale() {
        // find min and max values
        max = Float.MIN_VALUE;
        min = Float.MAX_VALUE;
        for(DrawableAsset line : assets) {
            int start = line.find(left, Asset.choose.CLOSEST);
            int end = line.find(right, Asset.choose.CLOSEST);

            if(start == -1 || end == -1)
                break;

            for (int i = start; i <= end; i++) {
                float val = line.getValues().get(i).value;
                if (val > max)
                    max = val;
                if (val < min)
                    min = val;
            }

        }
        float span = max - min;
        max += 0.2f * span;
        min -= 0.2f * span;

        scale.setVertical(min, max);
    }*/

    private int lastXPos = 0;
    private int lastYPos = 0;
    private boolean mouseDown = false;


    @Override
    public boolean mouseEvent(MouseEvent event, int x, int y, double z, MouseButton button)
    {
        // anchor
        float anchorXpos = (anchor.toInt() - left.toInt()) / (float)(right.toInt()-left.toInt());



        if(event == MouseEvent.PRESS && button == MouseButton.LEFT)
        {
            if(anchorState == HOVER)
                anchorState = PRESSED;
            else
                mouseDown = true;
        }
        if(event == MouseEvent.RELEASE && button == MouseButton.LEFT) {
            mouseDown = false;
            if(anchorState == PRESSED)
                anchorState = NORMAL;
        }

        if(Math.abs(anchorXpos * currWidth - x) < MOUSE_THRESHOLD || (anchorHold && Math.abs(x) < MOUSE_THRESHOLD))
        {
            if(anchorState == NORMAL)
                anchorState = HOVER;

        } else if(anchorState == HOVER)
            anchorState = NORMAL;

        // scroll
        int width = right.toInt() - left.toInt();
        if((width > 200 || z < 0) && (width < 1280000000 || z > 0)) {
            left = new Time(left.toInt() + (int)(width * 0.07f * z));
            right = new Time(right.toInt() - (int)(width * 0.07f * z));

            int span = right.toInt() - left.toInt();
            if(left.getYear() < 1960)
            {
                left = new Time(1960, 1, 1);
                right = new Time(left.toInt() + span);
            }
            if(right.getYear() >= 2035)
            {
                right = new Time(2035, 1, 1);
                left = new Time(right.toInt() - span);
            }
            scale.setHorizontal(left.toInt(), right.toInt());
        }

        // drag scale
        if(mouseDown)
        {
            int dx = (x - lastXPos);
            left = new Time((left.toInt() - (int)(width * (dx/(float)currWidth))));
            right = new Time((right.toInt() -(int)(width * (dx/(float)currWidth))));
            int span = right.toInt() - left.toInt();
            if(left.getYear() < 1960)
            {
                left = new Time(1960, 1, 1);
                right = new Time(left.toInt() + span);
            }
            if(right.getYear() >= 2035)
            {
                right = new Time(2035, 1, 1);
                left = new Time(right.toInt() - span);
            }
            scale.setHorizontal(left.toInt(), right.toInt());
        }

        // drag anchor
        if(anchorState == PRESSED)
        {
            int dx = (x - lastXPos);
            if(x < MOUSE_THRESHOLD) {

                anchorHold = true;
                anchor = new Time(1960, 1, 1);
            }
            else {
                if(anchorHold)
                    anchor = new Time(left.toInt());
                anchorHold = false;
                anchor = new Time((anchor.toInt() + (int)(width * (dx/(float)currWidth))));
            }




        }



        lastXPos = x;
        lastYPos = y;

        return mouseDown || anchorState != NORMAL;
    }


    public void setWindow(Time start, Time end) {
        left = start;
        right = end;
    }
}
