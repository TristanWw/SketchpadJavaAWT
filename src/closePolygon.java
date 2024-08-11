import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

class ClosePolygon extends baseObj {
    private List<Point> points;
    private Polygon polygon;

    ClosePolygon(List<Point> points) {
        this.points = new ArrayList<>(points);
        updatePolygon();
    }

    private void updatePolygon() {
        int[] xPoints = points.stream().mapToInt(p -> p.x).toArray();
        int[] yPoints = points.stream().mapToInt(p -> p.y).toArray();
        polygon = new Polygon(xPoints, yPoints, points.size());
    }

    @Override
    void translate(double dx, double dy) {
        for (Point p : points) {
            p.translate((int) dx, (int) dy);
        }
        updatePolygon();
    }

    @Override
    void draw(Graphics g, int offsetX, int offsetY) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getColor());

        // Translate graphics context
        g2.translate(offsetX, offsetY);

        // Draw the polygon outline
        g2.drawPolygon(polygon);

        // Revert translation
        g2.translate(-offsetX, -offsetY);
    }

    @Override
    boolean contains(Point p, int offsetX, int offsetY) {
        Point translatedPoint = new Point(p.x - offsetX, p.y - offsetY);
        return polygon.contains(translatedPoint);
    }

    @Override
    void gradient(Graphics g, int offsetX, int offsetY) {
        Graphics2D g2 = (Graphics2D) g;
        Rectangle2D bounds = polygon.getBounds2D();

        // Create a GradientPaint object
        GradientPaint gradientPaint = new GradientPaint(
                (float) bounds.getX(), (float) bounds.getY(), Color.CYAN,
                (float) (bounds.getX() + bounds.getWidth()), (float) (bounds.getY() + bounds.getHeight()),
                Color.MAGENTA);

        g2.setPaint(gradientPaint);
        g2.setStroke(new BasicStroke(2)); // Set stroke size

        // Translate graphics context
        g2.translate(offsetX, offsetY);

        // Draw the polygon with gradient
        g2.drawPolygon(polygon);

        // Revert translation
        g2.translate(-offsetX, -offsetY);
    }

    @Override
    java.awt.Rectangle getBounds() {
        return polygon.getBounds();
    }

    @Override
    baseObj copy() {
        List<Point> temp = new ArrayList<>();
        for (Point p: points) {
            temp.add(new Point(p.x,p.y));
        }
        return new ClosePolygon(new ArrayList<>(temp));
    }
}
