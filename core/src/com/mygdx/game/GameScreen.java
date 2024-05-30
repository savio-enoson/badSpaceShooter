package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GameScreen extends ScreenAdapter {
    badSpaceShooter game;
    Weapon weaponType;
    Array<Bullet> bullets;
    Array<Ship> ships;
    Array<Floater> floaters;

    Music music;
    Sound start;

    int score, shotsFired, shotsMissed, shotsDodged, killCount;
    int endScore, endShotsDodged;

    float multiplier;
    float negativeMultiplier;

    long lastScoreIncrement;
    long lastEnemySpawn;
    long lastAsteroidSpawn;
    boolean showGameOver;

    public GameScreen(badSpaceShooter game, Weapon weaponType) {
        this.game = game;
        this.weaponType = weaponType;
    }

    @Override
    public void show() {
        score=0; shotsFired=0; shotsMissed=0; shotsDodged=0;
        multiplier = 1.0f;
        negativeMultiplier = 1.0f;
        lastScoreIncrement = 0;
        lastEnemySpawn = 0;
        lastAsteroidSpawn = 0;
        bullets = new Array<>();
        ships = new Array<>();
        floaters = new Array<>();

        start = Gdx.audio.newSound(Gdx.files.internal("Common\\start.wav"));
        start.play();
        music = Gdx.audio.newMusic(Gdx.files.internal("Music\\game_music.mp3"));
        music.play();
        music.setLooping(true);

        ships.add(new PlayerShip(120, 140, 400, weaponType));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.valueOf("060708FF"));
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.camera.update();
        game.startSpawningStars((int) (100000000*negativeMultiplier), multiplier);

        //add 10 points to score for every second played
        if (TimeUtils.nanoTime() - lastScoreIncrement > 1000000000L)
        {
            lastScoreIncrement = TimeUtils.nanoTime();
            score += 10;
        }

        //spawn enemy
        if (TimeUtils.nanoTime() - lastEnemySpawn > 4000000000L*negativeMultiplier)
        {
            spawnEnemy();
        }

        //spawn asteroid / heal
        if (TimeUtils.nanoTime() - lastAsteroidSpawn > 4000000000L*negativeMultiplier)
        {
            spawnAsteroid();
        }

        //move and render bullets
        moveBullets();
        //move and render floaters
        moveFloaters();

        //setup player movement inputs
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) //W
        {
            ships.get(0).moveShip(Input.Keys.W);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) //A
        {
            ships.get(0).moveShip(Input.Keys.A);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) //S
        {
            ships.get(0).moveShip(Input.Keys.S);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) //D
        {
            ships.get(0).moveShip(Input.Keys.D);
        }

        //setup player attack
        Gdx.input.setInputProcessor(new InputAdapter()
        {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ENTER)
                {
                    ships.get(0).weapon.fire(bullets, ships.get(0).getShipHead());
                    shotsFired++;
                }
                return super.keyDown(keycode);
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                ships.get(0).weapon.fire(bullets, ships.get(0).getShipHead());
                shotsFired++;
                return super.touchDown(screenX, screenY, pointer, button);
            }
        });

        //main game batch
        game.batch.begin();
        //draw floaters
        for (int i=0; i<floaters.size; i++)
        {
            floaters.get(i).sprite.draw(game.batch);
        }

        //draw ships
        for (Ship ship : ships)
        {
            //enemy movement and attack
            if (!(ship instanceof PlayerShip))
            {
                if (TimeUtils.nanoTime() - ((EnemyShip)ship).lastShotTime > MathUtils.random(20,30)*100000000L)   //downcast into EnemyShip type
                {
                    ship.weapon.fire(bullets, ship.getShipHead());
                    ((EnemyShip) ship).lastShotTime = TimeUtils.nanoTime();
                }
                ship.moveShip(0);
            }

            //make ships flash white when taking damage
            if (TimeUtils.nanoTime() - ship.lastDamageTakenTime >= 50000000)
            {
                ship.sprite.setTexture(ship.txt[0]);
                if (ship.health <= 0)
                {
                    ships.removeValue(ship, true);
                    killCount++;
                    score += 50;
                    if (multiplier <=1.75f) //negative multiplier stops at 0.25f
                    {
                        multiplier += 0.05f;
                        negativeMultiplier -= 0.05f;
                        if (!(weaponType instanceof Pistol))
                        {
                            float temp = negativeMultiplier;
                            if (temp < 0.5) {temp=0.5f;}
                            ships.get(0).weapon.setCoolDown(temp);
                            if (weaponType instanceof Laser)
                            {
                                ((Laser)ships.get(0).weapon).setBulletSize((int) (5 * multiplier));
                            }
                        }
                        else
                        {
                            ships.get(0).weapon.setDamage((int) (30 * multiplier));
                        }
                    }
                }
            }
            ship.getSprite().draw(game.batch);
        }

        //print score
        game.menuFont.setColor(1,1,1,1);
        game.menuFont.draw(game.batch, Integer.toString(score), 25, 1175);
        game.menuFont.draw(game.batch, "Health  ", 375, 1175);

        game.batch.end();

        //show health bar
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(Color.valueOf("B80F0AFF"));
        game.shapeRenderer.rect(525, 1175-18, ships.get(0).health, 18);
        game.shapeRenderer.end();

        if (checkGameEndCondition())
        {
            if (!showGameOver)
            {
                endScore = score; endShotsDodged = shotsDodged;
                showGameOver=true;
                try {
                    addToLeaderboard();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                showGameOverScreen();
            }
        }
    }

    @Override
    public void hide() {
        music.dispose();
        start.dispose();
    }

    private void moveBullets()
    {
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Bullet bullet : bullets)
        {
            bullet.move();
            if (bullet.getY() < -5 || bullet.getY() > 1205)
            {
                if (bullet.yVelocity > 0)
                {
                    shotsMissed++;
                }
                else
                {
                    shotsDodged++;
                }
                bullets.removeValue(bullet, true);
                continue;
            }

            if (Math.abs(bullet.yVelocity) > 2000)     //render laser bullets differently
            {
                game.shapeRenderer.setColor(darkenColor(bullet.getColor()));
                game.shapeRenderer.rect(bullet.getX()-(bullet.getSize()*1.8f/2), bullet.getY(), bullet.getSize()*1.8f, bullet.yVelocity);

                game.shapeRenderer.setColor(bullet.getColor());
                game.shapeRenderer.rect(bullet.getX()-(bullet.getSize()*0.8f/2), bullet.getY(), bullet.getSize()*0.8f, bullet.yVelocity);
            }
            else
            {
                game.shapeRenderer.setColor(bullet.getColor());
                game.shapeRenderer.circle(bullet.getX(), bullet.getY(), bullet.getSize());
            }

            for (Ship ship : ships)
            {
                if (bullet.hitbox.overlaps(ship.hitbox1) || bullet.hitbox.overlaps(ship.hitbox2))
                {
                    float dmgMultiplier = 1f;
                    if (bullet.hitbox.overlaps(ship.hitbox1))
                    {
                        dmgMultiplier = 1.5f;
                    }
                    if (ship.takeHit(bullet, dmgMultiplier))
                    {
                        bullets.removeValue(bullet, true);
                    }
                }
            }
        }
        game.shapeRenderer.end();
    }

    private void moveFloaters()
    {
        for (Floater floater : floaters)
        {
            floater.move();

            if (floater.hitBox.overlaps(ships.get(0).hitbox1) || floater.hitBox.overlaps(ships.get(0).hitbox2))
            {
                if (floater instanceof Heal)
                {
                    ((Heal) floater).sound.play();
                    ships.get(0).health+=30;
                    if (ships.get(0).health>200) {ships.get(0).health=200;}
                }
                else
                {
                    ships.get(0).takeHit(new Bullet(((Asteroid)floater).damage), 1);
                }
                floaters.removeValue(floater, true);
            }

            if (floater.sprite.getY() <= -25)
            {
                floaters.removeValue(floater, true);
            }
        }
    }

    private void spawnEnemy()
    {
        if (ships.size > 15) {lastEnemySpawn = TimeUtils.nanoTime(); return;}
        Weapon enemyWeapon = new Pistol();
        int n = MathUtils.random(1,10);
        int strongEnemyRate = (int) (3 * multiplier);
        int topBorder = 1150;
        int row=1;
        if (ships.size/3 > 0)
        {
            row = ships.size/3;
        }
        int maxY = topBorder - (112 * row) - (50 * row);
        if (maxY <= 400) {maxY=400;}

        if (n > strongEnemyRate)
        {
            if (score >=3000 && MathUtils.random(3)==0) {enemyWeapon = new Shotgun();}
            ships.add(new NormalEnemy(125, 80, (int) (MathUtils.random(150,200) * multiplier),
                    enemyWeapon, maxY, 50));
        }
        else
        {
            if (score >=3000 && MathUtils.random(3)==0) {enemyWeapon = new Laser(); ((Laser)enemyWeapon).setBulletSize(5);}
            ships.add(new HardEnemy(125, 80, (int) (MathUtils.random(200,250) * multiplier),
                   enemyWeapon, maxY, 50));
        }
        lastEnemySpawn = TimeUtils.nanoTime();
    }

    private void spawnAsteroid()        //also spawns heals
    {
        int n = MathUtils.random(0, 20);
        if (n < 15)
        {
            floaters.add(new Asteroid());
        }
        else if (n == 18)
        {
            floaters.add(new Heal());
        }
        lastAsteroidSpawn = TimeUtils.nanoTime();
    }

    private boolean checkGameEndCondition()
    {
        for (Ship ship : ships)
        {
            if (ship instanceof PlayerShip)
            {
                return false;
            }
        }
        return true;
    }

    private void addToLeaderboard() throws IOException
    {
        if (game.leaderBoard.size < 10 || endScore >= game.leaderBoard.get(game.leaderBoard.size - 1).getScore())
        {
            String[] temp = weaponType.getClass().toString().split("\\.");
            String s1 = temp[temp.length-1];
            String s2 = String.valueOf(endScore);
            String s3 = String.valueOf(shotsFired);
            String s4 = String.valueOf(shotsMissed);
            String s5 = String.valueOf(shotsDodged);
            String s6 = String.valueOf(killCount);
            String s7 = new SimpleDateFormat("dd MM yyyy").format(new Date());
            game.leaderBoard.add(new Run(s1,s2,s3,s4,s5,s6,s7));
            game.leaderBoard.sort(new sortByScore());
            game.writeToLeaderboard();
        }
    }

    private void showGameOverScreen()
    {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //show semi-transparent background
        game.shapeRenderer.setColor(0,0,0,0.8f);
        game.shapeRenderer.rect(0,0, 750, 1200);
        game.shapeRenderer.end();

        game.batch.begin();
        game.menuFont.setColor(1,1,1,1);
        game.titleFont.setColor(Color.valueOf("90A1B4FF"));
        game.titleFont.draw(game.batch, "Game Over", 195, 1005);
        game.titleFont.setColor(Color.valueOf("FFFFFFFF"));
        game.titleFont.draw(game.batch, "Game Over", 200, 1000);

        game.menuFont.draw(game.batch, "Score", 100, 850);
        game.menuFont.draw(game.batch, Integer.toString(endScore), 500, 850);
        game.menuFont.draw(game.batch, "Shots Fired", 100, 750);
        game.menuFont.draw(game.batch, Integer.toString(shotsFired), 500, 750);
        game.menuFont.draw(game.batch, "Shots Missed", 100, 650);
        game.menuFont.draw(game.batch, Integer.toString(shotsMissed), 500, 650);
        game.menuFont.draw(game.batch, "Shots Dodged", 100, 550);
        game.menuFont.draw(game.batch, Integer.toString(endShotsDodged), 500, 550);
        game.menuFont.draw(game.batch, "Kill Count", 100, 450);
        game.menuFont.draw(game.batch, Integer.toString(killCount), 500, 450);
        game.batch.end();

        Gdx.input.setInputProcessor(new InputAdapter()
        {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                game.setScreen(new MainScreen(game));
                return super.touchDown(screenX, screenY, pointer, button);
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ENTER)
                {
                    game.setScreen(new MainScreen(game));
                }
                return super.keyDown(keycode);
            }
        });
    }

    private Color darkenColor(Color originalColor)
    {
        return new Color(originalColor.r/3, originalColor.g/3, originalColor.b/3, 1f);
    }
}
