package net.creuroja.android.vehicletracking.model.vehicles;

import android.text.TextUtils;

import net.creuroja.android.vehicletracking.model.webservice.Auth;
import net.creuroja.android.vehicletracking.model.webservice.Response;
import net.creuroja.android.vehicletracking.model.webservice.ServerData;
import net.creuroja.android.vehicletracking.model.webservice.lib.RestWebServiceClient;
import net.creuroja.android.vehicletracking.model.webservice.lib.WebServiceOption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Vehicles {
	private List<Vehicle> vehicleList;

	public Vehicles(String json) throws JSONException {
		vehicleList = new ArrayList<>();
		JSONArray array = new JSONArray(json);
		for (int i = 0; i < array.length(); i++) {
			extractVehicle(array.getJSONObject(i));
		}
	}

	private void extractVehicle(JSONObject item) throws JSONException {
		if (!TextUtils.isEmpty(item.getString(ServerData.INDICATIVE)) &&
			!item.getString(ServerData.INDICATIVE).equals("null")) {
			vehicleList.add(new Vehicle(item));
		}
	}

	public List<Vehicle> getAsList() {
		return vehicleList;
	}

	public static List<Vehicle> getFromServer(String accessToken)
			throws JSONException, IOException {
		String jsonResponse = requestVehicleList(accessToken);
		Vehicles vehicles = new Vehicles(jsonResponse);
		return vehicles.getAsList();
	}

	private static String requestVehicleList(String accessToken) throws IOException {
		RestWebServiceClient client =
				new RestWebServiceClient(new VehicleResponseFactory(), ServerData.PROTOCOL,
						ServerData.SERVER_ADDRESS);
		List<WebServiceOption> options = headerOptions(accessToken);
		Response response = client.get(ServerData.RESOURCE_VEHICLES + ".json", options,
				WebServiceOption.empty());
		return response.content();
	}

	private static List<WebServiceOption> headerOptions(String accessToken) {
		List<WebServiceOption> options = new ArrayList<>();
		options.add(Auth.getAuthOption(accessToken));
		return options;
	}
}
