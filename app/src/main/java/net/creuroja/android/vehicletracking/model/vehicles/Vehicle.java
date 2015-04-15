package net.creuroja.android.vehicletracking.model.vehicles;

import net.creuroja.android.vehicletracking.model.webservice.Auth;
import net.creuroja.android.vehicletracking.model.webservice.ServerData;
import net.creuroja.android.vehicletracking.model.webservice.lib.RestWebServiceClient;
import net.creuroja.android.vehicletracking.model.webservice.lib.WebServiceOption;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public void upload(String accessToken) throws IOException {
		RestWebServiceClient client = new RestWebServiceClient(new UpdatedLocationResponseFactory(),
				ServerData.PROTOCOL, ServerData.SERVER_ADDRESS);
		client.post(ServerData.RESOURCE_NEW_VEHICLE_POSITION + ".json",
				headerOptions(accessToken), null, postOptions());

	}

	private List<WebServiceOption> headerOptions(String accessToken) {
		List<WebServiceOption> list = new ArrayList<>();
		list.add(Auth.getAuthOption(accessToken));
		list.add(new WebServiceOption("Content-Type", "application/json"));
		return list;
	}

	private List<WebServiceOption> postOptions() {
		List<WebServiceOption> list = new ArrayList<>();
		list.add(new WebServiceOption(ServerData.ARG_VEHICLE_POSITION, asJson().toString()));
		return list;
	}

	public JSONObject asJson() {
		Map<String, Map<String, String>> holder = new HashMap<>();
		Map<String, String> values = new HashMap<>();
		values.put(ServerData.ARG_VEHICLE_ID, Integer.toString(id));
		values.put(ServerData.ARG_VEHICLE_LATITUDE, Double.toString(latitude));
		values.put(ServerData.ARG_VEHICLE_LONGITUDE, Double.toString(longitude));
		holder.put(ServerData.ARG_VEHICLE_POSITION, values);
		return new JSONObject(holder);
	}

	public String toString() {
		return indicative;
	}
}
