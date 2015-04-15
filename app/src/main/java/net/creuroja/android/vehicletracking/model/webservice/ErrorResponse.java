package net.creuroja.android.vehicletracking.model.webservice;

import net.creuroja.android.vehicletracking.R;

public class ErrorResponse extends Response {
    private final String content;
    private final int responseCode;

    public ErrorResponse(String content, int responseCode) {
        this.content = content;
        this.responseCode = responseCode;
    }

    @Override public boolean isValid() {
        return false;
    }

    @Override public String content() {
        return content;
    }

    @Override public int errorMessageResId() {
        return R.string.common_google_play_services_network_error_text;
    }

    @Override public int responseCode() {
        return responseCode;
    }
}