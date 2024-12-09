package application.write.ui;

import application.shared.ui.PopupMenu;

import javax.swing.*;

public class WritePopupMenu extends PopupMenu {

    private JMenu addToGroupItem;
    private JMenuItem removeFromGroupItem;
    private JMenuItem addNewPartItem;

    public WritePopupMenu() {
        addToGroupItem = new JMenu("Add to a group");

        removeFromGroupItem = new JMenuItem("Remove from group");

        addNewPartItem = new JMenuItem("Add new part");
        addNewPartItem.setIcon(new ImageIcon(getClass().getClassLoader().getResource("buttonIcons/addSectionIcon.png")));

        add(addNewPartItem, 0);
        add(addToGroupItem, 1);
        add(removeFromGroupItem, 2);
    }

    public JMenu getAddToGroupItem() {
        return addToGroupItem;
    }

    public JMenuItem getRemoveFromGroupItem() {
        return removeFromGroupItem;
    }

    public JMenuItem getAddNewPartItem() {
        return addNewPartItem;
    }

}
