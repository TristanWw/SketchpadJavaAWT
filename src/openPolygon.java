import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

class OpenPolygon extends baseObj {
    private List<Point> points;

    OpenPolygon(List<Point> points) {
        this.points = new ArrayList<Point>(points);
    }

    OpenPolygon() {
        this.points = new ArrayList<Point>();
    }

    @Override
    void translate(double dx, double dy) {
        for (Point p : points) {
            p.translate((int) dx, (int) dy);
        }
    }

    @Override
    void draw(Graphics g, int offsetX, int offsetY) {
        if (points.size() < 2) {
            return; // Not enough points to draw
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getColor());
        g2.translate(offsetX, offsetY);

        Point lastPoint = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            Point currentPoint = points.get(i);
            Line2D line = new Line2D.Double(lastPoint, currentPoint);
            g2.draw(line);
            lastPoint = currentPoint;
        }

        g2.translate(-offsetX, -offsetY);
    }

    private boolean isPointNearLineSegment(Point p, Point start, Point end, double epsilon) {
        // Calculate the length of the line segment
        double segmentLengthSquared = start.distanceSq(end);
        if (segmentLengthSquared == 0.0) {
            // The line segment is just a point
            return p.distance(start) < epsilon;
        }

        // Projection formula to find the projection of point p onto the line segment
        double t = ((p.x - start.x) * (end.x - start.x) + (p.y - start.y) * (end.y - start.y)) / segmentLengthSquared;
        t = Math.max(0, Math.min(1, t));

        // Find the nearest point on the segment to p
        double nearestX = start.x + t * (end.x - start.x);
        double nearestY = start.y + t * (end.y - start.y);
        Point nearestPoint = new Point((int) nearestX, (int) nearestY);

        // Check the distance from p to the nearest point on the line segment
        return p.distance(nearestPoint) < epsilon;
    }

    @Override
    boolean contains(Point p, int offsetX, int offsetY) {
        // calculate the distance between the points on the scribbled line and p
        // to determine whether p is on the scribbled line
        double epsilon = 5.0; // Threshold distance to determine if the point is on the line

        Point adjustedP = new Point(p.x - offsetX, p.y - offsetY);
        for (int i = 0; i < points.size() - 1; i++) {
            Point start = points.get(i);
            Point end = points.get(i + 1);

            if (isPointNearLineSegment(adjustedP, start, end, epsilon)) {
                return true;
            }
        }
        return false;
    }

    @Override
    void gradient(Graphics g, int offsetX, int offsetY) {
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(offsetX, offsetY);
        if (points.size() == 0)
            return;

        Point last = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            Point current = points.get(i);

            GradientPaint gradientPaint = new GradientPaint(
                    last.x, last.y, Color.CYAN,
                    current.x, current.y, Color.MAGENTA);

            g2.setPaint(gradientPaint);
            g2.setStroke(new BasicStroke(2));
            Line2D line = new Line2D.Double(last, points.get(i));
            g2.draw(line);
            last = current;
        }
        g2.translate(-offsetX, -offsetY);
    }

    void addPoints(Point p) {
        points.add(p);
    }

    @Override
    java.awt.Rectangle getBounds() {
        if (points.isEmpty()) {
            return new Rectangle();
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Point p : points) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }

        return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    @Override
    baseObj copy() {
        OpenPolygon copy = new OpenPolygon();
        copy.setColor(getColor()); // Copy the color

        // Create a deep copy of the scribbledPoints list
        List<Point> copiedPoints = new ArrayList<>();
        for (Point p : points) {
            copiedPoints.add(new Point(p.x, p.y));
        }
        copy.points = copiedPoints;

        return copy;
    }

    public List<Point> getPoints() {
        List<Point> temp = new ArrayList<Point>();
        for (Point p: points) {
            temp.add(new Point(p.x,p.y));
        }
        return temp; // Return a copy to avoid external modification
    }
}
