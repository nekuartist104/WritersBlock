package application.world.ui;

import application.shared.domain.CheckChars;
import application.shared.ui.PopClickListener;
import application.shared.ui.PopupMenu;
import application.shared.ui.*;
import application.shared.domain.Characteristic;
import application.shared.domain.Section;
import application.factory.TextAndPopupFactory;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class FeaturesPanel extends JPanel {

    private JLabel nameLabel;
    private JTextArea nameText;
    private Characteristics fixedPanel;
    private java.util.List<SectionComponents> listOfSectionComponents = new ArrayList<>();
    private Toolbar toolBar;
    private java.util.List<String> listOfSectionNames = new ArrayList<>();
    private JPanel worldDetailsPanel;
    private GalleryPanel gallery;
    private java.util.List<ImageIcon> images = new ArrayList<>();
    private java.util.List<String> listOfImageTitles = new ArrayList<>();
    private List<String> imageStrings = new ArrayList<>();
    private JCheckBox enableGallery;
    private int startingIndex = 0;
    private PopClickListener popClick;
    private WorldPanel worldPanel;

    public FeaturesPanel(WorldPanel worldPanel) {
        setLayout(new MigLayout("wrap 2, insets 0 0 0 0", "[][grow]"));
        setBackground(Color.WHITE);
//        setOpaque(false);
        this.worldPanel = worldPanel;

        enableGallery = new JCheckBox();
        enableGallery.setBackground(Color.WHITE);
        enableGallery.setOpaque(false);
        add(enableGallery);

        toolBar = new Toolbar();
        add(toolBar, "right, span 2");

        worldDetailsPanel = new JPanel();
        gallery = new GalleryPanel();

        worldDetailsPanel.setLayout(new MigLayout("wrap 2, insets 0 30 0 0"));
        worldDetailsPanel.setBackground(Color.WHITE);
        worldDetailsPanel.setOpaque(false);
        add(worldDetailsPanel, "grow");
        add(gallery, "grow, right");

        TitlePanel featuresTitle = new TitlePanel("Features");
        worldDetailsPanel.add(featuresTitle, "wrap");
        featuresTitle.getAddButton().setToolTipText("Creates a new feature");
        featuresTitle.getTitle().setFont(new Font("", Font.BOLD, 16));

        fixedPanel = new Characteristics("feature");
        worldDetailsPanel.add(fixedPanel, "span 2");
        nameLabel = new JLabel("Name:");
        nameLabel.setPreferredSize(new Dimension(150,0));

        fixedPanel.add(nameLabel);
        nameText = TextAndPopupFactory.createTextAreaBox();
        fixedPanel.add(nameText);

        featuresTitle.getAddButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fixedPanel.addCharacteristicFieldToFixedPanel();
            }
        });

        toolBar.getAddSection().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sectionComponentsName = TextAndPopupFactory.createPopupWindow("What would you like to name this section?");

                if (sectionComponentsName != null) {
                    if (!sectionComponentsName.equals("")) {
                        int spaceCount = CheckChars.checkSpaceChars(sectionComponentsName);
                        boolean preexists = CheckChars.checkPrexists(listOfSectionNames, sectionComponentsName);

                        if (spaceCount == sectionComponentsName.length()) {
                            JOptionPane.showMessageDialog(null,
                                    "Sorry! A blank space is not a title!",
                                    "Name error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        else if (preexists) {
                            JOptionPane.showMessageDialog(null,
                                    "Sorry! It looks like you already have a section with this name!",
                                    "Name error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            SectionComponents sectionComponents = new SectionComponents();
                            TitlePanel newSectionTitle = new TitlePanel(sectionComponentsName);
                            sectionComponents.setNameLabel(newSectionTitle.getTitle());
                            sectionComponents.setTitle(newSectionTitle);
                            Characteristics sectionPanel = new Characteristics("feature");
                            sectionComponents.setCharacteristics(sectionPanel);
                            addSectionToPanel(sectionComponents);
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(null,
                                "Sorry! It looks like you didn't enter a name!",
                                "Name error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null,
                            "Sorry! It looks like you didn't enter a name!",
                            "Name error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        enableGallery.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayGallery();
            }
        });

        toolBar.getAddImage().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index=0;
                JFileChooser fileChooser = new JFileChooser();

                fileChooser.setAccessory(new ImagePreview(fileChooser));
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif", "bmp"));
                fileChooser.setAcceptAllFileFilterUsed(true);
                int response = fileChooser.showOpenDialog(null);

                if (response == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    ImageIcon icon = new ImageIcon(file.getAbsolutePath());

                    if (images.size() > 0) {
                        Object[] options = {"Previous", "Next",
                                "End"};
                        int confirmation = JOptionPane.showOptionDialog(null,
                                "Where would you like to insert this image? \n",
                                "Insert image index",
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                options[2]);

                        if (confirmation == 0) {
                            index = startingIndex;
                        }
                        else if (confirmation == 1) {
                            index = startingIndex+1;
                        }
                        else if (confirmation == 2) {
                            index = images.size();
                        }
                    }

                    images.add(index, icon);
                    String imageTitle = TextAndPopupFactory.createPopupWindow("What would you like to caption this image?");
                    listOfImageTitles.add(index, imageTitle);
                    byte[] fileContent = new byte[0];
                    try {
                        fileContent = FileUtils.readFileToByteArray(file);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    String encodedString = Base64.getEncoder().encodeToString(fileContent);
                    imageStrings.add(index, encodedString);

                    startingIndex = images.indexOf(icon);
                    gallery.showChosenImage(images, startingIndex, listOfImageTitles);
                    popClick.setDisabled(false);
                }
            }
        });

        gallery.getRightArrow().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (startingIndex == images.size()-1) {
                    startingIndex = 0;
                }
                else {
                    startingIndex = startingIndex+1;
                }
                gallery.showChosenImage(images, startingIndex, listOfImageTitles);
            }
        });

        gallery.getLeftArrow().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (startingIndex == 0) {
                    startingIndex = images.size()-1;
                }
                else {
                    startingIndex = startingIndex-1;
                }
                gallery.showChosenImage(images, startingIndex, listOfImageTitles);
            }
        });

        toolBar.getRemoveImage().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (images.size() > 0) {
                    Object[] options = {"Continue",
                            "Cancel"};
                    int confirmation = JOptionPane.showOptionDialog(null,
                            "Are you sure you want to remove this image? \n"
                                    + "This action cannot be undone.",
                            "Warning",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            options,
                            options[0]);

                    if (confirmation == 0) {
                        images.remove(startingIndex);
                        listOfImageTitles.remove(startingIndex);
                        imageStrings.remove(startingIndex);
                        if (startingIndex == 0) {
                            if (images.size() > 0) {
                                gallery.showChosenImage(images, startingIndex, listOfImageTitles);
                            }
                            else {
                                gallery.getSelectedLabel().setIcon(null);
                                gallery.getTitle().setText(null);
                            }
                        }
                        else {
                            startingIndex = startingIndex-1;
                            gallery.showChosenImage(images, startingIndex, listOfImageTitles);
                        }
                    }
                }
            }
        });

        toolBar.getSaveBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                worldPanel.save();
            }
        });

        toolBar.getAddTab().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                worldPanel.addTab();
            }
        });

        PopupMenu pop = new PopupMenu();
        pop.remove(pop.getRemoveMenuItem());
        pop.getRenameMenuItem().setAction(new AbstractAction("Edit", pop.getRenameMenuItem().getIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (images.size() > 0) {
                    String oldImageName = listOfImageTitles.get(startingIndex);
                    String newImageName = TextAndPopupFactory.createPopupWindow("What would you like to caption this image?");
                    gallery.getTitle().setText(newImageName);
                    listOfImageTitles.remove(oldImageName);
                    listOfImageTitles.add(startingIndex, newImageName);
                }
            }
        });
        popClick = new PopClickListener(pop);
        gallery.getTitle().addMouseListener(popClick);
    }

    public JLabel getNameLabel() {
        return nameLabel;
    }

    public void setNameLabel(JLabel nameLabel) {
        this.nameLabel = nameLabel;
    }

    public JTextArea getNameText() {
        return nameText;
    }

    public void setNameText(JTextArea nameText) {
        this.nameText = nameText;
    }

    public Characteristics getFixedPanel() {
        return fixedPanel;
    }

    public void setFixedPanel(Characteristics fixedPanel) {
        this.fixedPanel = fixedPanel;
    }

    public List<SectionComponents> getListOfSectionComponents() {
        return listOfSectionComponents;
    }

    public void setListOfSectionComponents(List<SectionComponents> listOfSectionComponents) {
        this.listOfSectionComponents = listOfSectionComponents;
    }

    public void setToolBar(Toolbar toolBar) {
        this.toolBar = toolBar;
    }

    public List<String> getListOfSectionNames() {
        return listOfSectionNames;
    }

    public void setListOfSectionNames(List<String> listOfSectionNames) {
        this.listOfSectionNames = listOfSectionNames;
    }

    public JPanel getWorldDetailsPanel() {
        return worldDetailsPanel;
    }

    public void setWorldDetailsPanel(JPanel worldDetailsPanel) {
        this.worldDetailsPanel = worldDetailsPanel;
    }

    public GalleryPanel getGallery() {
        return gallery;
    }

    public void setGallery(GalleryPanel gallery) {
        this.gallery = gallery;
    }

    public List<ImageIcon> getImages() {
        return images;
    }

    public void setImages(List<ImageIcon> images) {
        this.images = images;
    }

    public List<String> getListOfImageTitles() {
        return listOfImageTitles;
    }

    public void setListOfImageTitles(List<String> listOfImageTitles) {
        this.listOfImageTitles = listOfImageTitles;
    }

    public List<String> getImageStrings() {
        return imageStrings;
    }

    public void setImageStrings(List<String> imageStrings) {
        this.imageStrings = imageStrings;
    }

    public void setEnableGallery(JCheckBox enableGallery) {
        this.enableGallery = enableGallery;
    }

    public PopClickListener getPopClick() {
        return popClick;
    }

    public void setPopClick(PopClickListener popClick) {
        this.popClick = popClick;
    }

    public WorldPanel getWorldPanel() {
        return worldPanel;
    }

    public void setWorldPanel(WorldPanel worldPanel) {
        this.worldPanel = worldPanel;
    }


    public void addSectionToPanel(SectionComponents sectionCom) {
        worldDetailsPanel.add(sectionCom.getTitle(), "wrap");
        sectionCom.getTitle().getTitle().setFont(new Font("", Font.BOLD, 14));
        sectionCom.getTitle().getAddButton().setToolTipText("Adds a new characteristic to section");
        worldDetailsPanel.add(sectionCom.getCharacteristics(), "span 2");
        listOfSectionComponents.add(sectionCom);
        String sectionName = sectionCom.getTitle().getTitle().getText().substring(0, sectionCom.getTitle().getTitle().getText().length()-1);
        listOfSectionNames.add(sectionName);

        sectionCom.getTitle().getAddButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sectionCom.getCharacteristics().addCharacteristicField();
            }
        });

        PopupMenu pop = new PopupMenu();
        pop.getRemoveMenuItem().setAction(new AbstractAction("Remove", pop.getRemoveMenuItem().getIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"Continue",
                        "Cancel"};
                int confirmation = JOptionPane.showOptionDialog(null,
                        "Are you sure you want to remove this section? \n"
                                + "This action cannot be undone.",
                        "Warning",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[0]);

                if (confirmation == 0) {
                    String currentSectionName = sectionCom.getTitle().getTitle().getText().substring(0, sectionCom.getTitle().getTitle().getText().length()-1);
                    worldDetailsPanel.remove(sectionCom.getTitle().getTitle());
                    worldDetailsPanel.remove(sectionCom.getTitle().getAddButton());
                    worldDetailsPanel.remove(sectionCom.getTitle());
                    worldDetailsPanel.remove(sectionCom.getCharacteristics());
                    listOfSectionComponents.remove(sectionCom);
                    listOfSectionNames.remove(currentSectionName);
                    revalidate();
                    repaint();
                }
            }
        });

        pop.getRenameMenuItem().setAction(new AbstractAction("Rename", pop.getRenameMenuItem().getIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                String oldSectionName = sectionCom.getTitle().getTitle().getText().substring(0, sectionCom.getTitle().getTitle().getText().length()-1);
                String newSectionNameInputChange = TextAndPopupFactory.createPopupWindow("What would you like to rename " + oldSectionName + " to?");

                if (newSectionNameInputChange != null) {
                    if (!newSectionNameInputChange.equals("")) {
                        int spaceCount = CheckChars.checkSpaceChars(newSectionNameInputChange);
                        boolean preexists = CheckChars.checkPrexists(listOfSectionNames, newSectionNameInputChange);

                        if (spaceCount == newSectionNameInputChange.length()) {
                            JOptionPane.showMessageDialog(null,
                                    "Sorry! A blank space is not a title!",
                                    "Name error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        else if (newSectionNameInputChange.equalsIgnoreCase(oldSectionName)) {
                            sectionCom.getTitle().getTitle().setText(newSectionNameInputChange + ":");
                            listOfSectionNames.remove(oldSectionName);
                            listOfSectionNames.add(newSectionNameInputChange);
                        }
                        else if (preexists) {
                            JOptionPane.showMessageDialog(null,
                                    "Sorry! It looks like you already have a section with this name!",
                                    "Rename error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            sectionCom.getTitle().getTitle().setText(newSectionNameInputChange + ":");
                            listOfSectionNames.remove(oldSectionName);
                            listOfSectionNames.add(newSectionNameInputChange);
                        }
                    }
                }
            }
        });
        sectionCom.getTitle().getTitle().addMouseListener(new PopClickListener(pop));
        revalidate();
        repaint();
    }

    public void addSectionComponents(Section section) {
        SectionComponents sectionComponents = new SectionComponents();
        JLabel label = new JLabel();
        sectionComponents.setNameLabel(label);
        Characteristics panel = new Characteristics("feature");
        for (Characteristic item : section.getListOfCharacteristics()) {
            CharacteristicField field = new CharacteristicField();
            field.getLabel().setText(item.getCharacteristicTitle() + ":");
            field.getValue().setText(item.getCharacteristicValue());
            panel.addCharacteristicToPanel(field);
        }
        sectionComponents.setCharacteristics(panel);
        sectionComponents.setTitle(new TitlePanel(section.getName()));
        addSectionToPanel(sectionComponents);
    }

    public Toolbar getToolBar() {
        return toolBar;
    }

    public void displayFirstImage() {
        startingIndex = 0;
        gallery.showChosenImage(images, startingIndex, listOfImageTitles);
        gallery.getImagePanel().revalidate();
        gallery.getImagePanel().repaint();
    }

    public JCheckBox getEnableGallery() {
        return enableGallery;
    }

    public void displayGallery() {
        if (enableGallery.isSelected()) {
            enableGallery.setText("Disable Gallery");
            gallery.setVisible(true);
            toolBar.getAddImage().setEnabled(true);
            toolBar.getRemoveImage().setEnabled(true);
        }
        else {
            enableGallery.setText("Enable Gallery");
            gallery.setVisible(false);
            toolBar.getAddImage().setEnabled(false);
            toolBar.getRemoveImage().setEnabled(false);
        }
    }
}

