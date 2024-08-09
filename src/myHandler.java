import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.Point;
import java.io.Serializable;

interface DrawingModeHandler {
    void myMousePressed(MouseEvent e);

    void myMouseDragged(MouseEvent e);

    void myMouseReleased(MouseEvent e);

    void myMouseClicked(MouseEvent e);
}

class RectangleHandler implements DrawingModeHandler, Serializable {
    private myPanel panel;
    private Point startPoint;
    private Rectangle currentRectangle;

    public RectangleHandler(myPanel panel) {
        this.panel = panel;
    }

    @Override
    public void myMouseClicked(MouseEvent e) {
        // Do nothing for now
    }

    @Override
    public void myMouseDragged(MouseEvent e) {
        if (currentRectangle != null) {
            Point endPoint = e.getPoint();
            panel.resetTempRenderList();
            // Update the current rectangle's size based on drag
            currentRectangle = new Rectangle(startPoint, endPoint);
            currentRectangle.setColor(panel.getPanelColor());
            panel.addTempRenderObj(currentRectangle);
            panel.repaint();
        }
    }

    @Override
    public void myMousePressed(MouseEvent e) {
        // Start drawing a new rectangle
        startPoint = e.getPoint();
        currentRectangle = new Rectangle(startPoint, startPoint); // Initially, it's a point
        currentRectangle.setColor(panel.getPanelColor());
        panel.addTempRenderObj(currentRectangle);
        panel.repaint();
    }

    @Override
    public void myMouseReleased(MouseEvent e) {
        if (currentRectangle != null) {
            // Finalize the rectangle on mouse release
            Point endPoint = e.getPoint();
            currentRectangle = new Rectangle(startPoint, endPoint);
            currentRectangle.setColor(panel.getPanelColor());
            // Possibly add the finalized rectangle to a list of shapes, if needed
            panel.addObj(currentRectangle);
            panel.repaint();
            currentRectangle = null; // Reset the current rectangle
        }
        panel.resetTempRenderList();
    }

}

class ScribbleHandler implements DrawingModeHandler, Serializable {
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
        sLine.setColor(panel.getPanelColor());
        sLine.addPoints(e.getPoint());
        // first add a line for drag update
        sLine.addPoints(e.getPoint()); // sLine need at least two points
        panel.addTempRenderObj(sLine);
        panel.repaint();
    }

    @Override
    public void myMouseDragged(MouseEvent e) {
        if (isDrawingScribbledLine) {
            panel.resetTempRenderList();
            sLine.addPoints(e.getPoint());
            panel.addTempRenderObj(sLine);
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
        panel.resetTempRenderList();
    }

    @Override
    public void myMouseClicked(MouseEvent e) {
    }
}

class LineHandler implements DrawingModeHandler, Serializable {
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
        endPoint = e.getPoint();
        // first add a line for drag update
        Line line = new Line(new Line2D.Double(startPoint, endPoint));
        line.setColor(panel.getPanelColor());
        panel.addTempRenderObj(line);
        panel.repaint();
    }

    @Override
    public void myMouseDragged(MouseEvent e) {
        if (isDrawingLine) {
            panel.resetTempRenderList();
            endPoint = e.getPoint();
            Line line = new Line(new Line2D.Double(startPoint, endPoint));
            line.setColor(panel.getPanelColor());
            panel.addTempRenderObj(line);
            panel.repaint();
        }
    }

    @Override
    public void myMouseReleased(MouseEvent e) {
        isDrawingLine = false;
        endPoint = e.getPoint();
        Line line = new Line(new Line2D.Double(startPoint, endPoint));
        line.setColor(panel.getPanelColor());
        panel.addObj(line);
        panel.repaint();
        panel.resetTempRenderList();
    }

    @Override
    public void myMouseClicked(MouseEvent e) {
    }
}

class SelectHandler implements DrawingModeHandler, Serializable {
    private myPanel panel;
    private Point startPoint;

    public SelectHandler(myPanel panel) {
        this.panel = panel;
    }

    @Override
    public void myMousePressed(MouseEvent e) {
        // record the start point
        startPoint = e.getPoint();
        // judge the current point
        for (baseObj o : panel.getBaseObjs()) {
            if (o.contains(startPoint, 0, 0)) {
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
            if (o.contains(p, 0, 0)) {
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
        panel.resetTempRenderList();
        panel.repaint();
    }

}
