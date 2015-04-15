package net.creuroja.android.vehicletracking.model.vehicles;

import net.creuroja.android.vehicletracking.model.webservice.Response;

/**
 * Created by denis on 15.04.15.
 */
public class VehicleResponse extends Response {
    String vehicle;

    public VehicleResponse(String input) {
        this.vehicle = input;
    }

    @Override public boolean isValid() {
        return true;
    }

    @Override public String content() {
        return vehicle;
    }

    @Override public int errorMessageResId() {
        return 0;
    }

    @Override public int responseCode() {
        return 200;
    }
}
