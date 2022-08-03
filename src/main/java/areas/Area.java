package areas;

import java.util.ArrayList;
import java.util.List;

public class Area {

    private List<String> enterText;
    private String name;
    private String article;
    private List<String> reachableAreas = new ArrayList<>();

    public List<String> getEnterText() {
        return enterText;
    }

    public void setEnterText(List<String> enterText) {
        this.enterText = enterText;
    }

    public void enterArea() {
        for(String text: this.getEnterText()) {
            System.out.println(text);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public List<String> getReachableAreas() {
        return reachableAreas;
    }

    public void setReachableAreas(List<String> reachableAreas) {
        this.reachableAreas = reachableAreas;
    }

    public String getNameWithArticle() {
        return this.article + " " + this.name;
    }
}
