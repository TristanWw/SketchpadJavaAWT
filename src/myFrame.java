import java.awt.*;
import java.awt.event.*;

public class myFrame extends Frame {
    private myCanvas canvas;
    private Panel buttonPanel;
    private Button straightLineButton;
    private Button scribbledButton;

    private boolean isDrawingLine;
    private Point startPoint, endPoint;
    private boolean isDrawingScribbledLine;
    private ScribbledLine sLine;

    public myFrame() {
        setSize(800, 600);

        // Create button panel
        buttonPanel = new Panel();
        buttonPanel.setPreferredSize(new Dimension(100, 600));

        // Create button
        straightLineButton = new Button("Straight Line");
        buttonPanel.add(straightLineButton);
        scribbledButton = new Button("Scribbled Line");
        buttonPanel.add(scribbledButton);

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

        // Add action listeners to buttons
        straightLineButton.addActionListener(new ButtonClickListener());
        scribbledButton.addActionListener(new ButtonClickListener());
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == straightLineButton) {
                isDrawingLine = true; // Enable drawing straight lines
                isDrawingScribbledLine = false;
                for (MouseListener l : canvas.getMouseListeners()) {
                    canvas.removeMouseListener(l);
                }
                // set up listeners
                canvas.addMouseListener(new myStraightLineMouseHandler()); // Attach to canvas
                canvas.addMouseMotionListener(new myStraightLineMouseMotionHandler()); // Attach to canvas
            } else if (e.getSource() == scribbledButton) {
                isDrawingLine = false; // Enable drawing scribbled lines
                isDrawingScribbledLine = true;
                for (MouseListener l : canvas.getMouseListeners()) {
                    canvas.removeMouseListener(l);
                }
                // set up listeners
                canvas.addMouseListener(new myScribbledLineMouseHandler()); // Attach to canvas
                canvas.addMouseMotionListener(new myScribbledLineMouseMotionHandler()); // Attach to canvas
            }
        }
    }

    public class myStraightLineMouseHandler extends MouseAdapter {
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

    public class myStraightLineMouseMotionHandler extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            if (isDrawingLine) {
                endPoint = e.getPoint();
                Line dashLine = new Line(startPoint, endPoint);
                dashLine.draw(getGraphics());
                canvas.repaint();
            }
        }
    }

    public class myScribbledLineMouseHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            isDrawingScribbledLine = true;
            sLine = new ScribbledLine();
            sLine.addPoints(e.getPoint());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            isDrawingScribbledLine = false;
            sLine.addPoints(e.getPoint());
            canvas.addObj(sLine);
            canvas.repaint();
        }
    }

    public class myScribbledLineMouseMotionHandler extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            if (isDrawingScribbledLine) {
                sLine.addPoints(e.getPoint());
                canvas.repaint();
            }
        }
    }

    public static void main(String[] args) {
        new myFrame().setVisible(true);
    }
}
