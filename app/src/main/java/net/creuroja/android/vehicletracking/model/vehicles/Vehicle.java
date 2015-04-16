package net.creuroja.android.vehicletracking.model.vehicles;

import net.creuroja.android.vehicletracking.model.webservice.Auth;
import net.creuroja.android.vehicletracking.model.webservice.Response;
import net.creuroja.android.vehicletracking.model.webservice.ServerData;
import net.creuroja.android.vehicletracking.model.webservice.lib.RestWebServiceClient;
import net.creuroja.android.vehicletracking.model.webservice.lib.WebServiceOption;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Vehicle {
	public final String indicative;
	public double latitude;
	public double longitude;
	public int id;

	public Vehicle(JSONObject json) throws JSONException {
		this.indicative = json.getString(ServerData.INDICATIVE);
		this.id = json.getInt(ServerData.ID);
	}

	public Vehicle(int id, String indicative) {
		this.indicative = indicative;
		this.id = id;
	}

	public void setPosition(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	// The server expects {vehicle_position: { vehicle_id: id, latitude: lat, longitude: lng } }
	public void upload(String accessToken) throws IOException, JSONException {
		RestWebServiceClient client = new RestWebServiceClient(new UpdatedLocationResponseFactory(),
				ServerData.PROTOCOL, ServerData.SERVER_ADDRESS);
		Response response = client.post(ServerData.RESOURCE_NEW_VEHICLE_POSITION + ".json",
				headerOptions(accessToken), WebServiceOption.empty(), postOptions());
		if(response.responseCode() != 201) {
			throw new IOException(response.content());
		}
	}

	private List<WebServiceOption> headerOptions(String accessToken) {
		List<WebServiceOption> list = new ArrayList<>();
		list.add(Auth.getAuthOption(accessToken));
		list.add(new WebServiceOption("Content-Type", "application/json"));
		return list;
	}

	private List<WebServiceOption> postOptions() throws JSONException {
		List<WebServiceOption> list = new ArrayList<>();
		list.add(new WebServiceOption(ServerData.ARG_VEHICLE_POSITION, asJson().toString()));
		return list;
	}

	public JSONObject asJson() throws JSONException {
		JSONObject values = new JSONObject();
		values.put(ServerData.ARG_VEHICLE_ID, Integer.toString(id));
		values.put(ServerData.ARG_VEHICLE_LATITUDE, Double.toString(latitude));
		values.put(ServerData.ARG_VEHICLE_LONGITUDE, Double.toString(longitude));
		return values;
	}

	public String toString() {
		return indicative;
	}
}
