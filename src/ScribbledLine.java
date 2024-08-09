import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Line2D;

class ScribbledLine extends baseObj {
    private List<Point> scribbledPoints;

    ScribbledLine() {
        this.scribbledPoints = new ArrayList<Point>();
    }

    @Override
    void translate(double dx, double dy) {
        for (Point p : scribbledPoints) {
            p.x += dx;
            p.y += dy;
        }
    }

    @Override
    baseObj copy() {
        // wait for implementation
        return new ScribbledLine();
    }

    @Override
    void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getColor());
        Point last = scribbledPoints.get(0);
        for (int i = 1; i < scribbledPoints.size(); i++) {
            Line2D line = new Line2D.Double(last, scribbledPoints.get(i));
            g2.draw(line);
            g2.fill(line);
            last = scribbledPoints.get(i);
        }
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
    boolean contains(Point p) {
        // calculate the distance between the points on the scribbled line and p
        // to determine whether p is on the scribbled line
        double epsilon = 5.0; // Threshold distance to determine if the point is on the line

        for (int i = 0; i < scribbledPoints.size() - 1; i++) {
            Point start = scribbledPoints.get(i);
            Point end = scribbledPoints.get(i + 1);

            if (isPointNearLineSegment(p, start, end, epsilon)) {
                return true;
            }
        }
        return false;
    }

    void addPoints(Point p) {
        scribbledPoints.add(p);
    }

    @Override
    void gradient(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (scribbledPoints.size() == 0)
            return;

        Point last = scribbledPoints.get(0);
        for (int i = 1; i < scribbledPoints.size(); i++) {
            Point current = scribbledPoints.get(i);

            GradientPaint gradientPaint = new GradientPaint(
                    last.x, last.y, Color.CYAN,
                    current.x, current.y, Color.MAGENTA);

            g2.setPaint(gradientPaint);
            g2.setStroke(new BasicStroke(2));
            Line2D line = new Line2D.Double(last, scribbledPoints.get(i));
            g2.draw(line);
            last = current;
        }
    }
}
