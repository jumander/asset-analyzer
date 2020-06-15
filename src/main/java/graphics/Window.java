package graphics;

import GUI.components.Console;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by johannes on 16/12/17.
 */
public class Window {

    private long window;

    public Window(int width, int height, String title)
    {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        //glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable
        glfwWindowHint(GLFW_RED_BITS, 8);
        glfwWindowHint(GLFW_GREEN_BITS, 8);
        glfwWindowHint(GLFW_BLUE_BITS, 8);
        glfwWindowHint(GLFW_ALPHA_BITS, 8);
        glfwWindowHint(GLFW_DEPTH_BITS, 24);


        // Create the window
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scanCode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in our rendering loop
        });

        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
                window,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        //Resize window callback
        //glfwSetWindowSizeCallback(window, (w, newWidth, newHeight) -> {
        //    Console.println(String.format("%d x %d", newWidth, newHeight));
        //});

        glfwSetFramebufferSizeCallback(window, (window, currWidth, currHeight) -> {
            GL.createCapabilities();
            glViewport(0, 0, currWidth, currHeight);
        });


        // Make the window visible
        glfwShowWindow(window);

    }



    public OpenGLRenderer getRenderer() {
        GL.createCapabilities();

        OpenGLRenderer render = new OpenGLRenderer(window);
        if(!render.init())
            return null;
        else
            return render;
    }

    public void setMouseCursorPosCallback(GLFWCursorPosCallbackI func)
    {
        glfwSetCursorPosCallback(window, func);
    }

    public void setMouseScrollCallback(GLFWScrollCallbackI func) { glfwSetScrollCallback(window, func); }

    public void setMouseButtonCallback(GLFWMouseButtonCallbackI func)
    {
        glfwSetMouseButtonCallback(window, func);
    }

    public void setKeyCallback(GLFWKeyCallbackI func){glfwSetKeyCallback(window, func);}

    public void setCharCallback(GLFWCharCallbackI func){glfwSetCharCallback(window, func);}
}
