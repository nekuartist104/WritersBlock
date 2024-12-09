package application.write.ui;

import application.factory.ButtonFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class TextToolbar extends JToolBar {

    private JButton undoBtn;
    private JButton redoBtn;
    private JButton boldBtn;
    private JButton italicBtn;
    private JButton copyBtn;
    private JButton pasteBtn;
    private JButton previewBtn;
    private JButton saveBtn;

    public JButton getUndoBtn() {
        return undoBtn;
    }

    public JButton getRedoBtn() {
        return redoBtn;
    }

    public JButton getBoldBtn() {
        return boldBtn;
    }

    public JButton getItalicBtn() {
        return italicBtn;
    }

    public JButton getCopyBtn() {
        return copyBtn;
    }

    public JButton getPasteBtn() {
        return pasteBtn;
    }

    public JButton getPreviewBtn() {
        return previewBtn;
    }

    public JButton getSaveBtn() {
        return saveBtn;
    }

    public TextToolbar() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder());

        undoBtn = ButtonFactory.createUndoButton();
        add(undoBtn);
        addSeparator(new Dimension(3,0));

        redoBtn = ButtonFactory.createRedoButton();
        add(redoBtn);
        addSeparator(new Dimension(3,0));

        boldBtn = ButtonFactory.createBoldButton();
        add(boldBtn);
        addSeparator(new Dimension(3,0));

        italicBtn = ButtonFactory.createItalicButton();
        add(italicBtn);
        addSeparator(new Dimension(3,0));

        copyBtn = ButtonFactory.createCopyButton();
        add(copyBtn);
        addSeparator(new Dimension(3,0));

        pasteBtn = ButtonFactory.createPasteButton();
        add(pasteBtn);
        addSeparator(new Dimension(3,0));

        previewBtn = ButtonFactory.createPreviewButton();
        add(previewBtn);
        addSeparator(new Dimension(3,0));

        saveBtn = ButtonFactory.createSaveButton();
        add(saveBtn);

        setFloatable(false);
    }
}
