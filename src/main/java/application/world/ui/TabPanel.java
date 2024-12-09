package application.world.ui;

import application.factory.ButtonFactory;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class TabPanel extends JPanel {
    private JButton closeTabButton;
    private JLabel label;

    public TabPanel(String tabName) {
        setLayout(new MigLayout("wrap 2, ins 0"));
        closeTabButton = ButtonFactory.removeTabButton();

        label = new JLabel(tabName, JLabel.CENTER);
        label.setFont(label.getFont().deriveFont(16f));

        add(label);
        add(closeTabButton);
        setOpaque(false);
    }

    public JButton getCloseTabButton() {
        return closeTabButton;
    }

    public void setCloseTabButton(JButton closeTabButton) {
        this.closeTabButton = closeTabButton;
    }

    public JLabel getLabel() {
        return label;
    }

    public void setLabel(JLabel label) {
        this.label = label;
    }
}
