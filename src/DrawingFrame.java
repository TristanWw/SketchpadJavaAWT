import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class DrawingFrame extends JFrame implements ActionListener {
    //private static final int SMALL = 4;
    //private static final int MEDIUM = 8;
    //private static final int LARGE = 10;

    private DrawingPanel drawPanel = new DrawingPanel(Color.BLACK);
    private JToolBar toolbar = new JToolBar();

    DrawingFrame() {
        Container con = getContentPane();
        con.setLayout(new BorderLayout());

        setupMenu();
        setupToolbar();

        con.add(toolbar, BorderLayout.NORTH);
        con.add(drawPanel, BorderLayout.CENTER);

        drawPanel.setPreferredSize(new Dimension(600, 600));
    }

    private void setupMenu() {
        JMenuBar mainMenuBar = new JMenuBar();
        JMenu menu1 = new JMenu("File");
        //JMenu menu2 = new JMenu("Size");
        JMenu menu3 = new JMenu("Color");
        JMenu menu4 = new JMenu("Help");

        JMenuItem clear = new JMenuItem("Clear");
        JMenuItem exit = new JMenuItem("Exit");
        //JMenuItem small = new JMenuItem("Small");
        //JMenuItem medium = new JMenuItem("Medium");
        //JMenuItem large = new JMenuItem("Large");
        JMenuItem blackMenu = new JMenuItem("Black");
        JMenuItem greenMenu = new JMenuItem("Green");
        JMenuItem yellowMenu = new JMenuItem("Yellow");
        JMenuItem redMenu = new JMenuItem("Red");
        JMenuItem blueMenu = new JMenuItem("Blue");
        JMenuItem about = new JMenuItem("About");

        menu1.add(clear);
        menu1.add(exit);
        //menu2.add(small);
        //menu2.add(medium);
        //menu2.add(large);
        menu3.add(blackMenu);
        menu3.add(greenMenu);
        menu3.add(yellowMenu);
        menu3.add(redMenu);
        menu3.add(blueMenu);
        menu4.add(about);

        clear.addActionListener(this);
        exit.addActionListener(this);
        //small.addActionListener(this);
        //medium.addActionListener(this);
        //large.addActionListener(this);
        blackMenu.addActionListener(this);
        greenMenu.addActionListener(this);
        yellowMenu.addActionListener(this);
        redMenu.addActionListener(this);
        blueMenu.addActionListener(this);
        about.addActionListener(this);

        setJMenuBar(mainMenuBar);
        mainMenuBar.add(menu1);
        //mainMenuBar.add(menu2);
        mainMenuBar.add(menu3);
        mainMenuBar.add(menu4);
    }

    private void setupToolbar() {
        JButton circleButton = new JButton("Circle");
        circleButton.addActionListener(e -> drawPanel.setMode(DrawingMode.CIRCLE));
        toolbar.add(circleButton);

        JButton lineButton = new JButton("Line");
        lineButton.addActionListener(e -> drawPanel.setMode(DrawingMode.LINE));
        toolbar.add(lineButton);

        JButton rectangleButton = new JButton("Rectangle");
        rectangleButton.addActionListener(e -> drawPanel.setMode(DrawingMode.RECTANGLE));
        toolbar.add(rectangleButton);

        JButton ellipseButton = new JButton("Ellipse");
        ellipseButton.addActionListener(e -> drawPanel.setMode(DrawingMode.ELLIPSE));
        toolbar.add(ellipseButton);

        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(e -> drawPanel.setMode(DrawingMode.SELECT));
        toolbar.add(selectButton);

        JButton copyButton = new JButton("Copy");
        copyButton.addActionListener(e -> drawPanel.setMode(DrawingMode.COPY));
        toolbar.add(copyButton);

        JButton cutButton = new JButton("Cut");
        cutButton.addActionListener(e -> drawPanel.setMode(DrawingMode.CUT));
        toolbar.add(cutButton);

        JButton pasteButton = new JButton("Paste");
        pasteButton.addActionListener(e -> drawPanel.setMode(DrawingMode.PASTE));
        toolbar.add(pasteButton);

        // Add OPEN_POLYGON and CLOSED_POLYGON buttons
        JButton openPolygonButton = new JButton("Open Polygon");
        openPolygonButton.addActionListener(e -> drawPanel.setMode(DrawingMode.OPEN_POLYGON));
        toolbar.add(openPolygonButton);

        JButton closedPolygonButton = new JButton("Closed Polygon");
        closedPolygonButton.addActionListener(e -> drawPanel.setMode(DrawingMode.CLOSED_POLYGON));
        toolbar.add(closedPolygonButton);
        
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> {
            if (drawPanel.historyIndex>-1){
                DrawingMode temp = drawPanel.modeHistory.get(drawPanel.historyIndex);
                if(temp == DrawingMode.SELECT) {
                }else if(temp == DrawingMode.COPY){
                }else if(temp == DrawingMode.CUT){
                }else if(temp == DrawingMode.PASTE){
                }else{
                    //System.out.println(drawPanel.modeHistory.get(drawPanel.modeHistory.size()-1));
                    drawPanel.shapes.remove(drawPanel.shapes.size()-1);
                    drawPanel.historyIndex--;
                    drawPanel.repaint();
                }
            }
            
        });
        toolbar.add(undoButton);
        
        JButton redoButton = new JButton("Redo");
        //redoButton.addActionListener(e -> drawPanel.setMode(DrawingMode.REDO));
        toolbar.add(redoButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String arg = e.getActionCommand();
        switch (arg) {
            case "Exit":
                System.exit(0);
                break;
            case "About":
                JOptionPane.showMessageDialog(null, "Drawing Program by a");
                break;
            case "Clear":
                drawPanel.clear();
                break;
            case "Red":
                drawPanel.setShapeColor(Color.RED);
                break;
            case "Black":
                drawPanel.setShapeColor(Color.BLACK);
                break;
            case "Yellow":
                drawPanel.setShapeColor(Color.YELLOW);
                break;
            case "Green":
                drawPanel.setShapeColor(Color.GREEN);
                break;
            case "Blue":
                drawPanel.setShapeColor(Color.BLUE);
                break;
            //case "Small":
                //drawPanel.setShapeSize(SMALL);
                //break;
            //case "Medium":
                //drawPanel.setShapeSize(MEDIUM);
                //break;
            //case "Large":
                //drawPanel.setShapeSize(LARGE);
                //break;
        }
    }
}
