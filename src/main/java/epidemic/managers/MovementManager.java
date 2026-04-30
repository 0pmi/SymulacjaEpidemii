package epidemic.managers;

import epidemic.model.*;

public class MovementManager {

    public void moveAgents(WorldMap world) {
        for (Agent agent : world.getAgents()) {
            if (agent instanceof HospitalUser user && user.isInHospital()) {
                continue;
            }
            Point2D nextPos = agent.getMovementStrategy().calculateNextPosition(agent, world);

            if (world.isWithinBounds(nextPos)) {
                agent.setPosition(nextPos);
            }
            checkHospitalInteraction(agent, world);
        }
        world.rebuildSpatialIndex();
    }

    private void checkHospitalInteraction(Agent agent, WorldMap world) {
        if (agent instanceof HospitalUser user) {
            Hospital hospital = world.getHospitalAt(user.getPosition());

            if (hospital != null && user.isWantsHospital()) {
                if (hospital.addPatient(user)) {
                    user.setIsInHospital(true);
                }
            }
        }
    }
}