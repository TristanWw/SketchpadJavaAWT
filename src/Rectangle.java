import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

class Rectangle extends baseObj {
    Rectangle2D rect;

    Rectangle(Point startPoint, Point endPoint) {
        int x = Math.min(startPoint.x, endPoint.x);
        int y = Math.min(startPoint.y, endPoint.y);
        int width = Math.abs(endPoint.x - startPoint.x);
        int height = Math.abs(endPoint.y - startPoint.y);
        this.rect = new Rectangle2D.Double(x, y, width, height);
    }

    @Override
    void translate(double dx, double dy) {
        rect.setFrame(rect.getX() + dx, rect.getY() + dy, rect.getWidth(), rect.getHeight());
    }

    @Override
    void draw(Graphics g, int offsetX, int offsetY) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getColor());
        g2.drawRect((int) rect.getX() + offsetX, (int) rect.getY() + offsetY,
                (int) rect.getWidth(), (int) rect.getHeight());
    }

    @Override
    boolean contains(Point p, int offsetX, int offsetY) {
        return rect.contains(p.getX() - offsetX, p.getY() - offsetY);
    }

    @Override
    void gradient(Graphics g, int offsetX, int offsetY) {
        Graphics2D g2 = (Graphics2D) g;
        java.awt.Rectangle bounds = rect.getBounds();

        // Create a GradientPaint object
        GradientPaint gradientPaint = new GradientPaint(bounds.x, bounds.y, Color.CYAN,
                bounds.x + bounds.width, bounds.y + bounds.height,
                Color.MAGENTA);

        g2.setPaint(gradientPaint);
        g2.setStroke(new BasicStroke(2)); // Set stroke size

        // Apply translation
        g2.translate(offsetX, offsetY);

        // Draw the rectangle with gradient
        g2.draw(rect);

        // Revert translation
        g2.translate(-offsetX, -offsetY);
    }

    @Override
    java.awt.Rectangle getBounds() {
        return rect.getBounds();
    }

    @Override
    baseObj copy() {
        return new Rectangle(new Point((int) rect.getX(), (int) rect.getY()),
                new Point((int) (rect.getX() + rect.getWidth()),
                        (int) (rect.getY() + rect.getHeight())));
    }

}
