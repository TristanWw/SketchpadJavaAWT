import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

class Square extends baseObj {
    Rectangle2D square;

    Square(Point startPoint, Point endPoint) {
        int x = Math.min(startPoint.x, endPoint.x);
        int y = Math.min(startPoint.y, endPoint.y);
        int sideLength = Math.min(Math.abs(endPoint.x - startPoint.x), Math.abs(endPoint.y - startPoint.y));
        this.square = new Rectangle2D.Double(x, y, sideLength, sideLength);
    }

    @Override
    void translate(double dx, double dy) {
        square.setFrame(square.getX() + dx, square.getY() + dy, square.getWidth(), square.getHeight());
    }

    @Override
    void draw(Graphics g, int offsetX, int offsetY) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getColor());
        g2.drawRect((int) square.getX() + offsetX, (int) square.getY() + offsetY,
                (int) square.getWidth(), (int) square.getHeight());
    }

    @Override
    boolean contains(Point p, int offsetX, int offsetY) {
        return square.contains(p.getX() - offsetX, p.getY() - offsetY);
    }

    @Override
    void gradient(Graphics g, int offsetX, int offsetY) {
        Graphics2D g2 = (Graphics2D) g;
        java.awt.Rectangle bounds = square.getBounds();

        // Create a GradientPaint object
        GradientPaint gradientPaint = new GradientPaint(bounds.x, bounds.y, Color.CYAN,
                bounds.x + bounds.width, bounds.y + bounds.height,
                Color.MAGENTA);

        g2.setPaint(gradientPaint);
        g2.setStroke(new BasicStroke(2)); // Set stroke size

        // Apply translation
        g2.translate(offsetX, offsetY);

        // Draw the square with gradient
        g2.draw(square);

        // Revert translation
        g2.translate(-offsetX, -offsetY);
    }

    @Override
    java.awt.Rectangle getBounds() {
        return square.getBounds();
    }

    @Override
    baseObj copy() {
        return new Square(new Point((int) square.getX(), (int) square.getY()),
                new Point((int) (square.getX() + square.getWidth()),
                        (int) (square.getY() + square.getHeight())));
    }
}
