package nl.wouter0100.one2xs.utilities;

import android.text.TextUtils;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import nl.wouter0100.one2xs.exceptions.AuthTokenException;
import nl.wouter0100.one2xs.exceptions.LoginException;

public class LoginUtilities {

    public static String login(String username, String password) throws LoginException {
        try {
            Connection loginConnection = Jsoup
                    .connect("http://www.one2xs.com/login")
                    .data("gebruikersnaam", username)
                    .data("wachtwoord", password)
                    .data("submit", "");

            Response loginResponse = RequestUtilities.post(loginConnection);
            Document loginDocument = loginResponse.parse();

            if (loginDocument.getElementsByClass("error").first() == null) {
                for (String key : loginResponse.cookies().keySet()) {
                    if (key.startsWith("store")) {
                        return key + "=" + loginResponse.cookie(key);
                    }
                }
                throw new LoginException("Unable to find login cookie");
            } else {
                throw new LoginException(loginDocument.getElementsByClass("error").first().text());
            }

        } catch (IOException e) {
            throw new LoginException(e.getMessage());
        }
    }

    public static boolean checkAuthToken(String authToken) throws AuthTokenException {
        if (TextUtils.isEmpty(authToken)) {
            return false;
        }

        try {
            Connection profileConnection = Jsoup
                    .connect("http://www.one2xs.com/index");

            profileConnection = RequestUtilities.setAuthToken(profileConnection, authToken);

            Response profileRequest = RequestUtilities.post(profileConnection);
            Document profileDocument = profileRequest.parse();

            return profileDocument.html().contains("Je bent ingelogd als");
        } catch (IOException e) {
            throw new AuthTokenException(e.getMessage());
        }
    }
}
