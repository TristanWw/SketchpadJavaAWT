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
    GROUP,
    UNGROUP,
    SELECT
}

public class myPanel extends JPanel implements MouseMotionListener, MouseListener {
    private Color myColor;
    private DrawingModeHandler modeHandler = new ScribbleHandler(this);
    public DrawingMode mode = DrawingMode.SCRIBBED;
    private List<baseObj> baseObjs;
    private List<baseObj> selectedObjs;
    private List<baseObj> tempRenderObjs;
    private List<baseObj> copyObjs;

    public void copy() {
        // copy the selected objects into the copyObjs array
        for (baseObj o : selectedObjs) {
            copyObjs.add(o.copy());
        }
    }

    public void paste() {
        // paste the copied objests to the panel
        for (baseObj o : copyObjs) {
            o.translate(10, 10); // add aoffset
            baseObjs.add(o);
        }
        copyObjs.clear();
        repaint();
    }

    public void cut() {
        // delte the selected objects
        for (int i = 0; i < selectedObjs.size(); i++) {
            baseObj o = selectedObjs.get(i);
            if (baseObjs.contains(o)) {
                // remove from baseObjs
                baseObjs.remove(o);
            }
        }
        selectedObjs.clear();
        repaint();
    }

    public void debug() {
        System.out.println("selectedObjs.size:" + selectedObjs.size());
        System.out.println("baseObjs.size:" + baseObjs.size());
        System.out.println("tempRenderObjs.size:" + tempRenderObjs.size());
        System.out.println("copyObjs.size:" + copyObjs.size());
    }

    public void addTempRenderObj(baseObj o) {
        tempRenderObjs.add(o);
    }

    public void resetTempRenderList() {
        tempRenderObjs.clear();
    }

    public void groupSelectedObjs() {
        // put selected objects into one object and then put into the array
        groupBaseobjs g = new groupBaseobjs();
        for (int i = 0; i < selectedObjs.size(); i++) {
            baseObj o = selectedObjs.get(i);
            baseObjs.remove(o);
            g.addObj(o);
        }
        baseObjs.add(g);
        selectedObjs.clear();

        // System.out.println("selectedObjs.size:" + selectedObjs.size());
        // System.out.println("baseObjs.size:" + baseObjs.size());
        setMode(DrawingMode.SELECT);
        repaint();
    }

    public void ungroupSelectedObjs() {
        // ungroup the selected groupBaseobjs object and rejoin the array
        for (int i = 0; i < selectedObjs.size(); i++) {
            if (selectedObjs.get(i) instanceof groupBaseobjs) {
                baseObj sObj = selectedObjs.get(i);
                // remove the selected from baseObjs
                baseObjs.remove(sObj);
                // break the objects and rejoin the array
                groupBaseobjs go = (groupBaseobjs) sObj;
                List<baseObj> groupObjs = go.getGroupObjs();
                for (baseObj o : groupObjs) {
                    baseObjs.add(o);
                }
            }
        }
        selectedObjs.clear();
        // System.out.println("selectedObjs.size:" + selectedObjs.size());
        // System.out.println("baseObjs.size:" + baseObjs.size());
        setMode(DrawingMode.SELECT);
        repaint();
    }

    public void resetBeforeSave() {
        // restore the default select mode
        modeHandler = new SelectHandler(this);
        // clear the selectedObjs array
        selectedObjs.clear();
    }

    public void setPanelColor(Color color) {
        myColor = color;
    }

    public Color getPanelColor() {
        return myColor;
    }

    myPanel() {
        myColor = Color.BLACK;
        this.baseObjs = new ArrayList<baseObj>();
        this.selectedObjs = new ArrayList<baseObj>();
        this.tempRenderObjs = new ArrayList<baseObj>();
        this.copyObjs = new ArrayList<baseObj>();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    myPanel(Color color) {
        myColor = color;
        this.baseObjs = new ArrayList<baseObj>();
        this.selectedObjs = new ArrayList<baseObj>();
        this.tempRenderObjs = new ArrayList<baseObj>();
        this.copyObjs = new ArrayList<baseObj>();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public void clear() {
        myColor = Color.BLACK;
        baseObjs.clear();
        selectedObjs.clear();
        tempRenderObjs.clear();
        copyObjs.clear();
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
            o.draw(g, 0, 0);
        }
        // draw the select highlight surroundings
        for (baseObj o : selectedObjs) {
            o.gradient(g, 0, 0);
        }
        // render the drawing process
        for (baseObj o : tempRenderObjs) {
            o.draw(g, 0, 0);
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
