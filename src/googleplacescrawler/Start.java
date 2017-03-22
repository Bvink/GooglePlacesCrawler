package googleplacescrawler;

import googleplacescrawler.classes.DataInserter;
import googleplacescrawler.classes.LocationRetriever;
import googleplacescrawler.classes.PlacesService;
import googleplacescrawler.objects.Connector;
import googleplacescrawler.objects.Place;

import java.util.List;

public class Start {

    public static final String PLACE_TYPE = "restaurant";

    public static void main(String args[]) {
        LocationRetriever lc = new LocationRetriever();
        List<Connector> connectors = lc.retrieveLocations();
        //print(connectors);
        PlacesService placesService = new PlacesService(PLACE_TYPE);
        placesService.setPlaces(connectors);
        //print(connectors);
        DataInserter dataInserter = new DataInserter();
        dataInserter.setPlaces(connectors);
    }

    public static void print(List<Connector> connectors) {
        for(Connector connector : connectors) {
            System.out.println("Connector ID(s): " + connector.getId());
            System.out.println("Connector: Location: " + connector.getGeoLocation().getLatitude() + ", " + connector.getGeoLocation().getLongitude());
            System.out.println();
            for(Place place : connector.getPlaces()) {
                System.out.println("Place Name: " + place.getName());
                System.out.println("Address: " + place.getAddress());
                System.out.println("Rating: " + place.getRating());
                System.out.println("Type: " + place.getType());
                System.out.println("Location: " + place.getGeoLocation().getLatitude() + ", " + place.getGeoLocation().getLongitude());
                System.out.println();
            }
            System.out.println();
        }
    }

}