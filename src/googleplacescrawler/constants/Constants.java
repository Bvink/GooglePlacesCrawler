package googleplacescrawler.constants;

public class Constants {

    public static final String CONNECTOR_LOCATION_QUERY = "SELECT connector_id AS id, lengtegraad AS longitude, breedtegraad AS latitude, type FROM connectoren WHERE type <> ? AND lengtegraad <= ? AND lengtegraad >= ? AND breedtegraad <= ? AND breedtegraad >= ? AND lengtegraad <> ? AND breedtegraad <> ? OR type IS NULL";
    public static final String CONNECTOR_LOCATION_PREP[] = {"DEMO", "180", "-180", "90", "-90", "0.0", "0.0"};

    public static final String GOOGLE_PLACES_API_KEY = "";

}
