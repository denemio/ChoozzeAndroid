package com.dennisvandalen.choozze;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dennisvandalen.choozze.event.FetchUsageEvent;
import com.dennisvandalen.choozze.event.JsoupFailedEvent;
import com.dennisvandalen.choozze.event.UsageEvent;
import com.prashantsolanki.secureprefmanager.SecurePrefManager;
import com.prashantsolanki.secureprefmanager.SecurePrefManagerInit;
import com.squareup.otto.Subscribe;

import org.jsoup.nodes.Document;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.sms)
    TextView textViewSms;

    @Bind(R.id.smsUsed)
    TextView textViewSmsUsed;

    @Bind(R.id.call)
    TextView textViewCall;

    @Bind(R.id.callUsed)
    TextView textViewCallUsed;

    @Bind(R.id.internet)
    TextView textViewInternet;

    @Bind(R.id.internetUsed)
    TextView textViewInternetUsed;

    @Bind(R.id.other)
    TextView textViewOther;

    @Bind(R.id.costs)
    TextView textViewCosts;

    @Bind(R.id.type)
    TextView textViewType;

    @Bind(R.id.status)
    TextView textViewStatus;

    @Bind(R.id.refresh_button)
    FloatingActionButton refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // @todo: refactor
        new SecurePrefManagerInit.Initializer(getApplicationContext())
                .useEncryption(true)
                .initialize();
    }

    @OnClick(R.id.refresh_button)
    public void refreshClick(View v) {
        handleLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register ourselves so that we can provide the initial value.
        ChoozzeApplication.bus.register(this);
        handleLogin();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Always unregister when an object no longer should be on the bus.
        ChoozzeApplication.bus.unregister(this);
    }

    /**
     * Try login and fetch usage
     *
     * @todo: refactor
     */
    private void handleLogin() {
        startRotateRefresh();

        String username = SecurePrefManager.with(this)
                                           .get("login_username")
                                           .defaultValue("")
                                           .go();

        String password = SecurePrefManager.with(this)
                                           .get("password")
                                           .defaultValue("")
                                           .go();


        if ("".equals(username)) {
            // Show dialog when we dont have an username
            loginDialog();
        } else {
            // Fetch usage, when credentials are set
            ChoozzeApplication.bus.post(new FetchUsageEvent(username, password));
        }
    }

    /**
     * Show or hide the data
     *
     * @param show boolean to show (true) or hide (false) the data
     */
    public void showResults(boolean show) {
        View hasResults = this.findViewById(R.id.hasResults);
        View noResults = this.findViewById(R.id.noResults);

        if (show) {
            hasResults.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        } else {
            hasResults.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Generate a login dialog
     *
     * @todo: refactor
     */
    private void loginDialog() {
        final MainActivity mainActivity = this;
        final Dialog login = new Dialog(this);

        login.setContentView(R.layout.login_dialog);
        login.setTitle(getString(R.string.login));

        Button btnLogin = (Button) login.findViewById(R.id.btnLogin);
        Button btnCancel = (Button) login.findViewById(R.id.btnCancel);

        // fix, width of dialog
        WindowManager.LayoutParams params = login.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        login.getWindow().setAttributes(params);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((EditText) login.findViewById(R.id.txtUsername)).getText().toString();
                String password = ((EditText) login.findViewById(R.id.txtPassword)).getText().toString();

                if (!"".equals(username) && !"".equals(password)) {
                    SecurePrefManager.with(mainActivity)
                                     .set("login_username")
                                     .value(username)
                                     .go();

                    SecurePrefManager.with(mainActivity)
                                     .set("password")
                                     .value(password)
                                     .go();

                    mainActivity.handleLogin();
                    login.dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.dismiss();
            }
        });

        login.show();
    }

    /**
     * Handle UsageEvent, when data is received from the Choozze server
     *
     * @param usageEvent
     */
    @Subscribe
    public void get(UsageEvent usageEvent) {
        Document document = usageEvent.getDocument();

        if (document == null || !document.select("body > div.container > div > p:nth-child(4)").hasText()) {
            loginDialog();
            Snackbar.make(getWindow().getDecorView().getRootView(), R.string.login_failed, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();

            return;
        }

        UsageParser usage = new UsageParser(document);
        String[] callData = usage.getCall();
        String[] textData = usage.getSms();
        String[] internetData = usage.getInternet();

        textViewCallUsed.setText(String.format("%s%%", callData[1]));
        textViewCall.setText(String.format("%s\n%s %s", getString(R.string.of_the), callData[0], getString(R.string.minutes)));

        textViewSmsUsed.setText(String.format("%s%%", textData[1]));
        textViewSms.setText(String.format("%s\n%s %s", getString(R.string.of_the), textData[0], getString(R.string.texts)));

        textViewInternetUsed.setText(String.format("%s%%", internetData[1]));
        textViewInternet.setText(String.format("%s\n%s %s", getString(R.string.of_the), internetData[0], getString(R.string.internet)));

        textViewType.setText(usage.getType());
        textViewOther.setText(usage.getOther());
        textViewCosts.setText(usage.getCosts());

        showResults(true);
        stopRotateRefresh();

        Snackbar.make(getWindow().getDecorView().getRootView(), R.string.login_success, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    /**
     * Handle FailedJsoupEvent, when data isn't received from the Choozze server
     *
     * @param jsoupFailedEvent
     */
    @Subscribe
    public void get(JsoupFailedEvent jsoupFailedEvent) {
        textViewStatus.setText(R.string.error_fetching);
    }

    /**
     * Handle FetchUsageEvent, start a new thread to fetch the usage data
     *
     * @param fetchUsageEvent
     */
    @Subscribe
    public void get(FetchUsageEvent fetchUsageEvent) {
        Thread thread = new ChoozzeUsageFetcher(fetchUsageEvent.getLoginUsername(), fetchUsageEvent.getPassword());
        thread.start();
    }

    /**
     * Create options menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Option menu handler
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_login:
                loginDialog();
                return true;
            case R.id.menu_logout:
                handleLogout();
                return true;
            case R.id.menu_refresh:
                handleLogin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Remove stored credentials
     *
     * @todo: refactor
     */
    private void handleLogout() {
        showResults(false);

        SecurePrefManager.with(this)
                         .remove("login_username")
                         .confirm();

        SecurePrefManager.with(this)
                         .remove("password")
                         .confirm();

        handleLogin();
    }

    /**
     * Start rotating refresh button
     */
    private void startRotateRefresh() {
        RotateAnimator.rotate360(refreshButton);
    }

    /**
     * Stop rotating refresh button
     */
    private void stopRotateRefresh() {
        refreshButton.setAnimation(null);
    }
}
