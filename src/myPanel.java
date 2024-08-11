import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

enum DrawingMode {
    SCRIBBED,
    LINE,
    RECTANGLE,
    SQUARE,
    ECLIPSE,
    CIRCLE,
    CLOSEPOLYGON,
    OPENPOLYGON,
    SELECT,
    PASTE
}

public class myPanel extends JPanel implements MouseMotionListener, MouseListener {
    private Color myColor;
    private DrawingModeHandler modeHandler = new ScribbleHandler(this);
    public DrawingMode mode = DrawingMode.SCRIBBED;
    private List<baseObj> baseObjs;
    private List<baseObj> selectedObjs;
    private List<baseObj> tempRenderObjs;
    private List<baseObj> copyObjs;
    // Stack to store undo and redo actions
    private Stack<Action> undoStack;
    private Stack<Action> redoStack;

    public void undo() {
        selectedObjs.clear();
        if (!undoStack.isEmpty()) {
            Action lastAction = undoStack.pop();
            redoStack.push(new Action(new ArrayList<>(baseObjs), lastAction.originalState, lastAction.type));
            baseObjs.clear();
            baseObjs.addAll(lastAction.originalState);
            repaint();
        }
    }

    public void redo() {
        selectedObjs.clear();
        if (!redoStack.isEmpty()) {
            Action lastAction = redoStack.pop();
            undoStack.push(new Action(new ArrayList<>(baseObjs), lastAction.newState, lastAction.type));
            baseObjs.clear();
            baseObjs.addAll(lastAction.originalState);
            repaint();
        }
    }

    public void copy() {
        // copy the selected objects into the copyObjs array
        System.out.println("copy selected obj to baseObjs array");
        for (baseObj o : selectedObjs) {
            copyObjs.add(o.copy());
        }
    }

    /*public void paste() {
        // paste the copied objests to the panel
        System.out.println("paste obj to baseObjs array");
        List<baseObj> originalState = new ArrayList<>(baseObjs);
        for (baseObj o : copyObjs) {
            o.translate(10, 10); // add aoffset
            baseObjs.add(o);
        }
        List<baseObj> newState = new ArrayList<>(baseObjs);
        undoStack.push(new Action(originalState, newState, ActionType.ADD));
        redoStack.clear(); // Clear redo stack whenever a new action is performed
        repaint();
    }*/

    public void cut() {
        // delete the selected objects
        List<baseObj> originalState = new ArrayList<>();
        List<baseObj> newState = new ArrayList<>();
        for (baseObj o: baseObjs) {
            baseObj o2 = o.copy();
            if (!(selectedObjs.contains(o))) {
                newState.add(o2);
            }
            originalState.add(o2);
        }
        for (baseObj o : selectedObjs){
            copyObjs.add(o.copy());
            baseObjs.remove(o);
        }
        //baseObjs.remove(selectedObjs);
        undoStack.push(new Action(originalState, newState, ActionType.REMOVE));
        redoStack.clear(); // Clear redo stack whenever a new action is performed
        selectedObjs.clear();
        repaint();
    }

    public void debug() {
        System.out.println("----------------");
        System.out.println("selectedObjs.size:" + selectedObjs.size());
        System.out.println("baseObjs.size:" + baseObjs.size());
        System.out.println("tempRenderObjs.size:" + tempRenderObjs.size());
        System.out.println("copyObjs.size:" + copyObjs.size());
        System.out.println("undoStack.size:" + undoStack.size());
        System.out.println("redoStack.size:" + redoStack.size());
        System.out.println("Color:" + myColor);
        System.out.println("++++++++++++++++");
        for (Action a : redoStack) {
            System.out.print(
                    "redoStack-> newstate size:" + a.newState.size() + " originalstate size:" + a.originalState.size() + " ActionType:" + a.type);
            System.out.println("");
        }
        for (Action a : undoStack) {
            System.out.print(
                    "undoStack-> newstate size:" + a.newState.size() + " originalstate size:" + a.originalState.size() + " ActionType:" + a.type);
            System.out.println("");
        }
        System.out.println("===============");
    }

    public void addTempRenderObj(baseObj o) {
        tempRenderObjs.add(o);
    }

    public void resetTempRenderList() {
        tempRenderObjs.clear();
    }

