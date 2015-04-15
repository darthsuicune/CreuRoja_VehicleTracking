package net.creuroja.android.vehicletracking.model.webservice;

import net.creuroja.android.vehicletracking.model.webservice.lib.RestWebServiceClient;
import net.creuroja.android.vehicletracking.model.webservice.lib.WebServiceOption;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 05.09.14.
 */
public class Auth {
    RestWebServiceClient client;
    public String email;
    public String password;

    public Auth(RestWebServiceClient client, String email, String password) {
        this.client = client;
        this.email = email;
        this.password = password;
    }

    public String getToken() throws IOException {
        List<WebServiceOption> postOptions = getLoginOptions(email, password);
        Response response =
                client.post(ServerData.RESOURCE_SESSIONS, null, null, postOptions);
        return response.content();
    }

    private List<WebServiceOption> getLoginOptions(String email, String password) {
        List<WebServiceOption> options = new ArrayList<>();
        options.add(new WebServiceOption(ServerData.ARG_EMAIL, email));
        options.add(new WebServiceOption(ServerData.ARG_PASSWORD, password));
        return options;
    }

    public static WebServiceOption getAuthOption(String accessToken) {
        return new WebServiceOption(ServerData.ARG_ACCESS_TOKEN, "Token token=" + accessToken);
    }
}
