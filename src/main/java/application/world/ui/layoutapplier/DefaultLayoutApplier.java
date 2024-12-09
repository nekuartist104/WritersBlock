package application.world.ui.layoutapplier;

import application.shared.domain.Characteristic;
import application.shared.domain.Section;
import application.world.domain.LocationState;

public class DefaultLayoutApplier implements LayoutApplier {

    @Override
    public void apply(LocationState locationState) {
        Characteristic placeType  = new Characteristic();
        placeType.setCharacteristicTitle("Type of Place");
        locationState.getListOfFeatures().add(placeType);

        Characteristic nationality = new Characteristic();
        nationality.setCharacteristicTitle("Nationality");
        locationState.getListOfFeatures().add(nationality);

        Characteristic leader = new Characteristic();
        leader.setCharacteristicTitle("Leader");
        locationState.getListOfFeatures().add(leader);

        Characteristic population = new Characteristic();
        population.setCharacteristicTitle("Population");
        locationState.getListOfFeatures().add(population);

        Characteristic climate = new Characteristic();
        climate.setCharacteristicTitle("Climate");
        locationState.getListOfFeatures().add(climate);



        Section appearance = new Section();
        appearance.setName("Appearance");
        locationState.getListOfSections().add(appearance);

        Characteristic terrain = new Characteristic();
        terrain.setCharacteristicTitle("Terrain");
        appearance.getListOfCharacteristics().add(terrain);



        Section culture = new Section();
        culture.setName("Culture");
        locationState.getListOfSections().add(culture);

        Characteristic religion = new Characteristic();
        religion.setCharacteristicTitle("Religion");
        culture.getListOfCharacteristics().add(religion);



        Section landmarks = new Section();
        landmarks.setName("Landmarks");
        locationState.getListOfSections().add(landmarks);



        Section history = new Section();
        history.setName("History");
        locationState.getListOfSections().add(history);



        locationState.setGalleryEnabled(true);
    }
}
