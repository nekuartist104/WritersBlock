package application.shared.ui;

import application.factory.OvalButton;

import javax.swing.*;

public class SectionComponents {

    private Characteristics characteristics;
    private JLabel nameLabel;
    private OvalButton addSubSection;
    private TitlePanel title;

    public TitlePanel getTitle() {
        return title;
    }

    public void setTitle(TitlePanel title) {
        this.title = title;
    }

    public JLabel getNameLabel() {
        return nameLabel;
    }

    public void setNameLabel(JLabel nameLabel) {
        this.nameLabel = nameLabel;
    }

    public Characteristics getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(Characteristics characteristics) {
        this.characteristics = characteristics;
    }

    public OvalButton getAddSubSection() {
        return addSubSection;
    }

    public void setAddSubSection(OvalButton addSubSection) {
        this.addSubSection = addSubSection;
    }
}
