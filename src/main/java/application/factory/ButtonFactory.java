package application.factory;

import javax.swing.*;
import javax.swing.text.StyledEditorKit;
import java.awt.*;

public class ButtonFactory extends OvalButton {

    public static OvalButton createAddButton() {
        Icon addIcon = new ImageIcon(ButtonFactory.class.getClassLoader().getResource("buttonIcons/addButton.png"));
        OvalButton button = new OvalButton();
        button.setIcon(addIcon);
        button.setMaximumSize(new Dimension(16, 16));
        button.setMinimumSize(new Dimension(16, 16));
        button.setBorderThickness(1);
        button.setColorBorderNormal(Color.GRAY);
        return button;
    }

    public static JButton createSaveButton() {
        Icon saveIcon = new ImageIcon(ButtonFactory.class.getClassLoader().getResource("buttonIcons/saveIcon.png"));
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(16,16));
//        button.setBorder(BorderFactory.createEmptyBorder());
//        button.setBackground(Color.WHITE);
        button.setIcon(saveIcon);
        button.setToolTipText("Save");
        return button;
    }

    public static JButton createAddSectionButton() {
        Icon addNewSectionIcon = new ImageIcon(ButtonFactory.class.getClassLoader().getResource("buttonIcons/addSectionIcon.png"));
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(16,16));
        button.setIcon(addNewSectionIcon);
        button.setToolTipText("Adds a new section");
        return button;
    }

    public static JButton createRenameButton() {
        Icon renameIcon = new ImageIcon(ButtonFactory.class.getClassLoader().getResource("buttonIcons/editIcon.png"));
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(16,16));
        button.setIcon(renameIcon);
        button.setToolTipText("Rename");
        return button;
    }

    public static JButton createRemoveButton() {
        Icon removeIcon = new ImageIcon(ButtonFactory.class.getClassLoader().getResource("buttonIcons/removeButton.png"));
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(16,16));
        button.setIcon(removeIcon);
        button.setToolTipText("Remove");
        return button;
    }

    public static JButton addImageButton() {
        Icon addImageIcon = new ImageIcon(ButtonFactory.class.getClassLoader().getResource("buttonIcons/addImageIcon.png"));
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(16,16));
        button.setIcon(addImageIcon);
        button.setToolTipText("Add Image");
        return button;
    }

    public static JButton removeImageButton() {
        Icon removeImageIcon = new ImageIcon(ButtonFactory.class.getClassLoader().getResource("buttonIcons/removeImageIcon.png"));
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(16,16));
        button.setIcon(removeImageIcon);
        button.setToolTipText("Remove Image");
        return button;
    }

    public static JButton createAddTabButton() {
        Icon addTabIcon = new ImageIcon(ButtonFactory.class.getClassLoader().getResource("buttonIcons/addTabIcon.png"));
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(16,16));
        button.setIcon(addTabIcon);
        button.setToolTipText("Adds a new tab");
        return button;
    }

    public static JButton createRenameTabButton() {
        Icon addTabIcon = new ImageIcon(ButtonFactory.class.getClassLoader().getResource("buttonIcons/renameTabIcon.png"));
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(16,16));
        button.setIcon(addTabIcon);
        button.setToolTipText("Rename tab");
        return button;
    }

    public static JButton removeTabButton() {
        Icon removeTabIcon = new ImageIcon(ButtonFactory.class.getClassLoader().getResource("buttonIcons/removeTabIcon.png"));
        JButton button = new JButton();
        button.setMaximumSize(new Dimension(18,18));
        button.setIcon(removeTabIcon);
        button.setToolTipText("Removes current tab");
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        return button;
    }

    public static JButton createUndoButton() {
        Icon undoIcon = new ImageIcon(ButtonFactory.class.getClassLoader().getResource("buttonIcons/undoIcon.png"));
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(16,16));
        button.setIcon(undoIcon);
        button.setToolTipText("Undo");
        return button;
    }

    public static JButton createRedoButton() {
        Icon redoIcon = new ImageIcon(ButtonFactory.class.getClassLoader().getResource("buttonIcons/redoIcon.png"));
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(16,16));
        button.setIcon(redoIcon);
        button.setToolTipText("Redo");
        return button;
    }

    public static JButton createPreviewButton() {
        Icon previewIcon = new ImageIcon(ButtonFactory.class.getClassLoader().getResource("buttonIcons/previewIcon.png"));
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(16,16));
        button.setIcon(previewIcon);
        button.setToolTipText("Read-Only Mode");
        return button;
    }

    public static JButton createCopyButton() {
        Icon copyIcon = new ImageIcon(ButtonFactory.class.getClassLoader().getResource("buttonIcons/copyIcon.png"));
        JButton button = new JButton();
        button.setAction(new StyledEditorKit.CopyAction());
        button.setPreferredSize(new Dimension(16,16));
        button.setText("");
        button.setIcon(copyIcon);
        button.setToolTipText("Copy");
        button.setEnabled(false);
        return button;
    }

    public static JButton createPasteButton() {
        Icon pasteIcon = new ImageIcon(ButtonFactory.class.getClassLoader().getResource("buttonIcons/pasteIcon.png"));
        JButton button = new JButton();
        button.setAction(new StyledEditorKit.PasteAction());
        button.setPreferredSize(new Dimension(16,16));
        button.setText("");
        button.setIcon(pasteIcon);
        button.setToolTipText("Paste");
//        enablePaste(Toolkit.getDefaultToolkit().getSystemClipboard());
        return button;
    }

    public static JButton createBoldButton() {
        Icon boldIcon = new ImageIcon(ButtonFactory.class.getClassLoader().getResource("buttonIcons/boldIcon.png"));
        JButton button = new JButton();
        button.setAction(new StyledEditorKit.BoldAction());
        button.setPreferredSize(new Dimension(16,16));
        button.setText("");
        button.setIcon(boldIcon);
        button.setToolTipText("Bold");
        return button;
    }

    public static JButton createItalicButton() {
        Icon italicsIcon = new ImageIcon(ButtonFactory.class.getClassLoader().getResource("buttonIcons/italicsIcon.png"));
        JButton button = new JButton();
        button.setAction(new StyledEditorKit.ItalicAction());
        button.setPreferredSize(new Dimension(16,16));
        button.setText("");
        button.setIcon(italicsIcon);
        button.setToolTipText("Italic");
        return button;
    }
}
