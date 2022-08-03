package areas;

import java.util.ArrayList;
import java.util.List;

public class House extends Area {

    public House() {
        this.setArticle("Das");
        this.setName("haus");

        this.getReachableAreas().add("Der Wald");
        this.getReachableAreas().add("Der See");

        List<String> enterText = new ArrayList<>();

        enterText.add("Du betrittst das Haus.");
        enterText.add("Ein leichter Luftzug geht durch den Eingangsbereich. Irgendjemand hat ein Fenster aufgelassen.");

        this.setEnterText(enterText);
    }

}
