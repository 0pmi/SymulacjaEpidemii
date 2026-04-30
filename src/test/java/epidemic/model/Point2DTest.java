package epidemic.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Point2DTest {

    @Test
    void shouldCalculateCorrectDistanceBetweenTwoPoints() {
        Point2D p1 = new Point2D(0, 0);
        Point2D p2 = new Point2D(3, 4);

        double distance = p1.distanceTo(p2);

        assertEquals(5.0, distance, 0.001, "Odległość euklidesowa między (0,0) a (3,4) powinna wynosić 5.0");
    }

    @Test
    void distanceToSelfShouldBeZero() {
        Point2D p = new Point2D(10, 10);

        assertEquals(0.0, p.distanceTo(p), "Odległość punktu do samego siebie powinna wynosić 0.0");
    }
}