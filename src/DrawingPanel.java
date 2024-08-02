package t11.t6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

enum DrawingMode {
    CIRCLE, LINE, RECTANGLE, ELLIPSE, SELECT, SQUARE, OPEN_POLYGON, CLOSED_POLYGON, COPY, CUT, PASTE
}

class DrawingPanel extends JPanel implements MouseMotionListener, MouseListener {
    private int shapeSize;
    private Color shapeColor;
    private DrawingMode mode = DrawingMode.CIRCLE;
    private CustomShape currentShape = null;
    private List<CustomShape> shapes = new ArrayList<>();
    private Point startPoint;
    private CustomShape selectedShape = null;
    private List<Point> polygonPoints = new ArrayList<>();
    private boolean drawingPolygon = false;
    private boolean isDrawingCircle = false;
    private Point circleCenter;

    private CustomShape clipboardShape = null;
    private boolean isCutOperation = false;

    DrawingPanel(Color color, int size) {
        setShapeColor(color);
        setShapeSize(size);
        addMouseMotionListener(this);
        addMouseListener(this);
        setBackground(Color.WHITE);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (CustomShape shape : shapes) {
            shape.draw(g2);
        }

        // Draw the open polygon while drawing
        if (drawingPolygon) {
            g2.setColor(shapeColor);
            for (int i = 0; i < polygonPoints.size() - 1; i++) {
                g2.drawLine(polygonPoints.get(i).x, polygonPoints.get(i).y, polygonPoints.get(i + 1).x, polygonPoints.get(i + 1).y);
            }
            if (mode == DrawingMode.CLOSED_POLYGON && polygonPoints.size() > 2) {
                g2.drawLine(polygonPoints.get(polygonPoints.size() - 1).x, polygonPoints.get(polygonPoints.size() - 1).y, polygonPoints.get(0).x, polygonPoints.get(0).y);
            }
        }
    }

    void setShapeSize(int size) {
        shapeSize = size;
    }

    void setShapeColor(Color color) {
        shapeColor = color;
    }

    int getShapeSize() {
        return shapeSize;
    }

    Color getShapeColor() {
        return shapeColor;
    }

    void setMode(DrawingMode newMode) {
        mode = newMode;
        if (mode != DrawingMode.OPEN_POLYGON && mode != DrawingMode.CLOSED_POLYGON) {
            drawingPolygon = false;
            polygonPoints.clear();
        }
    }

