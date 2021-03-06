package nl.wouter0100.one2xs;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

import nl.wouter0100.one2xs.adapters.UserSyncAdapter;
import nl.wouter0100.one2xs.fragments.ForumsFragment;
import nl.wouter0100.one2xs.fragments.MessagesFragment;
import nl.wouter0100.one2xs.fragments.SectionsFragment;
import nl.wouter0100.one2xs.fragments.ThreadsFragment;
import nl.wouter0100.one2xs.models.Forum;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ForumsFragment.OnForumInteractionListener,
        MessagesFragment.OnMessageInteractionListener,
        FragmentManager.OnBackStackChangedListener {

    private AccountManager mAccountManager;
    private Account mAccount;

    private Context mContext;

    private NavigationView mNavigationView;

    private BroadcastReceiver mUserSyncFinishedReceiver;
    private FragmentManager mFragmentManager;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get and set application context
        mContext = getApplicationContext();

        // Get and set fragment manager
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(this);

        // Get account manager and all our accounts
        mAccountManager = AccountManager.get(mContext);

        // Toolbar stuff
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Connect drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mFragmentManager.popBackStack();
            }
        });
        mDrawerToggle.syncState();

        // Navigation
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        // First page is the Forum..
        mNavigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(mNavigationView.getMenu().getItem(0));

        shouldDisplayHomeUp();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Account[] accounts = mAccountManager.getAccounts();

        View navigationHeader = mNavigationView.getHeaderView(0);

        if (accounts.length >= 1) {
            // Always use first account
            mAccount = accounts[0];

            if (accounts.length >= 2) {
                // Show Toast that only one account is allowed
                Toast.makeText(mContext, R.string.one_account_allowed, Toast.LENGTH_LONG).show();
            }

            // Make some items visible, just to be sure it's there
            mNavigationView.getMenu().getItem(1).setVisible(true);

            setUserDetails();

            mUserSyncFinishedReceiver = new BroadcastReceiver(){

                @Override
                public void onReceive(Context context, Intent intent) {
                    setUserDetails();
                }
            };

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(UserSyncAdapter.FINISHED);
            mContext.registerReceiver(mUserSyncFinishedReceiver, intentFilter);
        } else {
            // No accounts yet

            // Make some items invisible
            mNavigationView.getMenu().getItem(1).setVisible(false);

            // Set on click for when a user clicks the header
            navigationHeader.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mAccountManager.addAccount(One2xsAuthenticator.ACCOUNT_TYPE, One2xsAuthenticator.AUTHTOKEN_TYPE, null, null, MainActivity.this, null, null);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mUserSyncFinishedReceiver != null) {
            mContext.unregisterReceiver(mUserSyncFinishedReceiver);
        }
    }

    private void setUserDetails() {
        View navigationHeader = mNavigationView.getHeaderView(0);

        // Get all required views we need to modify
        TextView usernameView = (TextView) navigationHeader.findViewById(R.id.text_username);
        TextView statusView = (TextView) navigationHeader.findViewById(R.id.text_status);
        ImageView avatarView = (ImageView) navigationHeader.findViewById(R.id.image_avatar);

        // Set all views values
        usernameView.setText(mAccount.name);

        String status = mAccountManager.getUserData(mAccount, "status");
        statusView.setText((status == null) ? getText(R.string.still_receiving) : status);

        try {
            Bitmap avatar = BitmapFactory.decodeStream(mContext.openFileInput("avatar.png"));
            avatarView.setImageBitmap(avatar);
        } catch (FileNotFoundException e) {
            // avatar not found
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;

        try {
            // Handle navigation view item clicks here.
            switch (item.getItemId()) {
                case R.id.nav_forum:
                    fragment = SectionsFragment.newInstance();
                    break;

                case R.id.nav_messages:
                    fragment = MessagesFragment.newInstance();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Clear back stack
        for(int i = 0; i < mFragmentManager.getBackStackEntryCount(); ++i) {
            mFragmentManager.popBackStack();
        }

        // Commit new fragment
        mFragmentManager.beginTransaction().replace(R.id.fragment_content, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp(){
        //Enable Up button only if there are entries in the back stack
        boolean canBack = getSupportFragmentManager().getBackStackEntryCount() > 0;

        mDrawerToggle.setDrawerIndicatorEnabled(!canBack);

        getSupportActionBar().setDisplayHomeAsUpEnabled(canBack);

        // Resync state
        if (!canBack) {
            mDrawerToggle.syncState();
        }
    }

    /**
     * Listener for when a user selects a forum
     *
     * @param forum Forum to load
     */
    @Override
    public void onForumSelected(Forum forum) {
        Fragment fragment = ThreadsFragment.newInstance(forum);

        mFragmentManager.beginTransaction().replace(R.id.fragment_content, fragment).addToBackStack(null).commit();
    }

    /**
     * Listener for when a user clicks a message
     *
     * @param id Id of the message to load
     * @param title Title of the message to load
     */
    @Override
    public void onMessageClicked(int id, String title) {

    }
}
