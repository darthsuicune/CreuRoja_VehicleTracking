package net.creuroja.android.vehicletracking.model.vehicles;

import net.creuroja.android.vehicletracking.model.webservice.Response;

/**
 * Created by denis on 16.04.15.
 */
public class VehiclePositionUpdatedResponse extends Response {
    String response;
    public VehiclePositionUpdatedResponse(String input) {
        this.response = input;
    }

    @Override public boolean isValid() {
        return true;
    }

    @Override public String content() {
        return response;
    }

    @Override public int errorMessageResId() {
        return 0;
    }

    @Override public int responseCode() {
        return 201;
    }
}
