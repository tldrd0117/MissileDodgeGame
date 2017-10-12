package com.sungjae.missiledodge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sungjae.missiledodge.ui.CountChangeListener;
import com.sungjae.missiledodge.ui.StarFrameLayout;
import com.sungjae.missiledodge.ui.StateListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by iseongjae on 2017. 8. 12..
 */

public class MainFragment extends Fragment{

    @BindView(R.id.starFrameLayout)
    StarFrameLayout starFrameLayout;

    @BindView(R.id.dodgeScoreTV)
    TextView dodgeScoreTV;

    @BindView(R.id.diamondScoreTV)
    TextView diamondScoreTV;

    @BindView(R.id.centerTV)
    TextView centerTV;

    @BindView(R.id.scoreLayout)
    LinearLayout scoreLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        starFrameLayout.setCountChangeListener(new CountChangeListener() {
            @Override
            public void changeCount1(int dodgeCount, int diamondCount) {
                dodgeScoreTV.setText(""+dodgeCount);
                diamondScoreTV.setText(""+diamondCount);
            }
        });

        starFrameLayout.setStateListener(new StateListener() {
            @Override
            public void gameover() {
                centerTV.setText("다시시작");
                startCenterTVAnim();
            }
        });
        startCenterTVAnim();

        centerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerTV.setVisibility(View.GONE);
                scoreLayout.setVisibility(View.VISIBLE);
                if( centerTV.getText().toString().equals("시작")) {
                    starFrameLayout.start();
                }
                else{
                    starFrameLayout.restart();
                }
            }
        });
        scoreLayout.setVisibility(View.GONE);

    }

    void startCenterTVAnim(){
        centerTV.setVisibility(View.VISIBLE);
        centerTV.setScaleX(0.f);
        centerTV.setScaleY(0.f);
        centerTV.animate().scaleX(1.0f).scaleY(1.0f).start();
    }
}
