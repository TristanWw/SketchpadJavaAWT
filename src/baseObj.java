import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;
import java.io.Serializable;

abstract class baseObj implements Serializable {
    private static final long serialVersionUID = 1L; // for verification
    private Color color;

    public Color getColor() {
        return color;
    }

    public void setColor(Color c) {
        this.color = c;
    }

    abstract void translate(int dx, int dy);

    abstract void draw(Graphics g);

    abstract boolean contains(Point p);

    abstract void gradient(Graphics g);

    abstract baseObj copy();
}
