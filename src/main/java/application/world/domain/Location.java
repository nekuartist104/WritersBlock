package application.world.domain;

import application.world.ui.FeaturesPanel;

import java.util.ArrayList;
import java.util.List;

public class Location extends Area {

    private int id;
    private String name;
    private String type;
    private List<LocationState> listOfStates = new ArrayList<>();
    private List<Area> listOfAreas = new ArrayList<>();
    private List<Integer> listOfAreaIDs = new ArrayList<>();
    private List<String> listOfTabNames = new ArrayList<>();
    private List<FeaturesPanel> listOfFeaturesPanels = new ArrayList<>();
    private List<Integer> listOfIDs = new ArrayList<>();

    public List<Integer> getListOfIDs() {
        return listOfIDs;
    }

    public void setListOfIDs(List<Integer> listOfIDs) {
        this.listOfIDs = listOfIDs;
    }

    public List<FeaturesPanel> getListOfFeaturesPanels() {
        return listOfFeaturesPanels;
    }

    public void setListOfFeaturesPanels(List<FeaturesPanel> listOfFeaturesPanels) {
        this.listOfFeaturesPanels = listOfFeaturesPanels;
    }

    public List<String> getListOfTabNames() {
        return listOfTabNames;
    }

    public void setListOfTabNames(List<String> listOfTabNames) {
        this.listOfTabNames = listOfTabNames;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LocationState> getListOfStates() {
        return listOfStates;
    }

    public void setListOfStates(List<LocationState> listOfStates) {
        this.listOfStates = listOfStates;
    }

    public List<Area> getListOfAreas() {
        return listOfAreas;
    }

    public void setListOfAreas(List<Area> listOfAreas) {
        this.listOfAreas = listOfAreas;
    }

    public List<Integer> getListOfAreaIDs() {
        return listOfAreaIDs;
    }

    public void setListOfAreaIDs(List<Integer> listOfAreaIDs) {
        this.listOfAreaIDs = listOfAreaIDs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
