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
 * Złożony komponent graficzny pełniący rolę głównego widoku operacyjnego.
 * Składa się z dynamicznie odświeżanego płótna (DrawingPanel) renderującego stan przestrzenny
 * mapy oraz bocznego panelu telemetrycznego (InspectorPanel) wykorzystującego
 * polimorfizm do wyświetlania szczegółów wybranych obiektów.
 */
public class MapPanel extends JPanel {
    private final WorldMap world;
    private final int scale;

    private final DrawingPanel drawingPanel;
    private final InspectorPanel inspectorPanel;

    // Referencja do aktualnie śledzonego obiektu, by odświeżać go na bieżąco
    private Object currentlyInspected = null;

    /**
     * Tworzy i konfiguruje układ paneli wizualnych.
     *
     * @param world Referencja do mapy świata, z której pobierane będą obiekty do renderowania.
     */
    public MapPanel(WorldMap world) {
        this.world = world;
        this.scale = Config.getInt("gui.mapScale", 7);
        setLayout(new BorderLayout());

        // --- LEWA STRONA: RYSOWANIE MAPY ---
        this.drawingPanel = new DrawingPanel();
        add(drawingPanel, BorderLayout.CENTER);

        // --- PRAWA STRONA: PANEL INFORMACYJNY ---
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
     * Wewnętrzna warstwa prezentacji mapy.
     * Zapewnia sprzętową akcelerację renderowania prymitywów geometrycznych
     * przy użyciu Graphics2D. Definiuje porządek rysowania (tzw. Z-index):
     * pola infekcji -> szpitale -> agenci -> nakładki zaznaczenia.
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

            // Rysowanie pól infekcji
            int maxAlpha = Config.getInt("gui.infectionFieldMaxAlpha", 150);
            double alphaMultiplier = Config.getDouble("gui.infectionFieldAlphaMultiplier", 250.0);

            for (InfectionField field : world.getActiveFields()) {
                Point2D pos = field.getPosition();

                // Przelicza siłę infekcji na przezroczystość (alpha) koloru czerwonego
                int alpha = (int) Math.min(maxAlpha, (field.getInfectivity() * alphaMultiplier));
                if (alpha > 0) {
                    g2.setColor(new Color(255, 0, 0, alpha));
                    // Rysuje mgłę
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

                // Oznaczenie zaznaczonego agenta
                if (currentlyInspected == agent) {
                    g2.setColor(Color.BLACK);
                    // Rysuje większy okrąg otaczający agenta
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
     * Rejestruje nasłuchiwacz zdarzeń myszy, implementujący logikę hit-testingu.
     * Priorytetyzuje wybór obiektów na mapie: w pierwszej kolejności sprawdza obiekty
     * infrastruktury (szpitale), a w przypadku ich braku przechodzi do wyszukiwania
     * agentów w promieniu zdefiniowanym w konfiguracji.
     *
     * @param engine Referencja do silnika symulacji (gotowa do ewentualnego rozszerzenia interakcji).
     */
    public void setupMouseListener(SimulationEngine engine) {
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mapX = e.getX() / scale;
                int mapY = e.getY() / scale;
                Point2D clickPos = new Point2D(mapX, mapY);

                currentlyInspected = null;

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
                // Wymuszenie odświeżenia paneli
                repaint();
            }
        });
    }

    /**
     * Przeciąża standardową metodę przerysowania komponentu, kaskadowo
     * wymuszając aktualizację na podobiektach: płótnie i inspektorze.
     */
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

    /**
     * Wewnętrzny komponent telemetrii.
     * Obsługuje dynamiczne budowanie interfejsu (etykiety, paski postępu)
     * na podstawie metadanych dostarczanych przez obiekty implementujące
     * interfejs {@link epidemic.model.Inspectable}.
     */
    private class InspectorPanel extends JPanel {

        public InspectorPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(10, 10, 10, 10));
            refresh(null);
        }

        /**
         * Czyści aktualny widok i przebudowuje go na podstawie właściwości
         * przekazanego obiektu.
         *
         * @param obj Wybrany na mapie obiekt infrastruktury lub agent, bądź null w przypadku braku wyboru.
         */
        public void refresh(Object obj) {
            removeAll();

            if (obj == null) {
                JLabel emptyLabel = new JLabel("<html><i>Kliknij obiekt na mapie,<br>aby wyświetlić telemetrię.</i></html>");
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                add(emptyLabel);
            } else if (obj instanceof Inspectable inspectable) { // ← Jedno polimorficzne wywołanie

                JLabel titleLabel = new JLabel(inspectable.getObjectName());
                titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
                add(titleLabel);
                add(Box.createVerticalStrut(15));

                for (InspectionProperty prop : inspectable.getInspectionProperties()) {
                    add(new JLabel(prop.label() + ":"));

                    if (prop.progressMax() != null) {
                        // Jeśli dostarczono dane dla paska postępu - stwórz ProgressBar
                        JProgressBar bar = new JProgressBar(0, prop.progressMax());
                        bar.setValue(prop.progressValue());
                        bar.setStringPainted(true);
                        if (prop.highlightColor() != null) {
                            bar.setForeground(prop.highlightColor());
                        }
                        add(bar);
                    } else {
                        // Jeśli nie, dodaj wartość jako zwykły tekst (z ew. kolorem)
                        JLabel valLabel = new JLabel(prop.stringValue());
                        if (prop.highlightColor() != null) {
                            valLabel.setForeground(prop.highlightColor());
                            valLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                        }
                        add(valLabel);
                    }
                    add(Box.createVerticalStrut(10));
                }
            }

            revalidate();
            repaint();
        }
    }
}