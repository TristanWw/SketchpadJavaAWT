import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

enum DrawingMode {
    SCRIBBED,
    LINE,
    RECTANGLE,
    SELECT
}

public class myPanel extends JPanel implements MouseMotionListener, MouseListener {
    private Color myColor;
    private DrawingModeHandler modeHandler = new ScribbleHandler(this);
    public DrawingMode mode = DrawingMode.SCRIBBED;
    private List<baseObj> baseObjs;
    private List<baseObj> selectedObjs;

    public void setColor(Color color) {
        myColor = color;
    }

    myPanel() {
        myColor = Color.BLACK;
        this.baseObjs = new ArrayList<baseObj>();
        this.selectedObjs = new ArrayList<baseObj>();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    myPanel(Color color) {
        myColor = color;
        this.baseObjs = new ArrayList<baseObj>();
        this.selectedObjs = new ArrayList<baseObj>();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public void clear() {
        myColor = Color.BLACK;
        baseObjs.clear();
        selectedObjs.clear();
        repaint();
    }

    void setMode(DrawingMode newMode) {
        mode = newMode;
        switch (mode) {
            case SCRIBBED:
                selectedObjs.clear();
                modeHandler = new ScribbleHandler(this);
                break;
            case LINE:
                selectedObjs.clear();
                modeHandler = new LineHandler(this);
                break;
            /**
             * case RECTANGLE:
             * modeHandler = new RectangleHandler(this);
             * break;
             **/
            case SELECT:
                modeHandler = new SelectHandler(this);
                break;
        }
    }

    void addObj(baseObj o) {
        System.out.println("add obj to baseObjs array");
        this.baseObjs.add(o);
    }

    void removeObj(baseObj o) {
        System.out.println("remove obj from baseObjs array");
        this.baseObjs.remove(o);
    }

    void addSelect(baseObj o) {
        System.out.println("add obj to selectedObjs array");
        this.selectedObjs.add(o);
    }

    void removeSelect(baseObj o) {
        System.out.println("remove obj from selectedObjs array");
        this.selectedObjs.remove(o);
    }

    baseObj getLastObj() {
        return baseObjs.get(baseObjs.size() - 1);
    }

    void removeLastObj() {
        if (baseObjs.size() != 0) {
            System.out.println("remove the last obj from baseObjs array");
            baseObjs.remove(baseObjs.size() - 1);
        }
    }

    List<baseObj> getBaseObjs() {
        return baseObjs;
    }

    List<baseObj> getSelectedObjs() {
        return selectedObjs;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // draw the objs
        for (baseObj o : baseObjs) {
            o.draw(g);
        }
        // draw the select highlight surroundings
        for (baseObj o : selectedObjs) {
            o.gradient(g);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (modeHandler != null) {
            modeHandler.myMouseClicked(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (modeHandler != null) {
            modeHandler.myMousePressed(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (modeHandler != null) {
            modeHandler.myMouseDragged(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (modeHandler != null) {
            modeHandler.myMouseReleased(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
