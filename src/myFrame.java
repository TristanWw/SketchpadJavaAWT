import java.awt.*;
import java.awt.event.*;
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
        // Help subsection
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(this);
        menu4.add(about);

        // seup the main meu
        setJMenuBar(mainMenuBar);
        mainMenuBar.add(menu1);
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
            default:
                // do nothing
                break;
        }
    }
}
