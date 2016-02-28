package nl.wouter0100.one2xs.adapters;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import nl.wouter0100.one2xs.One2xsAuthenticator;
import nl.wouter0100.one2xs.utilities.LoginUtilities;
import nl.wouter0100.one2xs.utilities.RequestUtilities;

public class UserSyncAdapter extends AbstractThreadedSyncAdapter {

    private final AccountManager mAccountManager;

    public UserSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        try {
            String authToken = mAccountManager.blockingGetAuthToken(account, One2xsAuthenticator.ACCOUNT_TYPE, true);

            if (!LoginUtilities.checkAuthToken(authToken)) {
                mAccountManager.invalidateAuthToken(One2xsAuthenticator.ACCOUNT_TYPE, authToken);

                authToken = mAccountManager.blockingGetAuthToken(account, One2xsAuthenticator.ACCOUNT_TYPE, true);
            }

            Connection profileConnection = Jsoup.connect("http://www.one2xs.com/profiel/mijnprofiel");

            profileConnection = RequestUtilities.setAuthToken(profileConnection, authToken);

            Response profileResponse = RequestUtilities.get(profileConnection);
            Document profileDocument = profileResponse.parse();

            System.out.println(profileDocument.html());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
