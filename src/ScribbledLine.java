import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

class ScribbledLine extends baseObj {
    private List<Point> scribbledPoints;

    ScribbledLine() {
        this.scribbledPoints = new ArrayList<Point>();
    }

    @Override
    void rotate(double angle) {
        // wait for implementation
    }

    @Override
    void translate(int dx, int dy) {
        for (Point p : scribbledPoints) {
            p.x += dx;
            p.y += dy;
        }
    }

    @Override
    boolean copy() {
        // wait for implementation
        return true;
    }

    @Override
    void draw(Graphics g) {
        Point last = scribbledPoints.get(0);
        for (int i = 1; i < scribbledPoints.size(); i++) {
            g.drawLine(last.x, last.y, scribbledPoints.get(i).x, scribbledPoints.get(i).y);
            last = scribbledPoints.get(i);
        }
    }

    void addPoints(Point p) {
        scribbledPoints.add(p);
    }
}