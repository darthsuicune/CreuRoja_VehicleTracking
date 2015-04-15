package net.creuroja.android.vehicletracking.model;

import net.creuroja.android.vehicletracking.model.webservice.ErrorResponse;
import net.creuroja.android.vehicletracking.model.webservice.Response;
import net.creuroja.android.vehicletracking.model.webservice.ResponseFactory;
import net.creuroja.android.vehicletracking.model.webservice.ServerData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by denis on 16.04.15.
 */
public class LoginResponseFactory extends ResponseFactory {

    String authToken;

    public LoginResponseFactory() {
    }


    @Override public Response fillResponseData(String response) {
        String errorMessage = "";
        int errorCode = -1;
        try {
            JSONObject object = new JSONObject(response);
            if (object.has(ServerData.AUTH_TOKEN_HOLDER)) {
                authToken = object.getString(ServerData.AUTH_TOKEN_HOLDER);
            } else {
                errorCode = object.getInt(Response.ERROR_CODE);
                errorMessage = object.getString(Response.ERROR_MESSAGE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new ErrorResponse("Malformed JSONObject", 500);
        }
        if(errorCode != -1) {
            return new ErrorResponse(errorMessage, errorCode);
        }
        return new LoginResponse(authToken);
    }
}
