package GUI;

/**
 * Created by johannes on 16/12/18.
 */
public class Insets {
    public int left;
    public int right;
    public int top;
    public int bottom;

    public Insets(int left, int right, int top, int bottom)
    {
        this.left   = left;
        this.right  = right;
        this.top    = top;
        this.bottom = bottom;
    }

    public Insets(int inset)
    {
        this.left   = inset;
        this.right  = inset;
        this.top    = inset;
        this.bottom = inset;
    }

    public int getBottom() {
        return bottom;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public int getTop() {
        return top;
    }
}
