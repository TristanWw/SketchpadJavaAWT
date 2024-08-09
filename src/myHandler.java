import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.List;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;

interface DrawingModeHandler {
    void myMousePressed(MouseEvent e);

    void myMouseDragged(MouseEvent e);

    void myMouseReleased(MouseEvent e);

    void myMouseClicked(MouseEvent e);
}

class OpenPolygonHandler implements DrawingModeHandler, Serializable {
    private myPanel panel;
    private OpenPolygon openPolygon;
    private boolean isDrawingPolygon = false;
    private Point lastPoint = null;

    public OpenPolygonHandler(myPanel panel) {
        this.panel = panel;
        openPolygon = new OpenPolygon();
        openPolygon.setColor(panel.getPanelColor());
    }

    @Override
    public void myMousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) { // Right-click
            finalizePolygon();
        } else if (!isDrawingPolygon) {
            // Start drawing the polygon on first click
            lastPoint = e.getPoint();
            openPolygon.addPoints(lastPoint);
            isDrawingPolygon = true;
            panel.repaint();
        }
    }

    @Override
    public void myMouseDragged(MouseEvent e) {
        if (isDrawingPolygon && lastPoint != null) {
            panel.resetTempRenderList(); // Clear previous temporary objects

            openPolygon.setColor(panel.getPanelColor());

            // Add the line segment from the last point to the current cursor position
            Line temporaryLine = new Line(new Line2D.Double(lastPoint, e.getPoint()));
            temporaryLine.setColor(panel.getPanelColor());

            // Add the temporary line and the polygon to the panel
            panel.addTempRenderObj(temporaryLine);
            panel.addTempRenderObj(openPolygon);
            panel.repaint();
        }
    }

    @Override
    public void myMouseReleased(MouseEvent e) {
        if (isDrawingPolygon) {
            // After releasing the mouse button, finalize the addition of a new point
            if (lastPoint != null) {
                openPolygon.addPoints(e.getPoint());
                lastPoint = e.getPoint(); // Update the lastPoint
                panel.repaint(); // Ensure the panel is repainted after adding points
            }
        }
    }

    @Override
    public void myMouseClicked(MouseEvent e) {
        // Not needed for OpenPolygonHandler
    }

    private void finalizePolygon() {
        if (isDrawingPolygon) {
            // Add the openPolygon to the panel
            openPolygon.setColor(panel.getPanelColor());
            panel.addObj(openPolygon);
            panel.repaint();
            panel.resetTempRenderList();
            isDrawingPolygon = false; // End drawing mode

            // Clear the openPolygon and prepare for a new one
            openPolygon = new OpenPolygon();
            lastPoint = null;
            openPolygon.setColor(panel.getPanelColor());
        }
    }
}

class ClosePolygonHandler implements DrawingModeHandler, Serializable {
    private myPanel panel;
    private boolean isDrawingPolygon;
    private List<Point> points;
    private ClosePolygon tempPolygon;

    public ClosePolygonHandler(myPanel panel) {
        this.panel = panel;
        this.points = new ArrayList<>();
    }

    @Override
    public void myMousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) { // Right-click
            finalizePolygon();
        } else if (!isDrawingPolygon) {
            isDrawingPolygon = true;
            points.clear(); // Start a new polygon
        }

        if (isDrawingPolygon) {
            points.add(e.getPoint()); // Add point on each click
            tempPolygon = new ClosePolygon(points);
            tempPolygon.setColor(panel.getPanelColor());
            panel.resetTempRenderList();
            panel.addTempRenderObj(tempPolygon);
            panel.repaint();
        }
    }

    private void finalizePolygon() {
        if (isDrawingPolygon) {
            ClosePolygon polygon = new ClosePolygon(new ArrayList<>(points));
            polygon.setColor(panel.getPanelColor());
            panel.addObj(polygon);
            panel.repaint();
            panel.resetTempRenderList();
            isDrawingPolygon = false; // End drawing mode
        }
    }

    @Override
    public void myMouseDragged(MouseEvent e) {
        // Optionally handle dragging if needed (e.g., for moving points)
    }

    @Override
    public void myMouseReleased(MouseEvent e) {
        // Optional: Additional logic for mouse release if needed
    }

    @Override
    public void myMouseClicked(MouseEvent e) {
        // Optionally handle clicks for specific logic if needed
    }
}

class CircleHandler implements DrawingModeHandler, Serializable {
    private myPanel panel;
    private Point startPoint;
    private Circle currentCircle;

    public CircleHandler(myPanel panel) {
        this.panel = panel;
    }