    void clear() {
        shapes.clear();
        polygonPoints.clear();
        drawingPolygon = false;
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mode == DrawingMode.SELECT && selectedShape != null) {
            int dx = e.getX() - startPoint.x;
            int dy = e.getY() - startPoint.y;
            selectedShape.move(dx, dy);
            startPoint = e.getPoint();
            repaint();
        } else if (mode != DrawingMode.SELECT && mode != DrawingMode.OPEN_POLYGON && mode != DrawingMode.CLOSED_POLYGON) {
            Point endPoint = e.getPoint();
            switch (mode) {
                case LINE:
                    currentShape = new CustomShape(new Line2D.Double(startPoint, endPoint), shapeColor);
                    break;
                case RECTANGLE:
                    currentShape = new CustomShape(new Rectangle2D.Double(
                            Math.min(startPoint.x, endPoint.x),
                            Math.min(startPoint.y, endPoint.y),
                            Math.abs(startPoint.x - endPoint.x),
                            Math.abs(startPoint.y - endPoint.y)
                    ), shapeColor);
                    break;
                case ELLIPSE:
                    currentShape = new CustomShape(new Ellipse2D.Double(
                            Math.min(startPoint.x, endPoint.x),
                            Math.min(startPoint.y, endPoint.y),
                            Math.abs(startPoint.x - endPoint.x),
                            Math.abs(startPoint.y - endPoint.y)
                    ), shapeColor);
                    break;
                case SQUARE:
                    int side = Math.min(Math.abs(startPoint.x - endPoint.x), Math.abs(startPoint.y - endPoint.y));
                    currentShape = new CustomShape(new Rectangle2D.Double(
                            Math.min(startPoint.x, startPoint.x + side),
                            Math.min(startPoint.y, startPoint.y + side),
                            side,
                            side
                    ), shapeColor);
                    break;
                case CIRCLE:
                    int diameter = (int) Math.round(startPoint.distance(endPoint));
                    currentShape = new CustomShape(new Ellipse2D.Double(
                            startPoint.x - diameter,
                            startPoint.y - diameter,
                            diameter * 2,
                            diameter * 2
                    ), shapeColor);
                    break;
                default:
                    break;
            }
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (mode == DrawingMode.SELECT && selectedShape != null) {
            selectedShape = null;
            repaint();
        } else if (mode != DrawingMode.SELECT && currentShape != null) {
            shapes.add(currentShape);
            currentShape = null;
            repaint();
        }

//        else if (mode == DrawingMode.PASTE && clipboardShape != null) {
//            Point endPoint = e.getPoint();
//            CustomShape newShape = cloneShape(clipboardShape);
//            if (newShape != null) {
//                // Calculate the new position for the shape based on the endPoint
//                int dx = (int) endPoint.getX() - (int) startPoint.getX();
//                int dy = (int) endPoint.getY() - (int) startPoint.getY();
//
//                // Move the shape by dx and dy
//                moveShape(newShape, dx, dy);
//
//                shapes.add(newShape);
//                clipboardShape = null; // Clear clipboard after pasting
//                repaint();
//            }
//        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (mode == DrawingMode.OPEN_POLYGON || mode == DrawingMode.CLOSED_POLYGON) {
            Point point = e.getPoint();
            polygonPoints.add(point);
            if (e.getClickCount() == 2) { // Double click to finish the polygon
                drawingPolygon = false;
                GeneralPath path = new GeneralPath();
                path.moveTo(polygonPoints.get(0).x, polygonPoints.get(0).y);
                for (int i = 1; i < polygonPoints.size(); i++) {
                    path.lineTo(polygonPoints.get(i).x, polygonPoints.get(i).y);
                }
                if (mode == DrawingMode.CLOSED_POLYGON) {
                    path.closePath();
                }
                shapes.add(new CustomShape(path, shapeColor));
                polygonPoints.clear();
                repaint();
            } else {
                drawingPolygon = true;
            }
            repaint();
        } else if (mode == DrawingMode.COPY || mode == DrawingMode.CUT) {
            Point point = e.getPoint();
            for (CustomShape shape : shapes) {
                if (shape.contains(point)) {
                    clipboardShape = cloneShape(shape);
                    if (mode == DrawingMode.CUT) {
                        shapes.remove(shape);
                        isCutOperation = true;
                    }
                    repaint();
                    break;
                }
            }
        } else if (mode == DrawingMode.PASTE && clipboardShape != null) {
            Point endPoint = e.getPoint();
            CustomShape newShape = cloneShape(clipboardShape);
            if (newShape != null) {


                // Move the new shape to the location of the mouse click
                moveShape(newShape, endPoint.x - clipboardShape.getShape().getBounds().x, endPoint.y - clipboardShape.getShape().getBounds().y);
                shapes.add(newShape);

                shapes.add(newShape);
                if (isCutOperation) {
                    clipboardShape = null;
                    isCutOperation = false;
                }
                repaint();
            }
        }





    }

    @Override
    public void mousePressed(MouseEvent e) {
        startPoint = e.getPoint();
        if (mode == DrawingMode.SELECT) {
            for (CustomShape shape : shapes) {
                if (shape.contains(startPoint)) {
                    selectedShape = shape;
                    break;
                }
            }
        } else if (mode == DrawingMode.PASTE) {
            startPoint = e.getPoint();
        }
    }

    private CustomShape cloneShape(CustomShape shape) {
        Shape original = shape.getShape();
        Color color = shape.getColor();
        if (original instanceof Ellipse2D) {
            Ellipse2D ellipse = (Ellipse2D) original;
            return new CustomShape(new Ellipse2D.Double(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), ellipse.getHeight()), color);
        } else if (original instanceof Rectangle2D) {
            Rectangle2D rect = (Rectangle2D) original;
            return new CustomShape(new Rectangle2D.Double(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()), color);
        } else if (original instanceof Line2D) {
            Line2D line = (Line2D) original;
            return new CustomShape(new Line2D.Double(line.getX1(), line.getY1(), line.getX2(), line.getY2()), color);
        } else if (original instanceof GeneralPath) {
            GeneralPath path = (GeneralPath) original;
            return new CustomShape((Shape) path.clone(), color);
        }
        return null;
    }

    private void moveShape(CustomShape shape, int dx, int dy) {
        shape.move(dx, dy);
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
