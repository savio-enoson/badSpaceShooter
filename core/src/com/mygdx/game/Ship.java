package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

abstract class Ship {
    int health;
    Rectangle hitbox1, hitbox2;
    Texture[] txt;
    Sprite sprite;
    Sound hitSound;
    Sound deathSound;
    Weapon weapon;
    int shipHeight, shipWidth, moveSpeed;
    long lastDamageTakenTime;

    public void moveShip(int keycode) {

    }
    public Sprite getSprite() {
        return sprite;
    }
    public Vector2 getShipHead()
    {
        return new Vector2(0,0);
    }   //prototype function

    public boolean takeHit (Bullet bullet, float multiplier)
    {
        if (this instanceof EnemyShip && bullet.yVelocity<0)
        {
            return false;
        }
        else
        {
            this.health -= bullet.damage * multiplier;
            this.lastDamageTakenTime = TimeUtils.nanoTime();
            this.sprite.setTexture(txt[1]);
            hitSound.play();
            if (this.health <=0)
            {
                this.sprite.setTexture(txt[2]);
                deathSound.play();
            }
            return true;
        }
    }
}

class PlayerShip extends Ship {

    public PlayerShip(int shipWidth, int shipHeight, int moveSpeed, Weapon weapon)
    {
        this.shipWidth=shipWidth; this.shipHeight=shipHeight; lastDamageTakenTime=0;
        this.moveSpeed=moveSpeed; this.weapon = weapon; health=200; txt = new Texture[3];
        if (weapon instanceof Pistol)
        {
            txt[0] = new Texture(Gdx.files.internal("Textures\\pistol_ship.png"));
        }
        else if (weapon instanceof Shotgun)
        {
            txt[0] = new Texture(Gdx.files.internal("Textures\\shotgun_ship.png"));
        }
        else
        {
            txt[0] = new Texture(Gdx.files.internal("Textures\\laser_ship.png"));
        }
        txt[1] = new Texture(Gdx.files.internal("Textures\\ship_takedamage.png"));
        txt[2] = new Texture(Gdx.files.internal("Textures\\ship_takedamage.png"));
        sprite = new Sprite(txt[0]);
        sprite.setSize(shipWidth, shipHeight); sprite.setPosition(375-(shipWidth/2f), (shipHeight/2f));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("Common\\player_hit.wav"));
        deathSound = Gdx.audio.newSound(Gdx.files.internal("Common\\player_death.wav"));

        hitbox1 = new Rectangle();hitbox1.setPosition((int) sprite.getX(), (int) sprite.getY());
        hitbox1.setSize(shipWidth, (int) (shipHeight * 0.4f));
        hitbox2 = new Rectangle(); hitbox2.setPosition((int) (sprite.getX() + shipWidth * 0.3125f), (int)(sprite.getY() + shipHeight * 0.4f));
        hitbox2.setSize((int)(shipWidth * 0.375f), (int)(shipHeight * 0.6f));
    }

    public void moveShip (int keycode)
    {
        //setup movement
        if (keycode==Input.Keys.W) //W
        {
            sprite.setY(sprite.getY() + moveSpeed * Gdx.graphics.getDeltaTime());
        }
        if (keycode==Input.Keys.A) //A
        {
            sprite.setX(sprite.getX() - moveSpeed * Gdx.graphics.getDeltaTime());
        }
        if (keycode==Input.Keys.S) //S
        {
            sprite.setY(sprite.getY() - moveSpeed * Gdx.graphics.getDeltaTime());
        }
        if (keycode==Input.Keys.D) //D
        {
            sprite.setX(sprite.getX() + moveSpeed * Gdx.graphics.getDeltaTime());
        }

        //movement bounds
        if (sprite.getX() <= 0 - shipWidth/2f)   //left bound
        {
            sprite.setX(750 - shipWidth);
        }
        if (sprite.getX() >= 750 - shipWidth/2f)   //right bound
        {
            sprite.setX(0);
        }
        if (sprite.getY() >= 300 - shipHeight/2f)   //top bound
        {
            sprite.setY(300 - shipHeight/2f);
        }
        if (sprite.getY() <= 0 + shipHeight/2f)   //bottom bound
        {
            sprite.setY(0 + shipHeight/2f);
        }
        moveHitbox();
    }

    private void moveHitbox()
    {
        hitbox1.setPosition((int) sprite.getX(), (int) sprite.getY());
        hitbox2.setPosition((int) (sprite.getX() + shipWidth * 0.3125f), (int)(sprite.getY() + shipHeight * 0.4f));
    }

    @Override
    public Vector2 getShipHead() {
        return new Vector2(sprite.getX() + sprite.getWidth()/2, sprite.getY() + sprite.getHeight());
    }
}

class EnemyShip extends Ship {
    int maxY;
    long lastShotTime;
    long lastBounceTime;

    public EnemyShip(int shipWidth, int shipHeight, int moveSpeed, Weapon weapon, int maxY, int health)
    {
        this.shipWidth=shipWidth; this.shipHeight=shipHeight; this.health=health; lastDamageTakenTime=0;
        this.moveSpeed=moveSpeed; this.weapon = weapon; this.maxY=maxY; txt = new Texture[3];

        //modify bullet speed and bullet color for enemy ships
        this.weapon.setBulletSpeed(this.weapon.getBulletSpeed() * -1);
        this.weapon.setBulletColor(Color.valueOf("39FF14FF"));
        if (weapon instanceof Pistol) {this.weapon.setDamage(10);}
        else if (weapon instanceof Shotgun) {this.weapon.setDamage(5);}
        else if (weapon instanceof Laser) {this.weapon.setDamage(2);}
        lastShotTime = 0;

        hitSound = Gdx.audio.newSound(Gdx.files.internal("Enemy\\enemy_hit.wav"));
        deathSound = Gdx.audio.newSound(Gdx.files.internal("Enemy\\enemy_death.wav"));
    }

