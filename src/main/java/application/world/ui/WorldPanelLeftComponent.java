package application.world.ui;

import application.shared.domain.CheckChars;
import application.shared.ui.PopupMenu;
import application.factory.ButtonFactory;
import application.factory.OvalButton;
import application.factory.TextAndPopupFactory;
import application.shared.ui.ImagePreview;
import application.world.domain.*;
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

public class WorldPanelLeftComponent extends JPanel {

    private DefaultMutableTreeNode rootNode;
    private JTree tree;
    private OvalButton addLocationButton;
    private ExtraLocationTypes extraTypes = new ExtraLocationTypes();
    private DefaultMutableTreeNode node;
    private JMenuItem addArea = new JMenuItem();
    private final String usersDir = System.getProperty("user.home");

    public WorldPanelLeftComponent() {
        setLayout(new MigLayout("", "grow", "grow"));
        addLocationButton = ButtonFactory.createAddButton();
        addLocationButton.setToolTipText("Creates a new location");
        setBackground(Color.WHITE);

        Icon leafIcon = new ImageIcon(getClass().getClassLoader().getResource("leafIcons/treeIcon.png"));
        UIManager.put("Tree.leafIcon", leafIcon);

        rootNode = new DefaultMutableTreeNode("Locations");

        tree = new JTree(rootNode);
        tree.setMinimumSize(new Dimension(150,0));
        add(tree, "grow, id tree");
        add(addLocationButton, "pos (tree.x + 78) (tree.y + 0)");
        setComponentZOrder(addLocationButton, 0);

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
                            Area location = (Area) node.getUserObject();
                            PopupMenu pop = new PopupMenu();
                            if (node.getUserObject() instanceof Location) {
                                pop.add(addArea, 0);
                            }

                            addArea.setText("Add area");
                            addArea.setIcon(new ImageIcon(getClass().getClassLoader().getResource("buttonIcons/addSectionIcon.png")));

                            JMenu typeMenu = new JMenu("Type");

                            for (LocationTypes type : LocationTypes.values()) {
                                if (type.getTitle().equals(location.getType())) {
                                    typeMenu.setIcon(new ImageIcon(getClass().getClassLoader().getResource(type.getIcon())));
                                }
                            }

                            JMenuItem landType = new JMenuItem();
                            landType.setText(LocationTypes.LAND.getTitle());
                            landType.setIcon(new ImageIcon(getClass().getClassLoader().getResource(LocationTypes.LAND.getIcon())));
                            typeMenu.add(landType);

                            JMenuItem villageType = new JMenuItem();
                            villageType.setText(LocationTypes.VILLAGE.getTitle());
                            villageType.setIcon(new ImageIcon(getClass().getClassLoader().getResource(LocationTypes.VILLAGE.getIcon())));
                            typeMenu.add(villageType);

                            JMenuItem cityType = new JMenuItem();
                            cityType.setText(LocationTypes.CITY.getTitle());
                            cityType.setIcon(new ImageIcon(getClass().getClassLoader().getResource(LocationTypes.CITY.getIcon())));
                            typeMenu.add(cityType);

                            JMenuItem addType = new JMenuItem();
                            addType.setText("Add new type");
                            typeMenu.add(addType);

                            JMenuItem removeType = new JMenuItem();
                            removeType.setText("Remove type");
                            if (extraTypes.getTypes().size() != 0) {
                                typeMenu.add(removeType);
                            }

                            pop.add(typeMenu);

                            pop.getRemoveMenuItem().setAction(new AbstractAction("Remove", pop.getRemoveMenuItem().getIcon()) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (node.getUserObject() instanceof Location) {
                                        Object[] options = {"Continue",
                                                "Cancel"};
                                        int confirmation = JOptionPane.showOptionDialog(null,
                                                "Are you sure you want to remove this location? \n"
                                                        + "This action cannot be undone.",
                                                "Warning",
                                                JOptionPane.YES_NO_CANCEL_OPTION,
                                                JOptionPane.WARNING_MESSAGE,
                                                null,
                                                options,
                                                options[0]);

                                        if (confirmation == 0) {
                                            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                                            model.removeNodeFromParent(node);
                                            Location location = (Location) node.getUserObject();
                                            File directory = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName());
                                            File statesDirectory = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\LocationStates");
                                            File areasDirectory = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas");
                                            File[] statesDirectoryListing = statesDirectory.listFiles();
                                            File[] areasDirectoryListing = areasDirectory.listFiles();
                                            for (File file : statesDirectoryListing) {
                                                file.delete();
                                            }
                                            statesDirectory.delete();

                                            for (File folder : areasDirectoryListing) {
                                                File[] areaStates = folder.listFiles();
                                                for (File file : areaStates) {
                                                    file.delete();
                                                }
                                                folder.delete();
                                            }
                                            areasDirectory.delete();

                                            directory.delete();
                                        }
                                    }

                                    else {
                                        Object[] options = {"Continue",
                                                "Cancel"};
                                        int confirmation = JOptionPane.showOptionDialog(null,
                                                "Are you sure you want to remove this area? \n"
                                                        + "This action cannot be undone.",
                                                "Warning",
                                                JOptionPane.YES_NO_CANCEL_OPTION,
                                                JOptionPane.WARNING_MESSAGE,
                                                null,
                                                options,
                                                options[0]);

                                        if (confirmation == 0) {
                                            Area area = (Area) node.getUserObject();
                                            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
                                            Location location = (Location) parentNode.getUserObject();

                                            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                                            model.removeNodeFromParent(node);

                                            File areaDirectory = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas\\" + area.getId() + "_" + area.getType() + "_" + area.getName());
                                            File[] areaDirectoryListing = areaDirectory.listFiles();
                                            for (File file : areaDirectoryListing) {
                                                file.delete();
                                            }
                                            areaDirectory.delete();
                                            location.getListOfAreas().remove(area);
                                            location.getListOfAreaIDs().remove(location.getListOfAreaIDs().indexOf(area.getId()));
                                        }
                                    }
                                }
                            });

                            pop.getRenameMenuItem().setAction(new AbstractAction("Rename", pop.getRenameMenuItem().getIcon()) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (node.getUserObject() instanceof Location) {
                                        Location location = (Location) node.getUserObject();
                                        String newName = TextAndPopupFactory.createPopupWindow("What would you like to rename " + location.getName() + " to?");
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
                                                else {
                                                    File folder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName());
                                                    location.setName(newName);
                                                    File newFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + newName);
                                                    folder.renameTo(newFolder);
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
                                    else {
                                        Area area = (Area) node.getUserObject();
                                        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
                                        Location location = (Location) parentNode.getUserObject();
                                        String newName = TextAndPopupFactory.createPopupWindow("What would you like to rename " + area.getName() + " to?");
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
                                                else {
                                                    File folder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas\\" + area.getId() + "_" + area.getType() + "_" + area.getName());
                                                    area.setName(newName);
                                                    File newFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas\\" + area.getId() + "_" + area.getType() + "_" + newName);
                                                    folder.renameTo(newFolder);
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
                                }
                            });

                            landType.setAction(new AbstractAction(landType.getText(), landType.getIcon()) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    setType(typeMenu, landType);
                                }
                            });

                            villageType.setAction(new AbstractAction(villageType.getText(), villageType.getIcon()) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    setType(typeMenu, villageType);
                                }
                            });

                            cityType.setAction(new AbstractAction(cityType.getText(), cityType.getIcon()) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    setType(typeMenu, cityType);
                                }
                            });

                            if (extraTypes.getTypes().size() != 0) {
                                for (String name : extraTypes.getTypes()) {
                                    File file = new File(usersDir + "\\WritersBlock\\Location\\LocationExtraLeafIcons\\" + name + ".png");
                                    Icon imageIcon = new ImageIcon(file.getAbsolutePath());

                                    JMenuItem newType = new JMenuItem();
                                    newType.setText(name);
                                    newType.setIcon(imageIcon);

                                    typeMenu.remove(addType);
                                    typeMenu.remove(removeType);
                                    typeMenu.add(newType);

                                    if (name.equals(location.getType())) {
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
                                                File iconFile = new File(usersDir + "\\WritersBlock\\Location\\LocationExtraLeafIcons\\" + typeName + ".png");

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
                                                LocationDialog.savesExtraTypesJson(extraTypes);
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
                                    new LocationDialog(extraTypes);

                                    for (int i = 0; i < rootNode.getChildCount(); i++) {
                                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNode.getChildAt(i);
                                        if (node.getUserObject() instanceof Location) {
                                            Location location = (Location) node.getUserObject();
                                            File extraLeafIconsFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExtraLeafIcons");

                                            if (node.getChildCount() != 0) {
                                                for (int j = 0; j < node.getChildCount(); j++) {
                                                    DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(j);
                                                    Area area = (Area) childNode.getUserObject();
                                                    if (LocationTypes.isFixed(area.getType())) {
                                                        continue;
                                                    }
                                                    FilenameFilter newFilter = new FilenameFilter() {
                                                        @Override
                                                        public boolean accept(File dir, String name) {
                                                            return name.equals(area.getType() + ".png");
                                                        }
                                                    };
                                                    File[] areaTypeCheck = extraLeafIconsFolder.listFiles(newFilter);
                                                    if (areaTypeCheck == null || areaTypeCheck.length == 0) {
                                                        File oldFile = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas\\" + area.getId() + "_" + area.getType() + "_" + area.getName());
                                                        area.setType("Village");
                                                        File newFile = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas\\" + area.getId() + "_" + area.getType() + "_" + area.getName());
                                                        oldFile.renameTo(newFile);
                                                    }
//                                                    }
                                                }
                                            }

                                            if (LocationTypes.isFixed(location.getType())) {
                                                continue;
                                            }
                                            FilenameFilter filter = new FilenameFilter() {
                                                @Override
                                                public boolean accept(File dir, String name) {
                                                    return name.equals(location.getType() + ".png");
                                                }
                                            };
                                            File[] typeCheck = extraLeafIconsFolder.listFiles(filter);
                                            if (typeCheck == null || typeCheck.length == 0) {
                                                File oldFile = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName());
                                                location.setType("Land");
                                                File newFile = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName());
                                                oldFile.renameTo(newFile);
                                            }
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
                if (treeNode.getUserObject() instanceof Location) {
                    Location location = (Location) treeNode.getUserObject();
                    label.setText(location.getName());

                    if (LocationTypes.LAND.getTitle().equals(location.getType())) {
                        setIcon(new ImageIcon(getClass().getClassLoader().getResource(LocationTypes.LAND.getIcon())));
                    }
                    else if (LocationTypes.VILLAGE.getTitle().equals(location.getType())) {
                        setIcon(new ImageIcon(getClass().getClassLoader().getResource(LocationTypes.VILLAGE.getIcon())));
                    }
                    else if (LocationTypes.CITY.getTitle().equals(location.getType())) {
                        setIcon(new ImageIcon(getClass().getClassLoader().getResource(LocationTypes.CITY.getIcon())));
                    }
                    else if (extraTypes.getTypes().size() != 0) {
                        for (String type : extraTypes.getTypes()) {
                            if (type.equals(location.getType())) {
                                File file = new File(usersDir + "\\WritersBlock\\Location\\LocationExtraLeafIcons\\" + type + ".png");
                                Icon imageIcon = new ImageIcon(file.getAbsolutePath());
                                setIcon(imageIcon);
                                break;
                            }
                            else {
                                setIcon(new ImageIcon(getClass().getClassLoader().getResource(LocationTypes.LAND.getIcon())));
                            }
                        }
                    }
                }

                else if (treeNode.getUserObject() instanceof Area) {
                    Area area = (Area) treeNode.getUserObject();
                    label.setText(area.getName());

                    if (LocationTypes.LAND.getTitle().equals(area.getType())) {
                        setIcon(new ImageIcon(getClass().getClassLoader().getResource(LocationTypes.LAND.getIcon())));
                    }
                    else if (LocationTypes.VILLAGE.getTitle().equals(area.getType())) {
                        setIcon(new ImageIcon(getClass().getClassLoader().getResource(LocationTypes.VILLAGE.getIcon())));
                    }
                    else if (LocationTypes.CITY.getTitle().equals(area.getType())) {
                        setIcon(new ImageIcon(getClass().getClassLoader().getResource(LocationTypes.CITY.getIcon())));
                    }
                    else if (extraTypes.getTypes().size() != 0) {
                        for (String type : extraTypes.getTypes()) {
                            if (type.equals(area.getType())) {
                                File file = new File(usersDir + "\\WritersBlock\\Location\\LocationExtraLeafIcons\\" + type + ".png");
                                Icon imageIcon = new ImageIcon(file.getAbsolutePath());
                                setIcon(imageIcon);
                                break;
                            }
                            else {
                                setIcon(new ImageIcon(getClass().getClassLoader().getResource(LocationTypes.VILLAGE.getIcon())));
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

    public OvalButton getAddLocationButton() {
        return addLocationButton;
    }

    public DefaultMutableTreeNode getRootNode() {
        return rootNode;
    }

    public ExtraLocationTypes getExtraTypes() {
        return extraTypes;
    }

    public void setExtraTypes(ExtraLocationTypes extraTypes) {
        this.extraTypes = extraTypes;
    }

    public DefaultMutableTreeNode getNode() {
        return node;
    }

    public JMenuItem getAddArea() {
        return addArea;
    }

    public void setType(JMenu typeMenu, JMenuItem type) {
        if (node.getUserObject() instanceof Location) {
            Location location = (Location) node.getUserObject();
            File folder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName());
            location.setType(type.getText());
            typeMenu.setIcon(type.getIcon());
            File newFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName());
            folder.renameTo(newFolder);
            tree.updateUI();
        }
        else {
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
            Location location = (Location) parentNode.getUserObject();
            Area area = (Area) node.getUserObject();
            File folder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas\\" + area.getId() + "_" + area.getType() + "_" + area.getName());
            area.setType(type.getText());
            typeMenu.setIcon(type.getIcon());
            File newFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas\\" + area.getId() + "_" + area.getType() + "_" + area.getName());
            folder.renameTo(newFolder);
            tree.updateUI();
        }
    }
}
