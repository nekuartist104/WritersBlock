package application.character.domain;

public enum CharacterTypes {

    MALE("Male", "leafIcons/characterLeafIcon.png"),
    FEMALE("Female", "leafIcons/characterLeafIconFemale.png");

    private String title;
    private String icon;

    CharacterTypes(String title, String icon) {
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
        for (CharacterTypes type : CharacterTypes.values()) {
            if (type.title.equals(string)) {
                return true;
            }
        }
        return false;
    }
}
