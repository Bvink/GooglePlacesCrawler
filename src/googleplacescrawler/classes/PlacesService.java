package googleplacescrawler.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import googleplacescrawler.constants.Constants;
import googleplacescrawler.objects.Connector;
import googleplacescrawler.objects.GeoLocation;
import googleplacescrawler.objects.Place;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlacesService {

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_NEARBY_SEARCH = "/nearbysearch";
    private static final String OUT_JSON = "/json";
    public static final String[] JSON_HOOKS = {"results", "geometry", "location"};

    public static final int RADIUS = 1000;

    private String businessType;

    public PlacesService(String businessType) {
        this.businessType = businessType;
    }

    public List<Connector> setPlaces(List<Connector> connectors) {
        for(Connector connector : connectors) {
            connector.setPlaces(findPlaces(connector));
        }
        return connectors;
    }

    private List<Place> findPlaces(Connector connector) {
        List<Place> places;
        String searchResults = nearbySearch(connector).toString();
        places = parseJson(searchResults);
        return places;
    }

    public StringBuilder nearbySearch(Connector connector) {

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            String s = PLACES_API_BASE + TYPE_NEARBY_SEARCH + OUT_JSON
                    + "?location=" + connector.getGeoLocation().getLatitude() + "," + connector.getGeoLocation().getLongitude()
                    + "&radius=" + RADIUS
                    + "&type=" + businessType
                    + "&key=" + Constants.GOOGLE_PLACES_API_KEY;

            URL url = new URL(s);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return jsonResults;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return jsonResults;
    }

    private List<Place> parseJson(String s) {
        List<Place> places = new ArrayList<Place>();
        JsonParser parser = new JsonParser();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonElement el = parser.parse(s);
        s = gson.toJson(el);

        JSONObject obj = new JSONObject(s);
        JSONArray arr = obj.getJSONArray(JSON_HOOKS[0]);
        for(int i = 0; i < arr.length(); i++) {
            Place place = new Place();
            obj = arr.getJSONObject(i);
            place.setName(obj.getString("name"));
            place.setRating(obj.has("rating") ? obj.getDouble("rating") : 0.0);
            place.setType(businessType);
            place.setAddress(obj.getString("vicinity"));
            obj = obj.getJSONObject(JSON_HOOKS[1]);
            obj = obj.getJSONObject(JSON_HOOKS[2]);
            place.setGeoLocation(new GeoLocation(obj.getDouble("lat"), obj.getDouble("lng")));
            places.add(place);
        }

        return places;
    }
}