package net.creuroja.android.vehicletracking.model.vehicles;

import net.creuroja.android.vehicletracking.model.webservice.Response;
import net.creuroja.android.vehicletracking.model.webservice.ResponseFactory;

/**
 * Created by denis on 16.04.15.
 */
public class UpdatedLocationResponseFactory extends ResponseFactory {
    public UpdatedLocationResponseFactory() {
    }
    @Override public Response fillResponseData(String input) {
        return new VehiclePositionUpdatedResponse(input);
    }
}
