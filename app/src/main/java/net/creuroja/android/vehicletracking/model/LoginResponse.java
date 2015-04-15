package net.creuroja.android.vehicletracking.model;

import net.creuroja.android.vehicletracking.model.webservice.Response;

/**
 * Created by denis on 16.04.15.
 */
public class LoginResponse extends Response {
    private final String token;

    public LoginResponse(String token) {
        this.token = token;
    }
    @Override public boolean isValid() {
        return true;
    }

    @Override public String content() {
        return token;
    }

    @Override public int errorMessageResId() {
        return 0;
    }

    @Override public int responseCode() {
        return 200;
    }
}
