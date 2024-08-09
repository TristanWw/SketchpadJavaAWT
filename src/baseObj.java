import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
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

	abstract void translate(double dx, double dy);

	abstract void draw(Graphics g, int offsetX, int offsetY);

	abstract boolean contains(Point p, int offsetX, int offsetY);

	abstract void gradient(Graphics g, int offsetX, int offsetY);

	abstract Rectangle getBounds();

	abstract baseObj copy();
}
