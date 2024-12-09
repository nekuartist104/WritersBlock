package application.character.ui;

import application.character.domain.Character;
import application.character.domain.ExtraCharacterTypes;
import application.shared.domain.Characteristic;
import application.shared.domain.Section;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CharacterPanel extends JPanel {

    private CharacteristicsPanel characteristicsPanel;
    private JSplitPane splitPane;
    private CharacterPanelLeftComponent characterPanelLeftComponent;
    private JPanel plainPanel;
    private final String usersDir = System.getProperty("user.home");

    public CharacterPanel() {
        setLayout(new MigLayout("insets 0", "grow", "grow"));
        setBackground(Color.MAGENTA);
        characteristicsPanel = new CharacteristicsPanel();
        characterPanelLeftComponent = new CharacterPanelLeftComponent();
        plainPanel = new JPanel();
        splitPane = new JSplitPane();

        Border characteristicsPanelBorder = BorderFactory.createLineBorder(Color.MAGENTA);
        setBorder(BorderFactory.createCompoundBorder(characteristicsPanelBorder,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        setSplitPaneDividerColor(splitPane, Color.MAGENTA);
        add(splitPane, "grow");
        JScrollPane scrollPane = new JScrollPane(characterPanelLeftComponent);
        scrollPane.setMinimumSize(new Dimension(170,0));
        splitPane.setLeftComponent(scrollPane);
        splitPane.setRightComponent(new JScrollPane(characteristicsPanel));

        characteristicsPanel.getToolBar().getSaveBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) characterPanelLeftComponent.getTree().getLastSelectedPathComponent();
                Character character = (Character) node.getUserObject();
                characteristicsPanel.saveCharacteristicsDetails(character);
                characterPanelLeftComponent.getTree().updateUI();
                characterPanelLeftComponent.getAddCharacterButton().repaint();
                SwingUtilities.invokeLater(() -> characterPanelLeftComponent.getAddCharacterButton().repaint());
            }
        });

        characterPanelLeftComponent.getTree().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) characterPanelLeftComponent.getTree().getLastSelectedPathComponent();
                if (node == characterPanelLeftComponent.getTree().getModel().getRoot()) {
                    splitPane.setRightComponent(plainPanel);
                }
                else if (node != null) {
                    Character nodeCharacter = (Character) node.getUserObject();
                    characteristicsPanel.printCharacteristicsDetails(nodeCharacter);
                    splitPane.setRightComponent(new JScrollPane(characteristicsPanel));
                }
                else {
                    splitPane.setRightComponent(plainPanel);
                }
            }
        });

        File directory = new File(usersDir + "\\WritersBlock\\Character\\CharacterExports");
        if (!directory.exists()) {
            directory.mkdir();
        }

        File[] directoryListing = directory.listFiles();
        ObjectMapper mapper = new ObjectMapper();
        if (directoryListing != null && directoryListing.length != 0) {
            List<Character> listOfCharacters = new ArrayList<>();
            for (File file : directoryListing) {
                try {
                    Character savedCharacter = mapper.readValue(file, Character.class);

                    File dir = new File(file.getAbsolutePath());

                    String[] characterParts = dir.getName().split("_");
                    savedCharacter.setId(Integer.parseInt(characterParts[0]));
                    savedCharacter.setType(characterParts[1]);
                    savedCharacter.setName(characterParts[2].substring(0, characterParts[2].length()-5));

                    listOfCharacters.add(savedCharacter);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            Collections.sort(listOfCharacters, new IdComparator());
            for (Character character : listOfCharacters) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(character);
                characterPanelLeftComponent.getRootNode().add(node);
            }
        }
        else {
            Character defaultCharacter = new Character();
            defaultCharacter.setId(0);
            defaultCharacter.setName("New character");
            defaultCharacter.setType("Male");
            DefaultMutableTreeNode defaultNode = new DefaultMutableTreeNode(defaultCharacter);
            defaultLayout(defaultCharacter);
            characterPanelLeftComponent.getRootNode().add(defaultNode);

            try {
                String json = mapper.writeValueAsString(defaultCharacter);
                File defaultFile = new File(usersDir + "\\WritersBlock\\Character\\CharacterExports\\0_Male_New character.json");
                defaultFile.createNewFile();
                FileWriter fileWriter = new FileWriter(defaultFile);
                fileWriter.write(json);
                fileWriter.close();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        File extraTypes = new File(usersDir + "\\WritersBlock\\Character\\CharacterExtraTypes\\ExtraTypes.json");
        if (extraTypes.exists()) {
            try {
                ExtraCharacterTypes extraTypesFiles = mapper.readValue(extraTypes, ExtraCharacterTypes.class);
                characterPanelLeftComponent.getExtraTypes().getTypes().addAll(extraTypesFiles.getTypes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                extraTypes.createNewFile();
                ExtraCharacterTypes types = new ExtraCharacterTypes();
                CharacterDialog.savesExtraTypesJson(types);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        characterPanelLeftComponent.getTree().setSelectionPath(new TreePath(((DefaultMutableTreeNode) characterPanelLeftComponent.getTree().getModel().getChild(characterPanelLeftComponent.getTree().getModel().getRoot(), 0)).getPath()));

        characterPanelLeftComponent.getAddCharacterButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem defaultLayout = new JMenuItem("Default Layout");
                JMenuItem noLayout = new JMenuItem("Plain");

                popupMenu.add(defaultLayout);
                popupMenu.add(noLayout);
                popupMenu.show(characterPanelLeftComponent.getAddCharacterButton(), characterPanelLeftComponent.getAddCharacterButton().getX(), characterPanelLeftComponent.getAddCharacterButton().getY());
                popupMenu.setLocation(140, 80);

                int tempCharacterId = 0;
                if (characterPanelLeftComponent.getRootNode().getChildCount() != 0) {
                    DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) characterPanelLeftComponent.getTree().getModel().getChild(characterPanelLeftComponent.getRootNode(), characterPanelLeftComponent.getTree().getModel().getChildCount(characterPanelLeftComponent.getRootNode())-1);
                    Character lastCharacter = (Character) lastNode.getUserObject();
                    tempCharacterId = lastCharacter.getId() + 1;
                }

                int characterId = tempCharacterId;

                defaultLayout.setAction(new AbstractAction("Default Layout") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Character newCharacter = new Character();
                        newCharacter.setId(characterId);
                        newCharacter.setName("New character");
                        newCharacter.setType("Male");
                        DefaultMutableTreeNode newCharacterNode = new DefaultMutableTreeNode(newCharacter);
                        ((DefaultTreeModel) characterPanelLeftComponent.getTree().getModel()).insertNodeInto(newCharacterNode, characterPanelLeftComponent.getRootNode(), characterPanelLeftComponent.getRootNode().getChildCount());
                        defaultLayout(newCharacter);
                    }
                });

                noLayout.setAction(new AbstractAction("Plain") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Character newCharacter = new Character();
                        newCharacter.setId(characterId);
                        newCharacter.setName("New character");
                        newCharacter.setType("Male");
                        DefaultMutableTreeNode newCharacterNode = new DefaultMutableTreeNode(newCharacter);
                        ((DefaultTreeModel) characterPanelLeftComponent.getTree().getModel()).insertNodeInto(newCharacterNode, characterPanelLeftComponent.getRootNode(), characterPanelLeftComponent.getRootNode().getChildCount());
                        newCharacter.setGalleryEnabled(false);
                    }
                });
            }
        });
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

    public void defaultLayout(Character character) {
        Characteristic alias = new Characteristic();
        alias.setCharacteristicTitle("Alias");
        character.getListOfCharacteristics().add(alias);

        Characteristic age = new Characteristic();
        age.setCharacteristicTitle("Age");
        character.getListOfCharacteristics().add(age);

        Characteristic bday = new Characteristic();
        bday.setCharacteristicTitle("Birthday");
        character.getListOfCharacteristics().add(bday);

        Characteristic trait = new Characteristic();
        trait.setCharacteristicTitle("Characteristic Trait");
        character.getListOfCharacteristics().add(trait);



        Section appearance = new Section();
        appearance.setName("Appearance");
        character.getListOfSections().add(appearance);

        Characteristic eyes = new Characteristic();
        eyes.setCharacteristicTitle("Eye Colour");
        appearance.getListOfCharacteristics().add(eyes);

        Characteristic hair = new Characteristic();
        hair.setCharacteristicTitle("Hair Colour");
        appearance.getListOfCharacteristics().add(hair);

        Characteristic height = new Characteristic();
        height.setCharacteristicTitle("Height");
        appearance.getListOfCharacteristics().add(height);

        Characteristic skin = new Characteristic();
        skin.setCharacteristicTitle("Skin Tone");

        Characteristic build = new Characteristic();
        build.setCharacteristicTitle("Body Type");
        appearance.getListOfCharacteristics().add(build);

        Characteristic clothingStyle = new Characteristic();
        clothingStyle.setCharacteristicTitle("Clothing Style");
        appearance.getListOfCharacteristics().add(clothingStyle);



        Section personality = new Section();
        personality.setName("Personality");
        character.getListOfSections().add(personality);

        Characteristic attitude = new Characteristic();
        attitude.setCharacteristicTitle("Attitude");
        personality.getListOfCharacteristics().add(attitude);

        Characteristic intelligence = new Characteristic();
        intelligence.setCharacteristicTitle("Intelligence");
        personality.getListOfCharacteristics().add(intelligence);



        Section relationships = new Section();
        relationships.setName("Relationships");
        character.getListOfSections().add(relationships);



        Section background = new Section();
        background.setName("Background");
        character.getListOfSections().add(background);



        Section history = new Section();
        history.setName("History");
        character.getListOfSections().add(history);

        character.setGalleryEnabled(true);
    }
    class IdComparator implements java.util.Comparator<Character> {
        @Override
        public int compare(Character a, Character b) {
            return a.getId() - b.getId();
        }
    }
}
