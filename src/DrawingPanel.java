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
import java.io.Serializable;

enum DrawingMode {
    SCRIBBED, CIRCLE, LINE, RECTANGLE, ELLIPSE, SELECT, SQUARE, OPEN_POLYGON, CLOSED_POLYGON, COPY, CUT, PASTE,GROUP,UNGROUP}

class DrawingPanel extends JPanel implements MouseMotionListener, MouseListener, Serializable {
    private static final long SerialVerionUID = 1L;
    private int shapeSize;
    private Color shapeColor;
    public  DrawingMode mode = DrawingMode.SCRIBBED;
    private CustomShape currentShape = null;
    public  List<CustomShape> shapes = new ArrayList<>();
    private Point startPoint;
    
    public  List<CustomShape> selectedShapes = new ArrayList<CustomShape>();
    //private CustomShape selectedShape = null;
    
    private List<Point> polygonPoints = new ArrayList<>();
    private boolean drawingPolygon = false;
    private boolean isDrawingCircle = false;
    private Point circleCenter;
    public  History history = new History();
    
    public  List<CustomShape> clipboardShape = new ArrayList<CustomShape>();
    public  boolean isCutOperation = false;
    public  boolean hasReleased = false;

    public Scribbled sLine = new Scribbled(new Line2D.Double(new Point(), new Point()), Color.BLACK);

    class History implements Serializable {
        private static final long SerialVerionUID = 1L;
        int index=-1;
        public  ArrayList<HistoryInstance> historyLine = new ArrayList<HistoryInstance>();
        
        class HistoryInstance implements Serializable {
            private static final long SerialVerionUID = 1L;
            
            HistoryInstance() {
                if(index < historyLine.size()-1) {
                    historyLine.subList(index+1, historyLine.size()).clear();
                }
                index++;
            }
        }
        
        class HistoryDraw extends HistoryInstance implements Serializable {
            private static final long SerialVerionUID = 1L;
            CustomShape shapeChanged;
            HistoryDraw(CustomShape s) {
                super();
                shapeChanged = s;
            }
        }

        class HistorySelectMove extends HistoryInstance implements Serializable {
            private static final long SerialVerionUID = 1L;
            CustomShape shapeChanged;double[] coordinate;
            HistorySelectMove(CustomShape s) {
                super();
                shapeChanged = s;
                if(s!=null){coordinate=s.getCoordinate();}
                
            }
        }
        
        class HistoryCut extends HistoryInstance implements Serializable{
            private static final long SerialVerionUID = 1L;
            List<CustomShape> shapesChanged;
            HistoryCut(List<CustomShape> s){
                super();
                shapesChanged = s;
            }
        }

        class HistoryPaste extends HistoryInstance implements Serializable{
            private static final long SerialVerionUID = 1L;
            List<CustomShape> shapesChanged;
            HistoryPaste(List<CustomShape> s){
                super();
                shapesChanged = s;
            }
        }

        class HistoryGroupMove extends HistoryInstance implements Serializable {
            private static final long SerialVerionUID = 1L;
            List<CustomShape> shapesChanged; ArrayList<double[]> coordinatesBegin;ArrayList<double[]> coordinatesEnd;
            HistoryGroupMove(List<CustomShape> shapes){
                super();
                shapesChanged    = new ArrayList<CustomShape>();
                coordinatesBegin = new ArrayList<double[]>();
                coordinatesEnd   = new ArrayList<double[]>();
                for(CustomShape s: shapes){
                    shapesChanged.add(s);
                    coordinatesBegin.add(s.getCoordinate());
                }
            }
        }

        /*class HistoryGroupCut extends HistoryInstance implements Serializable{
            private static final long SerialVerionUID = 1L;
            List<CustomShape> shapesChanged;
            HistoryGroupCut(){
                super();
                shapesChanged = new ArrayList<CustomShape>();}}*/
    }
    
