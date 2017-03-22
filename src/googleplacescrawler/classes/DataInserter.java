package googleplacescrawler.classes;

import googleplacescrawler.objects.Connector;
import googleplacescrawler.objects.Place;

import java.sql.*;
import java.util.List;

public class DataInserter {

    public void setPlaces(List<Connector> connectors) {
        String url = "jdbc:mysql://localhost:3306/";
        String user = "root";
        String password = "";

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection(url, user, password);

            Statement stt = con.createStatement();

            stt.execute("USE x");

            createCompanyTable(con);
            createLinkedTable(con);

            for(Connector connector : connectors) {
                String key = "0";
                for(Place place : connector.getPlaces()) {
                    PreparedStatement prep = con.prepareStatement("INSERT INTO bedrijven(bedrijfsnaam, adres, type, rating, lengtegraad, breedtegraad) "
                                                                  + "SELECT * FROM (SELECT ?, ?, ?, ?, ?, ?) AS tmp "
                                                                  + "WHERE NOT EXISTS ("
                                                                  + "   SELECT bedrijfsnaam, adres, type FROM bedrijven WHERE bedrijfsnaam = ? AND adres = ? AND type = ?"
                                                                  + ") LIMIT 1;");
                    prep.setString(1, place.getName());
                    prep.setString(2, place.getAddress());
                    prep.setString(3, place.getType());
                    prep.setString(4, "" + place.getRating());
                    prep.setString(5, "" + place.getGeoLocation().getLongitude());
                    prep.setString(6, "" + place.getGeoLocation().getLatitude());
                    prep.setString(7, place.getName());
                    prep.setString(8, place.getAddress());
                    prep.setString(9, place.getType());
                    prep.executeUpdate();

                    for(int id : connector.getId()) {
                        String s = "0";
                        prep = con.prepareStatement("SELECT bedrijf_id FROM `bedrijven` WHERE bedrijfsnaam = ? AND adres = ? AND type = ? ");
                        prep.setString(1, place.getName());
                        prep.setString(2, place.getAddress());
                        prep.setString(3, place.getType());
                        ResultSet set = prep.executeQuery();
                        while(set.next()) {
                            s = set.getString("bedrijf_id");
                        }

                        prep = con.prepareStatement("INSERT INTO connector_bedrijven(connector_id, bedrijf_id) "
                                + "SELECT * FROM (SELECT ?, ?) AS tmp "
                                + "WHERE NOT EXISTS ("
                                + "   SELECT connector_id, bedrijf_id FROM connector_bedrijven WHERE connector_id = ? AND bedrijf_id = ?"
                                + ") LIMIT 1;");
                        prep.setString(1, "" + id);
                        prep.setString(2, s);
                        prep.setString(3, "" + id);
                        prep.setString(4, s);
                        prep.executeUpdate();

                    }
                    prep.close();
                }
            }

            stt.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createCompanyTable(Connection con) throws SQLException {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + "bedrijven"
                + "  (bedrijf_id INT(6) AUTO_INCREMENT PRIMARY KEY,"
                + "   bedrijfsnaam VARCHAR(255) NOT NULL,"
                + "   adres VARCHAR(255) NOT NULL,"
                + "   type VARCHAR(255) NOT NULL,"
                + "   rating DOUBLE,"
                + "   lengtegraad DECIMAL(9,6) NOT NULL,"
                + "   breedtegraad DECIMAL(9,6) NOT NULL)";

        Statement stt = con.createStatement();
        stt.execute(sqlCreate);
    }

    private void createLinkedTable(Connection con) throws SQLException {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + "connector_bedrijven"
                + "  (connector_id INT,"
                + "   bedrijf_id INT,"
                + "   PRIMARY KEY (connector_id, bedrijf_id),"
                + "   FOREIGN KEY (connector_id) REFERENCES connectoren(connector_id),"
                + "   FOREIGN KEY (bedrijf_id) REFERENCES bedrijven(bedrijf_id))";

        Statement stt = con.createStatement();
        stt.execute(sqlCreate);
    }
}
