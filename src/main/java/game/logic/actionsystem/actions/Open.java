package game.logic.actionsystem.actions;

import java.util.List;

import game.logic.EntityDescription;
import game.logic.GameLogic;
import game.logic.actionsystem.Action;
import game.state.Entity;
import game.state.EntitySet;

public class Open extends Action {
    private final List<EntityDescription> descriptions;

    public Open(List<EntityDescription> descriptions) {
        this.descriptions = descriptions;
    }

    @Override
    public void execute(GameLogic logic) {
        for (EntitySet set : this.descriptions.stream().map(logic::searchForNearbyEntity).toList()) {
            Entity e = set.collapse(logic);
            if(e == null) {
                logic.printToUser("open.noEntity");
            } else if (logic.canPlayerOpen(e)) {
                e.setClosed(false);
                logic.printToUser("open.success", e);
            } else {
                logic.printToUser("open.failed", e);
            }
        }
    }
}
