package br.com.pdasolucoes.standardconfig;

import android.content.res.Configuration;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import br.com.pdasolucoes.standardconfig.managers.NetworkManager;
import br.com.pdasolucoes.standardconfig.utils.MyApplication;

public abstract class PrincipalActivity extends AppCompatActivity implements MyApplication.FinishTimeSession {

    private LinearLayout activityContainer;
    private View viewHeader;
    private ImageView imageView;

    @Override
    public void setContentView(int layoutResID) {

        MyApplication.setOnFinishTimeSessionListener(this);

        LinearLayout llparentView = (LinearLayout) getLayoutInflater().inflate(R.layout.principal_activity, null);
        initViews(llparentView);
        View view = getLayoutInflater().inflate(layoutResID, activityContainer, true);
        imageView = view.findViewById(R.id.imageCliente);

        super.setContentView(llparentView);
    }

    private void initViews(View view) {

        viewHeader = view.findViewById(R.id.header);
        activityContainer = view.findViewById(R.id.linearLayoutContent);

        NetworkManager.updateInitialViews(view);

    }

    @Override
    public void onUserInteraction() {
        MyApplication.resetDisconnectTimer();
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

    @Override
    public void onFinishTime() {
        try {
            NetworkManager.openApk("br.com.pdasolucoes.basesystem");
        } catch (Exception ignored) {

        }
        finish();

    }
}
