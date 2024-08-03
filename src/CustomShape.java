import java.awt.*;
import java.awt.geom.*;

class CustomShape {
    private Shape shape;
    private Color color;

    public CustomShape(Shape shape, Color color) {
        this.shape = shape;
        this.color = color;
    }

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
        g.draw(shape);
        g.fill(shape);
    }

    public boolean contains(Point p) {
        if (shape instanceof Line2D) {
            Line2D line = (Line2D) shape;
            return line.ptSegDist(p) <= 5.0; // Adjust the threshold as needed
        }
        return shape.contains(p);
    }
    public void move(int dx, int dy) {
        if (shape instanceof Ellipse2D) {
            Ellipse2D ellipse = (Ellipse2D) shape;
            shape = new Ellipse2D.Double(
                    ellipse.getX() + dx,
                    ellipse.getY() + dy,
                    ellipse.getWidth(),
                    ellipse.getHeight()
            );
        } else if (shape instanceof Rectangle2D) {
            Rectangle2D rect = (Rectangle2D) shape;
            shape = new Rectangle2D.Double(
                    rect.getX() + dx,
                    rect.getY() + dy,
                    rect.getWidth(),
                    rect.getHeight()
            );
        } else if (shape instanceof Line2D) {
            Line2D line = (Line2D) shape;
            shape = new Line2D.Double(
                    line.getX1() + dx,
                    line.getY1() + dy,
                    line.getX2() + dx,
                    line.getY2() + dy
            );
        } else if (shape instanceof GeneralPath) {
            GeneralPath path = (GeneralPath) shape;
            AffineTransform transform = new AffineTransform();
            transform.translate(dx, dy);
            shape = path.createTransformedShape(transform);
        }
        System.out.println("New shape: " + shape.getBounds());
    }
}
