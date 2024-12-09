package application.character.ui;

import application.character.domain.CharacterTypes;
import application.character.domain.ExtraCharacterTypes;
import application.factory.TextAndPopupFactory;
import application.shared.ui.ImagePreview;
import application.shared.ui.PopupMenu;
import application.character.domain.Character;
import application.factory.ButtonFactory;
import application.factory.OvalButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class CharacterPanelLeftComponent extends JPanel {

    private DefaultMutableTreeNode rootNode;
    private JTree tree;
    private ExtraCharacterTypes extraTypes = new ExtraCharacterTypes();
    private DefaultMutableTreeNode node;
    private OvalButton addCharacterButton;
    private final String usersDir = System.getProperty("user.home");

    public CharacterPanelLeftComponent() {
        setLayout(new MigLayout("", "grow", "grow"));
        addCharacterButton = ButtonFactory.createAddButton();
        addCharacterButton.setToolTipText("Creates a new character");
        setBackground(Color.WHITE);

        Icon leafIcon = new ImageIcon(getClass().getClassLoader().getResource("leafIcons/characterLeafIcon.png"));
        UIManager.put("Tree.leafIcon", leafIcon);

        rootNode = new DefaultMutableTreeNode("Characters");

        tree = new JTree(rootNode);
        tree.setMinimumSize(new Dimension(150,0));
        add(tree, "grow, id tree");
        add(addCharacterButton, "pos (tree.x + 85) (tree.y + 0)");
        setComponentZOrder(addCharacterButton, 0);

        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int selRow = tree.getRowForLocation(e.getX(), e.getY());
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                    tree.setSelectionPath(selPath);
                    if (selRow > -1) {
                        tree.setSelectionRow(selRow);

                        node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                        if (node != rootNode) {
                            Character character = (Character) node.getUserObject();
                            PopupMenu pop = new PopupMenu();
                            JMenu typeMenu = new JMenu("Type");

                            for (CharacterTypes type : CharacterTypes.values()) {
                                if (type.getTitle().equals(character.getType())) {
                                    typeMenu.setIcon(new ImageIcon(getClass().getClassLoader().getResource(type.getIcon())));
                                }
                            }

                            JMenuItem maleType = new JMenuItem();
                            maleType.setText(CharacterTypes.MALE.getTitle());
                            maleType.setIcon(new ImageIcon(getClass().getClassLoader().getResource(CharacterTypes.MALE.getIcon())));
                            typeMenu.add(maleType);

                            JMenuItem femaleType = new JMenuItem();
                            femaleType.setText(CharacterTypes.FEMALE.getTitle());
                            femaleType.setIcon(new ImageIcon(getClass().getClassLoader().getResource(CharacterTypes.FEMALE.getIcon())));
                            typeMenu.add(femaleType);

                            JMenuItem addType = new JMenuItem();
                            addType.setText("Add new type");
                            typeMenu.add(addType);

                            JMenuItem removeType = new JMenuItem();
                            removeType.setText("Remove type");
                            if (extraTypes.getTypes().size() != 0) {
                                typeMenu.add(removeType);
                            }

                            pop.remove(pop.getRenameMenuItem());
                            pop.add(typeMenu);

                            pop.getRemoveMenuItem().setAction(new AbstractAction("Remove", pop.getRemoveMenuItem().getIcon()) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    Object[] options = {"Continue",
                                            "Cancel"};
                                    int confirmation = JOptionPane.showOptionDialog(null,
                                            "Are you sure you want to remove this character? \n"
                                                    + "This action cannot be undone.",
                                            "Warning",
                                            JOptionPane.YES_NO_CANCEL_OPTION,
                                            JOptionPane.WARNING_MESSAGE,
                                            null,
                                            options,
                                            options[0]);

                                    if (confirmation == 0) {
                                        File directory = new File(usersDir + "\\WritersBlock\\Character\\CharacterExports");
                                        File[] directoryListing = directory.listFiles();
                                        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                                        model.removeNodeFromParent(node);
                                        Character character = (Character) node.getUserObject();
                                        for (File file : directoryListing) {
                                            if (file.getName().equals(character.getId() + "_" + character.getType() + "_" + character.getName() + ".json")) {
                                                file.delete();
                                            }
                                        }
                                    }
                                }
                            });

                            maleType.setAction(new AbstractAction(maleType.getText(), maleType.getIcon()) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    setType(typeMenu, maleType);
                                }
                            });

                            femaleType.setAction(new AbstractAction(femaleType.getText(), femaleType.getIcon()) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    setType(typeMenu, femaleType);
                                }
                            });

                            if (extraTypes.getTypes().size() != 0) {
                                for (String name : extraTypes.getTypes()) {
                                    File file = new File(usersDir + "\\WritersBlock\\Character\\CharacterExtraLeafIcons\\" + name + ".png");
                                    Icon imageIcon = new ImageIcon(file.getAbsolutePath());

                                    JMenuItem newType = new JMenuItem();
                                    newType.setText(name);
                                    newType.setIcon(imageIcon);

                                    typeMenu.remove(addType);
                                    typeMenu.remove(removeType);
                                    typeMenu.add(newType);

                                    if (name.equals(character.getType())) {
                                        typeMenu.setIcon(imageIcon);
                                    }

                                    newType.setAction(new AbstractAction(newType.getText(), newType.getIcon()) {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            setType(typeMenu, newType);
                                        }
                                    });
                                }
                                typeMenu.add(addType);
                                typeMenu.add(removeType);
                            }

                            addType.setAction(new AbstractAction(addType.getText()) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    String typeName = TextAndPopupFactory.createPopupWindow("What would you like to name this type?");
                                    if (typeName != null) {
                                        if (!typeName.equals("")) {
                                            JFileChooser fileChooser = new JFileChooser();

                                            fileChooser.setAccessory(new ImagePreview(fileChooser));
                                            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif", "bmp"));
                                            fileChooser.setAcceptAllFileFilterUsed(true);
                                            int response = fileChooser.showOpenDialog(null);

                                            if (response == JFileChooser.APPROVE_OPTION) {
                                                File file = fileChooser.getSelectedFile();
                                                File iconFile = new File(usersDir + "\\WritersBlock\\Character\\CharacterExtraLeafIcons\\" + typeName + ".png");

                                                try {
                                                    FileInputStream in = new FileInputStream(file);
                                                    FileOutputStream out = new FileOutputStream(iconFile);
                                                    out.write(in.readAllBytes());
                                                    in.close();
                                                    out.close();
                                                } catch (FileNotFoundException ex) {
                                                    throw new RuntimeException(ex);
                                                } catch (IOException ex) {
                                                    throw new RuntimeException(ex);
                                                }

                                                extraTypes.getTypes().add(typeName);
                                                CharacterDialog.savesExtraTypesJson(extraTypes);
                                                pop.updateUI();
                                                tree.updateUI();
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

                            removeType.setAction(new AbstractAction(removeType.getText()) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    new CharacterDialog(extraTypes);
                                    for (int i = 0; i < rootNode.getChildCount(); i++) {
                                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNode.getChildAt(i);
                                        Character characterNode = (Character) node.getUserObject();
                                        File extraLeafIconsFolder = new File(usersDir + "\\WritersBlock\\Character\\CharacterExtraLeafIcons");
                                            if (CharacterTypes.isFixed(characterNode.getType())) {
                                                continue;
                                            }
                                            FilenameFilter newFilter = new FilenameFilter() {
                                                @Override
                                                public boolean accept(File dir, String name) {
                                                    return name.equals(characterNode.getType() + ".png");
                                                }
                                            };
                                            File[] characterTypeCheck = extraLeafIconsFolder.listFiles(newFilter);
                                            if (characterTypeCheck == null || characterTypeCheck.length == 0) {
                                                File oldFile = new File(usersDir + "\\WritersBlock\\Character\\CharacterExports\\" + characterNode.getId() + "_" + characterNode.getType() + "_" + characterNode.getName() + ".json");
                                                characterNode.setType("Male");
                                                File newFile = new File(usersDir + "\\WritersBlock\\Character\\CharacterExports\\" + characterNode.getId() + "_" + characterNode.getType() + "_" + characterNode.getName() + ".json");
                                                oldFile.renameTo(newFile);
                                            }
                                        }
                                    tree.updateUI();
                                }
                            });
                            pop.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
            }
        };
        tree.addMouseListener(ml);

        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
                if (treeNode.getUserObject() instanceof Character) {
                    Character character = (Character) treeNode.getUserObject();
                    label.setText(character.getName());

                    if (CharacterTypes.MALE.getTitle().equals(character.getType())) {
                        setIcon(new ImageIcon(getClass().getClassLoader().getResource(CharacterTypes.MALE.getIcon())));
                    }
                    else if (CharacterTypes.FEMALE.getTitle().equals(character.getType())) {
                        setIcon(new ImageIcon(getClass().getClassLoader().getResource(CharacterTypes.FEMALE.getIcon())));
                    }

                    else if (extraTypes.getTypes().size() != 0) {
                        for (String type : extraTypes.getTypes()) {
                            if (type.equals(character.getType())) {
                                File file = new File(usersDir + "\\WritersBlock\\Character\\CharacterExtraLeafIcons\\" + type + ".png");
                                Icon imageIcon = new ImageIcon(file.getAbsolutePath());
                                setIcon(imageIcon);
                                break;
                            }
                            else {
                                setIcon(new ImageIcon(getClass().getClassLoader().getResource(CharacterTypes.MALE.getIcon())));
                            }
                        }
                    }
                }

                else if (treeNode == rootNode) {
                    if (treeNode.getChildCount() == 0) {
                        setIcon(new ImageIcon(getClass().getClassLoader().getResource("leafIcons/closedTreeIcon.png")));
                    }
                    else {
                        TreePath path = new TreePath(treeNode.getPath());
                        if (tree.isCollapsed(path)) {
                            setIcon(new ImageIcon(getClass().getClassLoader().getResource("leafIcons/closedTreeIcon.png")));
                        }
                        else {
                            setIcon(new ImageIcon(getClass().getClassLoader().getResource("leafIcons/openTreeIcon.png")));
                        }
                    }
                }
                return label;
            }
        });
    }

    public JTree getTree() {
        return tree;
    }

    public OvalButton getAddCharacterButton() {
        return addCharacterButton;
    }

    public DefaultMutableTreeNode getRootNode() {
        return rootNode;
    }
    public ExtraCharacterTypes getExtraTypes() {
        return extraTypes;
    }

    public void setExtraTypes(ExtraCharacterTypes extraTypes) {
        this.extraTypes = extraTypes;
    }

    public DefaultMutableTreeNode getNode() {
        return node;
    }

    public void setType(JMenu typeMenu, JMenuItem type) {
        Character character = (Character) node.getUserObject();
        File folder = new File(usersDir + "\\WritersBlock\\Character\\CharacterExports\\" + character.getId() + "_" + character.getType() + "_" + character.getName() + ".json");
        character.setType(type.getText());
        typeMenu.setIcon(type.getIcon());
        File newFolder = new File(usersDir + "\\WritersBlock\\Character\\CharacterExports\\" + character.getId() + "_" + character.getType() + "_" + character.getName() + ".json");
        folder.renameTo(newFolder);
        tree.updateUI();
    }
}


