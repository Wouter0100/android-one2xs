package nl.wouter0100.one2xs;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import nl.wouter0100.one2xs.utilities.LoginUtilities;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;

public class One2xsAuthenticator extends AbstractAccountAuthenticator {

    public static final String AUTHTOKEN_TYPE = "nl.wouter0100.one2xs";
    public static final String ACCOUNT_TYPE = "nl.wouter0100.one2xs";
    private static final int ERROR_ONE_ACCOUNT_ALLOWED = 2001;
    private final Context mContext;
    private final AccountManager mAccountManager;

    public One2xsAuthenticator(Context context) {
        super(context);

        this.mContext = context;
        this.mAccountManager = AccountManager.get(context);
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Account[] accounts = mAccountManager.getAccounts();

        // Only allow one account to be logged in
        if (accounts.length >= 1) {
            final Bundle result = new Bundle();
            result.putInt(AccountManager.KEY_ERROR_CODE, ERROR_ONE_ACCOUNT_ALLOWED);
            result.putString(AccountManager.KEY_ERROR_MESSAGE, mContext.getString(R.string.one_account_allowed));
            return result;
        }

        // Open login
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        System.out.println("authtoken called");
        // Check if correct authtoken type
        if (!authTokenType.equals(One2xsAuthenticator.AUTHTOKEN_TYPE)) {
            // If not, return error
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "Invalid authTokenType");
            return result;
        }

        // Check if auth token is still cached
        String authToken = mAccountManager.peekAuthToken(account, authTokenType);

        // If there is no authToken, get a new one
        if (TextUtils.isEmpty(authToken)) {
            String password = mAccountManager.getPassword(account);

            // We may don't have a password
            if (password != null) {
                try {
                    authToken = LoginUtilities.login(account.name, password);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Check if the authToken is set at this stage
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // We weren't able to get the password and authtoken, show login
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_NAME, account.name);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (authTokenType.equals(One2xsAuthenticator.AUTHTOKEN_TYPE)) {
            return mContext.getString(R.string.app_name);
        }
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }
}
