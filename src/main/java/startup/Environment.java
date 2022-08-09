package startup;

import java.util.List;

import areas.Area;

public class Environment {
    public static final Environment instance = new Environment();

    private Area currentArea;
    private List<Area> gameAreas;

    public Area getCurrentArea() {
        return currentArea;
    }

    public Area getAreaByString(String area) {
        return gameAreas.stream().filter(item -> item.getName().equals(area)).findFirst().get();
    }

    public void setArea(Area area) {
        this.currentArea = area;
    }

    public List<Area> getGameAreas() {
        return gameAreas;
    }

    // Game Relevant Stuff
    public void setGameAreas(List<Area> gameAreas) {
        this.gameAreas = gameAreas;
    }

    public void setCurrentArea(Area currentArea) {
        this.currentArea = currentArea;
    }
}
