package epidemic.gui;

import epidemic.engine.SimulationEngine;
import epidemic.model.*;
import epidemic.service.Config;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Komponent graficzny (Widok) odpowiedzialny za renderowanie siatki świata,
 * agentów oraz infrastruktury. Zapewnia również interaktywność za pomocą myszy (Inspektor Obiektu).
 */
public class MapPanel extends JPanel {
    private final WorldMap world;
    private final int scale;

    private final JTextArea infoArea;
    private final DrawingPanel drawingPanel;

    public MapPanel(WorldMap world) {
        this.world = world;
        this.scale = Config.getInt("gui.mapScale", 7);
        setLayout(new BorderLayout());

        // --- LEWA STRONA: RYSOWANIE MAPY ---
        this.drawingPanel = new DrawingPanel();
        add(drawingPanel, BorderLayout.CENTER);

        // --- PRAWA STRONA: PANEL INFORMACYJNY ---
        this.infoArea = new JTextArea(10, 25);
        this.infoArea.setEditable(false);
        this.infoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        this.infoArea.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Inspektor Obiektu",
                TitledBorder.LEFT, TitledBorder.TOP));

        add(scrollPane, BorderLayout.EAST);
    }

    /**
     * Wewnętrzna klasa panelu płótna odpowiedzialna wyłącznie za logikę rysowania (paintComponent).
     */
    private class DrawingPanel extends JPanel {
        public DrawingPanel() {
            setPreferredSize(new Dimension(world.getWidth() * scale, world.getHeight() * scale));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Rysowanie szpitali jako półprzezroczystych, niebieskich kwadratów
            g2.setColor(new Color(100, 149, 237, 150));
            for (Hospital hospital : world.getHospitals()) {
                Point2D pos = hospital.getPosition();
                g2.fillRect(pos.x() * scale - 10, pos.y() * scale - 10, 20, 20);
            }

            // Rysowanie poszczególnych agentów
            for (Agent agent : world.getAgents()) {
                if (agent.isDead()) continue;

                g2.setColor(getColorForStatus(agent.getHealthStatus()));

                Point2D pos = agent.getPosition();
                int size = (agent instanceof Human) ? 6 : 4;

                g2.fillOval(pos.x() * scale - size/2, pos.y() * scale - size/2, size, size);
            }
        }
    }

    private Color getColorForStatus(HealthStatus status) {
        return switch (status) {
            case HEALTHY -> new Color(34, 139, 34);
            case SICK -> Color.RED;
            case CARRIER -> Color.ORANGE;
            case RECOVERED -> new Color(0, 191, 255);
        };
    }

    /**
     * Rejestruje nasłuchiwacz kliknięć myszą, pozwalający na inspekcję elementów na zablokowanej mapie.
     * Funkcja działa poprawnie tylko wtedy, gdy symulacja jest zapauzowana.
     *
     * @param engine Główny silnik symulacji do weryfikacji stanu pauzy.
     */
    public void setupMouseListener(SimulationEngine engine) {
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (engine.isPaused()) {
                    int mapX = e.getX() / scale;
                    int mapY = e.getY() / scale;
                    Point2D clickPos = new Point2D(mapX, mapY);

                    // Sprawdzenie kliknięcia w infrastrukturę szpitalną
                    for (Hospital hospital : world.getHospitals()) {
                        Point2D hPos = hospital.getPosition();
                        if (Math.abs(hPos.x() - mapX) <= 1 && Math.abs(hPos.y() - mapY) <= 1) {
                            updateInfoArea(hospital);
                            return;
                        }
                    }

                    // Sprawdzenie kliknięcia w agentów w promieniu konfiguracji
                    List<Agent> clickedAgents = world.getNeighbors(clickPos, Config.getDouble("gui.clickRadius", 2.0));
                    if (!clickedAgents.isEmpty()) {
                        updateInfoArea(clickedAgents.get(0));
                    } else {
                        infoArea.setText("Kliknij w obiekt, aby zobaczyć szczegóły.");
                    }
                }
            }
        });
    }

    private void updateInfoArea(Inspectable obj) {
        infoArea.setText(obj.getDetailedInfo());
        infoArea.setCaretPosition(0); // Przewija okno tekstowe na samą górę
    }

    @Override
    public void repaint() {
        super.repaint();
        if (drawingPanel != null) {
            drawingPanel.repaint();
        }
    }
}