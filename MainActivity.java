package com.example.recyclecatch;

import androidx.appcompat.app.AppCompatActivity;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.recyclecatch.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Button btnLogout;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;




    //Frame
    private FrameLayout gameFrame;
    private int frameHeight, frameWidth, initialFrameWidth;
    private LinearLayout startLayout,modeLayout;


    //Image
    private ImageView black, pink, orange, box, yellow;
    private Drawable imageBoxRight, imageBoxLeft;

    //Button
    private Button pauseBtn;
    private Button modeBtn;

    //Size
    private int boxSize;

    //Position
    private float boxX, boxY;
    private float blackX, blackY;
    private float orangeX, orangeY;
    private float pinkX, pinkY;
    private float yellowX, yellowY;

    //Score
    private TextView scoreLabel, highScoreLabel;
    private int score, highScore, timeCount;
    private SharedPreferences settings;

    //Class
    private Timer timer;
    private Handler handler = new Handler();
    private SoundPlayer soundPlayer;

    //Status
    private boolean action_flg = false;
    private boolean start_flg = false;
    private boolean pink_flg = false;
    private boolean pause_flg = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        soundPlayer = new SoundPlayer(this);

        gameFrame = findViewById(R.id.gameFrame);
        box = findViewById(R.id.box);
        black = findViewById(R.id.black);
        orange = findViewById(R.id.orange);
        pink = findViewById(R.id.pink);
        yellow = findViewById(R.id.yellow);
        scoreLabel = findViewById(R.id.scoreLabel);
        highScoreLabel = findViewById(R.id.highScoreLabel);
        startLayout = findViewById(R.id.startLayout);
        pauseBtn = findViewById(R.id.pauseBtn);
        btnLogout = findViewById(R.id.logout);
        modeBtn = findViewById(R.id.mode);


        //Logout
        btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FirebaseAuth.getInstance().signOut();
                Intent intoMain = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intoMain);
            }
    });




        imageBoxRight = getResources().getDrawable(R.drawable.box_right);
        imageBoxLeft = getResources().getDrawable(R.drawable.box_left);

        // High Score
        settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highScore = settings.getInt("HIGH_SCORE", 0);
        highScoreLabel.setText("High Score :" + highScore);

    }

    public void changePos() {

        // Add timeCount
        timeCount = 20;


        //Orange
        orangeY += 12;

        float orangeCenterX = orangeX + orange.getWidth() / 2;
        float orangeCenterY = orangeY + orange.getHeight() / 2;

        if (hitCheck(orangeCenterX, orangeCenterY)) {
            orangeY = frameHeight + 100;
            score += 10;
            soundPlayer.playHitOrangeSound();


        }

        if (orangeY > frameHeight) {
            orangeY = -100;
            orangeX = (float) Math.floor(Math.random() * (frameWidth - orange.getWidth()));


        }
        orange.setX(orangeX);
        orange.setY(orangeY);


        //Pink
        if (pinkY > frameHeight) {
            pink_flg = true;
            pinkY = -100;
            pinkX = (float) Math.floor(Math.random() * (frameWidth - pink.getWidth()));

        }

        if (pink_flg) {
            pinkY += 20;


            float pinkCenterX = pinkX + pink.getWidth() / 2;
            float pinkCenterY = pinkY + pink.getHeight() / 2;

            if (hitCheck(pinkCenterX, pinkCenterY)) {
                pinkY = frameHeight + 100;
                score += 30;
                //Change FrameWidth
                if (initialFrameWidth > frameWidth * 110 / 100) {
                    frameWidth = frameWidth * 110 / 100;
                    changeFrameWidth(frameWidth);
                }
                soundPlayer.playHitPinkSound();
            }


            if (pinkY > frameHeight) pink_flg = false;
            pink.setX(pinkX);
            pink.setY(pinkY);


        }

        //Black
        blackY += 18;

        float blackCenterX = blackX + black.getWidth() / 2;
        float blackCenterY = blackY + black.getHeight() / 2;

        if (hitCheck(blackCenterX, blackCenterY)) {
            blackY = frameHeight + 100;

            // Change FrameWidth
            frameWidth = frameWidth * 80 / 100;
            changeFrameWidth(frameWidth);
            score += -5;
            soundPlayer.playHitBlackSound();
            if (frameWidth <= boxSize) {
                gameOver();
            }

        }

        if (blackY > frameHeight) {
            blackY = -100;
            blackX = (float) Math.floor(Math.random() * (frameWidth - black.getWidth()));

        }

        black.setX(blackX);
        black.setY(blackY);

        //Yellow
        yellowY += 18;

        float yellowCenterX = yellowX + yellow.getWidth() / 2;
        float yellowCenterY = yellowY + yellow.getHeight() / 2;
        if (hitCheck(yellowCenterX, yellowCenterY)) {
            yellowY = frameHeight + 100;

            // Change FrameWidth
            frameWidth = frameWidth * 80 / 100;
            changeFrameWidth(frameWidth);
            score += -5;
            soundPlayer.playHitBlackSound();

            if (frameWidth <= boxSize) {
                gameOver();
            }

        }

        if (yellowY > frameHeight) {
            yellowY = -100;
            yellowX = (float) Math.floor(Math.random() * (frameWidth - yellow.getWidth()));

        }

        yellow.setX(yellowX);
        yellow.setY(yellowY);


        //Move Box
        if (action_flg) {
            //Touching
            boxX += 14;
            box.setImageDrawable(imageBoxRight);

        } else {
            //Releasing
            boxX -= 14;
            box.setImageDrawable(imageBoxLeft);

        }

        //Check box position
        if (boxX < 0) {
            boxX = 0;
            box.setImageDrawable(imageBoxRight);
        }
        if (frameWidth - boxSize < boxX) {
            boxX = frameWidth - boxSize;
            box.setImageDrawable(imageBoxLeft);
        }

        box.setX(boxX);

        scoreLabel.setText("Score : " + score);


    }

    public boolean hitCheck(float x, float y) {
        if (boxX <= x && x <= boxX + boxSize &&
                boxY <= y && y <= frameHeight) {
            return true;
        }
        return false;
    }

    public void changeFrameWidth(int frameWidth) {
        ViewGroup.LayoutParams params = gameFrame.getLayoutParams();
        params.width = frameWidth;
        gameFrame.setLayoutParams(params);
    }

    public void gameOver() {
        //stop timer
        timer.cancel();
        timer = null;
        start_flg = false;

        //Before showing startLayout, sleep 1 second
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        changeFrameWidth(initialFrameWidth);

        startLayout.setVisibility(View.VISIBLE);
        box.setVisibility(View.INVISIBLE);
        black.setVisibility(View.INVISIBLE);
        pink.setVisibility(View.INVISIBLE);
        orange.setVisibility(View.INVISIBLE);
        yellow.setVisibility(View.INVISIBLE);
        pauseBtn.setVisibility(View.INVISIBLE);

        //Update High Score
        if (score > highScore) {
            highScore = score;
            highScoreLabel.setText("High Score : " + highScore);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", highScore);
            editor.commit();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (start_flg) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;

            }
        }
        return true;
    }
    public void setModeBtn(View view) {
        start_flg = true;
        modeLayout.setVisibility(View.VISIBLE);



    }

    public void startGame(View view) {
        start_flg = true;
        startLayout.setVisibility(View.INVISIBLE);

        if (frameHeight == 0) {
            frameHeight = gameFrame.getHeight();
            frameWidth = gameFrame.getWidth();
            initialFrameWidth = frameWidth;

            boxSize = box.getHeight();
            boxX = box.getX();
            boxY = box.getY();
        }

        frameWidth = initialFrameWidth;


        box.setX(0.0f);
        black.setY(3000.0f);
        orange.setY(3000.0f);
        pink.setY(3000.0f);
        yellow.setY(3000.0f);


        blackY = black.getY();
        orangeY = orange.getY();
        pinkY = pink.getY();
        yellowY = yellow.getY();


        box.setVisibility(View.VISIBLE);
        black.setVisibility(View.VISIBLE);
        orange.setVisibility(View.VISIBLE);
        pink.setVisibility(View.VISIBLE);
        yellow.setVisibility(View.VISIBLE);
        pauseBtn.setVisibility(View.VISIBLE);


        timeCount = 0;
        score = 0;
        scoreLabel.setText("Score : 0 ");


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (start_flg) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    })
                    ;
                }


            }
        }, 0, 20);


    }


    public void quitGame(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }

    }


    public void pausePushed(View view) {

        if (pause_flg == false) {
            pause_flg = true;

            //Stop the timer.
            timer.cancel();
            timer = null;

            //Change Button Text
            pauseBtn.setText("START");


        } else {

            pause_flg = false;

            //Change Button Text.
            pauseBtn.setText("PAUSE");

            //Create and start the timer.
            timer = new Timer();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (start_flg) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                changePos();
                            }
                        })
                        ;
                    }


                }
            }, 0, 20);


        }

        }

    }

