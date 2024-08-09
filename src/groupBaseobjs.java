import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

class groupBaseobjs extends baseObj {
    private List<baseObj> groupObjs;
    private Rectangle bounds;

    groupBaseobjs() {
        this.groupObjs = new ArrayList<baseObj>();
        this.bounds = new Rectangle();
    }

    @Override
    void translate(double dx, double dy) {
        for (baseObj o : groupObjs) {
            o.translate(dx, dy);
        }
    }

    @Override
    baseObj copy() {
        // wait for implementation
        return new groupBaseobjs();
    }

    @Override
    void draw(Graphics g) {
        for (baseObj o : groupObjs) {
            o.draw(g);
        }
    }

    @Override
    boolean contains(Point p) {
        for (baseObj o : groupObjs) {
            if (o.contains(p))
                return true;
        }
        return false;
    }

    void addObj(baseObj o) {
        groupObjs.add(o);
    }

    @Override
    void gradient(Graphics g) {
        for (baseObj o : groupObjs) {
            o.gradient(g);
        }
    }

    private void updateBounds() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (baseObj o : groupObjs) {
            Rectangle r = o.getBounds();
            minX = Math.min(minX, r.x);
            minY = Math.min(minY, r.y);
            maxX = Math.max(maxX, r.x + r.width);
            maxY = Math.max(maxY, r.y + r.height);
        }

        bounds.setBounds(minX, minY, maxX - minX, maxY - minY);
    }
}
