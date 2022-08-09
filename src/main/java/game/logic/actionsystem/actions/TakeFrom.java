package game.logic.actionsystem.actions;

import java.util.List;

import game.logic.EntityDescription;
import game.logic.GameLogic;
import game.logic.actionsystem.Action;
import game.state.CircularLocationException;
import game.state.Entity;
import game.state.EntitySet;

public class TakeFrom extends Action {
    private final List<EntityDescription> what;
    private final List<EntityDescription> fromWhere;

    public TakeFrom(List<EntityDescription> what, List<EntityDescription> fromWhere) {
        this.what = what;
        this.fromWhere = fromWhere;
    }

    public List<EntityDescription> getWhat() {
        return this.what;
    }

    public List<EntityDescription> getFromWhere() {
        return this.fromWhere;
    }

    @Override
    public void execute(GameLogic logic) {
        // TODO incorporate fromWhere
        for (EntityDescription ed : this.what) {
            EntitySet es = logic.searchForNearbyEntity(ed);
            if(es.isEmpty()) {
                logic.printToUser("entity.notFound", ed);
            }
            Entity e = es.collapse(logic);
            if (e != null && logic.canPlayerTake(e)) {
                try {
                    e.setLocation(logic.getPlayer());
                    logic.printToUser("take.success", e);
                } catch (CircularLocationException ex) {
                    logic.printToUser("take.error", ex);
                }
            } else {
                logic.printToUser("take.failed", e);
            }
        }
    }
}