    @Override
    public void myMouseClicked(MouseEvent e) {
        // Do nothing for now
    }

    @Override
    public void myMouseDragged(MouseEvent e) {
        if (currentCircle != null) {
            Point endPoint = e.getPoint();
            panel.resetTempRenderList();
            // Update the current circle's size based on drag
            currentCircle = new Circle(startPoint, endPoint);
            currentCircle.setColor(panel.getPanelColor());
            panel.addTempRenderObj(currentCircle);
            panel.repaint();
        }
    }

    @Override
    public void myMousePressed(MouseEvent e) {
        // Start drawing a new circle
        startPoint = e.getPoint();
        currentCircle = new Circle(startPoint, startPoint); // Initially, it's a point
        currentCircle.setColor(panel.getPanelColor());
        panel.addTempRenderObj(currentCircle);
        panel.repaint();
    }

    @Override
    public void myMouseReleased(MouseEvent e) {
        if (currentCircle != null) {
            // Finalize the circle on mouse release
            Point endPoint = e.getPoint();
            currentCircle = new Circle(startPoint, endPoint);
            currentCircle.setColor(panel.getPanelColor());
            // Possibly add the finalized circle to a list of shapes, if needed
            panel.addObj(currentCircle);
            panel.repaint();
            currentCircle = null; // Reset the current circle
        }
        panel.resetTempRenderList();
    }
}

class EclipseHandler implements DrawingModeHandler, Serializable {
    private myPanel panel;
    private Point startPoint;
    private Eclipse currentEclipse;

    public EclipseHandler(myPanel panel) {
        this.panel = panel;
    }

    @Override
    public void myMouseClicked(MouseEvent e) {
        // Do nothing for now
    }

    @Override
    public void myMouseDragged(MouseEvent e) {
        if (currentEclipse != null) {
            Point endPoint = e.getPoint();
            panel.resetTempRenderList();
            // Update the current eclipse's size based on drag
            currentEclipse = new Eclipse(startPoint, endPoint);
            currentEclipse.setColor(panel.getPanelColor());
            panel.addTempRenderObj(currentEclipse);
            panel.repaint();
        }
    }

    @Override
    public void myMousePressed(MouseEvent e) {
        // Start drawing a new eclipse
        startPoint = e.getPoint();
        currentEclipse = new Eclipse(startPoint, startPoint); // Initially, it's a point
        currentEclipse.setColor(panel.getPanelColor());
        panel.addTempRenderObj(currentEclipse);
        panel.repaint();
    }

    @Override
    public void myMouseReleased(MouseEvent e) {
        if (currentEclipse != null) {
            // Finalize the eclipse on mouse release
            Point endPoint = e.getPoint();
            currentEclipse = new Eclipse(startPoint, endPoint);
            currentEclipse.setColor(panel.getPanelColor());
            // Possibly add the finalized eclipse to a list of shapes, if needed
            panel.addObj(currentEclipse);
            panel.repaint();
            currentEclipse = null; // Reset the current eclipse
        }
        panel.resetTempRenderList();
    }
}

class SquareHandler implements DrawingModeHandler, Serializable {
    private myPanel panel;
    private Point startPoint;
    private Square currentSquare;

    public SquareHandler(myPanel panel) {
        this.panel = panel;
    }

    @Override
    public void myMouseClicked(MouseEvent e) {
        // Do nothing for now
    }

    @Override
    public void myMouseDragged(MouseEvent e) {
        if (currentSquare != null) {
            Point endPoint = e.getPoint();
            panel.resetTempRenderList();
            // Update the current square's size based on drag
            currentSquare = new Square(startPoint, endPoint);
            currentSquare.setColor(panel.getPanelColor());
            panel.addTempRenderObj(currentSquare);
            panel.repaint();
        }
    }

    @Override
    public void myMousePressed(MouseEvent e) {
        // Start drawing a new square
        startPoint = e.getPoint();
        currentSquare = new Square(startPoint, startPoint); // Initially, it's a point
        currentSquare.setColor(panel.getPanelColor());
        panel.addTempRenderObj(currentSquare);
        panel.repaint();
    }

    @Override
    public void myMouseReleased(MouseEvent e) {
        if (currentSquare != null) {
            // Finalize the square on mouse release
            Point endPoint = e.getPoint();
            currentSquare = new Square(startPoint, endPoint);
            currentSquare.setColor(panel.getPanelColor());
            // Possibly add the finalized square to a list of shapes, if needed
            panel.addObj(currentSquare);
            panel.repaint();
            currentSquare = null; // Reset the current square
        }
        panel.resetTempRenderList();
    }
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
