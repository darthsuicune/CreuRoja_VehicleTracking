package net.creuroja.android.vehicletracking.model.vehicles;

import net.creuroja.android.vehicletracking.model.webservice.Response;
import net.creuroja.android.vehicletracking.model.webservice.ResponseFactory;

/**
 * Created by denis on 15.04.15.
 */
public class VehicleResponseFactory extends ResponseFactory {

    @Override public Response fillResponseData(String input) {
        Response response = new VehicleResponse(input);
        return response;
    }
}
