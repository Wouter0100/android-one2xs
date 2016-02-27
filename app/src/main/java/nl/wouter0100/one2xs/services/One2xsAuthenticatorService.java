package nl.wouter0100.one2xs.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import nl.wouter0100.one2xs.One2xsAuthenticator;

public class One2xsAuthenticatorService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        One2xsAuthenticator authenticator = new One2xsAuthenticator(this);
        return authenticator.getIBinder();
    }
}
