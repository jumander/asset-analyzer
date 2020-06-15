package graphics;

/**
 * Created by johannes on 16/02/20.
 */
public class Color {

    private float r;
    private float g;
    private float b;

    public Color(int r, int g, int b) {
        this.r = r/255f;
        this.g = g/255f;
        this.b = b/255f;
    }

    public Color(int b)
    {
        this.r = b/255f;
        this.g = b/255f;
        this.b = b/255f;
    }

    public Color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public float r() {
        return r;
    }

    public float g() {
        return g;
    }

    public float b() {
        return b;
    }
}