    public void groupSelectedObjs() {
        // put selected objects into one object and then put into the array
        System.out.println("grouped obj add to baseObjs array");
        List<baseObj> originalState = new ArrayList<>(baseObjs);
        groupBaseobjs g = new groupBaseobjs();
        for (int i = 0; i < selectedObjs.size(); i++) {
            baseObj o = selectedObjs.get(i);
            baseObjs.remove(o);
            g.addObj(o);
        }
        baseObjs.add(g);
        selectedObjs.clear();
        List<baseObj> newState = new ArrayList<>(baseObjs);
        undoStack.push(new Action(originalState, newState, ActionType.ADD));
        redoStack.clear(); // Clear redo stack whenever a new action is performed

        // System.out.println("selectedObjs.size:" + selectedObjs.size());
        // System.out.println("baseObjs.size:" + baseObjs.size());
        setMode(DrawingMode.SELECT);
        repaint();
    }

    public void ungroupSelectedObjs() {
        // ungroup the selected groupBaseobjs object and rejoin the array
        System.out.println("grouped obj removed to baseObjs array");
        List<baseObj> originalState = new ArrayList<>(baseObjs);
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
        List<baseObj> newState = new ArrayList<>(baseObjs);
        undoStack.push(new Action(originalState, newState, ActionType.ADD));
        redoStack.clear(); // Clear redo stack whenever a new action is performed
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
        // clear the tempRenderObjs array
        tempRenderObjs.clear();
        // clear the copyObjs
        copyObjs.clear();
        // clear the history stack
        undoStack.clear();
        redoStack.clear();
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
        this.undoStack = new Stack<Action>();
        this.redoStack = new Stack<Action>();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    myPanel(Color color) {
        myColor = color;
        this.baseObjs = new ArrayList<baseObj>();
        this.selectedObjs = new ArrayList<baseObj>();
        this.tempRenderObjs = new ArrayList<baseObj>();
        this.copyObjs = new ArrayList<baseObj>();
        this.undoStack = new Stack<Action>();
        this.redoStack = new Stack<Action>();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public void clear() {
        myColor = Color.BLACK;
        baseObjs.clear();
        selectedObjs.clear();
        tempRenderObjs.clear();
        copyObjs.clear();
        redoStack.clear();
        undoStack.clear();
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
            case RECTANGLE:
                selectedObjs.clear();
                modeHandler = new RectangleHandler(this);
                break;
            case SQUARE:
                selectedObjs.clear();
                modeHandler = new SquareHandler(this);
                break;
            case ECLIPSE:
                selectedObjs.clear();
                modeHandler = new EclipseHandler(this);
                break;
            case CIRCLE:
                selectedObjs.clear();
                modeHandler = new CircleHandler(this);
                break;
            case OPENPOLYGON:
                selectedObjs.clear();
                modeHandler = new OpenPolygonHandler(this);
                break;
            case CLOSEPOLYGON:
                selectedObjs.clear();
                modeHandler = new ClosePolygonHandler(this);
                break;
            case SELECT:
                modeHandler = new SelectHandler(this);
                break;
            case PASTE:
                modeHandler = new PasteHandler(this);
                break;
        }
    }

    void addObj(baseObj o) {
        System.out.println("add obj to baseObjs array");
        List<baseObj> originalState = new ArrayList<>();
        List<baseObj> newState = new ArrayList<>();
        for (baseObj o2: baseObjs) {
            baseObj o3 = o2.copy();
            originalState.add(o3);
            newState.add(o3);
        }
        this.baseObjs.add(o);
        newState.add(o.copy());
        undoStack.push(new Action(originalState, newState, ActionType.ADD));
        redoStack.clear(); // Clear redo stack whenever a new action is performed
        repaint();
    }

    void addSelect(baseObj o) {
        System.out.println("add obj to selectedObjs array");
        this.selectedObjs.add(o);
    }

    void removeSelect(baseObj o) {
        System.out.println("remove obj from selectedObjs array");
        this.selectedObjs.remove(o);
    }
    
    void addAction(List<baseObj> os, List<baseObj> ns, ActionType at) {
        undoStack.push(new Action(os, ns, at));
        redoStack.clear(); // Clear redo stack whenever a new action is performed
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
    
    List<baseObj> getCopyObjs() {
        return copyObjs;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // draw the objs
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        for (baseObj o : baseObjs) {
            o.draw(g2, 0, 0);
        }
        // draw the select highlight surroundings
        for (baseObj o : selectedObjs) {
            o.gradient(g2, 0, 0);
        }
        // render the drawing process
        for (baseObj o : tempRenderObjs) {
            o.draw(g2, 0, 0);
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
