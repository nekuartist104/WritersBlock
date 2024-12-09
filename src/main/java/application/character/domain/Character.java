package application.character.domain;

import application.shared.domain.Characteristic;
import application.shared.domain.Section;

import java.util.ArrayList;
import java.util.List;

public class Character {

    private String name;
    private String type;
    private int id;
    private List<Characteristic> listOfCharacteristics = new ArrayList<>();
    private List<Section> listOfSections = new ArrayList<>();
    private List<String> listOfImageTitles = new ArrayList<>();
    private List<String> listOfImageStrings = new ArrayList<>();
    private boolean galleryEnabled;
    private String description;

    public List<Section> getListOfSections() {
        return listOfSections;
    }

    public void setListOfSections(List<Section> listOfSections) {
        this.listOfSections = listOfSections;
    }

    public List<Characteristic> getListOfCharacteristics() {
        return listOfCharacteristics;
    }

    public void setListOfCharacteristics(List<Characteristic> listOfCharacteristics) {
        this.listOfCharacteristics = listOfCharacteristics;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getListOfImageTitles() {
        return listOfImageTitles;
    }

    public void setListOfImageTitles(List<String> listOfImageTitles) {
        this.listOfImageTitles = listOfImageTitles;
    }

    public List<String> getListOfImageStrings() {
        return listOfImageStrings;
    }

    public void setListOfImageStrings(List<String> listOfImageStrings) {
        this.listOfImageStrings = listOfImageStrings;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
