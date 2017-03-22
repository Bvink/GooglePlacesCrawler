package googleplacescrawler.objects;

import java.util.ArrayList;
import java.util.List;

public class Connector {

    private List<Integer> ids = new ArrayList<Integer>();
    private List<Place> places = new ArrayList<Place>();
    private GeoLocation geoLocation;

    public Connector(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public void addId(int id) {
        ids.add(id);
    }

    public List<Integer> getId() {
        return ids;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }
}
