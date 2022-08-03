import areas.Area;

public class Commands {

    public static void go(Area area) {
        Main.environment.setArea(area);
        area.enterArea();
    }

    public static void info() {
        String infoText = TextColors.PURPLE.colorize("Du bist hier: " +
                Main.environment.getCurrentArea().getNameWithArticle()) + "\n" +
                "Du kannst folgende Bereiche von hier erreichen: " +
                String.join(", ", Main.environment.getCurrentArea().getReachableAreas());

        System.out.println(infoText);
    }

}
