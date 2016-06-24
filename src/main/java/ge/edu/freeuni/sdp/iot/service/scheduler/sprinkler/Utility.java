package ge.edu.freeuni.sdp.iot.service.scheduler.sprinkler;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import ge.edu.freeuni.sdp.iot.service.scheduler.sprinkler.shchedule.Schedule;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by GM on 6/24/2016.
 */
public class Utility {
    private Map<Integer, Pair> houseIDAndLocations;
    private Map<Integer, Schedule> houseIDAndSchedules;

    public Utility(){
       this.houseIDAndLocations = new HashMap<>();
        this.houseIDAndSchedules = new HashMap<>();

        Thread refresher = new Thread(new Runnable() {
            public void run() {
                refreshData();
                try {
                    Thread.sleep(1800000);
                }
                catch (InterruptedException e){
                    System.out.println("pfffff");
                }
            }
        });
        refresher.start();
    }

    public void refreshData(){
        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);

        if(hours*3600 + minutes*60 + seconds < 1800){
            fetchNewDataFromLinks();
        }
    }

    private void fetchNewDataFromLinks(){
        this.houseIDAndLocations = getHousesData();
    }

    public Set<Integer> getHouseIDS(){
        return this.houseIDAndLocations.keySet();
    }

    public Schedule getHouseScheduleByID(int id){
        return this.houseIDAndSchedules.get(id);
    }

    /**
     * Fetches sunset times and sunrise times
     */
    public Pair<String> getSunData(int house_id) {
        Pair<Double> coordinates = this.houseIDAndLocations.get(house_id);
        double langtitude = coordinates.first;
        double longtitude = coordinates.second;

        Client client = ClientBuilder.newClient();
        Response response = client.target("http://api.sunrise-sunset.org/json")
                .queryParam("lat", langtitude)
                .queryParam("lng", longtitude)
                .queryParam("date", "today")
                .request(MediaType.TEXT_PLAIN_TYPE)
                .get();

        if (response.getStatus() == 200) {
            String jsonString = response.readEntity(String.class);
            JsonObject items = Json.parse(jsonString).asObject().get("results").asObject();
            String sunrise = items.asObject().getString("sunrise", "6:00:00 AM");
            String sunset = items.asObject().getString("sunset", "7:00:00 PM");
            System.out.println(sunrise + "    " + sunset);
            return new Pair<>(sunrise, sunset);
        } else {
            System.out.println("Could not load information about the sunset and sunrise");
        }
        return null;
    }

    /**
     * Fetches houses IDs
     */
    public Map<Integer, Pair> getHousesData() {
        Map<Integer, Pair> houseIDAndLocations = new HashMap<>();
        Client client = ClientBuilder.newClient();
        Response response = client.target("http://private-0ab61f-iothouseregistry.apiary-mock.com/houses")
                .request(MediaType.TEXT_PLAIN_TYPE)
                .get();
        if (response.getStatus() == 200) {
            String jsonString = response.readEntity(String.class);
            JsonArray houses = Json.parse(jsonString).asArray();
            System.out.println(houses);
            for (JsonValue house : houses) {
                JsonValue house_id = house.asObject().get("name");
                String id_string = house_id.asObject().getString("_", "-1");

                String[] houseNameParts = id_string.split("#");
                int houseID = Integer.parseInt(houseNameParts[1]);

                JsonValue house_location = house.asObject().get("geo_location");
                String location_string = house_location.asObject().getString("_", "-1");

                String[] parts = location_string.split(",");
                double latitude = Double.parseDouble(parts[0]);
                double longtitude = Double.parseDouble(parts[1]);
                Pair<Double> geoLoc = new Pair<>(latitude, longtitude);
                System.out.println("house id is:   " + houseID + "  coords are:   " + latitude + "  aaand  " + longtitude);
                houseIDAndLocations.put(houseID, geoLoc);
            }
        } else {
            System.out.println("Could not load houses' IDs");
        }
        return houseIDAndLocations;
    }

    private static class Pair<T> {
        T first, second;

        Pair(T a, T b) {
            this.first = a;
            this.second = b;
        }

    }



}