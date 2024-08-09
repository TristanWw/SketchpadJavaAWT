import java.awt.event.MouseEvent;
import java.awt.Point;

interface DrawingModeHandler {
    void myMousePressed(MouseEvent e);

    void myMouseDragged(MouseEvent e);

    void myMouseReleased(MouseEvent e);

    void myMouseClicked(MouseEvent e);
}

class ScribbleHandler implements DrawingModeHandler {
    private myPanel panel;
    private boolean isDrawingScribbledLine;
    private ScribbledLine sLine;

    public ScribbleHandler(myPanel panel) {
        this.panel = panel;
    }

    @Override
    public void myMousePressed(MouseEvent e) {
        isDrawingScribbledLine = true;
        sLine = new ScribbledLine();
        sLine.addPoints(e.getPoint());
        // first add a line for drag update
        sLine.addPoints(e.getPoint()); // sLine need at least two points
        panel.addObj(sLine);
        panel.repaint();
    }

    @Override
    public void myMouseDragged(MouseEvent e) {
        if (isDrawingScribbledLine) {
            panel.removeLastObj();
            sLine.addPoints(e.getPoint());
            panel.addObj(sLine);
            panel.repaint();
        }
    }

    @Override
    public void myMouseReleased(MouseEvent e) {
        isDrawingScribbledLine = false;
        if (sLine != null) {
            isDrawingScribbledLine = false;
            sLine.addPoints(e.getPoint());
            panel.addObj(sLine);
            panel.repaint();
        }
        sLine = null;
    }

    @Override
    public void myMouseClicked(MouseEvent e) {
    }
}

class LineHandler implements DrawingModeHandler {
    private myPanel panel;
    private boolean isDrawingLine;
    private Point startPoint, endPoint;

    public LineHandler(myPanel panel) {
        this.panel = panel;
    }

    @Override
    public void myMousePressed(MouseEvent e) {
        isDrawingLine = true;
        startPoint = e.getPoint();
        // first add a line for drag update
        Line line = new Line(startPoint, startPoint);
        panel.addObj(line);
        panel.repaint();
    }

    @Override
    public void myMouseDragged(MouseEvent e) {
        panel.removeLastObj();
        endPoint = e.getPoint();
        Line line = new Line(startPoint, endPoint);
        panel.addObj(line);
        panel.repaint();
    }

    @Override
    public void myMouseReleased(MouseEvent e) {
        isDrawingLine = false;
        endPoint = e.getPoint();
        Line line = new Line(startPoint, endPoint);
        panel.addObj(line);
        panel.repaint();
    }

    @Override
    public void myMouseClicked(MouseEvent e) {
    }
}

class SelectHandler implements DrawingModeHandler {
    private myPanel panel;
    private Point startPoint, endPoint;

    public SelectHandler(myPanel panel) {
        this.panel = panel;
    }

    @Override
    public void myMousePressed(MouseEvent e) {
        // record the start point
        startPoint = e.getPoint();
        // judge the current point
        for (baseObj o : panel.getBaseObjs()) {
            if (o.contains(startPoint)) {
                if (!panel.getSelectedObjs().contains(o)) {
                    // add to the list if not in it
                    panel.addSelect(o);
                }
            }
        }
        panel.repaint();
    }

    @Override
    public void myMouseDragged(MouseEvent e) {
        int dx = e.getPoint().x - startPoint.x;
        int dy = e.getPoint().y - startPoint.y;
        for (baseObj o : panel.getSelectedObjs()) {
            o.translate(dx, dy);
        }
        startPoint = e.getPoint();
        panel.repaint();
    }

    @Override
    public void myMouseReleased(MouseEvent e) {
        // No additional translation needed here
        panel.repaint();
    }

    @Override
    public void myMouseClicked(MouseEvent e) {
        boolean blankSpot = true;
        Point p = e.getPoint();
        // judge the current point
        for (baseObj o : panel.getBaseObjs()) {
            if (o.contains(p)) {
                blankSpot = false;
                if (panel.getSelectedObjs().contains(o)) {
                    // remove if already in the list
                    panel.removeSelect(o);
                } else {
                    // add to the list if not in it
                    panel.addSelect(o);
                }
            }
        }
        if (blankSpot) {
            panel.getSelectedObjs().clear();
        }
        panel.repaint();
    }

}
