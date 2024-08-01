import java.awt.Graphics;

abstract class baseObj {
	abstract void rotate(double angle);
	abstract void translate(int dx, int dy);
	abstract boolean copy();
	abstract void draw(Graphics g);
}
