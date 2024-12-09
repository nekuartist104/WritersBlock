package application.shared.ui;

import javax.swing.*;

public class PopupMenu extends JPopupMenu {

    protected JMenuItem renameMenuItem;
    protected JMenuItem removeMenuItem;

    public JMenuItem getRenameMenuItem() {
        return renameMenuItem;
    }

    public JMenuItem getRemoveMenuItem() {
        return removeMenuItem;
    }

    public PopupMenu() {
        Icon removeIcon = new ImageIcon(getClass().getClassLoader().getResource("buttonIcons/removeButton.png"));
        Icon renameIcon = new ImageIcon(getClass().getClassLoader().getResource("buttonIcons/editIcon.png"));
        renameMenuItem = new JMenuItem("Rename");
        renameMenuItem.setIcon(renameIcon);
        removeMenuItem = new JMenuItem("Remove");
        removeMenuItem.setIcon(removeIcon);
        add(renameMenuItem);
        add(removeMenuItem);
    }
}