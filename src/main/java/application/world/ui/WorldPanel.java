package application.world.ui;

import application.shared.domain.CheckChars;
import application.shared.ui.CharacteristicField;
import application.shared.domain.Characteristic;
import application.shared.domain.Section;
import application.factory.TextAndPopupFactory;
import application.shared.ui.SectionComponents;
import application.world.domain.Area;
import application.world.domain.ExtraLocationTypes;
import application.world.domain.Location;
import application.world.domain.LocationState;
import application.world.ui.layoutapplier.DefaultLayoutApplier;
import application.world.ui.layoutapplier.LayoutApplier;
import application.world.ui.layoutapplier.NoLayoutApplier;
import application.world.ui.layoutapplier.ThisLayoutApplier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
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
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class WorldPanel extends JPanel {
    private JSplitPane splitPane;
    private WorldPanelLeftComponent worldPanelLeftComponent;
    private JPanel plainPanel;
    private List<String> listOfTabNames = new ArrayList<>();
    private JTabbedPane tabbedPane;
    private List<FeaturesPanel> listOfFeaturesPanels = new ArrayList<>();
    private List<LocationState> listOfStates = new ArrayList<>();
    private List<Integer> stateIDs = new ArrayList<>();
    private int areaId = 0;

    private final String usersDir = System.getProperty("user.home");

    public WorldPanel() {
        setLayout(new MigLayout("insets 0", "grow", "grow"));
        setBackground(Color.MAGENTA);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setTabPlacement(JTabbedPane.TOP);

        worldPanelLeftComponent = new WorldPanelLeftComponent();
        plainPanel = new JPanel();
        splitPane = new JSplitPane();

        Border featuresPanelBorder = BorderFactory.createLineBorder(Color.MAGENTA);
        setBorder(BorderFactory.createCompoundBorder(featuresPanelBorder,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        setSplitPaneDividerColor(splitPane, Color.MAGENTA);
        add(splitPane, "grow");
        JScrollPane l = new JScrollPane(worldPanelLeftComponent);
        l.setMinimumSize(new Dimension(170,0));
        splitPane.setLeftComponent(l);
        splitPane.setRightComponent(new JScrollPane(tabbedPane));

        worldPanelLeftComponent.getTree().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) worldPanelLeftComponent.getTree().getLastSelectedPathComponent();
                if (node == worldPanelLeftComponent.getTree().getModel().getRoot()) {
                    splitPane.setRightComponent(plainPanel);
                }
                else if (node != null) {
                    if (node.getUserObject() instanceof Location) {
                        Location nodeLocation = (Location) node.getUserObject();
                        printCharacteristicsDetails(nodeLocation);

                        listOfStates.clear();
                        listOfStates.addAll(nodeLocation.getListOfStates());

                        listOfFeaturesPanels.clear();
                        listOfFeaturesPanels.addAll(nodeLocation.getListOfFeaturesPanels());

                        listOfTabNames.clear();
                        listOfTabNames.addAll(nodeLocation.getListOfTabNames());

                        stateIDs.clear();
                        stateIDs.addAll(nodeLocation.getListOfIDs());

                        splitPane.setRightComponent(new JScrollPane(tabbedPane));
                    }

                    else if (node.getUserObject() instanceof Area) {
                        Area nodeArea = (Area) node.getUserObject();
                        printCharacteristicsDetails(nodeArea);

                        listOfStates.clear();
                        listOfStates.addAll(nodeArea.getListOfStates());

                        listOfFeaturesPanels.clear();
                        listOfFeaturesPanels.addAll(nodeArea.getListOfFeaturesPanels());

                        listOfTabNames.clear();
                        listOfTabNames.addAll(nodeArea.getListOfTabNames());

                        stateIDs.clear();
                        stateIDs.addAll(nodeArea.getListOfIDs());

                        splitPane.setRightComponent(new JScrollPane(tabbedPane));
                    }
                }
                else {
                    splitPane.setRightComponent(plainPanel);
                }
            }
        });

        File directory = new File(usersDir + "\\WritersBlock\\Location\\LocationExports");
        if (!directory.exists()) {
            directory.mkdir();
        }

        File[] directoryListing = directory.listFiles();
        ObjectMapper mapper = new ObjectMapper();
        if (directoryListing != null && directoryListing.length != 0) {
            List<Location> listOfLocations = new ArrayList<>();
            for (File file : directoryListing) {
                Location location = new Location();
                listOfLocations.add(location);
                File dir = new File(file.getAbsolutePath());
                String[] parts = dir.getName().split("_");
                location.setId(Integer.parseInt(parts[0]));
                location.setType(parts[1]);
                location.setName(parts[2]);

                File statesFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\LocationStates");
                File areasFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas");

                if (!statesFolder.exists()) {
                    statesFolder.mkdir();
                }
                if (!areasFolder.exists()) {
                    areasFolder.mkdir();
                }

                File[] dirListing = statesFolder.listFiles();
                if (dirListing != null && dirListing.length != 0) {
                    List<LocationState> states = new ArrayList<>();
                    for (File f : dirListing) {
                        try {
                            LocationState savedLocationState = mapper.readValue(f, LocationState.class);

                            int underscoreIndex = f.getName().indexOf("_");
                            savedLocationState.setName(f.getName().substring(underscoreIndex+1, f.getName().length()-5));
                            savedLocationState.setId(Integer.parseInt(f.getName().substring(0, underscoreIndex)));

                            states.add(savedLocationState);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Collections.sort(states, new StateIdComparator());
                    location.getListOfStates().addAll(states);

                    for (LocationState state : location.getListOfStates()) {
                        FeaturesPanel featuresPanel = new FeaturesPanel(WorldPanel.this);
                        location.getListOfTabNames().add(state.getName());
                        location.getListOfIDs().add(state.getId());
                        location.getListOfFeaturesPanels().add(featuresPanel);
                    }
                }

                else {
                    LocationState defaultState = new LocationState();
                    defaultState.setId(0);
                    defaultState.setName("Default");
                    new DefaultLayoutApplier().apply(defaultState);

                    FeaturesPanel newFeaturesPanel = new FeaturesPanel(WorldPanel.this);

                    location.getListOfIDs().add(defaultState.getId());
                    location.getListOfStates().add(defaultState);
                    location.getListOfTabNames().add(defaultState.getName());
                    location.getListOfFeaturesPanels().add(newFeaturesPanel);

                    defaultStateCreation(location);
                }

                File[] areasListing = areasFolder.listFiles();
                if (areasListing != null && areasListing.length != 0) {
                    List<Area> listOfAreas = new ArrayList<>();
                    for (File f : areasListing) {
                        Area area = new Area();
                        listOfAreas.add(area);

                        File path = new File(f.getAbsolutePath());
                        String[] areaParts = path.getName().split("_");
                        area.setId(Integer.parseInt(areaParts[0]));
                        area.setType(areaParts[1]);
                        area.setName(areaParts[2]);

                        File areaStatesFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas\\" + area.getId() + "_" + area.getType() + "_" + area.getName());

                        File[] areaStatesListing = areaStatesFolder.listFiles();
                        if (areaStatesListing != null && areaStatesListing.length != 0) {
                            List<LocationState> areaStates = new ArrayList<>();
                            for (File g : areaStatesListing) {
                                try {
                                    LocationState savedAreaState = mapper.readValue(g, LocationState.class);

                                    int underscoreIndex = g.getName().indexOf("_");
                                    savedAreaState.setName(g.getName().substring(underscoreIndex+1, g.getName().length()-5));
                                    savedAreaState.setId(Integer.parseInt(g.getName().substring(0, underscoreIndex)));

                                    areaStates.add(savedAreaState);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            Collections.sort(areaStates, new StateIdComparator());
                            area.getListOfStates().addAll(areaStates);

                            for (LocationState state : area.getListOfStates()) {
                                FeaturesPanel featuresPanel = new FeaturesPanel(WorldPanel.this);
                                area.getListOfTabNames().add(state.getName());
                                area.getListOfIDs().add(state.getId());
                                area.getListOfFeaturesPanels().add(featuresPanel);
                            }
                        }
                        else {
                            LocationState defaultState = new LocationState();
                            defaultState.setId(0);
                            defaultState.setName("Default");
                            new NoLayoutApplier().apply(defaultState);

                            FeaturesPanel newFeaturesPanel = new FeaturesPanel(WorldPanel.this);

                            area.getListOfIDs().add(defaultState.getId());
                            area.getListOfStates().add(defaultState);
                            area.getListOfTabNames().add(defaultState.getName());
                            area.getListOfFeaturesPanels().add(newFeaturesPanel);

                            defaultAreaStateCreation(area, location);
                        }
                    }

                    Collections.sort(listOfAreas, new AreaIdComparator());
                    for (Area a : listOfAreas) {
                        location.getListOfAreas().add(a);
                        location.getListOfAreaIDs().add(a.getId());
                    }
                }
            }
            Collections.sort(listOfLocations, new LocationIdComparator());
            for (Location lc : listOfLocations) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(lc);
                worldPanelLeftComponent.getRootNode().add(node);

                for (Area area : lc.getListOfAreas()) {
                    DefaultMutableTreeNode areaNode = new DefaultMutableTreeNode(area);
                    node.add(areaNode);
                }
            }
        }
        else {
            Location defaultLocation = new Location();
            defaultLocation.setId(0);
            defaultLocation.setName("New location");
            defaultLocation.setType("Land");

            LocationState defaultLocationState = new LocationState();
            defaultLocationState.setId(0);
            defaultLocationState.setName("Default");
            new DefaultLayoutApplier().apply(defaultLocationState);

            FeaturesPanel newFeaturesPanel = new FeaturesPanel(WorldPanel.this);
            newFeaturesPanel.getNameText().setText(defaultLocationState.getName());

            defaultLocation.getListOfIDs().add(defaultLocationState.getId());
            defaultLocation.getListOfTabNames().add(defaultLocationState.getName());
            defaultLocation.getListOfStates().add(defaultLocationState);
            defaultLocation.getListOfFeaturesPanels().add(newFeaturesPanel);

            listOfFeaturesPanels.add(newFeaturesPanel);
            tabbedPane.add(defaultLocationState.getName(), newFeaturesPanel);
            tabTitle(defaultLocationState.getName());

            DefaultMutableTreeNode defaultNode = new DefaultMutableTreeNode(defaultLocation);
            worldPanelLeftComponent.getRootNode().add(defaultNode);

            File defaultFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\0_Land_New location");
            defaultFolder.mkdir();
            File defaultStatesFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\0_Land_New location\\LocationStates");
            defaultStatesFolder.mkdir();
            File defaultAreasFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\0_Land_New location\\Areas");
            defaultAreasFolder.mkdir();

            defaultStateCreation(defaultLocation);
        }

        File extraTypes = new File(usersDir + "\\WritersBlock\\Location\\LocationExtraTypes\\ExtraTypes.json");
        if (extraTypes.exists()) {
            try {
                ExtraLocationTypes extraTypesFiles = mapper.readValue(extraTypes, ExtraLocationTypes.class);
                worldPanelLeftComponent.getExtraTypes().getTypes().addAll(extraTypesFiles.getTypes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                extraTypes.createNewFile();
                ExtraLocationTypes types = new ExtraLocationTypes();
                LocationDialog.savesExtraTypesJson(types);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        worldPanelLeftComponent.getTree().setSelectionPath(new TreePath(((DefaultMutableTreeNode) worldPanelLeftComponent.getTree().getModel().getChild(worldPanelLeftComponent.getTree().getModel().getRoot(), 0)).getPath()));

        worldPanelLeftComponent.getAddLocationButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem defaultLayout = new JMenuItem("Default Layout");
                JMenuItem noLayout = new JMenuItem("Plain");

                popupMenu.add(defaultLayout);
                popupMenu.add(noLayout);
                popupMenu.show(worldPanelLeftComponent.getAddLocationButton(), worldPanelLeftComponent.getAddLocationButton().getX(), worldPanelLeftComponent.getAddLocationButton().getY());
//                popupMenu.setLocation(140, 80);
                popupMenu.setLocation(135, 80);

                defaultLayout.setAction(new AbstractAction("Default Layout") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Location newLocation = new Location();
                        newLocation.setType("Land");
                        addLocation(newLocation);

                        if (newLocation.getListOfStates().size() > 0) {
                            new DefaultLayoutApplier().apply(newLocation.getListOfStates().get(0));
                            defaultStateCreation(newLocation);
                        }
                    }
                });

                noLayout.setAction(new AbstractAction("Plain") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Location newLocation = new Location();
                        newLocation.setType("Land");
                        addLocation(newLocation);

                        if (newLocation.getListOfStates().size() > 0) {
                            newLocation.getListOfStates().get(0).setGalleryEnabled(false);
                            new NoLayoutApplier().apply(newLocation.getListOfStates().get(0));
                            defaultStateCreation(newLocation);
                        }
                    }
                });
            }
        });

        worldPanelLeftComponent.getAddArea().setAction(new AbstractAction(worldPanelLeftComponent.getAddArea().getText(), worldPanelLeftComponent.getAddArea().getIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Area newArea = new Area();
                DefaultMutableTreeNode selectedNode = worldPanelLeftComponent.getNode();
                addArea(newArea, selectedNode);

                if (newArea.getListOfStates().size() > 0) {
                    newArea.getListOfStates().get(0).setGalleryEnabled(false);
                    new NoLayoutApplier().apply(newArea.getListOfStates().get(0));
                    defaultAreaStateCreation(newArea, (Location) selectedNode.getUserObject());
                }
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
    class LocationIdComparator implements java.util.Comparator<Location> {
        @Override
        public int compare(Location a, Location b) {
            return a.getId() - b.getId();
        }
    }

    class AreaIdComparator implements java.util.Comparator<Area> {
        @Override
        public int compare(Area a, Area b) {
            return a.getId() - b.getId();
        }
    }

    class StateIdComparator implements java.util.Comparator<LocationState> {
        @Override
        public int compare(LocationState a, LocationState b) {
            return a.getId() - b.getId();
        }
    }

    public void saveAreaCharacteristicsDetails(Location location, Area area) {
        for (LocationState state : listOfStates) {
            int stateIndex = listOfStates.indexOf(state);
            FeaturesPanel featuresPanelAtStateIndex = listOfFeaturesPanels.get(stateIndex);

            boolean illegal = CheckChars.checkChars(featuresPanelAtStateIndex.getNameText().getText());
            int spaceCount = CheckChars.checkSpaceChars(featuresPanelAtStateIndex.getNameText().getText());
            if (illegal) {
                JOptionPane.showMessageDialog(null,
                        "Sorry! A character name can't contain any of the following characters: \n \\ / : * ? \" < > |",
                        "Name error",
                        JOptionPane.ERROR_MESSAGE);
            }
            else if (spaceCount == featuresPanelAtStateIndex.getNameText().getText().length()) {
                JOptionPane.showMessageDialog(null,
                        "Sorry! A blank space is not a name!",
                        "Name error",
                        JOptionPane.ERROR_MESSAGE);
            }
            else {
                state.setName(featuresPanelAtStateIndex.getNameText().getText());
                TabPanel tab = (TabPanel) tabbedPane.getTabComponentAt(stateIndex);
                tab.getLabel().setText(state.getName());
                listOfTabNames.set(stateIndex, state.getName());

                state.getListOfFeatures().clear();
                for (CharacteristicField item : featuresPanelAtStateIndex.getFixedPanel().getListOfCharacteristicFields()) {
                    Characteristic characteristic = new Characteristic();
                    String characteristicName = item.getLabel().getText().substring(0, item.getLabel().getText().length() - 1);
                    characteristic.setCharacteristicTitle(characteristicName);
                    characteristic.setCharacteristicValue(item.getValue().getText());
                    state.getListOfFeatures().add(characteristic);
                }
                state.getListOfSections().clear();
                for (SectionComponents sectionComponents : featuresPanelAtStateIndex.getListOfSectionComponents()) {
                    Section newSection = new Section();
                    String sectionName = sectionComponents.getTitle().getTitle().getText().substring(0, sectionComponents.getTitle().getTitle().getText().length() - 1);
                    newSection.setName(sectionName);
                    state.getListOfSections().add(newSection);
                    for (CharacteristicField item : sectionComponents.getCharacteristics().getListOfCharacteristicFields()) {
                        Characteristic characteristic = new Characteristic();
                        String characteristicName = item.getLabel().getText().substring(0, item.getLabel().getText().length() - 1);
                        characteristic.setCharacteristicTitle(characteristicName);
                        characteristic.setCharacteristicValue(item.getValue().getText());
                        newSection.getListOfCharacteristics().add(characteristic);
                    }
                }
                state.getListOfImageStrings().clear();
                state.getListOfImageTitles().clear();
                state.getListOfImageStrings().addAll(featuresPanelAtStateIndex.getImageStrings());
                state.getListOfImageTitles().addAll(featuresPanelAtStateIndex.getListOfImageTitles());

                state.setGalleryEnabled(featuresPanelAtStateIndex.getEnableGallery().isSelected());
                state.setDescription(featuresPanelAtStateIndex.getGallery().getDescription().getText());

                ObjectMapper mapper = new ObjectMapper();

                File exports = new File(usersDir + "\\WritersBlock\\Location\\LocationExports");

                File locationFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName());

                File areasFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas");
                File statesFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\LocationStates");
                File areaStatesFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas\\" + area.getId() + "_" + area.getType() + "_" + area.getName());

                FilenameFilter fileFilter = new FilenameFilter() {
                    public boolean accept(File g, String name) {
                        return name.startsWith(String.valueOf(location.getId() + "_"));
                    }
                };
                File[] fileName = exports.listFiles(fileFilter);
                if (fileName != null && fileName.length != 0) {
                    fileName[0].renameTo(locationFolder);
                } else {
                    locationFolder.mkdir();
                    areasFolder.mkdir();
                    statesFolder.mkdir();
                    areaStatesFolder.mkdir();
                }

                if (!statesFolder.exists()) {
                    statesFolder.mkdir();
                }

                if (!areasFolder.exists()) {
                    areasFolder.mkdir();
                }

                if (!areaStatesFolder.exists()) {
                    areaStatesFolder.mkdir();
                }

                try {
                    String json = mapper.writeValueAsString(state);
                    File file = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas\\" + area.getId() + "_" + area.getType() + "_" + area.getName() + "\\" + state.getId() + "_" + state.getName() + ".json");

                    FilenameFilter filter = new FilenameFilter() {
                        public boolean accept(File f, String name) {
                            return name.startsWith(String.valueOf(state.getId() + "_"));
                        }
                    };

                    File[] f = areaStatesFolder.listFiles(filter);
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
        area.getListOfStates().clear();
        area.getListOfFeaturesPanels().clear();
        area.getListOfTabNames().clear();
        area.getListOfIDs().clear();
        area.getListOfStates().addAll(listOfStates);
        area.getListOfFeaturesPanels().addAll(listOfFeaturesPanels);
        area.getListOfTabNames().addAll(listOfTabNames);
        area.getListOfIDs().addAll(stateIDs);
    }

    public void saveCharacteristicsDetails(Area location) {
        for (LocationState state : listOfStates) {
            int stateIndex = listOfStates.indexOf(state);
            FeaturesPanel featuresPanelAtStateIndex = listOfFeaturesPanels.get(stateIndex);

            boolean illegal = CheckChars.checkChars(featuresPanelAtStateIndex.getNameText().getText());
            int spaceCount = CheckChars.checkSpaceChars(featuresPanelAtStateIndex.getNameText().getText());
            if (illegal) {
                JOptionPane.showMessageDialog(null,
                        "Sorry! A character name can't contain any of the following characters: \n \\ / : * ? \" < > |",
                        "Name error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (spaceCount == featuresPanelAtStateIndex.getNameText().getText().length()) {
                JOptionPane.showMessageDialog(null,
                        "Sorry! A blank space is not a name!",
                        "Name error",
                        JOptionPane.ERROR_MESSAGE);
            }
            else {
                state.setName(featuresPanelAtStateIndex.getNameText().getText());
                TabPanel tab = (TabPanel) tabbedPane.getTabComponentAt(stateIndex);
                tab.getLabel().setText(state.getName());
                listOfTabNames.set(stateIndex, state.getName());

                state.getListOfFeatures().clear();
                for (CharacteristicField item : featuresPanelAtStateIndex.getFixedPanel().getListOfCharacteristicFields()) {
                    Characteristic characteristic = new Characteristic();
                    String characteristicName = item.getLabel().getText().substring(0, item.getLabel().getText().length() - 1);
                    characteristic.setCharacteristicTitle(characteristicName);
                    characteristic.setCharacteristicValue(item.getValue().getText());
                    state.getListOfFeatures().add(characteristic);
                }
                state.getListOfSections().clear();
                for (SectionComponents sectionComponents : featuresPanelAtStateIndex.getListOfSectionComponents()) {
                    Section newSection = new Section();
                    String sectionName = sectionComponents.getTitle().getTitle().getText().substring(0, sectionComponents.getTitle().getTitle().getText().length() - 1);
                    newSection.setName(sectionName);
                    state.getListOfSections().add(newSection);
                    for (CharacteristicField item : sectionComponents.getCharacteristics().getListOfCharacteristicFields()) {
                        Characteristic characteristic = new Characteristic();
                        String characteristicName = item.getLabel().getText().substring(0, item.getLabel().getText().length() - 1);
                        characteristic.setCharacteristicTitle(characteristicName);
                        characteristic.setCharacteristicValue(item.getValue().getText());
                        newSection.getListOfCharacteristics().add(characteristic);
                    }
                }
                state.getListOfImageStrings().clear();
                state.getListOfImageTitles().clear();
                state.getListOfImageStrings().addAll(featuresPanelAtStateIndex.getImageStrings());
                state.getListOfImageTitles().addAll(featuresPanelAtStateIndex.getListOfImageTitles());

                state.setGalleryEnabled(featuresPanelAtStateIndex.getEnableGallery().isSelected());
                state.setDescription(featuresPanelAtStateIndex.getGallery().getDescription().getText());

                ObjectMapper mapper = new ObjectMapper();

                File exports = new File(usersDir + "\\WritersBlock\\Location\\LocationExports");

                File locationFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName());

                File areasFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas");
                File statesFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\LocationStates");

                FilenameFilter fileFilter = new FilenameFilter() {
                    public boolean accept(File g, String name) {
                        return name.startsWith(String.valueOf(location.getId() + "_"));
                    }
                };
                File[] fileName = exports.listFiles(fileFilter);
                if (fileName != null && fileName.length != 0) {
                    fileName[0].renameTo(locationFolder);
                } else {
                    locationFolder.mkdir();
                    areasFolder.mkdir();
                    statesFolder.mkdir();
                }

                if (!statesFolder.exists()) {
                    statesFolder.mkdir();
                }

                if (!areasFolder.exists()) {
                    areasFolder.mkdir();
                }

                try {
                    String json = mapper.writeValueAsString(state);
                    File file = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\LocationStates\\" + state.getId() + "_" + state.getName() + ".json");
                    FilenameFilter filter = new FilenameFilter() {
                        public boolean accept(File f, String name) {
                            return name.startsWith(String.valueOf(state.getId() + "_"));
                        }
                    };

                    File[] f = statesFolder.listFiles(filter);
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
        location.getListOfStates().clear();
        location.getListOfFeaturesPanels().clear();
        location.getListOfTabNames().clear();
        location.getListOfIDs().clear();
        location.getListOfStates().addAll(listOfStates);
        location.getListOfFeaturesPanels().addAll(listOfFeaturesPanels);
        location.getListOfTabNames().addAll(listOfTabNames);
        location.getListOfIDs().addAll(stateIDs);
    }

    public void printCharacteristicsDetails(Area area) {

        for (FeaturesPanel fp : area.getListOfFeaturesPanels()) {
            fp.getNameText().setText("");
            for (CharacteristicField item : fp.getFixedPanel().getListOfCharacteristicFields()) {
                fp.getFixedPanel().remove(item.getLabel());
                fp.getFixedPanel().remove(item.getValue());
            }
            fp.getFixedPanel().getListOfCharacteristicFields().clear();
            fp.getFixedPanel().getListOfNames().clear();
            for (SectionComponents sectionComponents : fp.getListOfSectionComponents()) {
                fp.getWorldDetailsPanel().remove(sectionComponents.getTitle());
                fp.getWorldDetailsPanel().remove(sectionComponents.getCharacteristics());
            }
            fp.getListOfSectionComponents().clear();
            fp.getListOfSectionNames().clear();

            fp.getGallery().getSelectedLabel().setIcon(null);
            fp.getGallery().getTitle().setText("");

            fp.getListOfImageTitles().clear();
            fp.getImageStrings().clear();
            fp.getImages().clear();
        }
        listOfFeaturesPanels.clear();
        listOfStates.clear();
        listOfTabNames.clear();
        stateIDs.clear();

        if (tabbedPane.getTabCount() != 0) {
            while (tabbedPane.getTabCount() > 0) {
                tabbedPane.removeAll();
            }
        }

        for (FeaturesPanel fp : area.getListOfFeaturesPanels()) {
            tabbedPane.add(area.getListOfTabNames().get(area.getListOfFeaturesPanels().indexOf(fp)), fp);
            tabTitle(area.getListOfTabNames().get(area.getListOfFeaturesPanels().indexOf(fp)));
        }

        for (LocationState state : area.getListOfStates()) {
            int stateIndex = area.getListOfStates().indexOf(state);
            FeaturesPanel featuresPanelAtStateIndex = area.getListOfFeaturesPanels().get(stateIndex);
            loadFeaturesPanel(featuresPanelAtStateIndex, state);
        }
        disableCloseTabButton();
    }

    public void save() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) worldPanelLeftComponent.getTree().getLastSelectedPathComponent();
        if (node.getUserObject() instanceof Location) {
            Location location = (Location) node.getUserObject();
            saveCharacteristicsDetails(location);
        }
        else {
            Area area = (Area) node.getUserObject();
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
            Location location = (Location) parentNode.getUserObject();
            saveAreaCharacteristicsDetails(location, area);
        }
        worldPanelLeftComponent.getTree().updateUI();
        worldPanelLeftComponent.getAddLocationButton().repaint();
        SwingUtilities.invokeLater(() -> worldPanelLeftComponent.getAddLocationButton().repaint());
    }

    public void addTab() {
        String newTabName = TextAndPopupFactory.createPopupWindow("What would you like to name this tab?");
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
                else if (preexists) {
                    JOptionPane.showMessageDialog(null,
                            "Sorry! It looks like you already have a tab with this name!",
                            "Name error",
                            JOptionPane.ERROR_MESSAGE);
                }
                else {
                    Object[] options = {"No layout",
                            "Default layout", "This layout"};
                    int confirmation = JOptionPane.showOptionDialog(null,
                            "Which layout would you like for this tab?",
                            "Tab layout",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]);

                    if (confirmation == 0) {
                        addNewTab(newTabName, new NoLayoutApplier());
                    }
                    else if (confirmation == 1) {
                        addNewTab(newTabName, new DefaultLayoutApplier());
                    }
                    else if (confirmation == 2) {
                        addNewTab(newTabName, new ThisLayoutApplier(listOfFeaturesPanels.get(tabbedPane.getSelectedIndex())));
                    }
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

    public void addLocation(Location location) {
        String locationName = TextAndPopupFactory.createPopupWindow("What would you like to name this location?");
        if (locationName != null) {
            boolean illegal = CheckChars.checkChars(locationName);
            int spaceCount = CheckChars.checkSpaceChars(locationName);

            if (illegal) {
                JOptionPane.showMessageDialog(null,
                        "Sorry! A file name can't contain any of the following characters: \n \\ / : * ? \" < > |",
                        "Name error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (spaceCount == locationName.length()) {
                JOptionPane.showMessageDialog(null,
                        "Sorry! A blank space is not a title!",
                        "Name error",
                        JOptionPane.ERROR_MESSAGE);
            }
            else {
                if (!locationName.equals("")) {
                    String firstTabName = "Default";
                    int locationId = 0;
                    if (worldPanelLeftComponent.getRootNode().getChildCount() != 0) {
                        DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) worldPanelLeftComponent.getTree().getModel().getChild(worldPanelLeftComponent.getRootNode(), worldPanelLeftComponent.getTree().getModel().getChildCount(worldPanelLeftComponent.getRootNode())-1);
                        Location lastLocation = (Location) lastNode.getUserObject();
                        locationId = lastLocation.getId() + 1;
                    }

                    LocationState locationState = new LocationState();
                    locationState.setId(0);
                    locationState.setName(firstTabName);

                    location.setId(locationId);
                    location.setName(locationName);

                    FeaturesPanel newFeaturesPanel = new FeaturesPanel(WorldPanel.this);
                    newFeaturesPanel.getNameText().setText("Default");

                    location.getListOfFeaturesPanels().add(newFeaturesPanel);
                    location.getListOfStates().add(locationState);
                    location.getListOfTabNames().add(firstTabName);
                    location.getListOfIDs().add(0);

                    DefaultMutableTreeNode newLocationNode = new DefaultMutableTreeNode(location);
                    ((DefaultTreeModel) worldPanelLeftComponent.getTree().getModel()).insertNodeInto(newLocationNode, worldPanelLeftComponent.getRootNode(), worldPanelLeftComponent.getRootNode().getChildCount());

                    File folderDirectory = new File(usersDir + "\\WritersBlock\\Location\\LocationExports");
                    File newFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName());
                    File newAreasFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas");
                    File newStatesFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\LocationStates");

                    FilenameFilter folderFilter = new FilenameFilter() {
                        @Override
                        public boolean accept(File p, String folder) {
                            return folder.startsWith(String.valueOf(location.getId() + "_"));
                        }
                    };
                    File[] p = folderDirectory.listFiles(folderFilter);
                    if (p != null && p.length != 0) {
                        p[0].renameTo(newFolder);
                    } else {
                        newFolder.mkdir();
                        newStatesFolder.mkdir();
                        newAreasFolder.mkdir();
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
        else {
            JOptionPane.showMessageDialog(null,
                    "Sorry! It looks like you didn't enter a name!",
                    "Name error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void tabTitle(String tabName) {
        TabPanel tabPanel = new TabPanel(tabName);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount()-1, tabPanel);

        tabPanel.getCloseTabButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                    DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) worldPanelLeftComponent.getTree().getLastSelectedPathComponent();

                    if (currentNode.getUserObject() instanceof Location) {
                        Location currentLocation = (Location) currentNode.getUserObject();
                        File directory = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + currentLocation.getId() + "_" + currentLocation.getType() + "_" + currentLocation.getName() + "\\LocationStates");
                        LocationState state = listOfStates.get(listOfTabNames.indexOf(tabName));
                        FilenameFilter filter = new FilenameFilter() {
                            @Override
                            public boolean accept(File f, String name) {
                                return name.startsWith(String.valueOf(state.getId() + "_"));
                            }
                        };
                        File[] directoryListing = directory.listFiles(filter);
                        if (directoryListing != null && directoryListing.length != 0) {
                            for (File file : directoryListing) {
                                file.delete();
                            }
                        }

                        int tabIndex = listOfTabNames.indexOf(tabName);
                        tabbedPane.remove(listOfTabNames.indexOf(tabName));
                        FeaturesPanel featuresPanel = listOfFeaturesPanels.get(tabIndex) ;
                        tabbedPane.remove(featuresPanel);
                        listOfStates.remove(tabIndex);
                        listOfFeaturesPanels.remove(tabIndex);
                        stateIDs.remove(stateIDs.get(tabIndex));
                        listOfTabNames.remove(tabName);

                        currentLocation.getListOfFeaturesPanels().remove(featuresPanel);
                        currentLocation.getListOfIDs().remove(currentLocation.getListOfIDs().get(tabIndex));
                        currentLocation.getListOfStates().remove(tabIndex);
                        currentLocation.getListOfTabNames().remove(tabName);
                    }

                    else {
                        Area currentArea = (Area) currentNode.getUserObject();
                        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) currentNode.getParent();
                        Location currentLocation = (Location) parentNode.getUserObject();
                        File directory = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + currentLocation.getId() + "_" + currentLocation.getType() + "_" + currentLocation.getName() + "\\Areas\\" + currentArea.getId() + "_" + currentArea.getType() + "_" + currentArea.getName());
                        LocationState state = listOfStates.get(listOfTabNames.indexOf(tabName));
                        FilenameFilter filter = new FilenameFilter() {
                            @Override
                            public boolean accept(File f, String name) {
                                return name.startsWith(String.valueOf(state.getId() + "_"));
                            }
                        };
                        File[] directoryListing = directory.listFiles(filter);
                        if (directoryListing != null && directoryListing.length != 0) {
                            for (File file : directoryListing) {
                                file.delete();
                            }
                        }
                        int tabIndex = listOfTabNames.indexOf(tabName);
                        tabbedPane.remove(listOfTabNames.indexOf(tabName));
                        FeaturesPanel featuresPanel = listOfFeaturesPanels.get(tabIndex) ;
                        tabbedPane.remove(featuresPanel);
                        listOfStates.remove(tabIndex);
                        listOfFeaturesPanels.remove(tabIndex);
                        stateIDs.remove(stateIDs.get(tabIndex));
                        listOfTabNames.remove(tabName);

                        currentArea.getListOfFeaturesPanels().remove(featuresPanel);
                        currentArea.getListOfIDs().remove(currentArea.getListOfIDs().get(tabIndex));
                        currentArea.getListOfStates().remove(tabIndex);
                        currentArea.getListOfTabNames().remove(tabName);
                    }
                    disableCloseTabButton();
                }
            }
        });
    }

    public void loadFeaturesPanel(FeaturesPanel featuresPanel, LocationState state) {
        featuresPanel.getNameText().setText(state.getName());

        for (Characteristic item : state.getListOfFeatures()) {
            CharacteristicField field = new CharacteristicField();
            field.getLabel().setText(item.getCharacteristicTitle() + ":");
            field.getValue().setText(item.getCharacteristicValue());
            featuresPanel.getFixedPanel().addCharacteristicToPanel(field);
        }
        for (Section section : state.getListOfSections()) {
            featuresPanel.addSectionComponents(section);
        }
        for (String imageString : state.getListOfImageStrings()) {
            featuresPanel.getImageStrings().add(imageString);

            byte[] decodedBytes = Base64.getDecoder().decode(imageString);
            BufferedImage someImage = null;
            try {
                someImage = ImageIO.read(new ByteArrayInputStream(decodedBytes));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ImageIcon icon = new ImageIcon(someImage);
            featuresPanel.getImages().add(icon);
        }
        for (String imageTitle : state.getListOfImageTitles()) {
            featuresPanel.getListOfImageTitles().add(imageTitle);
        }
        if (featuresPanel.getImages().size() > 0) {
            featuresPanel.displayFirstImage();
            featuresPanel.getPopClick().setDisabled(false);
        }
        else if (featuresPanel.getImages().size() == 0) {
            featuresPanel.getPopClick().setDisabled(true);
        }

        featuresPanel.getEnableGallery().setSelected(state.isGalleryEnabled());
        featuresPanel.getGallery().getDescription().setText(state.getDescription());
        featuresPanel.displayGallery();
    }

    public void defaultStateCreation(Location location) {
        ObjectMapper stateMapper = new ObjectMapper();
        try {
            String stateJson = stateMapper.writeValueAsString(location.getListOfStates().get(0));
            File defaultFile = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\LocationStates\\0_Default.json");
            defaultFile.createNewFile();
            FileWriter fileWriter = new FileWriter(defaultFile);
            fileWriter.write(stateJson);
            fileWriter.close();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void disableCloseTabButton() {
        TabPanel tab = (TabPanel) tabbedPane.getTabComponentAt(0);
        if (tabbedPane.getTabCount() == 1) {
            tab.remove(tab.getCloseTabButton());
        }
        else {
            tab.add(tab.getCloseTabButton());
        }
    }
    public void addNewTab(String newTabName, LayoutApplier layoutApplier) {
        int currentID = stateIDs.get(stateIDs.size()-1);
        int newID = currentID+1;
        FeaturesPanel newFeaturesPanel = new FeaturesPanel(WorldPanel.this);
        newFeaturesPanel.getNameText().setText(newTabName);

        tabbedPane.add(newTabName, newFeaturesPanel);
        tabTitle(newTabName);
        disableCloseTabButton();

        LocationState state = new LocationState();

        state.setName(newTabName);
        state.setId(newID);
        layoutApplier.apply(state);
        listOfStates.add(state);
        listOfFeaturesPanels.add(newFeaturesPanel);
        listOfTabNames.add(newTabName);
        stateIDs.add(newID);

        loadFeaturesPanel(newFeaturesPanel, state);
    }

    public void addArea(Area area, DefaultMutableTreeNode selectedNode) {
        String areaName = TextAndPopupFactory.createPopupWindow("What would you like to name this area?");
        if (areaName != null) {
            if (!areaName.equals("")) {
                boolean illegal = CheckChars.checkChars(areaName);
                int spaceCount = CheckChars.checkSpaceChars(areaName);

                if (illegal) {
                    JOptionPane.showMessageDialog(null,
                            "Sorry! A file name can't contain any of the following characters: \n \\ / : * ? \" < > |",
                            "Name error",
                            JOptionPane.ERROR_MESSAGE);
                } else if (spaceCount == areaName.length()) {
                    JOptionPane.showMessageDialog(null,
                            "Sorry! A blank space is not a title!",
                            "Name error",
                            JOptionPane.ERROR_MESSAGE);
                }
                else {
                    Location location = (Location) selectedNode.getUserObject();

                    area.setName(areaName);
                    area.setType("Village");
                    location.getListOfAreas().add(area);

                    String firstTabName = "Default";

                    LocationState areaState = new LocationState();
                    areaState.setId(0);
                    areaState.setName(firstTabName);

                    if (selectedNode.getChildCount() == 0) {
                        area.setId(0);
                        location.getListOfAreaIDs().add(0);
                    } else {
                        areaId = location.getListOfAreaIDs().get(location.getListOfAreaIDs().size() - 1) + 1;
                        area.setId(areaId);
                        location.getListOfAreaIDs().add(areaId);
                    }

                    FeaturesPanel newFeaturesPanel = new FeaturesPanel(WorldPanel.this);
                    newFeaturesPanel.getNameText().setText("Default");

                    area.getListOfFeaturesPanels().add(newFeaturesPanel);
                    area.getListOfStates().add(areaState);
                    area.getListOfTabNames().add(firstTabName);
                    area.getListOfIDs().add(0);

                    DefaultMutableTreeNode newAreaNode = new DefaultMutableTreeNode(area);

                    ((DefaultTreeModel) worldPanelLeftComponent.getTree().getModel()).insertNodeInto(newAreaNode, selectedNode, selectedNode.getChildCount());

                    File newAreaFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas" + "\\" + area.getId() + "_" + area.getType() + "_" + area.getName());
                    File areasFolder = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas");

                    FilenameFilter folderFilter = new FilenameFilter() {
                        @Override
                        public boolean accept(File p, String folder) {
                            return folder.startsWith(String.valueOf(area.getId() + "_"));
                        }
                    };
                    File[] p = areasFolder.listFiles(folderFilter);
                    if (p != null && p.length != 0) {
                        p[0].renameTo(newAreaFolder);
                    } else {
                        newAreaFolder.mkdir();
                    }
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

    public void defaultAreaStateCreation(Area area, Location location) {
        ObjectMapper stateMapper = new ObjectMapper();
        try {
            String stateJson = stateMapper.writeValueAsString(area.getListOfStates().get(0));
            File defaultFile = new File(usersDir + "\\WritersBlock\\Location\\LocationExports\\" + location.getId() + "_" + location.getType() + "_" + location.getName() + "\\Areas\\" + area.getId() + "_" + area.getType() + "_" + area.getName() + "\\0_Default.json");

            defaultFile.createNewFile();
            FileWriter fileWriter = new FileWriter(defaultFile);
            fileWriter.write(stateJson);
            fileWriter.close();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
