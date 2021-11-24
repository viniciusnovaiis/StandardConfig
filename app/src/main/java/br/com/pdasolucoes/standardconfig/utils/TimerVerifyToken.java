package br.com.pdasolucoes.standardconfig.utils;

import android.os.CountDownTimer;

public class TimerVerifyToken extends CountDownTimer {

    private OnTimerProgrees onTimerProgrees;

    public interface OnTimerProgrees {
        void onTick(long l);

        void onFinish();
    }

    public void setOnTimerProgreesListener(OnTimerProgrees OnTimerProgrees) {
        this.onTimerProgrees = OnTimerProgrees;
    }

    public TimerVerifyToken(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long l) {
        onTimerProgrees.onTick(l);
    }

    @Override
    public void onFinish() {
        onTimerProgrees.onFinish();
    }
}
