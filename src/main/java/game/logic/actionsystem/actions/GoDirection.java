package game.logic.actionsystem.actions;

import game.logic.GameLogic;
import game.logic.actionsystem.Action;
import game.state.CircularLocationException;
import game.state.Entity;

public class GoDirection extends Action {
    private final String directionId;

    public GoDirection(String directionId) {
        this.directionId = directionId;
    }

    public String getDirectionId() {
        return this.directionId;
    }

    @Override
    public void execute(GameLogic logic) {
        if (logic.getPlayer() == null) {
            logic.printToUser("go.player.null");
        } else {
            Entity location = logic.getPlayer().getLocation();
            if (location == null) {
                logic.printToUser("go.player.location.null", logic.getPlayer());
            } else {
                Entity.EntityConnection connection = location.getConnection(this.directionId);
                if (connection == null || connection.to() == null) {
                    logic.printToUser("go.direction.unknown", logic.getPlayer().getLocation(), this.directionId);
                } else if (connection.associatedEntity() != null && connection.associatedEntity().isClosed()) {
                    logic.printToUser("go.location.closed", connection.associatedEntity());
                } else {
                    try {
                        logic.getPlayer().setLocation(connection.to());
                        logic.printToUser("go.success", location, connection.to());
                    } catch (CircularLocationException ex) {
                        logic.printToUser("go.locationCircular", ex);
                    }
                }
            }
        }
    }
}
