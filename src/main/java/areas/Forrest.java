package areas;

import java.util.ArrayList;
import java.util.List;

public class Forrest extends Area {

    public Forrest() {
        this.setArticle("Der");
        this.setName("Wald");

        this.getReachableAreas().add("Das Haus");

        List<String> enterText = new ArrayList<>();

        enterText.add("Du betrittst den Wald.");
        enterText.add("Eine kleine Lichtung befindet sich genau in der Mitte.");

        this.setEnterText(enterText);
    }

}
