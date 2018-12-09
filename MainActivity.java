package com.example.lukas.arkanoid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainActivity extends Activity implements SensorEventListener {

    private SoundPool soundPool;
    private int hitSound;
    private int looseLifesound;
    private int Wall;
    private int Destroy;
    private int Padd;

    ArkanoidGame arkanoidView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        arkanoidView = new ArkanoidGame(this);
        setContentView(arkanoidView);



    }

        class ArkanoidGame extends SurfaceView implements Runnable,SensorEventListener {

            SensorManager sensorManager;
            Sensor accelerometer;

            Thread gameThread = null;

        SurfaceHolder ourHolder;

        boolean play;
        boolean paused;

        Canvas canvas;
        Bitmap background;
        Bitmap padd;
        Paint paint;
        int screenX;
        int screenY;

        long fps;
        private long timeFrame;

        int score = 0;
        int lives = 3;

        Paddle paddle;
        Paddle obstruction;
        Ball ball;
       // SoundActivity soundActivity;


        Brick[] bricks = new Brick[200];
        int numBricks = 0;




        public ArkanoidGame(Context context)
        {

            super(context);

            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
            looseLifesound = soundPool.load(getContext(),R.raw.life,1);
            hitSound = soundPool.load(getContext(),R.raw.brick,1);
            Wall = soundPool.load(getContext(),R.raw.wall,1);
            Padd = soundPool.load(getContext(),R.raw.paddle,1);

           // soundActivity.InitializeSoundActivity();

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener((SensorEventListener) ArkanoidGame.this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            paused = true;
            background = BitmapFactory.decodeResource(getResources(),R.drawable.bg);
            padd = BitmapFactory.decodeResource(getResources(),R.drawable.des);

            ourHolder = getHolder();
            paint = new Paint();

            DisplayMetrics display = this.getResources().getDisplayMetrics();

            screenX = display.widthPixels;
            screenY = display.heightPixels;


            //obstruction = new Paddle(screenX,screenY+80);
            paddle = new Paddle(screenX,screenY);
            ball = new Ball(screenX,screenY);



            createBricksAndRestart();
        }

            public void createBricksAndRestart() {
            ball.reset(screenX,screenY);
            int width = screenX / 8;
            int height = screenY / 10;

            numBricks = 0;
            for (int column = 0; column < 8; column++)
            {
                for (int row = 0; row < 3; row++)
                {
                    bricks[numBricks] = new Brick(row, column, width, height);
                    numBricks++;
                }
            }

                if (lives == 0)
                {
                    score = 0;
                    lives = 3;
                    paddle.reset(screenX,screenY);
                }
            }


            public void run(){
            while(play)
            {
                long startFrameTime = System.currentTimeMillis();

                if(!paused){
                    update();
                }

                draw();

                timeFrame = System.currentTimeMillis() - startFrameTime;
                if (timeFrame >= 1) {
                    fps = 1000 / timeFrame;
                }
            }
        }

        public void update()
        {
            paddle.update(fps);
            ball.update(fps);

           for (int i = 0; i < numBricks; i++) {
                if (bricks[i].getVisibility()) {
                    if (RectF.intersects(bricks[i].getRect(), ball.getRect())) {
                        playHitSound();
                        bricks[i].setInvisible();
                        ball.reverseYVelocity();
                        score = score + 1;
                        if (score == 24 || score == 48 || score == 72 || score == 96 || score == 120 || score == 144 || score == 168 || score == 192 || score == 216)
                        {
                            lives = lives + 2;
                            paused = true;
                            createBricksAndRestart();
                            paddle.reset(screenX,screenY);
                           /* obstruction = new Paddle(screenX,screenY-(screenY/2));

                            paint.setColor(Color.argb(255,255,255,255));
                            canvas.drawRect(obstruction.getRect(),paint);*/

                        }
                    }
                }
            }

            // Kontrola kdy se míč dotkne paddle
            if (RectF.intersects(paddle.getRect(), ball.getRect())) {
                playPaddleHitSound();
                //ball.setRandomXVelocity();
                ball.reverseYVelocity();
                ball.BallDirection(ball,paddle);
                ball.clearObstacleY(paddle.getRect().top - 2);
            }

           /* if (RectF.intersects(obstruction.getRect(), ball.getRect())) {
                //ball.setRandomXVelocity();
                ball.reverseYVelocity();
                ball.BallDirection(ball,obstruction);
                ball.clearObstacleY(obstruction.getRect().top - 2);
            }*/


            // Pokud se míč dotkne země
            if (ball.getRect().bottom > screenY) {
                playLooseLifeSound();
                paused = true;
                // hráč ztratí život, hra se stopne a obnoví se pozice míčku a paddlu
                lives--;
                ball.reset(screenX,screenY);
                paddle.reset(screenX,screenY);

                // pokud jsou životy rovny 0 tak se hra stopne a obnoví se vše.
                if (lives == 0) {
                    paused = true;

                    String Hscore = Integer.toString(score);
                    Intent intent = new Intent(getContext(), GameOver.class);
                    intent.putExtra("highscore", Hscore);
                    startActivity(intent);
                }


            }


            // vrchní zeď odraz
            if (ball.getRect().top < 0)
            {
                playWallHitSound();
                ball.reverseYVelocity();
                ball.clearObstacleY(12);

            }

            // levá zeď
            if (ball.getRect().left < 0)
            {
                playWallHitSound();
                ball.reverseXVelocity();
                ball.clearObstacleX(2);
            }

            // pravá zeď
            if (ball.getRect().right > screenX - 10) {
                playWallHitSound();
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - 22);

            }

            // všechny cihly rozbité
            if (score == numBricks * 10)

            {
                paused = true;
                createBricksAndRestart();
            }


            // podmínka pro to jestli se paddle dotkne boků, buď levého nebo pravého
            if (paddle.getRect().right > screenX - 10) {

                paddle.setMovementState(paddle.STOPPED);
            }

            if (paddle.getRect().left < 0) {

                paddle.setMovementState(paddle.STOPPED);
            }

        }
        public void draw()
        {
            if(ourHolder.getSurface().isValid())
            {
                canvas = ourHolder.lockCanvas();
                canvas.drawBitmap(background,0,0,null);
                paint.setColor(Color.argb(255,255,255,255));

                canvas.drawRect(paddle.getRect(),paint);
                //canvas.drawRect(obstruction.getRect(),paint);
                canvas.drawRect(ball.getRect(),paint);
                paint.setColor(Color.argb(103, 56, 117, 170));

                for (int i = 0; i < numBricks; i++)
                {
                    if (bricks[i].getVisibility())
                    {
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }

                // výběr barvy pro nabarvení
                paint.setColor(Color.argb(255, 255, 255, 255));

                // skore
                paint.setTextSize(40);
                canvas.drawText("Score: " + score + "   Lives: " + lives, 10, 50, paint);

                // Pokud hráč vyhrál ( zničil všechny kostky )
                if (score == numBricks * 10) {
                    paint.setTextSize(90);
                    canvas.drawText("You Win!", 10, screenY / 2, paint);
                }

                // Pokud hráč prohrál
                if (lives <= 0) {
                    paint.setTextSize(90);
                    canvas.drawText("You loose!", 10, screenY / 2, paint);
                }

                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        // zastavení hry
        public void pause()
        {
            play = false;
        }

        // pokračování ve hře
        public void resume()
        {
            play = true;
            gameThread = new Thread(this);
            gameThread.start();
        }


        // kliky na zařízení. Levá / Pravá. Pokud hráč pustí prst ze zařízení tak se paddle stopne.
        public boolean onTouchEvent(MotionEvent motionEvent)
        {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:

                    paused = false;
                   /* if(motionEvent.getX() > screenX /2)
                    {
                        paddle.setMovementState(paddle.RIGHT);
                    }
                    else
                    {
                        paddle.setMovementState(paddle.LEFT);
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    paddle.setMovementState(paddle.STOPPED);
                    break;
*/
            }
            return true;
        }

            @Override
            public void onSensorChanged(SensorEvent event) {
                //Log.d("tt", "On sensor changed: X: " +event.values[0] + " Y: " + event.values[1] + " Z: " +event.values[2]);


                if((event.values[1] >=-1 && event.values[1] <= 1) || (paddle.getRect().right > screenX -50) || (paddle.getRect().left < 50))
                {
                    paddle.setMovementState(paddle.STOPPED);
                }

                if(event.values[1] > 1 && paddle.getRect().left > 50)
                {
                    paddle.setMovementState(paddle.LEFT);
                }

                if(event.values[1] < -1 && (paddle.getRect().right < screenX -50 ))
                {
                    paddle.setMovementState(paddle.RIGHT);
                }




            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }

    @Override
    public void onSensorChanged(SensorEvent event) {



    }

    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }

    // Resume
    protected void onResume()
    {
        super.onResume();
        arkanoidView.resume();
    }

    // Pauza
    protected void onPause()
    {
        super.onPause();
        arkanoidView.pause();
    }

    public void playHitSound()
    {
        soundPool.play(hitSound,0.9f,0.9f, 1, 0, 1);
    }

    public void playLooseLifeSound()
    {
        soundPool.play(looseLifesound,0.9f,0.9f, 1, 0, 1);
    }

    public void playWallHitSound()
    {
        soundPool.play(Wall,0.9f,0.9f, 1, 0, 1);
    }

    public void playPaddleHitSound()
    {
        soundPool.play(Padd,0.9f,0.9f, 1, 0, 1);
    }
}