    @Override
    public Vector2 getShipHead() {
        return new Vector2(sprite.getX() + shipWidth/2f, sprite.getY());
    }
}

class NormalEnemy extends EnemyShip {
    public NormalEnemy (int shipWidth, int shipHeight, int moveSpeed, Weapon weapon, int maxY, int health)
    {
        super(shipWidth, shipHeight, moveSpeed, weapon, maxY,health);
        lastBounceTime=0;

        txt[0] = new Texture(Gdx.files.internal("Enemy\\regular_enemy.png"));
        txt[1] = new Texture(Gdx.files.internal("Enemy\\regular_enemy_takedamage.png"));
        txt[2] = new Texture(Gdx.files.internal("Enemy\\regular_enemy_death.png"));
        sprite = new Sprite(txt[0]);
        sprite.setSize(shipWidth, shipHeight); sprite.setPosition(MathUtils.random(50+shipWidth/2, 600-shipWidth/2), 1200-(shipHeight/2f));

        hitbox2 = new Rectangle(); hitbox2.setSize(shipWidth, (int) (shipHeight * 0.77f));
        hitbox1 = new Rectangle(); hitbox1.setSize((int)(shipWidth * 0.52f), (int)(shipHeight * 0.25f));
        moveHitbox();
    }

    public void moveShip(int keycode)
    {
        if (sprite.getY() > maxY)
        {
            sprite.setY(sprite.getY() - (300 * Gdx.graphics.getDeltaTime()));
        }
        if ((sprite.getX() >= 745 - shipWidth || sprite.getX() <= 5) && TimeUtils.nanoTime() - lastBounceTime >= 200000000)
        {
            moveSpeed *= -1;
            lastBounceTime = TimeUtils.nanoTime();
        }
        sprite.setX(sprite.getX() + moveSpeed * Gdx.graphics.getDeltaTime());
        moveHitbox();
    }

    private void moveHitbox()
    {
        hitbox2.setPosition((int) sprite.getX(), (int) (sprite.getY() + shipHeight * 0.23f));
        hitbox1.setPosition((int) (sprite.getX() + shipWidth*0.24f), (int)sprite.getY());
    }
}

class HardEnemy extends EnemyShip {
    int minY;
    int xMoveSpeed;
    int yMoveSpeed;
    boolean inPosition;
    long lastBounceTimeY;

    public HardEnemy(int shipWidth, int shipHeight, int moveSpeed, Weapon weapon, int maxY, int health)
    {
        super(shipWidth, shipHeight, moveSpeed, weapon, maxY, health);
        lastBounceTime=0; lastBounceTimeY=0;

        txt[0] = new Texture(Gdx.files.internal("Enemy\\zigzag_enemy.png"));
        txt[1] = new Texture(Gdx.files.internal("Enemy\\zigzag_enemy_takedamage.png"));
        txt[2] = new Texture(Gdx.files.internal("Enemy\\zigzag_enemy_death.png"));
        sprite = new Sprite(txt[0]);
        sprite.setSize(shipWidth, shipHeight); sprite.setPosition(MathUtils.random(50+shipWidth/2, 600-shipWidth/2), 1200-(shipHeight/2f));

        this.minY = maxY - 30; this.maxY -= 75; inPosition = false;
        xMoveSpeed = moveSpeed; yMoveSpeed = moveSpeed;

        hitbox2 = new Rectangle(); hitbox2.setSize(shipWidth, (int) (shipHeight * 0.67f));
        hitbox1 = new Rectangle(); hitbox1.setSize((int)(shipWidth * 0.28f), (int)(shipHeight * 0.33f));
        moveHitbox();
    }

    public void moveShip(int keycode)
    {
        //x axis movement
        if ((sprite.getX() >= 745 - shipWidth || sprite.getX() <= 5)&& TimeUtils.nanoTime() - lastBounceTime >= 200000000)
        {
            xMoveSpeed *= -1;
            lastBounceTime = TimeUtils.nanoTime();
        }

        //y axis movement
        if (!inPosition)    //move to position first
        {
            if (sprite.getY() >= maxY)
            {
                sprite.setY(sprite.getY() - (600 * Gdx.graphics.getDeltaTime()));
            }
            else
            {
                inPosition=true;
            }
        }
        else
        {
            if ((sprite.getY() <= maxY || sprite.getY() >= minY+shipHeight) && TimeUtils.nanoTime() - lastBounceTimeY >= 450000000)
            {
                yMoveSpeed *= -1;
                lastBounceTimeY = TimeUtils.nanoTime();
            }
        }
        sprite.setX(sprite.getX() + xMoveSpeed * Gdx.graphics.getDeltaTime());
        sprite.setY(sprite.getY() + yMoveSpeed * Gdx.graphics.getDeltaTime());

        moveHitbox();
    }

    private void moveHitbox()
    {
        hitbox2.setPosition((int) sprite.getX(), (int) (sprite.getY() + shipHeight * 0.33f));
        hitbox1.setPosition((int) (sprite.getX() + shipWidth*0.36f), (int)sprite.getY());
    }
}
