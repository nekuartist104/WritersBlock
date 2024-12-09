package application.world.ui.layoutapplier;

import application.shared.domain.Characteristic;
import application.shared.domain.Section;
import application.shared.ui.CharacteristicField;
import application.shared.ui.SectionComponents;
import application.world.domain.LocationState;
import application.world.ui.FeaturesPanel;

public class ThisLayoutApplier implements LayoutApplier {

    private FeaturesPanel currentFeaturesPanel;

    public ThisLayoutApplier(FeaturesPanel currentFeaturesPanel) {
        this.currentFeaturesPanel = currentFeaturesPanel;
    }

    @Override
    public void apply(LocationState locationState) {
        locationState.getListOfFeatures().clear();
        for (CharacteristicField item : currentFeaturesPanel.getFixedPanel().getListOfCharacteristicFields()) {
            Characteristic characteristic = new Characteristic();
            String characteristicName = item.getLabel().getText().substring(0, item.getLabel().getText().length()-1);
            characteristic.setCharacteristicTitle(characteristicName);
            locationState.getListOfFeatures().add(characteristic);
        }
        locationState.getListOfSections().clear();
        for (SectionComponents sectionComponents : currentFeaturesPanel.getListOfSectionComponents()) {
            Section newSection = new Section();
            String sectionName = sectionComponents.getTitle().getTitle().getText().substring(0, sectionComponents.getTitle().getTitle().getText().length()-1);
            newSection.setName(sectionName);
            locationState.getListOfSections().add(newSection);
            for (CharacteristicField item : sectionComponents.getCharacteristics().getListOfCharacteristicFields()) {
                Characteristic characteristic = new Characteristic();
                String characteristicName = item.getLabel().getText().substring(0, item.getLabel().getText().length()-1);
                characteristic.setCharacteristicTitle(characteristicName);
                newSection.getListOfCharacteristics().add(characteristic);
            }
        }
        locationState.getListOfImageStrings().clear();
        locationState.getListOfImageTitles().clear();

        locationState.setGalleryEnabled(currentFeaturesPanel.getEnableGallery().isSelected());
        locationState.setDescription(currentFeaturesPanel.getGallery().getDescription().getText());
    }
}
