package startup;

import areas.Area;

import java.util.List;

public class Environment {

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
