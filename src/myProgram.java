import javax.swing.*;

public class myProgram {
    public static void main(String[] args) {
        JFrame frame = new myFrame();
        frame.setTitle("Sketchpad Program");
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

