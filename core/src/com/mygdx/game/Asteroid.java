package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

abstract class Floater
{
    Texture txt;
    Sprite sprite;
    Rectangle hitBox;
    int speed, size;

    public void move()
    {

    }
}

class Asteroid extends Floater
{
    int damage;
    float rotationSpeed, xVelocity;

    public Asteroid()
    {
        this.txt = new Texture(Gdx.files.internal("Textures\\asteroid.png"));
        this.sprite = new Sprite(txt);
        this.size = MathUtils.random(50, 100);
        this.speed = 15000/size;
        this.xVelocity = MathUtils.random(-5,5) * 0.1f * speed;
        //his.rotationSpeed = MathUtils.random(-5, 5) * 1f;
        this.damage = size/4;

        sprite.setSize(size, size); sprite.setPosition(375 - size/2f, 1200 + 2*size);
        //sprite.setOrigin(sprite.getX()+size/2f, sprite.getY()+size/2f);
        hitBox = new Rectangle(); hitBox.setPosition((int)sprite.getX(), (int)(sprite.getY() + 0.145*size));
        hitBox.setSize((int)(size * 0.885f), (int)(size*0.855f));
    }

    public void move()
    {
        sprite.setX(sprite.getX() + xVelocity * Gdx.graphics.getDeltaTime());
        sprite.setY(sprite.getY() - speed * Gdx.graphics.getDeltaTime());
        //sprite.rotate(rotationSpeed * Gdx.graphics.getDeltaTime());
        hitBox.setPosition(sprite.getX() + xVelocity * Gdx.graphics.getDeltaTime(),
                sprite.getY() - speed * Gdx.graphics.getDeltaTime());
    }
}

class Heal extends Floater
{
    Sound sound = Gdx.audio.newSound(Gdx.files.internal("Common\\heal_sound.mp3"));
    public Heal()
    {
        this.txt = new Texture(Gdx.files.internal("Textures\\heal.png"));
        this.sprite = new Sprite(txt);
        sprite.setSize(50, 50);
        sprite.setPosition(MathUtils.random(25, 725), 1250);
        hitBox = new Rectangle(); hitBox.setSize(50,50);
        hitBox.setPosition((int)sprite.getX(), (int)sprite.getY());
        speed = MathUtils.random(300, 500);
    }

    public void move()
    {
        sprite.setY(sprite.getY() - speed * Gdx.graphics.getDeltaTime());
        hitBox.setPosition((int)sprite.getX(), (int)sprite.getY());
    }

}
