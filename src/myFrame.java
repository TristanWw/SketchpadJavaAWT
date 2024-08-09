import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.*;

public class myFrame extends JFrame implements ActionListener {
    private myPanel drawPanel = new myPanel(Color.BLACK);
    private JToolBar toolbar = new JToolBar();

    public myFrame() {
        Container con = getContentPane();
        con.setLayout(new BorderLayout());

        setupMenu();
        setupToolbar();

        con.add(toolbar, BorderLayout.NORTH);
        con.add(drawPanel, BorderLayout.CENTER);

        drawPanel.setPreferredSize(new Dimension(1200, 750));
    }

    private void setupMenu() {
        JMenuBar mainMenuBar = new JMenuBar();

        JMenu menu1 = new JMenu("File");
        // File subsection
        JMenuItem clear = new JMenuItem("Clear");
        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem load = new JMenuItem("Load");
        clear.addActionListener(this);
        exit.addActionListener(this);
        save.addActionListener(this);
        load.addActionListener(this);
        menu1.add(clear);
        menu1.add(exit);
        menu1.add(save);
        menu1.add(load);

        JMenu menu2 = new JMenu("Operations");
        // Group and Ungroup
        JMenuItem group = new JMenuItem("Group");
        JMenuItem ungroup = new JMenuItem("Ungroup");
        group.addActionListener(this);
        ungroup.addActionListener(this);
        menu2.add(group);
        menu2.add(ungroup);

        JMenu menu3 = new JMenu("Color");
        // Color subsection
        JMenuItem blackMenu = new JMenuItem("Black");
        JMenuItem greenMenu = new JMenuItem("Green");
        JMenuItem yellowMenu = new JMenuItem("Yellow");
        JMenuItem redMenu = new JMenuItem("Red");
        JMenuItem blueMenu = new JMenuItem("Blue");
        blackMenu.addActionListener(this);
        greenMenu.addActionListener(this);
        yellowMenu.addActionListener(this);
        redMenu.addActionListener(this);
        blueMenu.addActionListener(this);
        menu3.add(blackMenu);
        menu3.add(greenMenu);
        menu3.add(yellowMenu);
        menu3.add(redMenu);
        menu3.add(blueMenu);

        JMenu menu4 = new JMenu("Help");
        // Debug function
        JMenuItem debug = new JMenuItem("Debug");
        // Help subsection
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(this);
        debug.addActionListener(this);
        menu4.add(about);
        menu4.add(debug);

        // seup the main meu
        setJMenuBar(mainMenuBar);
        mainMenuBar.add(menu1);
        mainMenuBar.add(menu2);
        mainMenuBar.add(menu3);
        mainMenuBar.add(menu4);
    }

    private void setupToolbar() {
        JButton scribbledButton = new JButton("ScribbledLine");
        scribbledButton.addActionListener(e -> drawPanel.setMode(DrawingMode.SCRIBBED));
        toolbar.add(scribbledButton);

        JButton lineButton = new JButton("Line");
        lineButton.addActionListener(e -> drawPanel.setMode(DrawingMode.LINE));
        toolbar.add(lineButton);

        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(e -> drawPanel.setMode(DrawingMode.SELECT));
        toolbar.add(selectButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String arg = e.getActionCommand();
        switch (arg) {
            case "Exit":
                System.exit(0);
                break;
            case "About":
                JOptionPane.showMessageDialog(null, "Sketchpad Program");
                break;
            case "Clear":
                drawPanel.clear();
                break;
            case "Red":
                drawPanel.setPanelColor(Color.RED);
                break;
            case "Black":
                drawPanel.setPanelColor(Color.BLACK);
                break;
            case "Yellow":
                drawPanel.setPanelColor(Color.YELLOW);
                break;
            case "Green":
                drawPanel.setPanelColor(Color.GREEN);
                break;
            case "Blue":
                drawPanel.setPanelColor(Color.BLUE);
                break;
            case "Save":
                // Serialize the object and save to a file
                saveToFile();
                break;
            case "Load":
                // Load from the file and substitute the object
                loadFromFile();
                break;
            case "Group":
                drawPanel.groupSelectedObjs();
                break;
            case "Ungroup":
                drawPanel.ungroupSelectedObjs();
                break;
            case "Debug":
                drawPanel.debug();
                break;
            default:
                // do nothing
                break;
        }
    }

    private void saveToFile() {
        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();

        // Show the save dialog
        int option = fileChooser.showSaveDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileOutputStream fileOut = new FileOutputStream(file);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                drawPanel.resetBeforeSave();
                out.writeObject(drawPanel);
                System.out.println("File saved: " + file.getAbsolutePath());
            } catch (IOException i) {
                i.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving file: " + i.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
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
            try (FileInputStream fileIn = new FileInputStream(file);
                    ObjectInputStream in = new ObjectInputStream(fileIn)) {
                // Deserialize the new DrawingPanel
                myPanel newDrawPanel = (myPanel) in.readObject();

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
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("Load command cancelled by user.");
        }
    }
}
