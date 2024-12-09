package application.shared.ui;

import application.factory.ButtonFactory;

import javax.swing.*;
import java.awt.*;

public class Toolbar extends JToolBar {

    private JButton addSection;
    private JButton saveBtn;
//    private JButton renameBtn;
//    private JButton removeBtn;
    private JButton addImage;
    private JButton removeImage;
    private JButton addTab;

    public JButton getAddImage() {
        return addImage;
    }

    public JButton getAddSection() {
        return addSection;
    }

    public JButton getSaveBtn() {
        return saveBtn;
    }

//    public JButton getRenameBtn() {
//        return renameBtn;
//    }
//
//    public JButton getRemoveBtn() {
//        return removeBtn;
//    }

    public JButton getRemoveImage() {
        return removeImage;
    }

    public JButton getAddTab() {
        return addTab;
    }

    public Toolbar() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder());

        addTab = ButtonFactory.createAddTabButton();
        add(addTab);
        addSeparator(new Dimension(3,0));

        addSection = ButtonFactory.createAddSectionButton();
        add(addSection);
        addSeparator(new Dimension(3,0));

        addImage = ButtonFactory.addImageButton();
        add(addImage);
        addSeparator(new Dimension(3,0));

        removeImage = ButtonFactory.removeImageButton();
        add(removeImage);
        addSeparator(new Dimension(3,0));

//        addSection = ButtonFactory.createAddSectionButton();
//        add(addSection);
//        addSeparator(new Dimension(3,0));

//        renameBtn = ButtonFactory.createRenameButton();
//        add(renameBtn);
//        addSeparator(new Dimension(3,0));
//
//        removeBtn = ButtonFactory.createRemoveButton();
//        add(removeBtn);
//        addSeparator(new Dimension(3,0));

        saveBtn = ButtonFactory.createSaveButton();
        add(saveBtn);

        setFloatable(false);
    }
}
