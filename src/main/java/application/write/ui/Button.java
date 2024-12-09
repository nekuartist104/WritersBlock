package application.write.ui;

import javax.swing.*;
import java.awt.*;

public class Button extends JButton {

    private int id;
    private boolean exists;
    private TextEditor textEditor;
    private WritePopupMenu popupMenu;

    public Button() {
        setMinimumSize(new Dimension(200, 50));
        setMaximumSize(new Dimension(200, 50));
        setFont(new Font("Cambria", Font.PLAIN, 16));
//        setBorder(BorderFactory.createEmptyBorder());
//        setBackground(Color.white);
        setExists(false);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public TextEditor getTextEditor() {
        return textEditor;
    }

    public void setTextEditor(TextEditor textEditor) {
        this.textEditor = textEditor;
    }

    public WritePopupMenu getPopupMenu() {
        return popupMenu;
    }

    public void setPopupMenu(WritePopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }
}
