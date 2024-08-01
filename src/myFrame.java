import java.awt.*;
import java.awt.event.*;

public class myFrame extends Frame {
    private myCanvas canvas;
    private Panel buttonPanel;
    private Button straightLineButton;

    private boolean isDrawingLine;
    private Point startPoint, endPoint;

    public myFrame() {
        setSize(800, 600);

        // Create button panel
        buttonPanel = new Panel();
        buttonPanel.setPreferredSize(new Dimension(100, 600));

        // Create button
        straightLineButton = new Button("Straight Line");
        buttonPanel.add(straightLineButton);

        // Add button panel to the left side of the frame
        add(buttonPanel, BorderLayout.WEST);

        // Create canvas
        canvas = new myCanvas();
        add(canvas, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose(); // Close the frame
            }
        });

        // set up listeners
        canvas.addMouseListener(new myMouseHandler()); // Attach to canvas
        canvas.addMouseMotionListener(new myMouseMotionHandler()); // Attach to canvas
    }

    public class myMouseHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            isDrawingLine = true;
            startPoint = e.getPoint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            isDrawingLine = false;
            endPoint = e.getPoint();
            Line line = new Line(startPoint, endPoint);
            canvas.addObj(line);
            canvas.repaint();
        }
    }

    public class myMouseMotionHandler extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            if (isDrawingLine) {
                endPoint = e.getPoint();
                canvas.repaint();
            }
        }
    }

    public static void main(String[] args) {
        new myFrame().setVisible(true);
    }
}
