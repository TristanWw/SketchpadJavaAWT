import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

public class Eclipse extends baseObj {
    private Ellipse2D ellipse;

    public Eclipse(Point startPoint, Point endPoint) {
        int x = Math.min(startPoint.x, endPoint.x);
        int y = Math.min(startPoint.y, endPoint.y);
        int width = Math.abs(endPoint.x - startPoint.x);
        int height = Math.abs(endPoint.y - startPoint.y);
        this.ellipse = new Ellipse2D.Double(x, y, width, height);
    }

    @Override
    void translate(double dx, double dy) {
        ellipse.setFrame(ellipse.getX() + dx, ellipse.getY() + dy, ellipse.getWidth(), ellipse.getHeight());
    }

    @Override
    void draw(Graphics g, int offsetX, int offsetY) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getColor());
        g2.draw(new Ellipse2D.Double(ellipse.getX() + offsetX, ellipse.getY() + offsetY,
                ellipse.getWidth(), ellipse.getHeight()));
    }

    @Override
    boolean contains(Point p, int offsetX, int offsetY) {
        return ellipse.contains(p.getX() - offsetX, p.getY() - offsetY);
    }

    @Override
    void gradient(Graphics g, int offsetX, int offsetY) {
        Graphics2D g2 = (Graphics2D) g;
        java.awt.Rectangle bounds = ellipse.getBounds();

        // Create a GradientPaint object
        GradientPaint gradientPaint = new GradientPaint(bounds.x, bounds.y, Color.CYAN,
                bounds.x + bounds.width, bounds.y + bounds.height,
                Color.MAGENTA);

        g2.setPaint(gradientPaint);
        g2.setStroke(new BasicStroke(2)); // Set stroke size

        // Apply translation
        g2.translate(offsetX, offsetY);

        // Draw the ellipse with gradient
        g2.draw(ellipse);

        // Revert translation
        g2.translate(-offsetX, -offsetY);
    }

    @Override
    java.awt.Rectangle getBounds() {
        return ellipse.getBounds();
    }

    @Override
    baseObj copy() {
        return new Eclipse(new Point((int) ellipse.getX(), (int) ellipse.getY()),
                new Point((int) (ellipse.getX() + ellipse.getWidth()),
                        (int) (ellipse.getY() + ellipse.getHeight())));
    }
}
