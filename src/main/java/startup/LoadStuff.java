package startup;

import areas.Area;
import areas.Forest;
import areas.House;

import java.util.ArrayList;
import java.util.List;

public class LoadStuff {

    public void load(Environment environment) {
        initializeAreas(environment);
    }

    private void initializeAreas(Environment environment) {
        List<Area> areas = new ArrayList<>();

        areas.add(new House());
        areas.add(new Forest());

        environment.setGameAreas(areas);
    }

}
