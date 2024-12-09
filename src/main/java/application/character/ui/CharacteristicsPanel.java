package application.character.ui;

import application.shared.domain.CheckChars;
import application.shared.ui.PopClickListener;
import application.shared.ui.PopupMenu;
import application.character.domain.Character;
import application.shared.ui.*;
import application.shared.domain.Characteristic;
import application.shared.domain.Section;
import application.factory.TextAndPopupFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CharacteristicsPanel extends JPanel {
    private JLabel nameLabel;
    private JTextArea nameText;
    private Characteristics fixedPanel;
    private List<SectionComponents> listOfSectionComponents = new ArrayList<>();
    private Toolbar toolBar;
    private List<String> listOfSectionNames = new ArrayList<>();
    private JPanel characterDetailsPanel;
    private GalleryPanel gallery;
    private List<ImageIcon> images = new ArrayList<>();
    private List<String> listOfImageTitles = new ArrayList<>();
    private List<String> imageStrings = new ArrayList<>();
    private JCheckBox enableGallery;
    private int startingIndex = 0;
    private PopClickListener popClick;
    private final String usersDir = System.getProperty("user.home");


    public CharacteristicsPanel() {
        setLayout(new MigLayout("wrap 2, insets 0 0 0 0", "[][grow]"));
        setBackground(Color.WHITE);

        enableGallery = new JCheckBox();
        enableGallery.setBackground(Color.WHITE);
        add(enableGallery);

        toolBar = new Toolbar();
        toolBar.remove(toolBar.getAddTab());
        add(toolBar, "right, span 2");

        characterDetailsPanel = new JPanel();
        gallery = new GalleryPanel();

        characterDetailsPanel.setLayout(new MigLayout("wrap 2, insets 0 30 0 0"));
        characterDetailsPanel.setBackground(Color.WHITE);
        characterDetailsPanel.setOpaque(false);
        add(characterDetailsPanel, "grow");
        add(gallery, "grow, right");

        TitlePanel characteristicsTitle = new TitlePanel("Characteristics");
        characterDetailsPanel.add(characteristicsTitle, "wrap");
        characteristicsTitle.getAddButton().setToolTipText("Creates a new characteristic");
        characteristicsTitle.getTitle().setFont(new Font("", Font.BOLD, 16));

//        add(addCharacteristicButton, "pos (characteristicsLabel.x+125) (characteristicsLabel.y+3.5)");

        fixedPanel = new Characteristics("characteristic");
        characterDetailsPanel.add(fixedPanel, "span 2");
        nameLabel = new JLabel("Name:");
        nameLabel.setPreferredSize(new Dimension(150,0));

        fixedPanel.add(nameLabel);
        nameText = TextAndPopupFactory.createTextAreaBox();
        fixedPanel.add(nameText);

        characteristicsTitle.getAddButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fixedPanel.addCharacteristicField();
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
                            Characteristics sectionPanel = new Characteristics("characteristic");
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
                    File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
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

    public Toolbar getToolBar() {
        return toolBar;
    }
    public JCheckBox getEnableGallery() {
        return enableGallery;
    }

    public void saveCharacteristicsDetails(Character character) {
        boolean illegal = CheckChars.checkChars(nameText.getText());
        int spaceCount = CheckChars.checkSpaceChars(nameText.getText());
        if (illegal) {
            JOptionPane.showMessageDialog(null,
                    "Sorry! A character name can't contain any of the following characters: \n \\ / : * ? \" < > |",
                    "Name error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (spaceCount == nameText.getText().length()) {
            JOptionPane.showMessageDialog(null,
                    "Sorry! A blank space is not a name!",
                    "Name error",
                    JOptionPane.ERROR_MESSAGE);
        }
        else {
            character.setName(nameText.getText());
            character.getListOfCharacteristics().clear();
            for (CharacteristicField item : fixedPanel.getListOfCharacteristicFields()) {
                Characteristic characteristic = new Characteristic();
                String characteristicName = item.getLabel().getText().substring(0, item.getLabel().getText().length() - 1);
                characteristic.setCharacteristicTitle(characteristicName);
                characteristic.setCharacteristicValue(item.getValue().getText());
                character.getListOfCharacteristics().add(characteristic);
            }
            character.getListOfSections().clear();
            for (SectionComponents sectionComponents : listOfSectionComponents) {
                Section newSection = new Section();
                String sectionName = sectionComponents.getTitle().getTitle().getText().substring(0, sectionComponents.getTitle().getTitle().getText().length() - 1);
                newSection.setName(sectionName);
                character.getListOfSections().add(newSection);
                for (CharacteristicField item : sectionComponents.getCharacteristics().getListOfCharacteristicFields()) {
                    Characteristic characteristic = new Characteristic();
                    String characteristicName = item.getLabel().getText().substring(0, item.getLabel().getText().length() - 1);
                    characteristic.setCharacteristicTitle(characteristicName);
                    characteristic.setCharacteristicValue(item.getValue().getText());
                    newSection.getListOfCharacteristics().add(characteristic);
                }
            }
            character.getListOfImageStrings().clear();
            character.getListOfImageTitles().clear();
            character.getListOfImageStrings().addAll(imageStrings);
            character.getListOfImageTitles().addAll(listOfImageTitles);

            character.setGalleryEnabled(enableGallery.isSelected());
            character.setDescription(gallery.getDescription().getText());

            ObjectMapper mapper = new ObjectMapper();
            try {
                String json = mapper.writeValueAsString(character);
                File file = new File(usersDir + "\\WritersBlock\\Character\\CharacterExports\\" + character.getId() + "_" + character.getType() + "_" + character.getName() + ".json");
                File directory = new File(usersDir + "\\WritersBlock\\Character\\CharacterExports");

                FilenameFilter filter = new FilenameFilter() {
                    public boolean accept(File f, String name) {
                        return name.startsWith(String.valueOf(character.getId() + "_"));
                    }
                };

                File[] f = directory.listFiles(filter);
                if (f != null && f.length != 0) {
                    f[0].renameTo(file);
                } else {
                    file.createNewFile();
                }

                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(json);
                fileWriter.close();

            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void printCharacteristicsDetails(Character character) {
        nameText.setText(character.getName());
        for (CharacteristicField item : fixedPanel.getListOfCharacteristicFields()) {
            fixedPanel.remove(item.getLabel());
            fixedPanel.remove(item.getValue());
        }
        fixedPanel.getListOfCharacteristicFields().clear();
        fixedPanel.getListOfNames().clear();
        for (SectionComponents sectionComponents : listOfSectionComponents) {
            characterDetailsPanel.remove(sectionComponents.getTitle());
            characterDetailsPanel.remove(sectionComponents.getCharacteristics());
        }
        listOfSectionComponents.clear();
        listOfSectionNames.clear();

        gallery.getSelectedLabel().setIcon(null);
        gallery.getTitle().setText("");

        listOfImageTitles.clear();
        imageStrings.clear();
        images.clear();

        for (Characteristic item : character.getListOfCharacteristics()) {
            CharacteristicField field = new CharacteristicField();
            field.getLabel().setText(item.getCharacteristicTitle() + ":");
            field.getValue().setText(item.getCharacteristicValue());
            fixedPanel.addCharacteristicToPanel(field);
        }
        for (Section section : character.getListOfSections()) {
            addSectionComponents(section);
        }
        for (String imageString : character.getListOfImageStrings()) {
            imageStrings.add(imageString);

            byte[] decodedBytes = Base64.getDecoder().decode(imageString);
            BufferedImage someImage = null;
            try {
                someImage = ImageIO.read(new ByteArrayInputStream(decodedBytes));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ImageIcon icon = new ImageIcon(someImage);
            images.add(icon);
        }
        for (String imageTitle : character.getListOfImageTitles()) {
            listOfImageTitles.add(imageTitle);
        }
        if (images.size() > 0) {
            displayFirstImage();
            popClick.setDisabled(false);
        }
        else if (images.size() == 0) {
            popClick.setDisabled(true);
        }

        enableGallery.setSelected(character.isGalleryEnabled());
        gallery.getDescription().setText(character.getDescription());
        displayGallery();
        }

    public void addSectionToPanel(SectionComponents sectionCom) {
        characterDetailsPanel.add(sectionCom.getTitle(), "wrap");
        sectionCom.getTitle().getTitle().setFont(new Font("", Font.BOLD, 14));
        sectionCom.getTitle().getAddButton().setToolTipText("Adds a new characteristic to section");
        characterDetailsPanel.add(sectionCom.getCharacteristics(), "span 2");
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
                    characterDetailsPanel.remove(sectionCom.getTitle().getTitle());
                    characterDetailsPanel.remove(sectionCom.getTitle().getAddButton());
                    characterDetailsPanel.remove(sectionCom.getTitle());
                    characterDetailsPanel.remove(sectionCom.getCharacteristics());
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
        Characteristics panel = new Characteristics("characteristic");
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

    public void displayFirstImage() {
        startingIndex = 0;
        gallery.showChosenImage(images, startingIndex, listOfImageTitles);
        gallery.getImagePanel().revalidate();
        gallery.getImagePanel().repaint();
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

