package application.shared.domain;

import application.shared.domain.Characteristic;

import java.util.ArrayList;
import java.util.List;

public class Section {
    private String name;
    private List<Characteristic> listOfCharacteristics = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Characteristic> getListOfCharacteristics() {
        return listOfCharacteristics;
    }

    public void setListOfCharacteristics(List<Characteristic> listOfCharacteristics) {
        this.listOfCharacteristics = listOfCharacteristics;
    }
}