    DrawingPanel(Color color) {
        setShapeColor(color);
        //setShapeSize(size);
        addMouseMotionListener(this);
        addMouseListener(this);
        setBackground(Color.WHITE);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        for (CustomShape shape : shapes) {
            shape.draw(g2);
        }
        
        for (CustomShape shape2 : selectedShapes){
            shape2.gradient(g2);
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
    
    void drawCurrentShape(Point startPoint,Point endPoint,String status){
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
        //if (status=="Pressed"){history.historyLine.add(history.new HistoryInstance(mode,currentShape) );}
        shapes.add(currentShape);
        if(status=="Released"){history.historyLine.add(history.new HistoryDraw(currentShape));} // status == "Released"
        currentShape = null;
        repaint();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
       // System.out.println("");
        
        //history.historyLine.forEach(l -> {
          //  System.out.print(l.shapeChanged.getCoordinate()[0]);
        //});*/  // development testing
    
        if ((mode == DrawingMode.SELECT && selectedShapes.size() != 0) ||mode==DrawingMode.GROUP ) {
            //System.out.println(selectedShapes.size());
            int dx = e.getX() - startPoint.x;
            int dy = e.getY() - startPoint.y;
            //System.out.println(selectedShapes.size());
            // when press and one selectedShapre for release to delete
            if(selectedShapes.size()==2 && selectedShapes.get(0)==selectedShapes.get(1)){for(CustomShape temp : selectedShapes){temp.move(dx, dy);break;}}
            else {for(CustomShape temp : selectedShapes){temp.move(dx, dy);}}
            startPoint = e.getPoint();
            repaint();
        }
        else if (mode == DrawingMode.SCRIBBED) {
            System.out.println("mouse dragged !");
            if (shapes.size() == 0) {
                shapes.add(sLine);
                repaint();
                return;
            }
            if (shapes.get(shapes.size()-1) instanceof Scribbled) {
                shapes.remove(shapes.size()-1);
            }
            sLine.addPoints(e.getPoint());
            shapes.add(sLine);
            repaint();
        }
        else if (mode != DrawingMode.SELECT && mode != DrawingMode.OPEN_POLYGON && mode != DrawingMode.CLOSED_POLYGON && mode != DrawingMode.COPY && mode != DrawingMode.CUT && mode != DrawingMode.PASTE) {
            Point endPoint = e.getPoint();
            shapes.remove(shapes.size()-1);
            drawCurrentShape(startPoint,endPoint,"Dragged");
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("release");
        if (mode == DrawingMode.SELECT && selectedShapes.size() != 0) { //SELECT MODE, NOT NULL
            
            history.historyLine.add(history.new HistorySelectMove(selectedShapes.get(selectedShapes.size()-1))); //add history, only one item in SELECT_MOVE
            System.out.print(selectedShapes.size());
            if(currentShape!=null){selectedShapes.remove(selectedShapes.size()-1);}
            
            
            repaint();
            hasReleased=true;

        } /*else if (mode != DrawingMode.SELECT && currentShape != null) {
            shapes.add(currentShape);
            currentShape = null;
            repaint();
        }*/
        else if (mode==DrawingMode.GROUP && selectedShapes.size() != 0){
            History.HistoryInstance temp = history.historyLine.get(history.historyLine.size()-1);
            if(temp instanceof History.HistoryGroupMove){
                DrawingPanel.History.HistoryGroupMove tempA=(DrawingPanel.History.HistoryGroupMove) temp;
                for(CustomShape s: selectedShapes){
                
                    tempA.coordinatesEnd.add(s.getCoordinate());
                }
                
            }
        }
        else if (mode == DrawingMode.SCRIBBED) {
            //System.out.println("mouse released");
            sLine.addPoints(e.getPoint());
            shapes.add(sLine);
            repaint();
            sLine = new Scribbled(new Line2D.Double(new Point(), new Point()), Color.BLACK);
        } else if(mode!=DrawingMode.SELECT&&mode!=DrawingMode.COPY&&mode!=DrawingMode.CUT&&mode!=DrawingMode.PASTE&&mode!=DrawingMode.OPEN_POLYGON &&mode!=DrawingMode.CLOSED_POLYGON && mode!=DrawingMode.UNGROUP){shapes.remove(shapes.size()-1);drawCurrentShape(startPoint,e.getPoint(),"Released");}
        
        //System.out.println("");
        //System.out.println(history.index);
        //history.historyLine.forEach(l -> {
            //System.out.print(l.shapeChanged.getCoordinate()[0]);
        //});*/ // development testing
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

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("clicked");
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
        }else if (mode == DrawingMode.PASTE && clipboardShape.size()>0) {
            
            Point endPoint = e.getPoint();
            
            List<CustomShape> templist = new ArrayList<CustomShape>();
            if(clipboardShape.size()==1){
                
                for(CustomShape temp : clipboardShape){
                    CustomShape newShape = cloneShape(temp);
                    // Move the new shape to the location of the mouse click
                    moveShape(newShape, endPoint.x - temp.getShape().getBounds2D().getX(), endPoint.y - temp.getShape().getBounds2D().getY());
                    //moveShape(newShape, dx, dy);
                    shapes.add(newShape);
                    templist.add(newShape);     
                }
                
            }else if(clipboardShape.size()>1){
                double dx=0,dy=0, xMin=900;
                for(CustomShape temp : clipboardShape){
                    if(temp.getShape().getBounds2D().getX()<xMin){ 
                        xMin=temp.getShape().getBounds2D().getX();
                        dx=endPoint.x-xMin;
                        dy=endPoint.y-temp.getShape().getBounds2D().getY();
                    }
                }
                
                for(CustomShape temp : clipboardShape){
                    CustomShape newShape = cloneShape(temp);
                    moveShape(newShape, dx, dy);// Move the new shape to the location of the mouse click
                    shapes.add(newShape);  
                    templist.add(newShape);
                }
                /*History.HistoryInstance tempA = history.historyLine.get(history.historyLine.size()-1);
                    if(tempA instanceof History.HistoryPaste){
                        History.HistoryPaste tempB=(History.HistoryPaste) tempA;
                        tempB.shapesChanged.add(newShape);} */ 
            }
            history.historyLine.add(history.new HistoryPaste(templist)); 
            if (isCutOperation) {
                clipboardShape.clear();
                isCutOperation = false;
            }
            repaint();
         
        } else if (mode == DrawingMode.SELECT){
            
           
                //System.out.print(selectedShapes.size());
            if(hasReleased==true){
                if(history.index>=1){history.index=history.index-2;}  // press->release->click, even with one click
                if(history.historyLine.size()>=2){history.historyLine.remove(history.historyLine.size()-1);history.historyLine.remove(history.historyLine.size()-1);}
            }
            
                
            if(currentShape==null){selectedShapes.clear();}
            else{selectedShapes.add(currentShape);}
                
            repaint();
            
        } else if (mode == DrawingMode.SCRIBBED) {
            // do nothing
        } else if (mode != DrawingMode.SELECT && mode != DrawingMode.OPEN_POLYGON && mode != DrawingMode.CLOSED_POLYGON && mode != DrawingMode.COPY && mode != DrawingMode.CUT && mode != DrawingMode.PASTE && mode != DrawingMode.GROUP && mode != DrawingMode.UNGROUP){
            if(history.historyLine.get(history.historyLine.size()-1) instanceof History.HistoryDraw){history.historyLine.remove(history.historyLine.size()-1);history.index--;}
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("press");
        startPoint = e.getPoint();
        if (mode == DrawingMode.SELECT) {
            hasReleased=false;
            boolean found=false;
            for (CustomShape shape : shapes) {
                if (shape.contains(startPoint)) { // found, clicked a shape
                    found=true;
                    
                    if(selectedShapes.contains(shape)){ // clicked a shape in select
                        if(selectedShapes.size()==1){
                           
                            currentShape=null;
                            history.historyLine.add(history.new HistorySelectMove(shape));// for clicked to remove
                        }else if(selectedShapes.size()>1){
                            selectedShapes.clear();
                            currentShape=shape;
                            selectedShapes.add(shape); // release will remove this
                            history.historyLine.add(history.new HistorySelectMove(shape));// for clicked to remove
                        }
                        
                    } else{// clicked a shape not in select
                        selectedShapes.clear();
                        selectedShapes.add(shape);
                        repaint();
                        currentShape=shape;
                        history.historyLine.add(history.new HistorySelectMove(shape));
                        
                    } 
                    break;
                }
            }
            if(found==false){selectedShapes.clear();repaint();currentShape=null;}
            
            
        }else if(mode == DrawingMode.GROUP){
            for (CustomShape shape : shapes) {
                if (shape.contains(startPoint)) { // found the shape
                    if(selectedShapes.contains(shape)){  // already grouped
                        history.historyLine.add(history.new HistoryGroupMove(selectedShapes));
                    }else{ // not grouped yet
                        selectedShapes.add(shape);
                        repaint();
                    }
                    break;
                }
            }
        }else if(mode == DrawingMode.UNGROUP){
            for (CustomShape shape : shapes) {
                if (shape.contains(startPoint)) { // found the shape                 
                    selectedShapes.remove(shape);
                    repaint();
                    break;
                }
            }
        } else if (mode == DrawingMode.SCRIBBED) {
            // System.out.println("mouse Pressed!");
            sLine.addPoints(e.getPoint());
        } else if (mode!=DrawingMode.COPY && mode!=DrawingMode.CUT && mode!=DrawingMode.PASTE&&mode!=DrawingMode.OPEN_POLYGON &&mode!=DrawingMode.CLOSED_POLYGON){ // might need changes
            drawCurrentShape(startPoint,startPoint,"Pressed");
        }
    }

    public CustomShape cloneShape(CustomShape shape) {
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

    private void moveShape(CustomShape shape, double dx, double dy) {
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
