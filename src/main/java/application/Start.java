package application;

import javax.swing.*;
import java.awt.*;

public class Start {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Writer's Block Beta");
//        frame.setPreferredSize(new Dimension(950, 700));
        frame.setContentPane(new Window());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }
}
