package text;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import shader.Shader;

import graphics.Color;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * Created by johannes on 16/02/16.
 */
public class Text {

    private class Letter {
        public int x;           // x position in texture
        public int y;           // y position in texture
        public int width;       // width of letter in texture
        public int height;      // height of letter in texture
        public int xOffset;     // x-offset to draw letter
        public int yOffset;     // y-offset to draw letter
        public int xAdvance;    // how much to advance after this letter when drawing
    }


    private ArrayList<Letter> letters;

    private String fontName;    // the name of the font
    private String fileName;    // the name of the opened font file
    private int size;           // the height of tallest characters in px
    private int base;           // the height of the tallest characters from the top to the line in px

    private int width;          // the width of the texture containing the letters
    private int height;         // the height of the texture containing the letters.

    private Shader shader;  // shader to render the text
    private int texture;            // the texture containing the letters

    public enum Align{LEFT, RIGHT, UP, DOWN};

    public int textWidth(String text) {

        int length = 0;
        for(int i = 0; i < text.length(); i++)
            length += letters.get((int)text.charAt(i)).xAdvance;
        return length;
    }

    public int textHeight(String text) {
        int maxHeight = 0;

        int minYOffset = Integer.MAX_VALUE;
        for(int i = 0; i < text.length(); i++) {
            Letter letter = letters.get((int)text.charAt(i));
            if(letter.yOffset < minYOffset)
                minYOffset = letter.yOffset;
        }

        for (int i = 0; i < text.length(); i++) {
            int index = (int) text.charAt(i);
            if(index == ' ')
                continue;
            int letterHeight = Math.min(base-minYOffset, letters.get(index).height + Math.abs(letters.get(index).yOffset-minYOffset));

            if (letterHeight > maxHeight)
                maxHeight = letterHeight;

        }
        return maxHeight;
    }

    public void draw(String text, float x, float y, float z, int windowWidth, int windowHeight, Align align) {
        float pixelHSize = 1/(float)windowWidth;
        float pixelVSize = 1/(float)windowHeight;

        float texelHSize = 1/(float)width;
        float texelVSize = 1/(float)height;

        // make sure x and y are on pixels and not half pixels
        x = (float)Math.floor(x*windowWidth)/(float)windowWidth;
        y = (float)Math.floor(y*windowHeight)/(float)windowHeight;

        shader.use();
        int texLoc = glGetAttribLocation(shader.getProgram(), "tex");
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);

        int length = 0;
        int height = 0;
        int minYOffset = Integer.MAX_VALUE;
        for(int i = 0; i < text.length(); i++) {
            Letter letter = letters.get((int)text.charAt(i));
            length += letter.xAdvance;
            int h = letter.height;
            int yOffset = letter.yOffset;
            if(h > height)
                height = h;
            if(yOffset < minYOffset)
                minYOffset = yOffset;
        }

        if(align == Align.LEFT)
            x -= length * pixelHSize;

        if(align == Align.UP || align == Align.DOWN) {
            x -= (length / 2) * pixelHSize;
            if(align == Align.UP)
                y -= height * pixelVSize;
            else
                y += height * pixelVSize;
        }


