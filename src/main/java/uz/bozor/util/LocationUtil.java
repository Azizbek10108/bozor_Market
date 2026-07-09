package uz.bozor.util;

public class LocationUtil {

    private static final double EARTH_RADIUS_KM = 6371.0;

    public static double calculateDistance(
            double lat1, double lon1,
            double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public static String formatDistance(double km) {
        if (km < 1.0) {
            return (int)(km * 1000) + " m";
        }
        return String.format("%.1f km", km);
    }

    public static String googleMapsNavUrl(double lat, double lon) {
        return String.format(
            "https://www.google.com/maps/dir/?api=1&destination=%f,%f", lat, lon);
    }

    public static boolean isValid(Double lat, Double lon) {
        return lat != null && lon != null
            && lat >= -90 && lat <= 90
            && lon >= -180 && lon <= 180;
    }
}
