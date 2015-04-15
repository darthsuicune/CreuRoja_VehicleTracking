package net.creuroja.android.vehicletracking.fragments.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import net.creuroja.android.vehicletracking.model.vehicles.Vehicle;
import net.creuroja.android.vehicletracking.model.vehicles.Vehicles;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 04.09.14.
 */
public class VehicleLoader extends AsyncTaskLoader<List<Vehicle>> {
	List<Vehicle> list = new ArrayList<>();
	String token;

	public VehicleLoader(Context context, String token) {
		super(context);
		this.token = token;
	}

	@Override protected void onStartLoading() {
		super.onStartLoading();
		if (list.isEmpty()) {
			forceLoad();
		}
	}

	@Override public List<Vehicle> loadInBackground() {
		try {
			list = Vehicles.getFromServer(token);
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		return list;
	}
}
