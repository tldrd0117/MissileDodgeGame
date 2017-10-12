package com.sungjae.missiledodge.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sungjae.missiledodge.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by iseongjae on 2017. 8. 13..
 */

public class StarFrameLayout extends FrameLayout {
    Random random = new Random();
    List<StarViewHolder> starList = new ArrayList<>();
    List<RockViewHolder> rockList = new ArrayList<>();
    List<DiamondViewHolder> diamondList = new ArrayList<>();
    RocketViewHolder rocketView;
    int starWidth;
    int rockWidth;
    int rocketWidth;
    int diamondWidth;
    Disposable moveStarDisposable;
    Disposable moveRockDisposable;
    Disposable moveDiamondDisposable;
    Disposable rockCollisionDisposable;
    Disposable diamondCollisionDisposable;

    boolean checkingDiamond = false;
    boolean checkingRock = false;

    int dodgeCount;
    int diamondCount;

    CountChangeListener countChangeListener;
    StateListener stateListener;

    FrameLayout container;

    public StarFrameLayout(@NonNull Context context) {
        super(context);
    }

    public StarFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StarFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StarFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getDodgeCount() {
        return dodgeCount;
    }

    public int getDiamondCount() {
        return diamondCount;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();

    }


    public void setCountChangeListener(CountChangeListener countChangeListener) {
        this.countChangeListener = countChangeListener;
    }

    public void setStateListener(StateListener stateListener) {
        this.stateListener = stateListener;
    }

    void init(){
        container = findViewById(R.id.container);
        starWidth = (int) getResources().getDimension(R.dimen.star_width);
        rockWidth = (int) getResources().getDimension(R.dimen.rock_width);
        rocketWidth = (int) getResources().getDimension(R.dimen.rocket_width);
        diamondWidth = (int) getResources().getDimension(R.dimen.diamond_width);

        post(new Runnable() {
            @Override
            public void run() {
                for( int i = 0; i < 100; ++i){
                    generateStarView();
                }
                moveStar();
            }
        });
    }

    public void start(){
        container.removeAllViews();
        post(new Runnable() {
            @Override
            public void run() {
                generateRocketView();

                for( int i = 0; i < 100; ++i){
                    generateStarView();
                }

                for( int i = 0; i < 30; ++i){
                    generateRockView();
                }

                for( int i = 0; i < 5; ++i){
                    generateDiamondView();
                }

                moveStar();
                moveRock();
                moveDiamond();
            }
        });
    }


    void generateStarView(){
        int x = Math.abs(random.nextInt() % getWidth());
        int y = Math.abs(random.nextInt() % (getHeight()/4));

        View view = new View(getContext());
        MarginLayoutParams params = new MarginLayoutParams(starWidth, starWidth);
        params.setMargins(x, y, 0, 0);
        view.setBackgroundResource(R.drawable.star);
        container.addView(view, 0, params);
        view.setTranslationY( - getHeight()/4 );
        int speed = Math.abs(random.nextInt()%10);
        if( speed < 1){
            speed = 1;
        }
        starList.add( new StarViewHolder( view, speed ) );
    }

    void generateRockView(){
        int x = Math.abs(random.nextInt() % getWidth());
        int y = Math.abs(random.nextInt() % (getHeight()/4));

        View view = new View(getContext());
        MarginLayoutParams params = new MarginLayoutParams(rockWidth, rockWidth);
        params.setMargins(x, y, 0, 0);
        view.setBackgroundResource(R.drawable.rock);
        container.addView(view, 0, params);
        view.setTranslationY( - getHeight()/4 );
        int speed = Math.abs(random.nextInt()%20);
        if( speed < 5){
            speed = 5;
        }
        rockList.add( new RockViewHolder( view, speed, rockWidth));
    }

    void generateDiamondView(){
        int x = Math.abs(random.nextInt() % getWidth());
        int y = Math.abs(random.nextInt() % (getHeight()/4));

        View view = new View(getContext());
        MarginLayoutParams params = new MarginLayoutParams(diamondWidth, diamondWidth);
        params.setMargins(x, y, 0, 0);
        view.setBackgroundResource(R.drawable.diamond);
        container.addView(view, 0, params);
        view.setTranslationY( - getHeight()/4 );
        int speed = Math.abs(random.nextInt()%10);
        if( speed < 1){
            speed = 1;
        }
        diamondList.add( new DiamondViewHolder( view, speed, diamondWidth));
    }

