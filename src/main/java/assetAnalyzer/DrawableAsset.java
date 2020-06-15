package assetAnalyzer;

import GUI.layout.Section;
import financial.Asset;
import financial.Time;
import graphics.Color;
import graphics.Renderer;
import org.lwjgl.BufferUtils;
import shader.Shader;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Created by johannes on 17/05/06.
 */
public class DrawableAsset extends  Asset {

    /* In Asset
    private String assetName;
    private String currency;
    private String market;
    List<AssetValue> values;
    */

    private Color color;

    private float anchor;

    private boolean initialized;

    private int vertexArrayObject;
    private int vertexBufferObject;
    private int bufferSize;
    private Shader shader;

    int capacity = 0;
    int previousSize = 0;

    private static int ID = 0;

    public DrawableAsset(Asset a, Color color, Shader shader) {
        super(a.getName(), a.getMarket(), a.getCurrency(), a.getValues());

        this.shader = shader;
        //init();
        initialized = false;
        anchor = 1;

        this.color = color;
        ID++;
    }

    public DrawableAsset(Asset a, Shader shader) {
        this(a, generateColor(), shader);

    }

    private static Color generateColor()
    {
        ArrayList<Color> colors = new ArrayList<>();
        colors.add(new Color(153, 179, 77));
        colors.add(new Color(240, 120, 60));
        colors.add(new Color(60, 120, 240));
        colors.add(new Color(240, 80, 30));
        colors.add(new Color(240, 240, 60));
        colors.add(new Color(200, 200, 200));

        return (colors.get(ID % colors.size()));
    }


    public boolean isInitialized() {
        return initialized;
    }

    public void init() {
        vertexArrayObject = glGenVertexArrays();
        glBindVertexArray(vertexArrayObject);

        vertexBufferObject = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexArrayObject);

        uploadData();

        int timeAttrib = glGetAttribLocation(shader.getProgram(), "time");
        glVertexAttribIPointer(timeAttrib, 1, GL_INT, 8, 0);
        glEnableVertexAttribArray(timeAttrib);

        int priceAttrib = glGetAttribLocation(shader.getProgram(), "price");
        glVertexAttribPointer(priceAttrib, 1, GL_FLOAT, false, 8, 4);
        glEnableVertexAttribArray(priceAttrib);
        initialized = true;
    }

    public void uploadData(){
        glBindBuffer(GL_ARRAY_BUFFER, vertexArrayObject);

        int newSize = values.size();
        if(newSize > capacity)
        {   // reinitialize buffer
            capacity = (int)(newSize * 1.5);
            ByteBuffer buffer = BufferUtils.createByteBuffer(capacity * 8);
            for (int i = 0; i < newSize; i++) {
                buffer.putInt(values.get(i).time.toInt());
                buffer.putFloat(values.get(i).value);
            }
            buffer.position(0);
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        }
        else {
            // add new data
            ByteBuffer buffer = BufferUtils.createByteBuffer((newSize - previousSize) * 8);
            for (int i = previousSize; i < newSize; i++) {
                buffer.putInt(values.get(i).time.toInt());
                buffer.putFloat(values.get(i).value);
            }
            buffer.position(0);
            glBufferSubData(GL_ARRAY_BUFFER, 8 * previousSize, buffer);

        }
        previousSize = newSize;
    }

    public Color getColor()
    {
        return color;
    }

    public void setAnchor(float anchor)
    {
        this.anchor = anchor;
    }

    public float getAnchor()
    {
        return anchor;
    }

    public void draw(Renderer render, Section section, Time left, Time right, float min, float max)
    {
        // upload eventual new data
        if(previousSize != values.size())
            uploadData();

        if(anchor == 0)
            return;

        shader.use();

        setWindowUniforms(render, section);

        int colorLoc = glGetUniformLocation(shader.getProgram(), "color");
        int leftLoc = glGetUniformLocation(shader.getProgram(), "leftTime");
        int rightLoc = glGetUniformLocation(shader.getProgram(), "rightTime");
        int minLoc = glGetUniformLocation(shader.getProgram(), "lowValue");
        int maxLoc = glGetUniformLocation(shader.getProgram(), "highValue");
        int anchorLoc = glGetUniformLocation(shader.getProgram(), "anchor");


        glUniform1i(leftLoc, left.toInt());
        glUniform1i(rightLoc, right.toInt());

        glUniform1f(minLoc, min);
        glUniform1f(maxLoc, max);

        glUniform1f(anchorLoc, anchor);

        glUniform3f(colorLoc, color.r(), color.g(), color.b());

        glBindVertexArray(vertexArrayObject);
        glDrawArrays(GL_LINE_STRIP, 0, previousSize);
    }


    private void setWindowUniforms(Renderer render, Section section) {
        shader.use();
        int xLoc = glGetUniformLocation(shader.getProgram(), "x");
        int yLoc = glGetUniformLocation(shader.getProgram(), "y");
        int widthLoc = glGetUniformLocation(shader.getProgram(), "width");
        int heightLoc = glGetUniformLocation(shader.getProgram(), "height");

        int windowWidthLoc = glGetUniformLocation(shader.getProgram(), "windowWidth");
        int windowHeightLoc = glGetUniformLocation(shader.getProgram(), "windowHeight");

        glUniform1f(xLoc, section.x);
        glUniform1f(yLoc, section.y);
        glUniform1f(widthLoc, section.width);
        glUniform1f(heightLoc, section.height);

        glUniform1f(windowWidthLoc, (float)render.getWindowWidth());
        glUniform1f(windowHeightLoc, (float)render.getWindowHeight());
    }

}
