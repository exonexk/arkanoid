package com.example.lukas.arkanoid;

import android.graphics.RectF;

public class Paddle {

    private RectF rect;

    private float length;
    private float height;

    private float x;
    private float y;

    private float paddleSpeed;

    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;


    private int paddleMoving = STOPPED;

    // konstruktor pro paddle
    public Paddle(int screenX, int screenY){

        length = 300;
        height = 20;


        x = (screenX / 2) - 150 ;
        y = screenY - 20;

        rect = new RectF(x, y, x + length, y + height);

        paddleSpeed = 600;
    }

    public RectF getRect(){
        return rect;
    }

    // funkce pro nastavení stavu například STOPPED.
    public void setMovementState(int state){
        paddleMoving = state;
    }

    // funkce update pro paddle která určuje pohyp na základě fps
    public void update(long fps){
        if(paddleMoving == LEFT){
            x = x - paddleSpeed / fps;
        }

        if(paddleMoving == RIGHT){
            x = x + paddleSpeed / fps;
        }

        rect.left = x;
        rect.right = x + length;
    }


    // obnovení pozice paddle
    public void reset(int screenX, int screenY)
    {
        length = 300;
        height = 20;

        x = (screenX / 2)  - 150 ;
        y = screenY - 20;

        rect.left = x;
        rect.top = y;
        rect.right = x + length ;
        rect.bottom = y + height;
    }

}
