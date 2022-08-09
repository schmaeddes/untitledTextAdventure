package util;

import areas.Area;
import startup.Environment;

public class Commands {

    public static void go(Area area) {
        Environment.instance.setArea(area);
        area.enterArea();
    }

    public static void info() {
        String infoText = TextColors.PURPLE.colorize("Du bist hier: " +
                Environment.instance.getCurrentArea().getNameWithArticle()) + "\n" +
                "Du kannst folgende Bereiche von hier erreichen: " +
                String.join(", ", Environment.instance.getCurrentArea().getReachableAreas());

        System.out.println(infoText);
    }

}
