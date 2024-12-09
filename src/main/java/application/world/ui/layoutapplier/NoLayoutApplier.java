package application.world.ui.layoutapplier;

import application.shared.domain.Characteristic;
import application.world.domain.LocationState;

public class NoLayoutApplier implements LayoutApplier {


    @Override
    public void apply(LocationState locationState) {
        Characteristic placeType = new Characteristic();
        placeType.setCharacteristicTitle("Type of Place");
        locationState.getListOfFeatures().add(placeType);
    }
}
