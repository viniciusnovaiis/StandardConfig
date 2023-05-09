package br.com.pdasolucoes.standardconfig;

import android.content.res.Configuration;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import br.com.pdasolucoes.standardconfig.managers.NetworkManager;
import br.com.pdasolucoes.standardconfig.utils.ConfigurationHelper;
import br.com.pdasolucoes.standardconfig.utils.NavigationHelper;

public abstract class PrincipalActivity extends AppCompatActivity {

    private LinearLayout activityContainer;
    private View viewHeader;
    private ImageView imageView;

    @Override
    public void setContentView(int layoutResID) {

        LinearLayout llparentView = (LinearLayout) getLayoutInflater().inflate(R.layout.principal_activity, null);
        initViews(llparentView);
        View view = getLayoutInflater().inflate(layoutResID, activityContainer, true);
        imageView = view.findViewById(R.id.imageCliente);

        super.setContentView(llparentView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        resetDisconnectTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopDisconnectTimer();
    }

    private void initViews(View view) {

        viewHeader = view.findViewById(R.id.header);
        activityContainer = view.findViewById(R.id.linearLayoutContent);

        NetworkManager.updateInitialViews(view);

    }

    private final Runnable disconnectCallback = () -> {
        try {
            NetworkManager.openApk("br.com.pdasolucoes.basesystem");
        } catch (Exception ignored) {

        }
        finish();
    };

    private static final Handler disconnectHandler = new Handler(msg -> {
        NavigationHelper.showToastShort(R.string.sessao_encerrada);
        return true;
    });

    public void stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    public void resetDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
        if (ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.TimeOutSession, 0) > 0)
            disconnectHandler.postDelayed(disconnectCallback,
                    (long) ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.TimeOutSession, 0) * 60 * 1000);
    }

    @Override
    public void onUserInteraction() {
        resetDisconnectTimer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewHeader.setVisibility(View.GONE);

            if (imageView != null) {
                imageView.setVisibility(View.GONE);
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewHeader.setVisibility(View.VISIBLE);
            if (imageView != null) {
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

}
