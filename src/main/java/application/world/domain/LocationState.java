package application.world.domain;

import application.shared.domain.Characteristic;
import application.shared.domain.Section;

import java.util.ArrayList;
import java.util.List;

public class LocationState {

    private String name;
    private int id;
    private List<Characteristic> listOfFeatures = new ArrayList<>();
    private List<Section> listOfSections = new ArrayList<>();
    private List<String> listOfImageStrings = new ArrayList<>();
    private List<String> listOfImageTitles = new ArrayList<>();
    private boolean galleryEnabled;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Characteristic> getListOfFeatures() {
        return listOfFeatures;
    }

    public void setListOfFeatures(List<Characteristic> listOfFeatures) {
        this.listOfFeatures = listOfFeatures;
    }

    public List<Section> getListOfSections() {
        return listOfSections;
    }

    public void setListOfSections(List<Section> listOfSections) {
        this.listOfSections = listOfSections;
    }

    public List<String> getListOfImageStrings() {
        return listOfImageStrings;
    }

    public void setListOfImageStrings(List<String> listOfImageStrings) {
        this.listOfImageStrings = listOfImageStrings;
    }

    public List<String> getListOfImageTitles() {
        return listOfImageTitles;
    }

    public void setListOfImageTitles(List<String> listOfImageTitles) {
        this.listOfImageTitles = listOfImageTitles;
    }

    public boolean isGalleryEnabled() {
        return galleryEnabled;
    }

    public void setGalleryEnabled(boolean galleryEnabled) {
        this.galleryEnabled = galleryEnabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
