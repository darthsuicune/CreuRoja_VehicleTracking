package net.creuroja.android.vehicletracking.model.webservice;

public abstract class Response {
    public final static String IS_VALID = "isValid";
    public final static String ERROR_CODE = "errorCode";
    public final static String ERROR_MESSAGE = "errorMessage";

    public abstract boolean isValid();

    public abstract String content();

    public abstract int errorMessageResId();

    public abstract int responseCode();
}