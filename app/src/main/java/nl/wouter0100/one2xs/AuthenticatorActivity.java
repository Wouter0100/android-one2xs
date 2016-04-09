package nl.wouter0100.one2xs;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import nl.wouter0100.one2xs.exceptions.LoginException;
import nl.wouter0100.one2xs.utilities.LoginUtilities;

public class AuthenticatorActivity extends AppCompatActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public final static String ARG_PASSWORD = "PASSWORD";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private Bundle mResultBundle = null;

    private TextInputLayout mUsernameLayout;
    private TextInputLayout mPasswordLayout;
    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // General layout things
        setContentView(R.layout.activity_authenticator);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set all private variables
        mAccountManager = AccountManager.get(getBaseContext());
        mUsernameLayout = (TextInputLayout) findViewById(R.id.input_text_username);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.input_text_password);

        // Set username when it's found
        String username = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        if (username != null) {
            EditText usernameField = mUsernameLayout.getEditText();
            usernameField.setInputType(InputType.TYPE_NULL);
            usernameField.setText(username);
        }

        // Some logic from the AccountAuthenticatorActivity because we want the AppCompactActivity
        mAccountAuthenticatorResponse =
                getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }

        AppCompatButton loginButton = (AppCompatButton) findViewById(R.id.btn_login);

        // set the OnClick listener for the login-button
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = mUsernameLayout.getEditText().getText().toString();
                String password = mPasswordLayout.getEditText().getText().toString();

                boolean hasValidationError = false;

                if (!validateUsername(username)) {
                    mUsernameLayout.setError(getString(R.string.error_invalid_username));
                    hasValidationError = true;
                } else {
                    mUsernameLayout.setErrorEnabled(false);
                }

                if (!validatePassword(password)) {
                    mPasswordLayout.setError(getString(R.string.error_invalid_password));
                    hasValidationError = true;
                } else {
                    mPasswordLayout.setErrorEnabled(false);
                }

                if (!hasValidationError) {
                    // Handle login
                    handleLogin(username, password);
                }
            }
        });
    }

    /**
     * Validate username on it's requirements
     *
     * @param username Username to validate
     */
    private boolean validateUsername(String username) {
        return username.length() >= 5 && username.length() <= 12;
    }

    /**
     * Validate password on it's requirements
     *
     * @param password Password to validate
     */
    private boolean validatePassword(String password) {
        return password.length() >= 5 && password.length() <= 20;
    }

    /**
     * Handle the login event when all validation is passed
     *
     * @param username Username to login with
     * @param password Password to login with
     */
    private void handleLogin(final String username, final String password) {
        // Start loading dialog
        final ProgressDialog progressDialog = new ProgressDialog(AuthenticatorActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        AsyncTask<Void, Void, Intent> loginTask = new AsyncTask<Void, Void, Intent>() {

            @Override
            protected Intent doInBackground(Void... params) {
                Bundle data = new Bundle();

                try {
                    String authToken = LoginUtilities.login(username, password);

                    data.putString(AccountManager.KEY_ACCOUNT_NAME, username);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, One2xsAuthenticator.ACCOUNT_TYPE);
                    data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                    data.putString(AuthenticatorActivity.ARG_PASSWORD, password);
                } catch (LoginException e) {
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }

                Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent res) {
                progressDialog.dismiss();

                if (res.hasExtra(KEY_ERROR_MESSAGE)) {
                    Snackbar.make(findViewById(R.id.snackbarPosition), res.getStringExtra(KEY_ERROR_MESSAGE), Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    finishLogin(res);
                }
            }
        };

        loginTask.execute();
    }

    /**
     * Finish the login
     *
     * @param intent Intent with all the necessary data
     */
    private void finishLogin(Intent intent) {

        String username = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String password = intent.getStringExtra(AuthenticatorActivity.ARG_PASSWORD);
        String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);

        Account account = new Account(username, accountType);

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            mAccountManager.addAccountExplicitly(account, password, null);
            mAccountManager.setAuthToken(account, One2xsAuthenticator.AUTHTOKEN_TYPE, authToken);
        } else {
            mAccountManager.setPassword(account, password);
        }

        ContentResolver.setSyncAutomatically(account, "nl.wouter0100.one2xs.providers", true);

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Sends the result or a Constants.ERROR_CODE_CANCELED error if a result isn't present
     */
    public void finish() {
        if (mAccountAuthenticatorResponse != null) {
            // send the result bundle back if set, otherwise send an error.
            if (mResultBundle != null) {
                mAccountAuthenticatorResponse.onResult(mResultBundle);
            } else {
                mAccountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED,
                        "canceled");
            }
            mAccountAuthenticatorResponse = null;
        }

        super.finish();
    }

    /**
     * Set the result that is to be sent as the result of the request that caused this
     * Activity to be launched. If result is null or this method is never called then
     * the request will be canceled.
     *
     * @param result this is returned as the result of the AbstractAccountAuthenticator request
     */
    public final void setAccountAuthenticatorResult(Bundle result) {
        mResultBundle = result;
    }
}
