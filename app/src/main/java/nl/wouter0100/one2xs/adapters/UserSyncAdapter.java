package nl.wouter0100.one2xs.adapters;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.io.OutputStream;

import nl.wouter0100.one2xs.MainActivity;
import nl.wouter0100.one2xs.One2xsAuthenticator;
import nl.wouter0100.one2xs.utilities.LoginUtilities;
import nl.wouter0100.one2xs.utilities.RequestUtilities;

public class UserSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String FINISHED = "FINISHED";

    private final AccountManager mAccountManager;
    private final Context mContext;

    public UserSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContext = context;
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

            Element table = profileDocument.getElementById("content").select("table").first();

            for (Element tableRows : table.select("tr")) {
                Elements tableData = tableRows.select("td");

                String key = tableData.first().text();
                Element value = tableData.last();

                switch(key) {
                    case "Status":
                        mAccountManager.setUserData(account, "status", value.text());
                        break;

                    case "Lid sinds":
                        mAccountManager.setUserData(account, "member_since", value.text());
                        break;

                    case "Referral van":
                        mAccountManager.setUserData(account, "referral_of", value.text());
                        break;

                    case "Aantal posts":
                        mAccountManager.setUserData(account, "post_count", value.text().replace(".", ""));
                        break;

                    case "Locatie":
                        mAccountManager.setUserData(account, "location", value.text());
                        break;

                    case "Avatar":
                        mAccountManager.setUserData(account, "avatar_uri", value.select("img").first().attr("src"));
                        break;
                }
            }

            // Download avatar
            String avatarUri = mAccountManager.getUserData(account, "avatar_uri");

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).build();

            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);

            Bitmap avatarBitmap = imageLoader.loadImageSync(avatarUri);

            OutputStream avatarOutputStream = mContext.openFileOutput("avatar.png", Context.MODE_PRIVATE);

            avatarBitmap.compress(Bitmap.CompressFormat.PNG, 100, avatarOutputStream);

            avatarOutputStream.close();

            // Notify front-end
            Intent intent = new Intent();
            intent.setAction(UserSyncAdapter.FINISHED);
            mContext.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
