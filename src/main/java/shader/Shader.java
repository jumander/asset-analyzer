package shader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;

/**
 * Created by johannes on 16/02/16.
 */
public class Shader {

    private int program;

    public boolean load(String vertexShaderFilename, String fragmentShaderFilename) {
        //String path = Shader.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1); // was .substring(1) in windows, does not work in linux

        String path = "shaders/";

        String vertexShaderSource = null;
        try {
            vertexShaderSource = readFile(path + vertexShaderFilename, Charset.defaultCharset());
        } catch (IOException e) {
            System.err.println("Could not open vertexShader: " + vertexShaderFilename);
            return false;
        }

        String fragmentShaderSource = null;
        try {
            fragmentShaderSource = readFile(path + fragmentShaderFilename, Charset.defaultCharset());
        } catch (IOException e) {
            System.err.println("Could not open fragmentShader: " + fragmentShaderFilename);
            return false;
        }


        program = glCreateProgram();
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        if(glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Could not compile vertex shader: "  + vertexShaderFilename);
            System.err.println(glGetShaderInfoLog(vertexShader));
            return false;
        }

        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        if(glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Could not compile fragment shader: "  + fragmentShaderFilename);
            System.err.println(glGetShaderInfoLog(fragmentShader));
            return false;
        }

        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        glValidateProgram(program);

        return true;
    }

    public void use() {
        glUseProgram(program);
    }

    public int getProgram() {
        return program;
    }

    private String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}