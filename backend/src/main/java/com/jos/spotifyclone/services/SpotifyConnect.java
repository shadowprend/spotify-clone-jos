package com.jos.spotifyclone.services;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;

@Component
public class SpotifyConnect {
    private static final String CLIENT_ID = "5ed267b3c0874b44838007d52dfaa70d";
    private static final String SECRET_ID = "1967c9034c894936813766d7481d8782";
    private static final String REDIRECT_URI = "http://localhost:8080/api/spotify-auth/callback";
    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(CLIENT_ID)
            .setClientSecret(SECRET_ID)
            .setRedirectUri(SpotifyHttpManager.makeUri(REDIRECT_URI))
            .build();
    private static final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
            .build();

    @PostConstruct
    public void openAuthWindow() {
        final URI uri = authorizationCodeUriRequest.execute();
        Runtime runtime = Runtime.getRuntime();
      
        try {
            runtime.exec("rundll32 url.dll,FileProtocolHandler " + uri);
        } catch (IOException e) {
            System.out.println("If you're running on Windows and read this it looks like we can't open your browser...");
        }
        try {
            runtime.exec("open " + uri);
        } catch (IOException e) {
            System.out.println("If you're running on MacOS and read this it looks like we can't open your browser...");
        }
    }

    public  void addAuthCode(String code) throws ParseException, SpotifyWebApiException, IOException {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();

        final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

        // Set access and refresh token for further "spotifyApi" object usage
        spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
        spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
    }

    public SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }
}
