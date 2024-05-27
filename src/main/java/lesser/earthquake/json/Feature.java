package lesser.earthquake.json;

public class Feature {
    public Properties properties;
    public Geometry geometry;

    public static class Geometry {
        public double[] coordinates;
    }
}
