package epidemic.gui;

import epidemic.engine.SimulationEngine;
import epidemic.model.*;
import epidemic.service.Config;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Komponent graficzny (Widok) odpowiedzialny za renderowanie siatki świata,
 * agentów oraz infrastruktury. Zapewnia również interaktywność za pomocą myszy
 * (Zaawansowany Inspektor Obiektu aktualizowany na żywo).
 */
public class MapPanel extends JPanel {
    private final WorldMap world;
    private final int scale;

    private final DrawingPanel drawingPanel;
    private final InspectorPanel inspectorPanel;

    // Referencja do aktualnie śledzonego obiektu, by odświeżać go na bieżąco
    private Object currentlyInspected = null;

    public MapPanel(WorldMap world) {
        this.world = world;
        this.scale = Config.getInt("gui.mapScale", 7);
        setLayout(new BorderLayout());

        // --- LEWA STRONA: RYSOWANIE MAPY ---
        this.drawingPanel = new DrawingPanel();
        add(drawingPanel, BorderLayout.CENTER);

        // --- PRAWA STRONA: ZAAWANSOWANY PANEL INFORMACYJNY ---
        this.inspectorPanel = new InspectorPanel();
        JScrollPane scrollPane = new JScrollPane(inspectorPanel);
        scrollPane.setPreferredSize(new Dimension(280, 0)); // Stała szerokość panelu
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Inspektor Obiektu",
                TitledBorder.LEFT, TitledBorder.TOP));
        // Ukrycie paska przewijania, jeśli nie jest potrzebny
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

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

            // Rysowanie pól infekcji (Aerozoli) - tworzy piękny efekt mgły / Heatmapy
            int maxAlpha = Config.getInt("gui.infectionFieldMaxAlpha", 150);
            double alphaMultiplier = Config.getDouble("gui.infectionFieldAlphaMultiplier", 250.0);

            for (InfectionField field : world.getActiveFields()) {
                Point2D pos = field.getPosition();

                // Przeliczamy siłę infekcji na przezroczystość (alpha) koloru czerwonego
                int alpha = (int) Math.min(maxAlpha, (field.getInfectivity() * alphaMultiplier));
                if (alpha > 0) {
                    g2.setColor(new Color(255, 0, 0, alpha));
                    // Rysujemy mgłę
                    g2.fillRect((int)pos.x() * scale, (int)pos.y() * scale, scale, scale);
                }
            }

            // Rysowanie szpitali jako półprzezroczystych, niebieskich kwadratów
            g2.setColor(new Color(100, 149, 237, 150));
            for (Hospital hospital : world.getHospitals()) {
                Point2D pos = hospital.getPosition();
                g2.fillRect(pos.x() * scale - 10, pos.y() * scale - 10, 20, 20);

                // Oznaczenie zaznaczonego szpitala ramką
                if (currentlyInspected == hospital) {
                    g2.setColor(Color.BLUE);
                    g2.drawRect(pos.x() * scale - 12, pos.y() * scale - 12, 24, 24);
                    g2.setColor(new Color(100, 149, 237, 150));
                }
            }

            // Rysowanie poszczególnych agentów
            for (Agent agent : world.getAgents()) {
                if (agent.isDead()) continue;

                g2.setColor(getColorForStatus(agent.getHealthStatus()));

                Point2D pos = agent.getPosition();
                int size = (agent instanceof Human) ? 6 : 4;

                int drawX = pos.x() * scale - size/2;
                int drawY = pos.y() * scale - size/2;

                g2.fillOval(drawX, drawY, size, size);

                // Rysowanie czarnej obwódki dla "wściekłych" agentów
                if (agent instanceof Human human && human.isHostile()) {
                    Stroke oldStroke = g2.getStroke(); // Zachowaj poprzednią grubość pędzla

                    g2.setColor(Color.BLACK);
                    g2.setStroke(new java.awt.BasicStroke(2)); // Pogrubiona krawędź
                    g2.drawOval(drawX, drawY, size, size); // Rysuj dokładnie na granicach `fillOval`

                    g2.setStroke(oldStroke); // Przywróć poprzedni pędzel
                }

                // Oznaczenie zaznaczonego agenta (Inspektor)
                if (currentlyInspected == agent) {
                    g2.setColor(Color.BLACK);
                    // Rysujemy większy okrąg otaczający agenta
                    g2.drawOval(pos.x() * scale - size, pos.y() * scale - size, size * 2, size * 2);
                }
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
     * Rejestruje nasłuchiwacz kliknięć myszą, pozwalający na wybór obiektu do śledzenia.
     */
    public void setupMouseListener(SimulationEngine engine) {
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mapX = e.getX() / scale;
                int mapY = e.getY() / scale;
                Point2D clickPos = new Point2D(mapX, mapY);

                currentlyInspected = null; // Reset zaznaczenia

                // 1. Sprawdzenie kliknięcia w infrastrukturę szpitalną
                for (Hospital hospital : world.getHospitals()) {
                    Point2D hPos = hospital.getPosition();
                    if (Math.abs(hPos.x() - mapX) <= 1 && Math.abs(hPos.y() - mapY) <= 1) {
                        currentlyInspected = hospital;
                        break;
                    }
                }

                // 2. Jeśli nie kliknięto szpitala, sprawdza agentów
                if (currentlyInspected == null) {
                    List<Agent> clickedAgents = world.getNeighbors(clickPos, Config.getDouble("gui.clickRadius", 2.0));
                    if (!clickedAgents.isEmpty()) {
                        // Pobiera pierwszego z brzegu żywego agenta w okolicy
                        currentlyInspected = clickedAgents.stream()
                                .filter(a -> !a.isDead())
                                .findFirst()
                                .orElse(null);
                    }
                }

                // Wymuszenie odświeżenia paneli (celownik na mapie + dane z boku)
                repaint();
            }
        });
    }

    @Override
    public void repaint() {
        super.repaint();
        if (drawingPanel != null) {
            drawingPanel.repaint();
        }
        if (inspectorPanel != null) {
            inspectorPanel.refresh(currentlyInspected);
        }
    }

    // ===================================================================================
    // Wewnętrzna klasa Inspektora - Dynamicznie buduje interfejs w zależności od obiektu
    // ===================================================================================
    private class InspectorPanel extends JPanel {

        public InspectorPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(10, 10, 10, 10));
            refresh(null);
        }

        public void refresh(Object obj) {
            removeAll(); // Czyści panel przed narysowaniem nowych danych

            if (obj == null) {
                JLabel emptyLabel = new JLabel("<html><i>Kliknij obiekt na mapie,<br>aby wyświetlić telemetrię.</i></html>");
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                add(emptyLabel);
            } else if (obj instanceof Agent agent) {
                buildAgentUI(agent);
            } else if (obj instanceof Hospital hospital) {
                buildHospitalUI(hospital);
            }

            revalidate(); // Zmusza Swinga do przeliczenia Layoutu
            repaint();
        }

        private void buildAgentUI(Agent agent) {
            // Nagłówek
            JLabel titleLabel = new JLabel("Typ: " + agent.getSpeciesType().name());
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            add(titleLabel);

            JLabel posLabel = new JLabel("Pozycja: [" + agent.getPosition().x() + ", " + agent.getPosition().y() + "]");
            add(posLabel);
            add(Box.createVerticalStrut(15)); // Odstęp

            // Wiek i Stan Życia
            add(new JLabel("Wiek: " + agent.getAge()));
            JProgressBar ageBar = new JProgressBar(0, Config.getInt("mortality.maxAge", 100));
            ageBar.setValue(agent.getAge());
            ageBar.setStringPainted(true);
            ageBar.setForeground(new Color(46, 139, 87));
            add(ageBar);

            if (agent.isDead()) {
                JLabel deadLabel = new JLabel("STAN: MARTWY");
                deadLabel.setForeground(Color.BLACK);
                deadLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                add(deadLabel);
                return; // Jeśli agent nie żyje, nie ma sensu wyświetlać reszty
            }

            add(Box.createVerticalStrut(15));

            // Status Zdrowotny
            JLabel healthLabel = new JLabel("Stan Zdrowia: " + agent.getHealthStatus());
            healthLabel.setForeground(getColorForStatus(agent.getHealthStatus()));
            healthLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            add(healthLabel);

            if (agent instanceof Human human && human.isHostile()) {
                JLabel hostileLabel = new JLabel("Status: WŚCIEKŁY!");
                hostileLabel.setForeground(Color.BLACK); // Lub ciemno-czerwony
                hostileLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                add(hostileLabel);
            }

            // Jeśli Agent jest chory lub jest nosicielem, pokazuje pasek przebiegu infekcji
            if (agent.getHealthStatus() == HealthStatus.SICK || agent.getHealthStatus() == HealthStatus.CARRIER) {
                add(new JLabel("Do końca infekcji (epoki):"));
                int defaultDuration = Config.getInt("virus.defaultDuration", 30);
                JProgressBar infectionBar = new JProgressBar(0, defaultDuration);
                infectionBar.setValue(agent.getRemainingInfectionEpochs());
                infectionBar.setString(agent.getRemainingInfectionEpochs() + " / " + defaultDuration);
                infectionBar.setStringPainted(true);
                infectionBar.setForeground(Color.RED);
                add(infectionBar);
            }

            add(Box.createVerticalStrut(15));

            // Informacje Medyczne (Tylko dla obiektów mogących korzystać ze szpitali)
            if (agent instanceof HospitalUser user) {
                add(new JLabel("<html><b>Dane Medyczne:</b></html>"));
                add(new JLabel("Szczepienie: " + (user.isVaccinated() ? "Tak" : "Nie")));
                add(new JLabel("W szpitalu: " + (user.isInHospital() ? "Tak" : "Nie")));
                add(new JLabel("Chce do szpitala: " + (user.isWantsHospital() ? "Tak" : "Nie")));
            }

            add(Box.createVerticalStrut(10));
            add(new JLabel("Podatność: " + String.format("%.2f", agent.getVulnerabilityMultiplier())));

            add(Box.createVerticalStrut(15));
            add(new JLabel("<html><b>Strategia Ruchu:</b></html>"));
            add(new JLabel(agent.getMovementStrategy().getClass().getSimpleName()));
        }

        private void buildHospitalUI(Hospital hospital) {
            JLabel titleLabel = new JLabel("Szpital Polowy");
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            add(titleLabel);

            JLabel posLabel = new JLabel("Pozycja: [" + hospital.getPosition().x() + ", " + hospital.getPosition().y() + "]");
            add(posLabel);
            add(Box.createVerticalStrut(15));

            add(new JLabel("Obłożenie Łóżek:"));
            JProgressBar capacityBar = new JProgressBar(0, hospital.getCapacity());
            capacityBar.setValue(hospital.getPatients().size());
            capacityBar.setString(hospital.getPatients().size() + " / " + hospital.getCapacity());
            capacityBar.setStringPainted(true);

            // Kolor zależny od zatłoczenia
            double fillRatio = (double) hospital.getPatients().size() / hospital.getCapacity();
            if (fillRatio > 0.9) capacityBar.setForeground(Color.RED);
            else if (fillRatio > 0.5) capacityBar.setForeground(Color.ORANGE);
            else capacityBar.setForeground(new Color(30, 144, 255));

            add(capacityBar);
        }
    }
}