    void generateRocketView(){
        ImageView view = new ImageView(getContext());
        MarginLayoutParams params = new MarginLayoutParams(rocketWidth, rocketWidth);
        params.setMargins( getWidth()/2 - rocketWidth/2, getHeight() - rocketWidth, 0 , 0 );
        view.setImageResource(R.drawable.rocket);
        container.addView(view, 0, params);
        rocketView = new RocketViewHolder(view, rocketWidth);

        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float downX = 0;
                float downY = 0;
                if( MotionEvent.ACTION_DOWN == motionEvent.getAction() ){
                    downX = motionEvent.getX();
                    downY = motionEvent.getY();
                    return true;
                }
                else if( MotionEvent.ACTION_MOVE == motionEvent.getAction() ){
                    float moveX = motionEvent.getX();
                    float moveY = motionEvent.getY();

                    view.setTranslationX( view.getTranslationX() + moveX - downX - rocketWidth/2 );
                    view.setTranslationY( view.getTranslationY() + moveY - downY - rocketWidth/2 );
                    checkDiamondCollision();
                    checkRockCollision();
                    return false;
                }
                return false;
            }
        });

    }

    public void checkRockCollision(){
        if( checkingRock ){
            return;
        }
        checkingRock = true;
        rockCollisionDisposable = Observable.fromIterable( rockList).concatMap(new Function<RockViewHolder, ObservableSource<Boolean>>() {
            @Override
            public ObservableSource<Boolean> apply(@io.reactivex.annotations.NonNull RockViewHolder rockViewHolder) throws Exception {
                boolean collision = rockViewHolder.getRect().intersect(rocketView.getRect());

                return Observable.just(collision);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception {
                checkingRock = false;
                if( aBoolean ){
                    moveRockDisposable.dispose();
                    rockCollisionDisposable.dispose();
                    moveStarDisposable.dispose();
                    moveDiamondDisposable.dispose();
                    diamondCollisionDisposable.dispose();
                    rocketView.view.setOnTouchListener(null);

                    if( stateListener != null){
                        stateListener.gameover();
                    }
                }
            }
        });
    }

    public void checkDiamondCollision(){
        if( checkingDiamond ){
            return;
        }
        checkingDiamond = true;
        diamondCollisionDisposable = Observable.fromIterable(diamondList)
                .concatMap(new Function<DiamondViewHolder, ObservableSource<DiamondViewHolder>>() {
            @Override
            public ObservableSource<DiamondViewHolder> apply(@io.reactivex.annotations.NonNull DiamondViewHolder diamondViewHolder) throws Exception {
                boolean collision = diamondViewHolder.getRect().intersect(rocketView.getRect());
                if( collision ){
                    diamondCount++;
                    if( countChangeListener != null) {
                        Observable.create(new ObservableOnSubscribe<Object>() {
                            @Override
                            public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<Object> e) throws Exception {
                                countChangeListener.changeCount1(dodgeCount, diamondCount);
                            }
                        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                    }
                    return Observable.just( diamondViewHolder);
                }
                checkingDiamond = false;
                return Observable.empty();
            }
        }).observeOn(AndroidSchedulers.mainThread()).buffer(Integer.MAX_VALUE)
                .subscribe(new Consumer<List<DiamondViewHolder>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull List<DiamondViewHolder> diamondViewHolderList) throws Exception {
                        for( DiamondViewHolder diamondViewHolder : diamondViewHolderList ){
                            diamondList.remove(diamondViewHolder);
                            container.removeView(diamondViewHolder.view);
                            if(diamondList.size() < 5) {
                                generateDiamondView();
                            }
                        }
                        checkingDiamond = false;
                    }
                });
    }

    public void restart(){
        container.removeAllViews();
        rockList.clear();
        starList.clear();
        diamondList.clear();
        dodgeCount = 0;
        diamondCount = 0;
        countChangeListener.changeCount1(dodgeCount, diamondCount);
        start();
    }

    public void moveStar(){
        moveStarDisposable = Observable.timer( 50, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .concatMap(new Function<Long, ObservableSource<StarViewHolder>>() {
                    @Override
                    public ObservableSource<StarViewHolder> apply(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                        return Observable.fromIterable(starList);
                    }
                }).concatMap(new Function<StarViewHolder, ObservableSource<StarViewHolder>>() {
                    @Override
                    public ObservableSource<StarViewHolder> apply(@io.reactivex.annotations.NonNull StarViewHolder starViewHolder) throws Exception {
                        starViewHolder.view.setTranslationY( starViewHolder.view.getTranslationY() + starViewHolder.speed);
                        if( getHeight() < starViewHolder.getYPosition() ){
                            return Observable.just(starViewHolder);
                        }
                        return Observable.empty();
                    }
                }).buffer(Integer.MAX_VALUE).concatMap(new Function<List<StarViewHolder>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@io.reactivex.annotations.NonNull List<StarViewHolder> starViewHolderList) throws Exception {
                        for( StarViewHolder starViewHolder : starViewHolderList ){
                            container.removeView( starViewHolder.view );
                            generateStarView();
                        }
                        starList.removeAll(starViewHolderList);

                        return Observable.empty();
                    }
                }).repeat().subscribe();

    }

    public void moveDiamond(){
        moveDiamondDisposable = Observable.timer( 50, TimeUnit.MILLISECONDS ).observeOn(AndroidSchedulers.mainThread())
                .concatMap(new Function<Long, ObservableSource<DiamondViewHolder>>() {
                    @Override
                    public ObservableSource<DiamondViewHolder> apply(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                        return Observable.fromIterable(diamondList);
                    }
                }).concatMap(new Function<DiamondViewHolder, ObservableSource<DiamondViewHolder>>() {
                    @Override
                    public ObservableSource<DiamondViewHolder> apply(@io.reactivex.annotations.NonNull DiamondViewHolder diamondViewHolder) throws Exception {
                        diamondViewHolder.view.setTranslationY( diamondViewHolder.view.getTranslationY() + diamondViewHolder.speed);
                        if( getHeight() < diamondViewHolder.getYPosition() ){
                            return Observable.just(diamondViewHolder);
                        }
                        else{
                            checkDiamondCollision();
                        }
                        return Observable.empty();
                    }
                }).buffer(Integer.MAX_VALUE).concatMap(new Function<List<DiamondViewHolder>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@io.reactivex.annotations.NonNull List<DiamondViewHolder> diamondViewHolderList) throws Exception {
                        for( DiamondViewHolder diamondViewHolder : diamondViewHolderList ){
                            container.removeView( diamondViewHolder.view );
                            if( diamondList.size() < 5) {
                                generateDiamondView();
                            }
                        }
                        diamondList.removeAll(diamondViewHolderList);

                        return Observable.empty();
                    }
                }).repeat().subscribe();
    }

    public void moveRock(){
//        moveRockDisposable = Observable.fromIterable(rockList).observeOn(AndroidSchedulers.mainThread()).concatMap(new Function<RockViewHolder, ObservableSource<RockViewHolder>>() {
//            @Override
//            public ObservableSource<RockViewHolder> apply(@io.reactivex.annotations.NonNull final RockViewHolder rockViewHolder) throws Exception {
//                return Observable.timer( 50 - rockViewHolder.speed, TimeUnit.MILLISECONDS).concatMap(new Function<Long, ObservableSource<RockViewHolder>>() {
//                    @Override
//                    public ObservableSource<RockViewHolder> apply(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
//                        return Observable.just(rockViewHolder);
//                    }
//                });
//            }
//        }).concatMap(new Function<RockViewHolder, ObservableSource<RockViewHolder>>() {
//            @Override
//            public ObservableSource<RockViewHolder> apply(@io.reactivex.annotations.NonNull RockViewHolder rockViewHolder) throws Exception {
//                rockViewHolder.view.setTranslationY( rockViewHolder.view.getTranslationY() + rockViewHolder.speed);
//                rockViewHolder.view.setRotation( rockViewHolder.view.getRotation() + random.nextInt()%5 );
//                if( getHeight() < rockViewHolder.getYPosition() ){
//                    return Observable.just(rockViewHolder);
//                }
//                else{
//                    checkRockCollision();
//                }
//                return Observable.empty();
//            }
//        }).concatMap(new Function<RockViewHolder, ObservableSource<?>>() {
//            @Override
//            public ObservableSource<?> apply(@io.reactivex.annotations.NonNull RockViewHolder rockViewHolder) throws Exception {
//                container.removeView( rockViewHolder.view );
//                generateRockView();
//                dodgeCount++;
//                if( countChangeListener != null)
//                    countChangeListener.changeCount1(dodgeCount, diamondCount);
//                rockList.remove(rockViewHolder);
//                return null;
//            }
//        }).repeat().subscribe();

        moveRockDisposable = Observable.timer( 20, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .concatMap(new Function<Long, ObservableSource<RockViewHolder>>() {
                    @Override
                    public ObservableSource<RockViewHolder> apply(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                        return Observable.fromIterable(rockList);
                    }
                }).concatMap(new Function<RockViewHolder, ObservableSource<RockViewHolder>>() {
                    @Override
                    public ObservableSource<RockViewHolder> apply(@io.reactivex.annotations.NonNull RockViewHolder rockViewHolder) throws Exception {
                        rockViewHolder.view.setTranslationY( rockViewHolder.view.getTranslationY() + rockViewHolder.speed);
                        rockViewHolder.view.setRotation( rockViewHolder.view.getRotation() + random.nextInt()%5 );
                        if( getHeight() < rockViewHolder.getYPosition() ){
                            return Observable.just(rockViewHolder);
                        }
                        else{
                            checkRockCollision();
                        }
                        return Observable.empty();
                    }
                }).buffer(Integer.MAX_VALUE).concatMap(new Function<List<RockViewHolder>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@io.reactivex.annotations.NonNull List<RockViewHolder> rockViewHolderList) throws Exception {
                        for( RockViewHolder rockViewHolder : rockViewHolderList ){
                            container.removeView( rockViewHolder.view );
                            generateRockView();
                            dodgeCount++;
                            if( countChangeListener != null)
                                countChangeListener.changeCount1(dodgeCount, diamondCount);
                        }
                        rockList.removeAll(rockViewHolderList);

                        return Observable.empty();
                    }
                }).repeat().subscribe();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        moveStarDisposable.dispose();
        diamondCollisionDisposable.dispose();
        rockCollisionDisposable.dispose();
        moveRockDisposable.dispose();
        moveDiamondDisposable.dispose();

    }
}
