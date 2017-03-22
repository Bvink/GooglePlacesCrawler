package googleplacescrawler.classes;

import googleplacescrawler.constants.Constants;
import googleplacescrawler.objects.Connector;
import googleplacescrawler.objects.GeoLocation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationRetriever {

    public List<Connector> retrieveLocations() {

        List<Connector> connectors = new ArrayList<Connector>();
        String url = "jdbc:mysql://localhost:3306/";
        String user = "root";
        String password = "";

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection(url, user, password);

            Statement stt = con.createStatement();

            stt.execute("USE x");

            PreparedStatement prep = con.prepareStatement(Constants.CONNECTOR_LOCATION_QUERY);
            for (int i = 0; i < Constants.CONNECTOR_LOCATION_PREP.length; i++) {
                prep.setString(i + 1, Constants.CONNECTOR_LOCATION_PREP[i]);
            }

            ResultSet res = prep.executeQuery();

            while (res.next()) {
                boolean exists = false;
                Connector connector = new Connector(new GeoLocation(Double.parseDouble(res.getString("latitude")), Double.parseDouble(res.getString("longitude"))));
                for (Connector invConn : connectors) {
                    if (invConn.getGeoLocation().getLongitude() == connector.getGeoLocation().getLongitude() && invConn.getGeoLocation().getLatitude() == connector.getGeoLocation().getLatitude()) {
                        exists = true;
                        invConn.addId(res.getInt("id"));
                    }
                }
                if (!exists) {
                    connector.addId(res.getInt("id"));
                    connectors.add(connector);
                }
            }

            res.close();
            stt.close();
            prep.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return connectors;
    }
}
