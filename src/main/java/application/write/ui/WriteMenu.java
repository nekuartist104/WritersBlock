package application.write.ui;

import application.factory.TextAndPopupFactory;
import application.shared.domain.CheckChars;
import application.world.ui.TabPanel;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WriteMenu extends JPanel {

    private JTabbedPane tabbedPane;
    private List<String> listOfTabNames = new ArrayList<>();
    private List<Integer> listOfIDs = new ArrayList<>();
    private List<Integer> tempList = new ArrayList<>();
    private final String usersDir = System.getProperty("user.home");

    public WriteMenu() {
        setLayout(new MigLayout("insets 0", "grow", "grow"));
        setBackground(Color.MAGENTA);
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.setBorder(BorderFactory.createEmptyBorder());
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setTabPlacement(JTabbedPane.TOP);

//        Border featuresPanelBorder = BorderFactory.createLineBorder(Color.MAGENTA);
//        setBorder(BorderFactory.createCompoundBorder(featuresPanelBorder,
//                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

//        writeLabel = new JLabel("Write", JLabel.CENTER);
//        writeLabel.setPreferredSize(new Dimension(250, 30));
//        writeLabel.setFont(new Font("Comic Sans", Font.BOLD, 20));

//        brainstormLabel = new JLabel("Brainstorm", JLabel.CENTER);
//        brainstormLabel.setPreferredSize(new Dimension(250, 30));
//        brainstormLabel.setFont(new Font("Comic Sans", Font.BOLD, 20));

//        tabbedPane.setBounds(50,50,200,200);

        File writersBlockDirectory = new File(usersDir + "\\WritersBlock");

        File characterDirectory = new File(usersDir + "\\WritersBlock\\Character");
        File characterExtraTypesDirectory = new File(usersDir + "\\WritersBlock\\Character\\CharacterExtraTypes");
        File characterExtraLeafIconsDirectory = new File(usersDir + "\\WritersBlock\\Character\\CharacterExtraLeafIcons");

        File locationDirectory = new File(usersDir + "\\WritersBlock\\Location");
        File locationExtraTypesDirectory = new File(usersDir + "\\WritersBlock\\Location\\LocationExtraTypes");
        File locationExtraLeafIconsDirectory = new File(usersDir + "\\WritersBlock\\Location\\LocationExtraLeafIcons");

        File writeDirectory = new File(usersDir + "\\WritersBlock\\Write");
        File writeExports = new File(usersDir + "\\WritersBlock\\Write\\WriteExports");
        File writeTab = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\0_Write");
        File brainstormTab = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\1_Brainstorm");

        //Create WritersBlock directory
        if (!writersBlockDirectory.exists()) {
            writersBlockDirectory.mkdir();
        }

        //Create all Character directories
        if (!characterDirectory.exists()) {
            characterDirectory.mkdir();
        }
        if (!characterExtraTypesDirectory.exists()) {
            characterExtraTypesDirectory.mkdir();
        }
        if (!characterExtraLeafIconsDirectory.exists()) {
            characterExtraLeafIconsDirectory.mkdir();
        }

        //Create all Location directories
        if (!locationDirectory.exists()) {
            locationDirectory.mkdir();
        }
        if (!locationExtraTypesDirectory.exists()) {
            locationExtraTypesDirectory.mkdir();
        }
        if (!locationExtraLeafIconsDirectory.exists()) {
            locationExtraLeafIconsDirectory.mkdir();
        }

        //Create all Write directories
        if (!writeDirectory.exists()) {
            writeDirectory.mkdir();
        }
        if (!writeExports.exists()) {
            writeExports.mkdir();
        }

        FileFilter fileWriteFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().startsWith(String.valueOf(0));
            }
        };
        FileFilter fileBrainstormFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().startsWith(String.valueOf(1));
            }
        };
        File[] directoryNoWrite = writeExports.listFiles(fileWriteFilter);
        File[] directoryNoBrainstorm = writeExports.listFiles(fileBrainstormFilter);
        if (directoryNoWrite != null && directoryNoWrite.length == 0) {
            writeTab.mkdir();
        }
        if (directoryNoBrainstorm != null && directoryNoBrainstorm.length == 0) {
            brainstormTab.mkdir();
        }

        for (File f : writeExports.listFiles()) {
            int underscoreIndex = f.getName().indexOf("_");
            String tabIndex = f.getName().substring(0, underscoreIndex);
            tempList.add(Integer.valueOf(tabIndex));
        }

        Collections.sort(tempList, new IdComparator());
        listOfIDs.addAll(tempList);

        for (int i : listOfIDs) {
            for (File f : writeExports.listFiles()) {
                if (f.getName().startsWith(i + "_")) {
                    int underscoreIndex = f.getName().indexOf("_");
                    String tabName = f.getName().substring(underscoreIndex+1);
                    listOfTabNames.add(tabName);

                    TabPanel tab = new TabPanel(tabName);
                    WritePanel newWritePanel = new WritePanel(i, tabName, WriteMenu.this);
                    tabbedPane.add(tabName, newWritePanel);
                    tabbedPane.setTabComponentAt(listOfTabNames.size()-1, tab);

                    tab.getCloseTabButton().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            closeTabButton(newWritePanel);
                        }
                    });
                    disableCloseTabButton(tabbedPane);
                }
            }
        }

