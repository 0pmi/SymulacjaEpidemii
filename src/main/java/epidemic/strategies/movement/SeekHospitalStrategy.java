package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Hospital;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;

public class SeekHospitalStrategy implements MovementStrategy {

    @Override
    public Point2D calculateNextPosition(Agent agent, WorldMap world) {
        Point2D currentPos = agent.getPosition();
        Hospital nearestHospital = null;
        double minDistance = Double.MAX_VALUE;

        for (Hospital hospital : world.getHospitals()) {
            double dist = currentPos.distanceTo(hospital.getPosition());
            if (dist < minDistance) {
                minDistance = dist;
                nearestHospital = hospital;
            }
        }

        if (nearestHospital == null) {
            return currentPos;
        }

        Point2D targetPos = nearestHospital.getPosition();

        int dx = Integer.compare(targetPos.x(), currentPos.x());
        int dy = Integer.compare(targetPos.y(), currentPos.y());

        return new Point2D(currentPos.x() + dx, currentPos.y() + dy);
    }
}