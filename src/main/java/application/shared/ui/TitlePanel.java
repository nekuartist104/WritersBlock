package application.shared.ui;

import application.factory.ButtonFactory;
import application.factory.OvalButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class TitlePanel extends JPanel {

    private JLabel title;
    private OvalButton addButton;

    public TitlePanel(String name) {
        setLayout(new MigLayout("wrap 2, insets 0 0 0 0", "grow", "grow"));
        setBackground(Color.WHITE);
        setOpaque(false);
        title = new JLabel(name + ":");
        addButton = ButtonFactory.createAddButton();
        add(title);
        add(addButton);
    }

    public JLabel getTitle() {
        return title;
    }

    public void setTitle(JLabel title) {
        this.title = title;
    }

    public OvalButton getAddButton() {
        return addButton;
    }

    public void setAddButton(OvalButton addButton) {
        this.addButton = addButton;
    }
}
