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
        /**
         * for (baseObj o : groupObjs) {
         * o.translate(dx, dy);
         * }
         **/
        bounds.translate((int) dx, (int) dy);
    }

    @Override
    baseObj copy() {
        groupBaseobjs copyGroup = new groupBaseobjs();

        for (baseObj o : this.groupObjs) {
            copyGroup.addObj(o.copy());
        }

        // Copy the bounds to the new group
        copyGroup.bounds = new Rectangle(this.bounds);

        return copyGroup;
    }

    @Override
    void draw(Graphics g, int offsetX, int offsetY) {
        for (baseObj o : groupObjs) {
            // o.draw(g);
            o.draw(g, bounds.x + offsetX, bounds.y + offsetY);
        }
    }

    @Override
    boolean contains(Point p, int offsetX, int offsetY) {
        for (baseObj o : groupObjs) {
            if (o.contains(new Point(p.x - bounds.x - offsetX, p.y - bounds.y - offsetY), 0, 0))
                return true;
        }
        return false;
    }

    void addObj(baseObj o) {
        groupObjs.add(o); // add copy to the array not the reference
        updateBounds();
    }

    @Override
    void gradient(Graphics g, int offsetX, int offsetY) {
        for (baseObj o : groupObjs) {
            // o.gradient(g);
            o.gradient(g, bounds.x + offsetX, bounds.y + offsetY);
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

    @Override
    Rectangle getBounds() {
        return bounds;
    }

    List<baseObj> getGroupObjs() {
        return groupObjs;
    }
}
