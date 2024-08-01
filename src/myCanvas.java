import java.awt.Canvas;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics;

public class myCanvas extends Canvas {
    private List<baseObj> baseObjs;

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
}
