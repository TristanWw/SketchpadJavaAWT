import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

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
        JMenuItem save = new JMenuItem("Save");
        JMenuItem load = new JMenuItem("Load");
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
        menu1.add(save);
        menu1.add(load);
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
        save.addActionListener(this);
        load.addActionListener(this);
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
        
        // Add OPEN_POLYGON and CLOSED_POLYGON buttons
        JButton openPolygonButton = new JButton("Open Polygon");
        openPolygonButton.addActionListener(e -> drawPanel.setMode(DrawingMode.OPEN_POLYGON));
        toolbar.add(openPolygonButton);

        JButton closedPolygonButton = new JButton("Closed Polygon");
        closedPolygonButton.addActionListener(e -> drawPanel.setMode(DrawingMode.CLOSED_POLYGON));
        toolbar.add(closedPolygonButton);

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

        
        
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> {

            if (drawPanel.history.index>-1){ // at least one historyInstance
                DrawingPanel.History.HistoryInstance temp = drawPanel.history.historyLine.get(drawPanel.history.index);
                if(temp instanceof DrawingPanel.History.HistoryCopy){
                }else if(temp instanceof DrawingPanel.History.HistoryCut){
                }else if(temp instanceof DrawingPanel.History.HistoryPaste){
                }else if(temp instanceof DrawingPanel.History.HistorySelectMove){
                    if (drawPanel.history.index>0){ // at least two historyInstance
                        DrawingPanel.History.HistoryInstance temp1 =  drawPanel.history.historyLine.get(drawPanel.history.index-1);
                        if(temp1 instanceof DrawingPanel.History.HistorySelectMove){ // preceding one is also SELECT, two SELECT in a row
                            DrawingPanel.History.HistorySelectMove tempA=(DrawingPanel.History.HistorySelectMove) temp;
                            DrawingPanel.History.HistorySelectMove temp1A=(DrawingPanel.History.HistorySelectMove) temp1;
                            temp1A.shapeChanged.move(temp1A.coordinate[0]-tempA.coordinate[0],temp1A.coordinate[1]-tempA.coordinate[1]);
                        
                            drawPanel.history.index--;
                            drawPanel.history.index--;
                            drawPanel.repaint();
                        }
                    }
                }else{ // DRAW
                    //System.out.println(drawPanel.modeHistory.get(drawPanel.modeHistory.size()-1));
                    drawPanel.shapes.remove(drawPanel.shapes.size()-1);
                    drawPanel.history.index--;
                    drawPanel.repaint();
                }
            }
            
            
        
        System.out.println(drawPanel.history.index);
        });
        toolbar.add(undoButton);
        
        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> {
            
            if (drawPanel.history.index<drawPanel.history.historyLine.size()-1){ // at least one more following
                DrawingPanel.History.HistoryInstance temp = drawPanel.history.historyLine.get(drawPanel.history.index+1);
                
                if(temp instanceof DrawingPanel.History.HistoryCopy){
                }else if(temp instanceof DrawingPanel.History.HistoryCut){
                }else if(temp instanceof DrawingPanel.History.HistoryPaste){
                }else if(temp instanceof DrawingPanel.History.HistorySelectMove){ // next one is SELECT. the first time encounter is not SELECT
                    if (drawPanel.history.index>0){ // at least two more following
                        DrawingPanel.History.HistoryInstance temp1 =  drawPanel.history.historyLine.get(drawPanel.history.index+2);
                        if(temp1 instanceof DrawingPanel.History.HistorySelectMove){ // this next following is also SELECT
                            DrawingPanel.History.HistorySelectMove tempA=(DrawingPanel.History.HistorySelectMove) temp;
                            DrawingPanel.History.HistorySelectMove temp1A=(DrawingPanel.History.HistorySelectMove) temp1;
                            temp1A.shapeChanged.move(temp1A.coordinate[0]-tempA.coordinate[0],temp1A.coordinate[1]-tempA.coordinate[1]);
                            drawPanel.history.index++;
                            drawPanel.history.index++;
                            drawPanel.repaint();
                        }
                    }
                }else{
                    //System.out.println(drawPanel.modeHistory.get(drawPanel.modeHistory.size()-1));
                    DrawingPanel.History.HistoryDraw tempA=(DrawingPanel.History.HistoryDraw) temp;
                    drawPanel.shapes.add(tempA.shapeChanged);
                    drawPanel.history.index++;
                    drawPanel.repaint();
                }
            }
        System.out.println(drawPanel.history.index);
        });
        
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
            case "Save":
                // Serialize the object and save to a file
                saveToFile();
                break;
            case "Load":
                // Load from the file and substitute the object
                loadFromFile();
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

    private void saveToFile() {
        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();

        // Show the save dialog
        int option = fileChooser.showSaveDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileOutputStream fileOut = new FileOutputStream(file); ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(drawPanel); // Assuming drawPanel is Serializable
                System.out.println("File saved: " + file.getAbsolutePath());
            } catch (IOException i) {
                i.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving file: " + i.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("Save command cancelled by user.");
        }
    }

    private void loadFromFile() {
        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();

        // Show the open dialog
        int option = fileChooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileInputStream fileIn = new FileInputStream(file); ObjectInputStream in = new ObjectInputStream(fileIn)) {
                // Deserialize the new DrawingPanel
                DrawingPanel newDrawPanel = (DrawingPanel) in.readObject();

                // Remove the old DrawingPanel
                Container con = getContentPane();
                con.remove(drawPanel);

                // Set the new DrawingPanel
                drawPanel = newDrawPanel;

                // Add the new DrawingPanel to the frame
                con.add(drawPanel, BorderLayout.CENTER);

                // Revalidate and repaint to update the frame
                con.revalidate();
                con.repaint();
                System.out.println("File loaded: " + file.getAbsolutePath());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("Load command cancelled by user.");
        }
    }
}
