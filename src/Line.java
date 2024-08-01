import java.awt.Graphics;
import java.awt.Point;

class Line extends baseObj {
    private Point start;
    private Point end;

    Line(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    @Override
    void rotate(double angle) {
        // do nothing
    }

    @Override
    void translate(int dx, int dy) {
        this.start.x += dx;
        this.start.y += dy;
        this.end.x += dx;
        this.end.y += dy;
    }

    @Override
    boolean copy() {
        // serialize the object and save to clip board
        return true;
    }

    @Override
    void draw(Graphics g) {
        g.drawLine(start.x, start.y, end.x, end.y);
    }
}
