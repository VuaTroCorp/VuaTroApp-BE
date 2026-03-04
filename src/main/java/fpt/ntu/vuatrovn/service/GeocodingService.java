package fpt.ntu.vuatrovn.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate = new RestTemplate();

public double[] getCoordinates(String address) {

    try {

        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);

        String url = "https://nominatim.openstreetmap.org/search?q="
                + encodedAddress
                + "&format=json&limit=1";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "VuaTroVN");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
        JSONArray array = (JSONArray) parser.parse(response.getBody());

        if (array.isEmpty()) {
            throw new RuntimeException("Không tìm thấy địa chỉ");
        }

        JSONObject obj = (JSONObject) array.get(0);

        double lat = Double.parseDouble(obj.getAsString("lat"));
        double lon = Double.parseDouble(obj.getAsString("lon"));

        return new double[]{lat, lon};

    } catch (Exception e) {
        throw new RuntimeException("Lỗi khi gọi Geocoding API", e);
    }
}
}