package application.write.ui;
import application.factory.ButtonFactory;
import application.factory.TextAndPopupFactory;
import application.shared.domain.CheckChars;
import application.shared.ui.PopClickListener;
import application.world.ui.TabPanel;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WritePanel extends JPanel {

    private WritePanelLeftComponent writePanelLeftComponent;
    private JPanel plainPanel;
    private JSplitPane splitPane;
    private List<File> listOfFiles = new ArrayList<>();
    private List<File> listOfChapters = new ArrayList<>();
    private List<String> listOfGroups = new ArrayList<>();
    private List<ButtonBar> listOfButtonBars = new ArrayList<>();
    private JPanel buttonPanel;
    private JButton addNewChapterBtn;
    private JButton addNewGroupBtn;
    private JButton addNewTabBtn;
    private JButton renameTabBtn;
    private int index = 0;
    private String tabName;
    private final String usersDir = System.getProperty("user.home");

    public WritePanel(int tabID, String tabNameText, WriteMenu writeMenu) {
        this.tabName = tabNameText;
        setLayout(new MigLayout("insets 0", "grow", "grow"));
        setBackground(Color.MAGENTA);

        writePanelLeftComponent = new WritePanelLeftComponent();
        plainPanel = new JPanel();
        splitPane = new JSplitPane();

        Border featuresPanelBorder = BorderFactory.createLineBorder(Color.MAGENTA);
        setBorder(BorderFactory.createCompoundBorder(featuresPanelBorder,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        setSplitPaneDividerColor(splitPane, Color.MAGENTA);
        add(splitPane, "grow");
        JScrollPane l = new JScrollPane(writePanelLeftComponent);
        l.setMinimumSize(new Dimension(525, 0));
        splitPane.setLeftComponent(l);
        splitPane.setRightComponent(plainPanel);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new MigLayout("wrap 4", "grow"));

        addNewChapterBtn = new JButton("Add New Chapter");
        addNewChapterBtn.setToolTipText("Creates a new part");

        addNewGroupBtn = new JButton("Add New Group");
        addNewGroupBtn.setToolTipText("Creates a new group of parts");

        addNewTabBtn = ButtonFactory.createAddTabButton();
        renameTabBtn = ButtonFactory.createRenameTabButton();

        buttonPanel.add(addNewChapterBtn);
        buttonPanel.add(addNewGroupBtn);
        buttonPanel.add(addNewTabBtn);
        buttonPanel.add(renameTabBtn);
        writePanelLeftComponent.add(buttonPanel);

        File directory = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabID + "_" + tabName);
        listOfFiles.addAll(List.of(directory.listFiles()));
        for (File file : directory.listFiles()) {
            if (file.getName().contains(".md")) {
                listOfChapters.add(file);
            }
            else {
                int underscoreIndex = file.getName().indexOf("_");
                String groupName = file.getName().substring(underscoreIndex+1);
                listOfGroups.add(file.getName());

                ButtonBar buttonBar = new ButtonBar();
                buttonBar.setText(groupName);
                buttonBar.setId(Integer.parseInt(file.getName().substring(0, underscoreIndex)));
                listOfButtonBars.add(buttonBar);
            }
        }
        Collections.sort(listOfFiles, new FileIdComparator());
        Collections.sort(listOfButtonBars, new ButtonIdComparator());
        Collections.sort(listOfGroups, new GroupIdComparator());

        for (int i = 0; i < listOfFiles.size(); i++) {
            File file = listOfFiles.get(i);
            int underscoreIndex = file.getName().indexOf("_");
            splitPane.setRightComponent(plainPanel);

            if (file.getName().contains(".md")) {
                String filename = file.getName().substring(underscoreIndex+1, file.getName().length()-3);
                Button button = new Button();
                button.setId(Integer.parseInt(file.getName().substring(0, underscoreIndex)));
                button.setText(filename);
                button.setExists(true);

                writePanelLeftComponent.add(button);
                writePanelLeftComponent.getListOfButtons().add(button);

                int fileIndex = i;

                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        TextEditor textEditor = new TextEditor();
                        button.setTextEditor(textEditor);
                        splitPane.setRightComponent(textEditor);

                        try {
                            for (File f : listOfChapters) {
                                if (f.getName().startsWith(button.getId() + "_")) {
                                    displayChapter(textEditor, f);
                                    break;
                                }
                            }

                            textEditor.getToolbar().getSaveBtn().addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    saveChapter(textEditor, button, WritePanel.this.tabName, tabID);
                                }
                            });
                        } catch (FileNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
                WritePopupMenu writePopupMenu = new WritePopupMenu();
                if (listOfButtonBars.size() == 0) {
                    writePopupMenu.getAddToGroupItem().setVisible(false);
                }
                writePopupMenu.remove(writePopupMenu.getRemoveFromGroupItem());
                writePopupMenu.remove(writePopupMenu.getAddNewPartItem());
                writePopupMenu.remove(writePopupMenu.getRenameMenuItem());
                button.setPopupMenu(writePopupMenu);

                for (String name : listOfGroups) {
                    int underscore = name.indexOf("_");
                    String groupName = name.substring(underscore+1);
                    JMenuItem groupNameItem = new JMenuItem(groupName);
                    writePopupMenu.getAddToGroupItem().add(groupNameItem);
                    groupNameItem.setAction(new AbstractAction(groupNameItem.getText()) {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            for (File f : listOfFiles) {
                                if (f.getName().startsWith(button.getId() + "_")) {
                                    addToGroup(button, name, button.getText(), f, groupName, tabName, tabID);
                                    break;
                                }
                            }
                        }
                    });
                }

                writePopupMenu.getRemoveMenuItem().setAction(new AbstractAction("Remove", writePopupMenu.getRemoveMenuItem().getIcon()) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Object[] options = {"Continue",
                                "Cancel"};
                        int confirmation = JOptionPane.showOptionDialog(null,
                                "Are you sure you want to remove this chapter? \n"
                                        + "This action cannot be undone.",
                                "Warning",
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.WARNING_MESSAGE,
                                null,
                                options,
                                options[0]);

                        if (confirmation == 0) {
                            deleteChapter(listOfChapters, button);
                        }
                    }
                });
                button.addMouseListener(new PopClickListener(writePopupMenu));
                revalidate();
                repaint();
            }
            else {
                ButtonBar buttonBar = null;
                for (String s : listOfGroups) {
                    int index = s.indexOf("_");
                    int id = Integer.parseInt(s.substring(0, index));
                    int fileIndex = Integer.parseInt(file.getName().substring(0, underscoreIndex));
                    if (id==fileIndex) {
                        buttonBar = listOfButtonBars.get(listOfGroups.indexOf(s));
                    }
                }

                writePanelLeftComponent.add(buttonBar);
                writePanelLeftComponent.getListOfButtons().add(buttonBar);

                File groupDirectory = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabID + "_" + tabName + "\\" + buttonBar.getId() + "_" + buttonBar.getText());
                List<File> groupFiles = new ArrayList<>();
                groupFiles.addAll(List.of(groupDirectory.listFiles()));
                Collections.sort(groupFiles, new FileIdComparator());
                buttonBar.getFiles().addAll(groupFiles);

                for (int number = 0; number < groupFiles.size(); number++) {
                    File f = groupFiles.get(number);
                    int fileUnderscoreIndex = f.getName().indexOf("_");
                    String name = f.getName().substring(fileUnderscoreIndex + 1, f.getName().length() - 3);
                    Button button = new Button();
                    button.setId(Integer.parseInt(f.getName().substring(0, fileUnderscoreIndex)));
                    button.setText(name);
                    button.setExists(true);
                    buttonBar.getListOfButtons().add(button);
                    buttonBar.getListOfIDs().add(button.getId());

                    int fileIndex = i;
                    ButtonBar b = buttonBar;

                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            TextEditor textEditor = new TextEditor();
                            button.setTextEditor(textEditor);
                            splitPane.setRightComponent(textEditor);

                            try {
                                for (File g : b.getFiles()) {
                                    if (g.getName().startsWith(button.getId() + "_")) {
                                        displayChapter(textEditor, g);
                                        break;
                                    }
                                }

                                textEditor.getToolbar().getSaveBtn().addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        saveGroupPart(textEditor, b, button, WritePanel.this.tabName, tabID);
                                    }
                                });
                            } catch (FileNotFoundException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    });
                    WritePopupMenu writePopupMenu = new WritePopupMenu();
                    writePopupMenu.remove(writePopupMenu.getAddNewPartItem());
                    writePopupMenu.remove(writePopupMenu.getAddToGroupItem());
                    writePopupMenu.remove(writePopupMenu.getRenameMenuItem());

                    writePopupMenu.getRemoveFromGroupItem().setAction(new AbstractAction("Remove from group", writePopupMenu.getRemoveFromGroupItem().getIcon()) {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            File updatedFile = b.getFiles().get(b.getListOfIDs().indexOf(button.getId()));
                            String updatedName = updatedFile.getName().substring(fileUnderscoreIndex + 1, updatedFile.getName().length() - 3);
                            removeFromGroup(button, b, updatedFile, updatedName, tabName, tabID);
                        }
                    });

                    writePopupMenu.getRemoveMenuItem().setAction(new AbstractAction("Remove", writePopupMenu.getRemoveMenuItem().getIcon()) {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Object[] options = {"Continue",
                                    "Cancel"};
                            int confirmation = JOptionPane.showOptionDialog(null,
                                    "Are you sure you want to remove this part? \n"
                                            + "This action cannot be undone.",
                                    "Warning",
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                    null,
                                    options,
                                    options[0]);

                            if (confirmation == 0) {
                                deleteChapter(b.getFiles(), button);
                                b.getListOfIDs().remove(b.getListOfButtons().indexOf(button));
                                b.getListOfButtons().remove(button);
                            }
                        }
                    });

                    button.addMouseListener(new PopClickListener(writePopupMenu));
                    revalidate();
                    repaint();
                }

                int j = i;
                ButtonBar c = buttonBar;

                buttonBar.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        displayGroupParts(c);
                    }
                });

                WritePopupMenu writePopupMenu = new WritePopupMenu();
                writePopupMenu.remove(writePopupMenu.getAddToGroupItem());
                writePopupMenu.remove(writePopupMenu.getRemoveFromGroupItem());

                writePopupMenu.getAddNewPartItem().setAction(new AbstractAction("Add new part", writePopupMenu.getAddNewPartItem().getIcon()) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addNewPart(c, tabName, tabID);
                    }
                });

                writePopupMenu.getRemoveMenuItem().setAction(new AbstractAction("Remove", writePopupMenu.getRemoveMenuItem().getIcon()) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Object[] options = {"Continue",
                                "Cancel"};
                        int confirmation = JOptionPane.showOptionDialog(null,
                                "Are you sure you want to remove this group? \n"
                                        + "This action cannot be undone.",
                                "Warning",
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.WARNING_MESSAGE,
                                null,
                                options,
                                options[0]);

                        if (confirmation == 0) {
                            deleteGroup(c, tabName, tabID);
                        }
                    }
                });

                writePopupMenu.getRenameMenuItem().setAction(new AbstractAction(writePopupMenu.getRenameMenuItem().getText(), writePopupMenu.getRenameMenuItem().getIcon() ) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        renameGroup(c, tabName, tabID);
                    }
                });

                buttonBar.addMouseListener(new PopClickListener(writePopupMenu));
                revalidate();
                repaint();
            }
        }
        getCurrentIndex();

        addNewChapterBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextEditor textEditor = new TextEditor();
                splitPane.setRightComponent(textEditor);

                Button button = new Button();
                button.setId(index);

                textEditor.getToolbar().getSaveBtn().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (button.isExists()) {
                            saveChapter(textEditor, button, tabName, tabID);
                        }
                        else {
                            if (textEditor.getTitlePane().getText() != null) {
                                if (!textEditor.getTitlePane().getText().equals("")) {
                                    boolean illegal = CheckChars.checkChars(textEditor.getTitlePane().getText());
                                    int spaceCount = CheckChars.checkSpaceChars(textEditor.getTitlePane().getText());

                                    if (illegal) {
                                        JOptionPane.showMessageDialog(null,
                                                "Sorry! A file name can't contain any of the following characters: \n \\ / : * ? \" < > |",
                                                "Name error",
                                                JOptionPane.ERROR_MESSAGE);
                                    } else if (spaceCount == textEditor.getTitlePane().getText().length()) {
                                        JOptionPane.showMessageDialog(null,
                                                "Sorry! A blank space is not a title!",
                                                "Name error",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                    else {
                                        File directory = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabID + "_" + tabName);
                                        File newFile = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabID + "_" + tabName + "\\" + index + "_" + textEditor.getTitlePane().getText() + ".md");

                                        FilenameFilter filenameFilter = new FilenameFilter() {
                                            @Override
                                            public boolean accept(File dir, String name) {
                                                return name.startsWith(index + "_");
                                            }
                                        };

                                        File[] fileName = directory.listFiles(filenameFilter);
                                        if (fileName != null && fileName.length != 0) {
                                            fileName[0].renameTo(newFile);
                                        } else {
                                            try {
                                                newFile.createNewFile();
                                                FileWriter fileWriter = new FileWriter(newFile);
                                                fileWriter.write(textEditor.htmlToMarkdown(textEditor.getPane().getText()));
                                                fileWriter.close();
                                            } catch (IOException ex) {
                                                throw new RuntimeException(ex);
                                            }
                                        }
                                        listOfChapters.add(newFile);
                                        listOfFiles.add(newFile);
                                        button.setText(textEditor.getTitlePane().getText());
                                        button.setExists(true);

                                        writePanelLeftComponent.add(button);
                                        writePanelLeftComponent.getListOfButtons().add(button);
                                        writePanelLeftComponent.revalidate();
                                        writePanelLeftComponent.repaint();

                                        button.addActionListener(new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                splitPane.setRightComponent(textEditor);

                                                try {
                                                    for (File file : listOfChapters) {
                                                        if (file.getName().startsWith(button.getId() + "_")) {
                                                            displayChapter(textEditor, file);
                                                            break;
                                                        }
                                                    }
                                                } catch (FileNotFoundException ex) {
                                                    throw new RuntimeException(ex);
                                                }
                                            }
                                        });
                                        WritePopupMenu writePopupMenu = new WritePopupMenu();
                                        if (listOfButtonBars.size() == 0) {
                                            writePopupMenu.getAddToGroupItem().setVisible(false);
//                                            writePopupMenu.remove(writePopupMenu.getAddToGroupItem());
                                        }
                                        writePopupMenu.remove(writePopupMenu.getRemoveFromGroupItem());
                                        writePopupMenu.remove(writePopupMenu.getAddNewPartItem());
                                        writePopupMenu.remove(writePopupMenu.getRenameMenuItem());
                                        button.setPopupMenu(writePopupMenu);
                                        writePopupMenu.getRemoveMenuItem().setAction(new AbstractAction("Remove", writePopupMenu.getRemoveMenuItem().getIcon()) {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                Object[] options = {"Continue",
                                                        "Cancel"};
                                                int confirmation = JOptionPane.showOptionDialog(null,
                                                        "Are you sure you want to remove this chapter? \n"
                                                                + "This action cannot be undone.",
                                                        "Warning",
                                                        JOptionPane.YES_NO_CANCEL_OPTION,
                                                        JOptionPane.WARNING_MESSAGE,
                                                        null,
                                                        options,
                                                        options[0]);

                                                if (confirmation == 0) {
                                                    deleteChapter(listOfChapters, button);
                                                }
                                            }
                                        });

                                        for (String name : listOfGroups) {
                                            int underscore = name.indexOf("_");
                                            String groupName = name.substring(underscore + 1);
                                            JMenuItem groupNameItem = new JMenuItem(groupName);
                                            writePopupMenu.getAddToGroupItem().add(groupNameItem);
                                            groupNameItem.setAction(new AbstractAction(groupNameItem.getText()) {
                                                @Override
                                                public void actionPerformed(ActionEvent e) {
                                                    addToGroup(button, name, button.getText(), newFile, groupName, tabName, tabID);
                                                }
                                            });
                                        }

                                        button.addMouseListener(new PopClickListener(writePopupMenu));
                                        revalidate();
                                        repaint();
                                        index = index + 1;
                                    }
                                }
                                else {
                                    JOptionPane.showMessageDialog(null,
                                            "Sorry! It looks like you didn't enter a title!",
                                            "Name error",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            else {
                                JOptionPane.showMessageDialog(null,
                                        "Sorry! It looks like you didn't enter a title!",
                                        "Name error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                });
            }
        });

        addNewGroupBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                splitPane.setRightComponent(plainPanel);
                String groupName = TextAndPopupFactory.createPopupWindow("What would you like to name this group?");
                if (groupName != null) {
                    if (!groupName.equals("")) {
                        boolean illegal = CheckChars.checkChars(groupName);
                        int spaceCount = CheckChars.checkSpaceChars(groupName);

                        if (illegal) {
                            JOptionPane.showMessageDialog(null,
                                    "Sorry! A file name can't contain any of the following characters: \n \\ / : * ? \" < > |",
                                    "Name error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        else if (spaceCount == groupName.length()) {
                            JOptionPane.showMessageDialog(null,
                                    "Sorry! A blank space is not a title!",
                                    "Name error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            ButtonBar buttonBar = new ButtonBar();
                            buttonBar.setId(index);
                            buttonBar.setDisplayParts(true);

                            buttonBar.setText(groupName);
                            writePanelLeftComponent.add(buttonBar);
                            writePanelLeftComponent.getListOfButtons().add(buttonBar);

                            File groupDirectory = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabID + "_" + tabName + "\\" + buttonBar.getId() + "_" + buttonBar.getText());
                            groupDirectory.mkdir();
                            listOfGroups.add(index + "_" + groupName);
                            listOfButtonBars.add(buttonBar);
                            writePanelLeftComponent.revalidate();
                            writePanelLeftComponent.repaint();

                            index = index + 1;

                            listOfFiles.add(groupDirectory);

                            for (Button button : writePanelLeftComponent.getListOfButtons()) {
                                if (button.getPopupMenu() != null) {
                                    JMenuItem groupNameItem = new JMenuItem(groupName);
                                    if (!button.getPopupMenu().getAddToGroupItem().isVisible()) {
                                        button.getPopupMenu().getAddToGroupItem().setVisible(true);
                                    }
                                    button.getPopupMenu().getAddToGroupItem().add(groupNameItem);
                                    groupNameItem.setAction(new AbstractAction(groupNameItem.getText()) {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            File file = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabID + "_" + tabName + "\\" + button.getId() + "_" + button.getText() + ".md");
                                            addToGroup(button, (buttonBar.getId() + "_" + groupName), button.getText(), file, groupName, tabName, tabID);
                                        }
                                    });
                                }
                            }

                            WritePopupMenu writePopupMenu = new WritePopupMenu();
                            writePopupMenu.remove(writePopupMenu.getAddToGroupItem());
                            writePopupMenu.remove(writePopupMenu.getRemoveFromGroupItem());
                            writePopupMenu.getRemoveMenuItem().setAction(new AbstractAction("Remove", writePopupMenu.getRemoveMenuItem().getIcon()) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    Object[] options = {"Continue",
                                            "Cancel"};
                                    int confirmation = JOptionPane.showOptionDialog(null,
                                            "Are you sure you want to remove this group? \n"
                                                    + "This action cannot be undone.",
                                            "Warning",
                                            JOptionPane.YES_NO_CANCEL_OPTION,
                                            JOptionPane.WARNING_MESSAGE,
                                            null,
                                            options,
                                            options[0]);

                                    if (confirmation == 0) {
                                        deleteGroup(buttonBar, tabName, tabID);
                                    }
                                }
                            });

                            writePopupMenu.getAddNewPartItem().setAction(new AbstractAction("Add new part", writePopupMenu.getAddNewPartItem().getIcon()) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    addNewPart(buttonBar, tabName, tabID);
                                }
                            });

                            writePopupMenu.getRenameMenuItem().setAction(new AbstractAction(writePopupMenu.getRenameMenuItem().getText(), writePopupMenu.getRenameMenuItem().getIcon()) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    renameGroup(buttonBar, tabName, tabID);
                                }
                            });

                            buttonBar.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    displayGroupParts(buttonBar);
                                }
                            });

                            buttonBar.addMouseListener(new PopClickListener(writePopupMenu));
                            revalidate();
                            repaint();
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(null,
                                "Sorry! It looks like you didn't enter a title!",
                                "Name error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null,
                            "Sorry! It looks like you didn't enter a title!",
                            "Name error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        addNewTabBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newTabName = TextAndPopupFactory.createPopupWindow("What would you like to title this tab?");
                if (newTabName != null) {
                    if (!newTabName.equals("")) {
                        boolean illegal = CheckChars.checkChars(newTabName);
                        int spaceCount = CheckChars.checkSpaceChars(newTabName);
                        boolean preexists = CheckChars.checkPrexists(writeMenu.getListOfTabNames(), newTabName);

                        if (illegal) {
                            JOptionPane.showMessageDialog(null,
                                    "Sorry! A file name can't contain any of the following characters: \n \\ / : * ? \" < > |",
                                    "Name error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        else if (spaceCount == newTabName.length()) {
                            JOptionPane.showMessageDialog(null,
                                    "Sorry! A blank space is not a title!",
                                    "Name error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        else if (preexists) {
                            JOptionPane.showMessageDialog(null,
                                    "Sorry! It looks like you already have a tab with this title!",
                                    "Name error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            File newFolder = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + (writeMenu.getCurrentTabID()+1) + "_" + newTabName);
                            newFolder.mkdir();

                            writeMenu.getListOfTabNames().add(newTabName);
                            WritePanel newWritePanel = new WritePanel(writeMenu.getCurrentTabID()+1, newTabName, writeMenu);
                            TabPanel tabPanel = new TabPanel(newTabName);

                            writeMenu.getTabbedPane().add(newTabName, newWritePanel);
                            writeMenu.getTabbedPane().setTabComponentAt(writeMenu.getTabbedPane().getTabCount()-1, tabPanel);
                            writeMenu.getListOfIDs().add(writeMenu.getCurrentTabID()+1);

                            tabPanel.getCloseTabButton().addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    writeMenu.closeTabButton(newWritePanel);
                                }
                            });
                            writeMenu.disableCloseTabButton(writeMenu.getTabbedPane());
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(null,
                                "Sorry! It looks like you didn't enter a title!",
                                "Name error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null,
                            "Sorry! It looks like you didn't enter a title!",
                            "Name error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        renameTabBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                writeMenu.renameTabButton(WritePanel.this);
            }
        });
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public List<File> getListOfFiles() {
        return listOfFiles;
    }

    public void setListOfFiles(List<File> listOfFiles) {
        this.listOfFiles = listOfFiles;
    }

    public List<File> getListOfChapters() {
        return listOfChapters;
    }

    public void setListOfChapters(List<File> listOfChapters) {
        this.listOfChapters = listOfChapters;
    }

    public List<ButtonBar> getListOfButtonBars() {
        return listOfButtonBars;
    }

    public void setListOfButtonBars(List<ButtonBar> listOfButtonBars) {
        this.listOfButtonBars = listOfButtonBars;
    }

    private void setSplitPaneDividerColor(JSplitPane splitPane, Color newDividerColor) {
        SplitPaneUI splitUI = splitPane.getUI();
        if (splitUI instanceof BasicSplitPaneUI) { // obviously this will not work if the ui doen't extend Basic...
            int divSize = splitPane.getDividerSize();
            BasicSplitPaneDivider div = ((BasicSplitPaneUI) splitUI).getDivider();
            assert div != null;
            Border divBorder = div.getBorder();
            Border newBorder = null;
            Border colorBorder = null;

            int insetsh = 0;
            int insetsv = 0;

            if (divBorder != null) {
                Insets i = divBorder.getBorderInsets(div);
                insetsh = i.left + i.right;
                insetsv = i.top + i.bottom;
            }

            // this border uses a fillRect
            colorBorder = BorderFactory.createMatteBorder(divSize - insetsv, divSize - insetsh, 0, 0, newDividerColor);

            if (divBorder == null) {
                newBorder = colorBorder;
            } else {
                newBorder = BorderFactory.createCompoundBorder(divBorder, colorBorder);
            }
            div.setBorder(newBorder);
        }
    }

    public void overwriteChapter(int listIndex, int index, File updatedFile, Button button, TextEditor textEditor, String tabName, int tabID) {
        File newFile = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabID + "_" + tabName + "\\" + index + "_" + textEditor.getTitlePane().getText() + ".md");
        int fileIndex = listOfFiles.indexOf(updatedFile);

        if (!updatedFile.getName().equals(newFile)) {
            updatedFile.renameTo(newFile);
            listOfChapters.set(listIndex, newFile);
            listOfFiles.set(fileIndex, newFile);
            button.setText(textEditor.getTitlePane().getText());
        }
        try {
            FileWriter fileWriter = new FileWriter(newFile);
            fileWriter.write(textEditor.htmlToMarkdown(textEditor.getPane().getText()));
            fileWriter.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void overwriteGroupChapter(int listIndex, File updatedFile, Button button, ButtonBar buttonBar, TextEditor textEditor, String tabName, int tabID) {
        File newFile = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabID + "_" + tabName + "\\" + buttonBar.getId() + "_" + buttonBar.getText() + "\\" + button.getId() + "_" + textEditor.getTitlePane().getText() + ".md");

        if (!updatedFile.getName().equals(newFile)) {
            updatedFile.renameTo(newFile);
            buttonBar.getFiles().set(listIndex, newFile);
            button.setText(textEditor.getTitlePane().getText());
        }
        try {
            FileWriter fileWriter = new FileWriter(newFile);
            fileWriter.write(textEditor.htmlToMarkdown(textEditor.getPane().getText()));
            fileWriter.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void displayChapter(TextEditor textEditor, File updatedFile) throws FileNotFoundException {
        int updatedUnderscoreIndex = updatedFile.getName().indexOf("_");
        String updatedName = updatedFile.getName().substring(updatedUnderscoreIndex+1, updatedFile.getName().length()-3);
        String text = textEditor.markdownToHtml(textEditor.getTextFromFile(updatedFile));
        textEditor.getPane().setText(text);
        textEditor.getTitlePane().setText(updatedName);

        if (textEditor.getHistories().size() > 1) {
            textEditor.getHistories().remove(0);
            textEditor.getToolbar().getUndoBtn().setEnabled(false);
            textEditor.setIndex(0);
        }
    }

    public int getCurrentIndex() {
        if (listOfFiles.size() != 0) {
            File lastFile = listOfFiles.get(listOfFiles.size()-1);
            int underscoreIndex = lastFile.getName().indexOf("_");
            int currentIndex = Integer.parseInt(lastFile.getName().substring(0, underscoreIndex));
            index = currentIndex+1;
        }
        return index;
    }

    public void deleteChapter(List<File> files, Button button) {
        for (File file : files) {
            if (file.getName().startsWith(button.getId() + "_")) {
                file.delete();
                files.remove(file);
                listOfFiles.remove(file);
                writePanelLeftComponent.getListOfButtons().remove(button);
                splitPane.setRightComponent(plainPanel);
                writePanelLeftComponent.remove(button);
                revalidate();
                repaint();
                break;
            }
        }
        getCurrentIndex();
    }

    public void deleteGroup(ButtonBar buttonBar, String tabName, int tabID) {
        File directory = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabID + "_" + tabName + "\\" + buttonBar.getId() + "_" + buttonBar.getText());
        for (File file : buttonBar.getFiles()) {
            file.delete();
            splitPane.setRightComponent(plainPanel);
        }
        directory.delete();

        for (Button b : buttonBar.getListOfButtons()) {
            writePanelLeftComponent.remove(b);
        }
        writePanelLeftComponent.remove(buttonBar);

        buttonBar.getListOfButtons().clear();
        buttonBar.getFiles().clear();
        buttonBar.getListOfIDs().clear();

        listOfFiles.remove(directory);
        writePanelLeftComponent.getListOfButtons().remove(buttonBar);
        listOfGroups.remove(buttonBar.getId() + "_" + buttonBar.getText());

        for (Button button : writePanelLeftComponent.getListOfButtons()) {
            if (button.getPopupMenu() != null) {
//                if (listOfGroups.size() != 0) {
                    JMenuItem groupNameItem = button.getPopupMenu().getAddToGroupItem().getItem(listOfButtonBars.indexOf(buttonBar));
                    button.getPopupMenu().getAddToGroupItem().remove(groupNameItem);
//                }
                if (listOfGroups.size() == 0) {
                    button.getPopupMenu().getAddToGroupItem().setVisible(false);
                }
            }
        }

//        listOfFiles.remove(directory);
        listOfButtonBars.remove(buttonBar);
//        writePanelLeftComponent.getListOfButtons().remove(buttonBar);
//        listOfGroups.remove(buttonBar.getId() + "_" + buttonBar.getText());

        revalidate();
        repaint();
        getCurrentIndex();
    }

    static class FileIdComparator implements java.util.Comparator<File> {
        @Override
        public int compare(File a, File b) {
            int underscoreA = a.getName().indexOf("_");
            int indexA = Integer.parseInt(a.getName().substring(0, underscoreA));

            int underscoreB = b.getName().indexOf("_");
            int indexB = Integer.parseInt(b.getName().substring(0, underscoreB));
            return indexA - indexB;
        }
    }

    static class ButtonIdComparator implements java.util.Comparator<Button> {
        @Override
        public int compare(Button a, Button b) {
            return a.getId() - b.getId();
        }
    }

    static class GroupIdComparator implements java.util.Comparator<String> {
        @Override
        public int compare(String a, String b) {
            int underscoreA = a.indexOf("_");
            int indexA = Integer.parseInt(a.substring(0, underscoreA));

            int underscoreB = b.indexOf("_");
            int indexB = Integer.parseInt(b.substring(0, underscoreB));
            return indexA - indexB;
        }
    }

    public void displayGroupParts(ButtonBar buttonBar) {
        if (buttonBar.isDisplayParts()) {
            for (Button button : buttonBar.getListOfButtons()) {
                writePanelLeftComponent.remove(button);
            }
            buttonBar.setDisplayParts(false);
        }
        else {
            removeAllButtons(buttonBar);
            for (Button button : buttonBar.getListOfButtons()) {
                writePanelLeftComponent.add(button, "center");
            }
            addAllButtonsBack(buttonBar);
            buttonBar.setDisplayParts(true);
        }
        writePanelLeftComponent.revalidate();
        writePanelLeftComponent.repaint();
    }

    public void removeAllButtons(ButtonBar buttonBar) {
        for (Button button : writePanelLeftComponent.getListOfButtons()) {
            if (button.getId() > buttonBar.getId()) {
                if (button instanceof ButtonBar) {
                    for (Button b : ((ButtonBar) button).getListOfButtons()) {
                        writePanelLeftComponent.remove(b);
                    }
                    writePanelLeftComponent.remove(button);
                }
                else {
                    writePanelLeftComponent.remove(button);
                }
            }
        }
    }

    public void addAllButtonsBack(ButtonBar buttonBar) {
        for (Button button : writePanelLeftComponent.getListOfButtons()) {
            if (button.getId() > buttonBar.getId()) {
                if (button instanceof ButtonBar) {
                    writePanelLeftComponent.add(button);
                    if (((ButtonBar) button).isDisplayParts()) {
                        for (Button b : ((ButtonBar) button).getListOfButtons()) {
                            writePanelLeftComponent.add(b, "center");
                        }
                    }
                }
                else {
                    writePanelLeftComponent.add(button);
                }
            }
        }
    }

    public void addNewPart(ButtonBar buttonBar, String tabName, int tabID) {
        TextEditor textEditor = new TextEditor();
        splitPane.setRightComponent(textEditor);

        Button button = new Button();
        int currentIndex;
        if (buttonBar.getListOfIDs().size() != 0) {
            currentIndex = buttonBar.getCurrentIndex()+1;
        }
        else {
            currentIndex = 0;
        }
        button.setId(currentIndex);

        textEditor.getToolbar().getSaveBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (button.isExists()) {
//                    saveGroupPart(textEditor, buttonBar, button, tabName, tabID);
                    saveGroupPart(textEditor, buttonBar, button, WritePanel.this.tabName, tabID);
                }
                else {
                    if (textEditor.getTitlePane().getText() != null) {
                        if (!textEditor.getTitlePane().getText().equals("")) {
                            boolean illegal = CheckChars.checkChars(textEditor.getTitlePane().getText());
                            int spaceCount = CheckChars.checkSpaceChars(textEditor.getTitlePane().getText());

                            if (illegal) {
                                JOptionPane.showMessageDialog(null,
                                        "Sorry! A file name can't contain any of the following characters: \n \\ / : * ? \" < > |",
                                        "Name error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                            else if (spaceCount == textEditor.getTitlePane().getText().length()) {
                                JOptionPane.showMessageDialog(null,
                                        "Sorry! A blank space is not a title!",
                                        "Name error",
                                        JOptionPane.ERROR_MESSAGE);
                            }

                            else {
                                File directory = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabID + "_" + WritePanel.this.tabName + "\\" + buttonBar.getId() + "_" + buttonBar.getText());
                                File newFile = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabID + "_" + WritePanel.this.tabName + "\\" + buttonBar.getId() + "_" + buttonBar.getText() + "\\" + currentIndex + "_" + textEditor.getTitlePane().getText() + ".md");

                                FilenameFilter filenameFilter = new FilenameFilter() {
                                    @Override
                                    public boolean accept(File dir, String name) {
                                        return name.startsWith(String.valueOf(currentIndex + "_"));
                                    }
                                };

                                File[] fileName = directory.listFiles(filenameFilter);
                                if (fileName != null && fileName.length != 0) {
                                    fileName[0].renameTo(newFile);
                                } else {
                                    try {
                                        newFile.createNewFile();
                                        FileWriter fileWriter = new FileWriter(newFile);
                                        fileWriter.write(textEditor.htmlToMarkdown(textEditor.getPane().getText()));
                                        fileWriter.close();
                                    } catch (IOException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }

                                if (buttonBar.getListOfButtons().size() != 0) {
                                    buttonBar.setDisplayParts(true);
                                }

                                buttonBar.getFiles().add(newFile);
                                button.setText(textEditor.getTitlePane().getText());
                                button.setExists(true);

                                buttonBar.getListOfButtons().add(button);
                                buttonBar.getListOfIDs().add(button.getId());

                                removeAllButtons(buttonBar);
                                writePanelLeftComponent.add(button, "center");
                                addAllButtonsBack(buttonBar);
                                buttonBar.setDisplayParts(false);
                                displayGroupParts(buttonBar);

                                writePanelLeftComponent.revalidate();
                                writePanelLeftComponent.repaint();

                                button.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        splitPane.setRightComponent(textEditor);

                                        try {
                                            for (File file : buttonBar.getFiles()) {
                                                if (file.getName().startsWith(button.getId() + "_")) {
                                                    displayChapter(textEditor, file);
                                                    break;
                                                }
                                            }
                                        } catch (FileNotFoundException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                });
                                WritePopupMenu writePopupMenu = new WritePopupMenu();
                                writePopupMenu.remove(writePopupMenu.getAddNewPartItem());
                                writePopupMenu.remove(writePopupMenu.getAddToGroupItem());
                                writePopupMenu.remove(writePopupMenu.getRenameMenuItem());
                                writePopupMenu.getRemoveFromGroupItem().setAction(new AbstractAction("Remove from group") {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
//                                        removeFromGroup(button, buttonBar, newFile, button.getText(), WritePanel.this.tabName, tabID);
                                        File file = new File("");
                                        for (File f : buttonBar.getFiles()) {
                                            if (f.getName().startsWith(button.getId() + "_")) {
                                                file = new File(f.getAbsolutePath());
                                            }
                                        }
                                        removeFromGroup(button, buttonBar, file, button.getText(), WritePanel.this.tabName, tabID);
                                    }
                                });

                                writePopupMenu.getRemoveMenuItem().setAction(new AbstractAction("Remove", writePopupMenu.getRemoveMenuItem().getIcon()) {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        Object[] options = {"Continue",
                                                "Cancel"};
                                        int confirmation = JOptionPane.showOptionDialog(null,
                                                "Are you sure you want to remove this part? \n"
                                                        + "This action cannot be undone.",
                                                "Warning",
                                                JOptionPane.YES_NO_CANCEL_OPTION,
                                                JOptionPane.WARNING_MESSAGE,
                                                null,
                                                options,
                                                options[0]);

                                        if (confirmation == 0) {
                                            deleteChapter(buttonBar.getFiles(), button);
                                            buttonBar.getListOfIDs().remove(buttonBar.getListOfButtons().indexOf(button));
                                            buttonBar.getListOfButtons().remove(button);
                                        }
                                    }
                                });

                                button.addMouseListener(new PopClickListener(writePopupMenu));
                                revalidate();
                                repaint();
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(null,
                                    "Sorry! It looks like you didn't enter a title!",
                                    "Name error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(null,
                                "Sorry! It looks like you didn't enter a title!",
                                "Name error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    public void addToGroup(Button button, String group, String filename, File oldFile, String groupName, String tabName, int tabID) {
        splitPane.setRightComponent(plainPanel);
        ButtonBar buttonBar = listOfButtonBars.get(listOfGroups.indexOf(group));
        Button newButton = new Button();
        TextEditor t = new TextEditor();

        int currentIndex;
        if (buttonBar.getListOfIDs().size() != 0) {
            currentIndex = buttonBar.getCurrentIndex()+1;
        }
        else {
            currentIndex = 0;
        }
        newButton.setId(currentIndex);

        newButton.setText(filename);
        newButton.setExists(true);
        buttonBar.getListOfButtons().add(newButton);
        buttonBar.getListOfIDs().add(newButton.getId());

        File copiedFile = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabID + "_" + tabName + "\\" + buttonBar.getId() + "_" + groupName + "\\" + (buttonBar.getCurrentIndex()) + "_" + filename + ".md");

        try {
            FileUtils.copyFile(oldFile, copiedFile);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        writePanelLeftComponent.remove(button);
        writePanelLeftComponent.getListOfButtons().remove(button);

        removeAllButtons(buttonBar);
        writePanelLeftComponent.add(newButton, "center");
        addAllButtonsBack(buttonBar);
        buttonBar.setDisplayParts(false);
        displayGroupParts(buttonBar);

        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                splitPane.setRightComponent(t);

                try {
                    for (File file : buttonBar.getFiles()) {
                        if (file.getName().startsWith(newButton.getId() + "_")) {
                            displayChapter(t, file);
                            break;
                        }
                    }
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        buttonBar.getFiles().add(copiedFile);
        listOfFiles.remove(oldFile);
        listOfChapters.remove(oldFile);
        oldFile.delete();

        t.getToolbar().getSaveBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (newButton.isExists()) {
                    saveGroupPart(t, buttonBar, newButton, WritePanel.this.tabName, tabID);
                }
            }
        });


        WritePopupMenu writePopupMenu = new WritePopupMenu();
        writePopupMenu.remove(writePopupMenu.getAddToGroupItem());
        writePopupMenu.remove(writePopupMenu.getAddNewPartItem());
        writePopupMenu.remove(writePopupMenu.getRenameMenuItem());
        writePopupMenu.getRemoveFromGroupItem().setAction(new AbstractAction("Remove from group") {
            @Override
            public void actionPerformed(ActionEvent e) {
                File updatedFile = buttonBar.getFiles().get(buttonBar.getListOfIDs().indexOf(newButton.getId()));
                String updatedName = newButton.getText();
                removeFromGroup(newButton, buttonBar, updatedFile, updatedName, WritePanel.this.tabName, tabID);
            }
        });
        writePopupMenu.getRemoveMenuItem().setAction(new AbstractAction("Remove", writePopupMenu.getRemoveMenuItem().getIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"Continue",
                        "Cancel"};
                int confirmation = JOptionPane.showOptionDialog(null,
                        "Are you sure you want to remove this part? \n"
                                + "This action cannot be undone.",
                        "Warning",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[0]);

                if (confirmation == 0) {
                    deleteChapter(buttonBar.getFiles(), newButton);
                    buttonBar.getListOfIDs().remove(buttonBar.getListOfButtons().indexOf(newButton));
                    buttonBar.getListOfButtons().remove(newButton);
                }
            }
        });

        newButton.addMouseListener(new PopClickListener(writePopupMenu));

        writePanelLeftComponent.revalidate();
        writePanelLeftComponent.repaint();
        getCurrentIndex();
    }

    public void removeFromGroup(Button oldButton, ButtonBar buttonBar, File oldFile, String chapterName, String tabName, int tabID) {
        Object[] options = {"Continue",
                "Cancel"};
        int confirmation = JOptionPane.showOptionDialog(null,
                "Are you sure you want to remove this part? \n"
                        + "This action cannot be undone.",
                "Warning",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]);

        if (confirmation == 0) {
            splitPane.setRightComponent(plainPanel);
            getCurrentIndex();
            TextEditor t = new TextEditor();

            Button newButton = new Button();
            newButton.setId(index);
            newButton.setText(oldButton.getText());
            newButton.setExists(true);

            File copiedFile = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabID + "_" + tabName + "\\" + index + "_" + chapterName + ".md");
            try {
                FileUtils.copyFile(oldFile, copiedFile);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            writePanelLeftComponent.remove(oldButton);
            writePanelLeftComponent.add(newButton);

            buttonBar.getListOfIDs().remove(buttonBar.getListOfButtons().indexOf(oldButton));
            buttonBar.getListOfButtons().remove(oldButton);
            buttonBar.getFiles().remove(oldFile);

            writePanelLeftComponent.getListOfButtons().add(newButton);
            listOfFiles.add(copiedFile);
            listOfChapters.add(copiedFile);

            oldFile.delete();

            newButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    splitPane.setRightComponent(t);

                    try {
                        for (File file : listOfChapters) {
                            if (file.getName().startsWith(newButton.getId() + "_")) {
                                displayChapter(t, file);
                                break;
                            }
                        }
                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            t.getToolbar().getSaveBtn().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (newButton.isExists()) {
                        saveChapter(t, newButton, WritePanel.this.tabName, tabID);
                    }
                }
            });

            WritePopupMenu writePopupMenu = new WritePopupMenu();
            writePopupMenu.remove(writePopupMenu.getAddNewPartItem());
            writePopupMenu.remove(writePopupMenu.getRemoveFromGroupItem());
            writePopupMenu.remove(writePopupMenu.getRenameMenuItem());
            newButton.setPopupMenu(writePopupMenu);

            writePopupMenu.getRemoveMenuItem().setAction(new AbstractAction("Remove", writePopupMenu.getRemoveMenuItem().getIcon()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object[] options = {"Continue",
                            "Cancel"};
                    int confirmation = JOptionPane.showOptionDialog(null,
                            "Are you sure you want to remove this chapter? \n"
                                    + "This action cannot be undone.",
                            "Warning",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            options,
                            options[0]);

                    if (confirmation == 0) {
                        deleteChapter(listOfChapters, newButton);
                    }
                }
            });

            for (String name : listOfGroups) {
                int underscore = name.indexOf("_");
                String groupName = name.substring(underscore + 1);
                JMenuItem groupNameItem = new JMenuItem(groupName);
                writePopupMenu.getAddToGroupItem().add(groupNameItem);
                groupNameItem.setAction(new AbstractAction(groupNameItem.getText()) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (File f : listOfFiles) {
                            if (f.getName().startsWith(newButton.getId() + "_")) {
                                addToGroup(newButton, name, newButton.getText(), f, groupName, WritePanel.this.tabName, tabID);
                                break;
                            }
                        }
                    }
                });
            }
            newButton.addMouseListener(new PopClickListener(writePopupMenu));
            getCurrentIndex();
        }
    }

    public void renameGroup(ButtonBar buttonBar, String tabName, int tabID) {
        String newName = TextAndPopupFactory.createPopupWindow("What would you like to rename " + buttonBar.getText() + " to?");

        if (newName != null) {
            if (!newName.equals("")) {
                boolean illegal = CheckChars.checkChars(newName);
                int spaceCount = CheckChars.checkSpaceChars(newName);

                if (illegal) {
                    JOptionPane.showMessageDialog(null,
                            "Sorry! A file name can't contain any of the following characters: \n \\ / : * ? \" < > |",
                            "Name error",
                            JOptionPane.ERROR_MESSAGE);
                }
                else if (spaceCount == newName.length()) {
                    JOptionPane.showMessageDialog(null,
                            "Sorry! A blank space is not a title!",
                            "Name error",
                            JOptionPane.ERROR_MESSAGE);
                }


                else if (newName.equalsIgnoreCase(buttonBar.getText())) {
                    listOfGroups.set(listOfButtonBars.indexOf(buttonBar), (buttonBar.getId() + "_" + newName));
                    File folder = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabID + "_" + tabName + "\\" + buttonBar.getId() + "_" + buttonBar.getText());
                    File renamedFolder = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabID + "_" + tabName + "\\" + buttonBar.getId() + "_" + newName);
                    folder.renameTo(renamedFolder);
                    buttonBar.setText(newName);
                }
                else {
                    listOfGroups.set(listOfButtonBars.indexOf(buttonBar), (buttonBar.getId() + "_" + newName));
                    List<File> newNameFiles = new ArrayList<>();

                    if (buttonBar.getFiles() != null && buttonBar.getFiles().size() != 0) {
                        for (File f : buttonBar.getFiles()) {
                            String buttonName = f.getName();
                            File copiedFile = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabID + "_" + tabName + "\\" + buttonBar.getId() + "_" + newName + "\\" + buttonName);
                            try {
                                FileUtils.copyFile(f, copiedFile);
                                newNameFiles.add(copiedFile);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        buttonBar.getFiles().clear();
                        buttonBar.getFiles().addAll(newNameFiles);
                        buttonBar.setText(newName);
                    }
                    else {
                        buttonBar.setText(newName);
                    }

                    File newFile = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabID + "_" + tabName + "\\" + buttonBar.getId() + "_" + newName);
                    for (File f : listOfFiles) {
                        if (f.getName().startsWith(buttonBar.getId() + "_")) {
                            listOfFiles.set(listOfFiles.indexOf(f), newFile);

                            for (File file : f.listFiles()) {
                                file.delete();
                            }
                            if (f.exists()) {
                                f.delete();
                            }
                        }
                    }

                    if (!newFile.exists()) {
                        newFile.mkdir();
                    }
                }

                for (Button button : writePanelLeftComponent.getListOfButtons()) {
                    if (button.getPopupMenu() != null) {
                        button.getPopupMenu().getAddToGroupItem().removeAll();
                    }
                }

                for (Button button : writePanelLeftComponent.getListOfButtons()) {
                    if (button.getPopupMenu() != null) {
                        for (String name : listOfGroups) {
                            int underscore = name.indexOf("_");
                            String groupName = name.substring(underscore+1);
                            JMenuItem groupNameItem = new JMenuItem(groupName);
                            button.getPopupMenu().getAddToGroupItem().add(groupNameItem);
                            groupNameItem.setAction(new AbstractAction(groupNameItem.getText()) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    for (File f : listOfFiles) {
                                        if (f.getName().startsWith(button.getId() + "_")) {
                                            addToGroup(button, name, button.getText(), f, groupName, WritePanel.this.tabName, tabID);
                                            break;
                                        }
                                    }
                                }
                            });
                        }
                    }
                }

            }
            else {
                JOptionPane.showMessageDialog(null,
                        "Sorry! It looks like you didn't enter a title!",
                        "Name error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            JOptionPane.showMessageDialog(null,
                    "Sorry! It looks like you didn't enter a title!",
                    "Name error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveChapter(TextEditor t, Button button, String tabName, int tabID) {
        if (t.getTitlePane().getText() != null) {
            if (!t.getTitlePane().getText().equals("")) {
                boolean illegal = CheckChars.checkChars(t.getTitlePane().getText());
                int spaceCount = CheckChars.checkSpaceChars(t.getTitlePane().getText());

                if (illegal) {
                    JOptionPane.showMessageDialog(null,
                            "Sorry! A file name can't contain any of the following characters: \n \\ / : * ? \" < > |",
                            "Name error",
                            JOptionPane.ERROR_MESSAGE);
                }
                else if (spaceCount == t.getTitlePane().getText().length()) {
                    JOptionPane.showMessageDialog(null,
                            "Sorry! A blank space is not a title!",
                            "Name error",
                            JOptionPane.ERROR_MESSAGE);
                }
                else {
                    for (File file : listOfChapters) {
                        if (file.getName().startsWith(button.getId() + "_")) {
                            int fileIndex = listOfChapters.indexOf(file);
                            overwriteChapter(fileIndex, button.getId(), file, button, t, tabName, tabID);
                            break;
                        }
                    }
                }
            }
            else {
                JOptionPane.showMessageDialog(null,
                        "Sorry! It looks like you didn't enter a title!",
                        "Name error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            JOptionPane.showMessageDialog(null,
                    "Sorry! It looks like you didn't enter a title!",
                    "Name error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveGroupPart(TextEditor t, ButtonBar buttonBar, Button button, String tabName, int tabID) {
        if (t.getTitlePane().getText() != null) {
            if (!t.getTitlePane().getText().equals("")) {
                boolean illegal = CheckChars.checkChars(t.getTitlePane().getText());
                int spaceCount = CheckChars.checkSpaceChars(t.getTitlePane().getText());

                if (illegal) {
                    JOptionPane.showMessageDialog(null,
                            "Sorry! A file name can't contain any of the following characters: \n \\ / : * ? \" < > |",
                            "Name error",
                            JOptionPane.ERROR_MESSAGE);
                }
                else if (spaceCount == t.getTitlePane().getText().length()) {
                    JOptionPane.showMessageDialog(null,
                            "Sorry! A blank space is not a title!",
                            "Name error",
                            JOptionPane.ERROR_MESSAGE);
                }

                else {
                    for (File file : buttonBar.getFiles()) {
                        if (file.getName().startsWith(button.getId() + "_")) {
                            int fileIndex = buttonBar.getFiles().indexOf(file);
                            overwriteGroupChapter(fileIndex, file, button, buttonBar, t, tabName, tabID);
                            break;
                        }
                    }
                }
            }
            else {
                JOptionPane.showMessageDialog(null,
                        "Sorry! It looks like you didn't enter a title!",
                        "Name error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            JOptionPane.showMessageDialog(null,
                    "Sorry! It looks like you didn't enter a title!",
                    "Name error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}