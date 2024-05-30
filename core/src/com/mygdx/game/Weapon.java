package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

interface Weapon
{
    void fire(Array<Bullet> bullets, Vector2 shipHead);

    void setBulletSpeed(int speed);
    int getBulletSpeed();
    void setBulletColor(Color color);
    void setDamage(int dmg);
    void setCoolDown (float multiplier);
}

abstract class spaceGun implements Weapon {
    int damage;
    long lastFired;
    int coolDown;
    int bulletCount;
    int bulletSpeed;
    int initialCD;
    Color bulletColor;
    Array<Sound> firingSounds;

    //public void upgrade() {}
}

class Pistol extends spaceGun
{
    public Pistol()
    {
        damage = 30;
        lastFired=0;
        coolDown=500000000;
        initialCD=coolDown;
        bulletCount=1;
        bulletSpeed=1000;
        bulletColor=Color.valueOf("FFFF00FF");
        firingSounds = new Array<>();
        firingSounds.add(Gdx.audio.newSound(Gdx.files.internal("Weapons\\pistol_1.wav")));
        firingSounds.add(Gdx.audio.newSound(Gdx.files.internal("Weapons\\pistol_2.wav")));
        firingSounds.add(Gdx.audio.newSound(Gdx.files.internal("Weapons\\pistol_3.wav")));
    }

    @Override
    public void fire(Array<Bullet> bullets, Vector2 shipHead)
    {
        if (TimeUtils.nanoTime() - lastFired > coolDown)
        {
            lastFired = TimeUtils.nanoTime();
            firingSounds.get(MathUtils.random(2)).play();

            for (int i=0; i<bulletCount; i++)
            {
                bullets.add(new Bullet(5, bulletColor, damage, 0, bulletSpeed, shipHead));
            }
        }
    }

    @Override
    public void setBulletSpeed(int speed) {
        this.bulletSpeed=speed;
    }

    @Override
    public int getBulletSpeed() {
        return bulletSpeed;
    }

    @Override
    public void setBulletColor(Color color) {
        this.bulletColor=color;
    }

    @Override
    public void setDamage(int dmg) {
        this.damage=dmg;
    }

    @Override
    public void setCoolDown(float multiplier) {
        this.coolDown = (int) (initialCD * multiplier);
    }
}

class Shotgun extends spaceGun
{
    public Shotgun()
    {
        damage = 25;
        lastFired=0;
        coolDown=1150000000;
        initialCD=coolDown;
        bulletCount=3;
        bulletSpeed=750;
        bulletColor=Color.valueOf("FF6600FF");
        firingSounds = new Array<>();
        firingSounds.add(Gdx.audio.newSound(Gdx.files.internal("Weapons\\shotgun_charge.mp3")));
        firingSounds.add(Gdx.audio.newSound(Gdx.files.internal("Weapons\\shotgun_1.wav")));
        firingSounds.add(Gdx.audio.newSound(Gdx.files.internal("Weapons\\shotgun_2.wav")));
    }

    @Override
    public void fire(Array<Bullet> bullets, Vector2 shipHead) {
        if (TimeUtils.nanoTime() - lastFired > coolDown)
        {
            lastFired = TimeUtils.nanoTime();
            firingSounds.get(0).play();
            firingSounds.get(MathUtils.random(1,2)).play();
            float spread = 0.05f;
            float spreadStart = bulletCount/2f * -spread;

            for (int i=0; i<bulletCount; i++)
            {
                bullets.add(new Bullet(5, bulletColor, damage, (bulletSpeed * (spreadStart + i * spread)), bulletSpeed, shipHead));
            }
        }
    }

    @Override
    public void setBulletSpeed(int speed) {
        this.bulletSpeed=speed;
    }

    @Override
    public int getBulletSpeed() {
        return bulletSpeed;
    }

    @Override
    public void setBulletColor(Color color) {
        this.bulletColor=color;
    }

    @Override
    public void setCoolDown(float multiplier) {
        this.coolDown = (int) (initialCD * multiplier);
    }

    @Override
    public void setDamage(int dmg) {
        this.damage=dmg;
    }
}

class Laser extends spaceGun
{
    int bulletSize;
    public Laser()
    {
        damage = 50;
        lastFired=0;
        coolDown=1500000000;
        initialCD=coolDown;
        bulletCount=10;
        bulletSpeed=2500;
        bulletSize=8;
        bulletColor=Color.valueOf("80fcfcFF");
        firingSounds = new Array<>();
        firingSounds.add(Gdx.audio.newSound(Gdx.files.internal("Weapons\\laser_charge.wav")));
        firingSounds.add(Gdx.audio.newSound(Gdx.files.internal("Weapons\\laser_1.wav")));
        firingSounds.add(Gdx.audio.newSound(Gdx.files.internal("Weapons\\laser_2.wav")));
        firingSounds.add(Gdx.audio.newSound(Gdx.files.internal("Weapons\\laser_3.wav")));
    }

    @Override
    public void fire(Array<Bullet> bullets, Vector2 shipHead) {
        if (TimeUtils.nanoTime() - lastFired > coolDown)
        {
            lastFired = TimeUtils.nanoTime();
            firingSounds.get(0).play();
            firingSounds.get(MathUtils.random(1,3)).play();

            for (int i=0; i<bulletCount; i++)
            {
                bullets.add(new Bullet(bulletSize, bulletColor, damage, 0, bulletSpeed, shipHead));
            }
        }
    }

    public void setBulletSize(int bulletSize) {
        this.bulletSize = bulletSize;
    }

    @Override
    public void setBulletSpeed(int speed) {
        this.bulletSpeed=speed;
    }

    @Override
    public int getBulletSpeed() {
        return bulletSpeed;
    }

    @Override
    public void setBulletColor(Color color) {
        this.bulletColor=color;
    }

    @Override
    public void setCoolDown(float multiplier) {
        this.coolDown = (int) (initialCD * multiplier);
    }

    @Override
    public void setDamage(int dmg) {
        this.damage=dmg;
    }
}


