package epidemic.gui;

import epidemic.model.*;
import javax.swing.*;
import java.awt.*;

public class MapPanel extends JPanel {
    private final WorldMap world;
    private final int scale = 7;

    public MapPanel(WorldMap world) {
        this.world = world;
        setPreferredSize(new Dimension(world.getWidth() * scale, world.getHeight() * scale));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(100, 149, 237, 150));
        for (Hospital hospital : world.getHospitals()) {
            Point2D pos = hospital.getPosition();
            g2.fillRect(pos.x() * scale - 10, pos.y() * scale - 10, 20, 20);
        }

        for (Agent agent : world.getAgents()) {
            if (agent.isDead()) continue;

            g2.setColor(getColorForStatus(agent.getHealthStatus()));

            Point2D pos = agent.getPosition();
            int size = (agent instanceof Human) ? 6 : 4;

            g2.fillOval(pos.x() * scale - size/2, pos.y() * scale - size/2, size, size);
        }
    }

    private Color getColorForStatus(HealthStatus status) {
        return switch (status) {
            case HEALTHY -> new Color(34, 139, 34);  // Ciemnozielony
            case SICK -> Color.RED;
            case CARRIER -> Color.ORANGE;
            case RECOVERED -> new Color(0, 191, 255); // Jasnoniebieski
        };
    }
}