//        tabbedPane.setBackground(Color.WHITE);

        add(tabbedPane,"grow");
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public List<String> getListOfTabNames() {
        return listOfTabNames;
    }

    public List<Integer> getListOfIDs() {
        return listOfIDs;
    }

    static class IdComparator implements java.util.Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return a - b;
        }
    }

    public int getCurrentTabID() {
        return listOfIDs.get(listOfIDs.size()-1);
    }

    public void disableCloseTabButton(JTabbedPane tabbedPane) {
        TabPanel tab = (TabPanel) tabbedPane.getTabComponentAt(0);
        if (tabbedPane.getTabCount() == 1) {
            tab.remove(tab.getCloseTabButton());
        }
        else {
            tab.add(tab.getCloseTabButton());
        }
    }

    public void closeTabButton(WritePanel writePanel) {
        Object[] options = {"Continue",
                "Cancel"};
        int confirmation = JOptionPane.showOptionDialog(null,
                "Are you sure you want to remove this tab? \n"
                        + "This action cannot be undone.",
                "Warning",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]);

        if (confirmation == 0) {
            int id = listOfIDs.get(listOfTabNames.indexOf(writePanel.getTabName()));
            File folder = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + id + "_" + writePanel.getTabName());
            listOfIDs.remove(listOfIDs.indexOf(id));
            listOfTabNames.remove(writePanel.getTabName());
            tabbedPane.remove(writePanel);

            for (File h : folder.listFiles()) {
                if (h.getName().contains(".md")) {
                    h.delete();
                }
                else {
                    for (File g : h.listFiles()) {
                        g.delete();
                    }
                    h.delete();
                }
            }
            folder.delete();
        }
        disableCloseTabButton(tabbedPane);
    }

    public void renameTabButton(WritePanel writePanel) {
        TabPanel selectedTab = (TabPanel) tabbedPane.getTabComponentAt(tabbedPane.getSelectedIndex());
        String tabName = selectedTab.getLabel().getText();
        String newTabName = TextAndPopupFactory.createPopupWindow("What would you like to rename " + tabName + " to?");

        if (newTabName != null) {
            if (!newTabName.equals("")) {
                boolean illegal = CheckChars.checkChars(newTabName);
                int spaceCount = CheckChars.checkSpaceChars(newTabName);
                boolean preexists = CheckChars.checkPrexists(listOfTabNames, newTabName);

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
                else {
                    int tabNameId = listOfTabNames.indexOf(tabName);
                    int tabId = listOfIDs.get(tabNameId);
                    File tabFolder = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabId + "_" + tabName);
                    File renamedTabFolder = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabId + "_" + newTabName);

                    if (newTabName.equalsIgnoreCase(tabName)) {
                        tabFolder.renameTo(renamedTabFolder);
                        listOfTabNames.set(tabNameId, newTabName);
                        selectedTab.getLabel().setText(newTabName);
                    }
                    else if (preexists) {
                        JOptionPane.showMessageDialog(null,
                                "Sorry! It looks like you already have a tab with this title!",
                                "Name error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        File[] tabFiles = tabFolder.listFiles();
                        listOfTabNames.set(tabNameId, newTabName);
                        selectedTab.getLabel().setText(newTabName);
                        if (tabFiles != null && tabFiles.length != 0) {
                            for (File f : tabFiles) {
                                if (f.getName().contains(".md")) {
                                    String filename = f.getName();
                                    File copiedFile = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabId + "_" + selectedTab.getLabel().getText() + "\\" + filename);
                                    try {
                                        FileUtils.copyFile(f, copiedFile);
                                    } catch (IOException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                                else {
                                    File[] groupFiles = f.listFiles();
                                    String foldername = f.getName();
                                    List<File> tempButtonBarFiles = new ArrayList<>();
                                    if (groupFiles != null && groupFiles.length != 0) {
                                        for (File g : groupFiles) {
                                            File copiedFile = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabId + "_" + selectedTab.getLabel().getText() + "\\" + foldername + "\\" + g.getName());

                                            try {
                                                FileUtils.copyFile(g, copiedFile);
                                            } catch (IOException ex) {
                                                throw new RuntimeException(ex);
                                            }

                                            tempButtonBarFiles.add(copiedFile);
                                        }

                                        int idIndex = foldername.indexOf("_");
                                        int id = Integer.parseInt(foldername.substring(0, idIndex));
                                        for (ButtonBar b : writePanel.getListOfButtonBars()) {
                                            if (id == b.getId()) {
                                                b.getFiles().clear();
                                                b.getFiles().addAll(tempButtonBarFiles);
                                            }
                                        }
                                    }
                                    else {
                                        File newFile = new File(usersDir + "\\WritersBlock\\Write\\WriteExports\\" + tabId + "_" + selectedTab.getLabel().getText() + "\\" + foldername);
                                        if (!newFile.exists()) {
                                            newFile.mkdir();
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            tabFolder.renameTo(renamedTabFolder);
                        }

                        if (tabFiles != null && tabFiles.length != 0) {
                            for (File f : tabFiles) {
                                if (f.getName().contains(".md")) {
                                    f.delete();
                                } else {
                                    for (File g : f.listFiles()) {
                                        g.delete();
                                    }
                                    f.delete();
                                }
                            }
                            tabFolder.delete();
                        }

                        writePanel.setTabName(newTabName);
                    }

                    List<File> tempFiles = new ArrayList<>();
                    if (renamedTabFolder.listFiles() != null && renamedTabFolder.listFiles().length != 0) {
                        tempFiles.addAll(List.of(renamedTabFolder.listFiles()));
                        Collections.sort(tempFiles, new WritePanel.FileIdComparator());

                        List<File> tempChapters = new ArrayList<>();
                        for (File f : tempFiles) {
                            if (f.getName().contains(".md")) {
                                tempChapters.add(f);
                            }
                            writePanel.getListOfFiles().clear();
                            writePanel.getListOfFiles().addAll(tempFiles);
                            writePanel.getListOfChapters().clear();
                            writePanel.getListOfChapters().addAll(tempChapters);
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
