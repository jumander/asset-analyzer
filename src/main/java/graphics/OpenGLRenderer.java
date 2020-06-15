package graphics;

import org.lwjgl.BufferUtils;
import shader.Shader;
import text.Text;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.nio.IntBuffer;
import java.util.ArrayList;


/**
 * Created by johannes on 16/12/17.
 */
public class OpenGLRenderer implements Renderer{
    private long device;
    private ArrayList<Text> fontList;

    private Shader rectShader;

    public OpenGLRenderer(long window)
    {
        fontList = new ArrayList<>();
        this.device = window;
    }

    public boolean init()
    {
        // Set OpenGL parameters
        glClearColor(0, 0, 0, 0);

        glEnable(GL_DEPTH_TEST);
        glClearDepth(1.0f);
        glDepthRange(0.0f, 1.0f);
        glDepthFunc(GL_LEQUAL);
        glDepthMask(true);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        //glDepthRange(-10, 10);
        //import rectangle shader
        rectShader = new Shader();
        if(!rectShader.load("rectangle.vs", "rectangle.fs"))
            return false;

        return true;
    }

    @Override
    public void begin()
    {
        glClear(GL_DEPTH_BUFFER_BIT);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    @Override
    public boolean windowShouldClose() {
        return glfwWindowShouldClose(device);
    }

    @Override
    public int getWindowWidth() {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        glfwGetWindowSize(device, width, height);
        return  width.get(0);
    }

    @Override
    public int getWindowHeight() {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        glfwGetWindowSize(device, width, height);
        return  height.get(0);
    }

    @Override
    public void drawRectangle(float x, float y, Level level, float width, float height, int lineWidth, Color color) {
        rectShader.use();
        int loc = glGetUniformLocation(rectShader.getProgram(), "color");
        glUniform3f(loc, color.r(), color.g(), color.b());

        float windowWidth = getWindowWidth();
        float windowHeight = getWindowHeight();

        float xLine = lineWidth/windowWidth;
        float yLine = lineWidth/windowHeight;

        if(xLine > width)
            xLine = width;
        if(yLine > height)
            yLine = height;

        float z = getZValue(level);

        glBegin(GL_QUADS);
        glVertex3f(x, y, z);                          glVertex3f(x + width, y, z);                        // top line
        glVertex3f(x + width, y + yLine, z);          glVertex3f(x, y + yLine, z);

        glVertex3f(x + width - xLine, y, z);          glVertex3f(x + width, y, z);                        // right line
        glVertex3f(x + width, y + height, z);         glVertex3f(x + width - xLine, y + height, z);

        glVertex3f(x, y + height - yLine, z);         glVertex3f(x + width, y + height - yLine, z);       // down line
        glVertex3f(x + width, y + height, z);         glVertex3f(x, y + height, z);

        glVertex3f(x, y, z);                          glVertex3f(x + xLine, y, z);                        // left line
        glVertex3f(x + xLine, y + height, z);         glVertex3f(x, y + height, z);
        glEnd();
    }

    @Override
    public void drawRectangle(int x, int y, Level level, int width, int height, int lineWidth, Color color) {
        float windowWidth = getWindowWidth();
        float windowHeight = getWindowHeight();
        drawRectangle(x/windowWidth, y/windowHeight, level, width/windowWidth, height/windowHeight, lineWidth, color);
    }

    @Override
    public void drawRectangleFilled(float x, float y, Level level, float width, float height, Color color) {
        rectShader.use();
        int loc = glGetUniformLocation(rectShader.getProgram(), "color");
        glUniform3f(loc, color.r(), color.g(), color.b());

        float z = getZValue(level);

        glBegin(GL_QUADS);
        glVertex3f(x, y, z);
        glVertex3f(x + width, y, z);
        glVertex3f(x + width, y + height, z);
        glVertex3f(x, y + height, z);
        glEnd();
    }

    @Override
    public void drawRectangleFilled(int x, int y, Level level, int width, int height, Color color) {
        float windowWidth = getWindowWidth();
        float windowHeight = getWindowHeight();
        drawRectangleFilled(x/windowWidth, y/windowHeight, level, width/windowWidth, height/windowHeight, color);
    }

    @Override
    public int loadFont(String font)
    {
        // Check if font already exists
        for(int i = 0; i < fontList.size(); i++)
        {
            if(fontList.get(i).getFileName().equals(font))
                return i;
        }

        // Otherwise load the font
        Text text = new Text();
        if(!text.load(font))
            return -1;

        fontList.add(text);
        return fontList.size()-1;
    }

    @Override
    public void drawText(int fontIndex, String text, float x, float y, Level level, Color color) {
        drawText(fontIndex, text, x, y, level, color, Text.Align.RIGHT);
    }

    @Override
    public void drawText(int fontIndex, String text, int x, int y, Level level, Color color) {
        float windowWidth = getWindowWidth();
        float windowHeight = getWindowHeight();
        drawText(fontIndex, text, x/windowWidth, y/windowHeight, level, color);
    }

    @Override
    public void drawText(int fontIndex, String text, float x, float y, Level level, Color color, Text.Align align) {
        if(fontIndex > -1 && fontIndex < fontList.size())
        {
            fontList.get(fontIndex).setColor(color);
            float z = getZValue(level);
            fontList.get(fontIndex).draw(text, x, y, z, getWindowWidth(), getWindowHeight(), align);
        }
    }

    @Override
    public void drawText(int fontIndex, String text, int x, int y, Level level, Color color, Text.Align align) {
        float windowWidth = getWindowWidth();
        float windowHeight = getWindowHeight();
        drawText(fontIndex, text, x/windowWidth, y/windowHeight, level, color, align);
    }

    @Override
    public int textWidth(int fontIndex, String text) {
        if(fontIndex > -1 && fontIndex < fontList.size()) {
            return fontList.get(fontIndex).textWidth(text);
        }
        return 0;
    }

    @Override
    public int textHeight(int fontIndex, String text) {
        if(fontIndex > -1 && fontIndex < fontList.size()) {
            return fontList.get(fontIndex).textHeight(text);
        }
        return 0;
    }

    @Override
    public int textHeight(int fontIndex) {
        if(fontIndex > -1 && fontIndex < fontList.size()) {
            return fontList.get(fontIndex).textHeight("Å");
        }
        return 0;
    }

    @Override
    public void drawTriangle(float x, float y, Level level, float width, int angle, Color color) {
        float z = getZValue(level);

        glUseProgram(0);
        glBegin(GL_TRIANGLES);
        glColor3f(color.r(), color.g(), color.b());
        glVertex3(x - width/2, y - width/2, z);
        glVertex3(x + width/2, y - width/2, z);
        glVertex3(x, y + width/2, z);
        glEnd();
    }

    private void glVertex3(float x, float y, float z)
    {
        glVertex3f(2*(x-0.5f), -2*(y-0.5f), z);
    }

    @Override
    public void present() {
        glfwSwapBuffers(device);
        glfwPollEvents();
    }

    private float getZValue(Level level)
    {
        float z = 1;
        switch(level)
        {
            case POPUP:
                z -= 0.25f;
            case FOREGROUND:
                z -= 0.25f;
        }
        return z;
    }

    public long getDevice() {
        return device;
    }
}
