package t12.t6;

import javax.swing.*;

public class DrawingProgram {
    public static void main(String[] args) {
        JFrame frame = new DrawingFrame();
        frame.setTitle("Drawing Program");
        frame.setSize(700, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}