import java.awt.Canvas;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics;
import java.awt.Point;

public class myCanvas extends Canvas {
    private List<baseObj> baseObjs;
    private baseObj selectedObj;

    myCanvas() {
        this.baseObjs = new ArrayList<baseObj>();
    }

    void addObj(baseObj o) {
        this.baseObjs.add(o);
    }

    @Override
    public void paint(Graphics g) {
        for (baseObj o : baseObjs) {
            o.draw(g);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 400); // Set the preferred size of the canvas
    }

    public boolean select(Point p) {
        for (baseObj o : baseObjs) {
            if (o.contains(p)) {
                selectedObj = o;
                return true;
            }
        }
        return false;
    }

    public baseObj getSelectedObj() {
        baseObj sObj = selectedObj;
        selectedObj = null;
        return sObj;
    }
}
