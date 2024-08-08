import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

class CustomShape implements Serializable {
    private static final long SerialVerionUID = 1L;
    private Shape shape;
    private Color color;

    public CustomShape(Shape shape, Color color) {
        this.shape = shape;
        this.color = color;
    }

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
        g.draw(shape);
        g.fill(shape);
    }
    
    public void gradient(Graphics2D g) {
        Rectangle bounds = shape.getBounds();
        GradientPaint gradientPaint = new GradientPaint(bounds.x,bounds.y,color.CYAN,bounds.x+bounds.width,bounds.y+bounds.height,color.MAGENTA);
        g.setPaint(gradientPaint);
        g.setStroke(new BasicStroke(5));
        g.draw(shape);
    }
    
    public boolean contains(Point p) {
        if (shape instanceof Line2D) {
            Line2D line = (Line2D) shape;
            return line.ptSegDist(p) <= 5.0; // Adjust the threshold as needed
        }
        return shape.contains(p);
    }
    
    public double[] getCoordinate(){
        if (shape instanceof Ellipse2D) {
            Ellipse2D ellipse = (Ellipse2D) shape;
            return new double[]{ellipse.getX(),ellipse.getY()};
            
        } else if (shape instanceof Rectangle2D) {
            Rectangle2D rect = (Rectangle2D) shape;
            return new double[]{rect.getX(),rect.getY()};
            
        } else if (shape instanceof Line2D) {
            Line2D line = (Line2D) shape;
            return new double[]{line.getX1(),line.getY1(),line.getX2(),line.getY2()};
        }
        return null;
    }
    
    public void move(double dx, double dy) {
        if (shape instanceof Ellipse2D) {
            Ellipse2D ellipse = (Ellipse2D) shape;
            shape = new Ellipse2D.Double(
                    ellipse.getX() + dx,
                    ellipse.getY() + dy,
                    ellipse.getWidth(),
                    ellipse.getHeight()
            );
        } else if (shape instanceof Rectangle2D) {
            Rectangle2D rect = (Rectangle2D) shape;
            shape = new Rectangle2D.Double(
                    rect.getX() + dx,
                    rect.getY() + dy,
                    rect.getWidth(),
                    rect.getHeight()
            );
        } else if (shape instanceof Line2D) {
            Line2D line = (Line2D) shape;
            shape = new Line2D.Double(
                    line.getX1() + dx,
                    line.getY1() + dy,
                    line.getX2() + dx,
                    line.getY2() + dy
            );
        } else if (shape instanceof GeneralPath) {
            GeneralPath path = (GeneralPath) shape;
            AffineTransform transform = new AffineTransform();
            transform.translate(dx, dy);
            shape = path.createTransformedShape(transform);
        }
        //System.out.println("New shape: " + shape.getBounds());
    }
}

class Scribbled extends CustomShape {
    private List<Point> scribbledPoints;

    public Scribbled(Shape shape, Color color) {
        super(shape, color);
        this.scribbledPoints = new ArrayList<Point>();
    }

    @Override
    public void draw(Graphics2D g) {
        if (scribbledPoints.size() == 0)
            return;
        Point last = scribbledPoints.get(0);
        for (int i = 1; i < scribbledPoints.size(); i++) {
            g.drawLine(last.x, last.y, scribbledPoints.get(i).x, scribbledPoints.get(i).y);
            last = scribbledPoints.get(i);
        }
    }

    @Override
    public void gradient(Graphics2D g) {
        if (scribbledPoints.size() == 0)
            return;

        Point last = scribbledPoints.get(0);
        for (int i = 1; i < scribbledPoints.size(); i++) {
            Point current = scribbledPoints.get(i);

            GradientPaint gradientPaint = new GradientPaint(
                    last.x, last.y, Color.CYAN,
                    current.x, current.y, Color.MAGENTA
                    );

            g.setPaint(gradientPaint);
            g.setStroke(new BasicStroke(2));
            g.drawLine(last.x, last.y, current.x, current.y);

            last = current;
        }
    }

    private boolean isPointNearLineSegment(Point p, Point start, Point end, double epsilon) {
        // Calculate the length of the line segment
        double segmentLengthSquared = start.distanceSq(end);
        if (segmentLengthSquared == 0.0) {
            // The line segment is just a point
            return p.distance(start) < epsilon;
        }

        // Projection formula to find the projection of point p onto the line segment
        double t = ((p.x - start.x) * (end.x - start.x) + (p.y - start.y) * (end.y - start.y)) / segmentLengthSquared;
        t = Math.max(0, Math.min(1, t));

        // Find the nearest point on the segment to p
        double nearestX = start.x + t * (end.x - start.x);
        double nearestY = start.y + t * (end.y - start.y);
        Point nearestPoint = new Point((int) nearestX, (int) nearestY);

        // Check the distance from p to the nearest point on the line segment
        return p.distance(nearestPoint) < epsilon;
    }

    @Override
    public boolean contains(Point p) {
        // calculate the distance between the points on the scribbled line and p
        // to determine whether p is on the scribbled line
        double epsilon = 5.0; // Threshold distance to determine if the point is on the line

        for (int i = 0; i < scribbledPoints.size() - 1; i++) {
            Point start = scribbledPoints.get(i);
            Point end = scribbledPoints.get(i + 1);

            if (isPointNearLineSegment(p, start, end, epsilon)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double[] getCoordinate() {
        return null;
    }

    @Override
    public void move(double dx, double dy) {
        for (Point p : scribbledPoints) {
            p.x += dx;
            p.y += dy;
        }
    }

    public void addPoints(Point p) {
        scribbledPoints.add(p);
    }
}
