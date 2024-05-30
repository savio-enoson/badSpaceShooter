package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

class Bullet {
    Rectangle hitbox;
    Color color;
    int damage;
    float xVelocity, yVelocity;

    public Bullet(int damage)     //constructor to simulate asteroid hit
    {
        this.damage=damage;
    }

    public Bullet(int size, Color color, int damage, float xVelocity, float yVelocity, Vector2 shipHead) {
        hitbox = new Rectangle();
        hitbox.setSize(size);
        hitbox.setPosition(shipHead);
        this.color=color; this.damage=damage;
        this.xVelocity=xVelocity; this.yVelocity=yVelocity;
    }

    public void move()
    {
        hitbox.x += xVelocity * Gdx.graphics.getDeltaTime();
        hitbox.y += yVelocity * Gdx.graphics.getDeltaTime();
    }

    public float getX ()
    {
        return hitbox.x;
    }

    public float getY ()
    {
        return hitbox.y;
    }

    public float getSize()
    {
        return hitbox.height;
    }

    public Color getColor()
    {
        return color;
    }
}
