package application.factory;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class TextAndPopupFactory {

    public static JTextArea createTextAreaBox() {
        JTextArea box = new JTextArea();
        Border nameBorder = BorderFactory.createLineBorder(Color.GRAY);
        box.setBorder(BorderFactory.createCompoundBorder(nameBorder,
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        box.setPreferredSize(new Dimension(400,0));
        box.setLineWrap(true);
        return box;
    }

    public static String createPopupWindow(String prompt) {
        UIManager.put("OptionPane.okButtonText", "Continue");
        UIManager.put("OptionPane.cancelButtonText", "Cancel");
        JOptionPane popup = new JOptionPane();
        return popup.showInputDialog(prompt, null);
    }
}