        for(int i = 0; i < text.length(); i++) {

            Letter l = letters.get((int)text.charAt(i));

            float leftX = x;
            float highY = y;
            float rightX = x + (l.width) * pixelHSize;
            float lowY = y + (l.height) * pixelVSize;
            leftX += l.xOffset * pixelHSize;
            rightX += l.xOffset * pixelHSize;
            highY += (l.yOffset-minYOffset) * pixelVSize;
            lowY += (l.yOffset-minYOffset) * pixelVSize;

            float leftS = (l.x) * texelHSize;
            float highT = (l.y) * texelVSize;
            float rightS = (l.x + l.width) * texelHSize;
            float lowT = (l.y + l.height) * texelVSize;


            glBegin(GL_QUADS);
            glVertexAttrib2f(texLoc, leftS,  highT);
            glVertex3f(2*(leftX) - 1, -1 * (2*(highY) - 1), z);

            glVertexAttrib2f(texLoc, leftS,  lowT);
            glVertex3f(2*(leftX) - 1, -1 * (2*(lowY) - 1), z);

            glVertexAttrib2f(texLoc, rightS,  lowT);
            glVertex3f(2*(rightX) - 1, -1 * (2*(lowY) - 1), z);

            glVertexAttrib2f(texLoc, rightS,  highT);
            glVertex3f(2*(rightX) - 1, -1 * (2*(highY) - 1), z);
            glEnd();

            x += l.xAdvance * pixelHSize;
        }
    }

    public void setColor(Color color) {
        shader.use();
        int loc = glGetUniformLocation(shader.getProgram(), "color");
        glUniform3f(loc, color.r(), color.g(), color.b());
    }

    public boolean load(String font) {

        fileName = font;
        String path = "fonts/";

        try {
            //String path = Text.class.getProtectionDomain().getCodeSource().getLocation().getPath();

            File xml = new File(path + font);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xml);

            doc.getDocumentElement().normalize();

            // INFO
            Element info = (Element)doc.getElementsByTagName("info").item(0);

            fontName = info.getAttribute("face");
            size = Integer.parseInt(info.getAttribute("size"));

            // COMMON
            Element common = (Element)doc.getElementsByTagName("common").item(0);

            width = Integer.parseInt(common.getAttribute("scaleW"));
            height = Integer.parseInt(common.getAttribute("scaleH"));
            base = Integer.parseInt(common.getAttribute("base"));

            // PAGE
            Element pages = (Element)doc.getElementsByTagName("page").item(0);

            String textureFilename = pages.getAttribute("file");

            // CHARS
            NodeList chars = doc.getElementsByTagName("char");
            Element lastChar = (Element)chars.item(chars.getLength()-1);
            int highestID = Integer.parseInt(lastChar.getAttribute("id"));

            letters = new ArrayList<>(highestID + 1);
            for(int i = 0 ; i <= highestID; i++)
                letters.add(null);


            for(int i = 0; i < chars.getLength(); i++) {

                Element element = (Element)chars.item(i);
                int id = Integer.parseInt(element.getAttribute("id"));
                Letter letter = new Letter();
                letter.x = Integer.parseInt(element.getAttribute("x"));
                letter.y = Integer.parseInt(element.getAttribute("y"));
                letter.width = Integer.parseInt(element.getAttribute("width"));
                letter.height = Integer.parseInt(element.getAttribute("height"));
                letter.xOffset = Integer.parseInt(element.getAttribute("xoffset"));
                letter.yOffset = Integer.parseInt(element.getAttribute("yoffset"));
                letter.xAdvance = Integer.parseInt(element.getAttribute("xadvance"));
                letters.add(id, letter);
            }

            try {
                texture = loadTexture(ImageIO.read(new File(path + textureFilename)));
            } catch (IOException e) {
                System.err.println("Unable to read texture " + path + textureFilename);
                return false;
            };

            if(!loadShader("text.vs", "text.fs"))
            {
                return false;
            }

            setColor(new Color(255, 255, 255));

        } catch (Exception e) {
            System.err.println("Unable to read font " + path + font);
            return false;
        }

        return true;
    }

    private boolean loadShader(String vertexShader, String fragmentShader) {
        shader = new Shader();
        return shader.load(vertexShader, fragmentShader);
    }

    private int loadTexture(BufferedImage image) {

        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        int BYTES_PER_PIXEL = 4;
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); //4 for RGBA, 3 for RGB

        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));             // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));      // Alpha component. Only for RGBA
            }
        }

        buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS

        // You now have a ByteBuffer filled with the color data of each pixel.
        // Now just create a texture ID and bind it. Then you can load it using
        // whatever OpenGL method you want, for example:

        int textureID = glGenTextures();            //Generate texture ID
        glBindTexture(GL_TEXTURE_2D, textureID);    //Bind texture ID

        //Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        //Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        //Return the texture ID so we can bind it later again
        return textureID;
    }

    public String getFileName()
    {
        return fileName;
    }

    private int getTexture() {
        return texture;
    }

    private int getShader() {
        return shader.getProgram();
    }
}
