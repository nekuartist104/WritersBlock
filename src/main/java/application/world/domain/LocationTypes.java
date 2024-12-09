package application.world.domain;

public enum LocationTypes {

    LAND("Land","leafIcons/treeIcon.png"),
    VILLAGE("Village","leafIcons/buildingIcon.png"),
    CITY("City","leafIcons/cityIcon.png");

    private String title;
    private String icon;

    LocationTypes(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }
    public String getTitle() {
        return title;
    }
    public String getIcon() {
        return icon;
    }

    public static boolean isFixed(String string) {
        for (LocationTypes type : LocationTypes.values()) {
            if (type.title.equals(string)) {
                return true;
            }
        }
        return false;
    }
}
