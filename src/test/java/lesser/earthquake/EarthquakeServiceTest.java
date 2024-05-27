package lesser.earthquake;

import lesser.earthquake.json.FeatureCollection;
import lesser.earthquake.json.Properties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EarthquakeServiceTest {

    @Test
    void oneHour() {
        // given
        EarthquakeService service = new EarthquakeServiceFactory().getService();

        // when
        FeatureCollection collection = service.oneHour().blockingGet();

        // then
        Properties properties = collection.features[0].properties;
        assertNotNull(properties.place, "Place should not be null");
        assertNotEquals(0, properties.mag, "Magnitude should not be zero");
        assertNotEquals(0, properties.time, "Time should not be zero");
    }

    @Test
    void significantLast30Days() {
        // given
        EarthquakeService service = new EarthquakeServiceFactory().getService();

        // when
        FeatureCollection collection = service.significantLast30Days().blockingGet();

        // then
        Properties properties = collection.features[0].properties;
        assertNotNull(properties.place, "Place should not be null");
        assertNotEquals(0, properties.mag, "Magnitude should not be zero");
        assertNotEquals(0, properties.time, "Time should not be zero");
    }
}
