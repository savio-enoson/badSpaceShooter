package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;

import java.awt.*;
import java.io.IOException;

public class MainScreen extends ScreenAdapter {
    badSpaceShooter game;

    //planet variables
    Texture planetTxt;
    Sprite planet;
    Long lastTurnTime;

    //music variables
    Music music = Gdx.audio.newMusic(Gdx.files.internal("Music\\main_menu_theme.mp3"));
    Sound hover = Gdx.audio.newSound(Gdx.files.internal("Common\\hover.wav"));
    Sound select = Gdx.audio.newSound(Gdx.files.internal("Common\\select.wav"));

    //variables used for menu
    Rectangle box1, box2, mousePointer;
    boolean soundPlayed=false;
    int menuX, menu1Y, menu2Y;
    boolean selectedMenu1, selectedMenu2 = false;

    //variables used for ship selection
    boolean showSelection=false;
    Rectangle shipBox1, shipBox2, shipBox3;
    boolean selectedShip1, selectedShip2, selectedShip3 = false;

    //variables used for leaderboard
    boolean showLeaderboard=false;

    public MainScreen(badSpaceShooter game) {
        this.game = game;
    }

    @Override
    public void show() {
        //setup rotating planet
        float planetSize = 900;
        planetTxt = new Texture(Gdx.files.internal("Textures\\sphere.png"));
        planet = new Sprite(planetTxt);
        planet.setPosition(-120, 60);
        planet.setSize(planetSize,planetSize);
        planet.setRotation(0);
        planet.setOrigin(planet.getX() + (planetSize/2), planet.getY() + (planetSize/2.5f));
        lastTurnTime = TimeUtils.nanoTime();

        //update leaderboard when reaching this screen
        game.leaderBoard = new Array<>();
        try {
            game.readFromLeaderboard();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //setup stars
        game.stars = new Array<>();
        game.generateStars();

        //setup music
        music.play();
        music.setLooping(true);

        //setup text boxes
        menuX = 375; menu1Y = 325; menu2Y = 200;
        box1 = new Rectangle(); box1.setSize(250, 30); box1.setLocation(menuX, menu1Y-30);
        box2 = new Rectangle(); box2.setSize(250, 30); box2.setLocation(menuX, menu2Y-30);
        mousePointer = new Rectangle(); mousePointer.setSize(1,1);

        //setup selection boxes
        shipBox1 = new Rectangle(); shipBox1.setSize(150,150); shipBox1.setLocation(300, 900);
        shipBox2 = new Rectangle(); shipBox2.setSize(150,150); shipBox2.setLocation(300, 600);
        shipBox3 = new Rectangle(); shipBox3.setSize(150,150); shipBox3.setLocation(300, 300);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.valueOf("060708FF"));
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.camera.update();
        game.startSpawningStars(100000000, 1.0f);

        game.batch.begin();

        //rotating planet
        planet.draw(game.batch);
        if(TimeUtils.nanoTime() - lastTurnTime > 10000000)
        {
            planet.setRotation(planet.getRotation()+0.05f);
            lastTurnTime = TimeUtils.nanoTime();
        }

        //print text and shadow
        float shadowX = 37.5f;
        game.titleFont.setColor(Color.valueOf("90A1B4FF"));
        game.titleFont.draw(game.batch, "Really Bad", shadowX, 1125);
        game.titleFont.draw(game.batch, "Space Shooter Game", shadowX, 1050);
        float titleX = 43.5f;
        game.titleFont.setColor(Color.valueOf("FFFFFFFF"));
        game.titleFont.draw(game.batch, "Really Bad", titleX, 1120);
        game.titleFont.draw(game.batch, "Space Shooter Game", titleX, 1045);

        //highlight selected text
        if (selectedMenu1) { game.menuFont.setColor(1,1,1,1);}
        else { game.menuFont.setColor(1,1,1,0.5f);}
        game.menuFont.draw(game.batch, "New Game", menuX, menu1Y);
        if (selectedMenu2) { game.menuFont.setColor(1,1,1,1);}
        else { game.menuFont.setColor(1,1,1,0.5f);}
        game.menuFont.draw(game.batch, "Leaderboard", menuX, menu2Y);
        game.batch.end();

        //setup inputs
        if (!showSelection) //default input
        {
            Gdx.input.setInputProcessor(new InputAdapter()
            {
                @Override
                public boolean mouseMoved(int screenX, int screenY)
                {
                    Vector3 touchPosition = new Vector3();
                    touchPosition.set(screenX, screenY, 0);
                    game.camera.unproject(touchPosition);
                    mousePointer.setLocation((int)touchPosition.x, (int)touchPosition.y);

                    if (mousePointer.intersects(box1))
                    {
                        if (!soundPlayed)
                        {
                            hover.play();
                            soundPlayed=true;
                        }
                        if (!selectedMenu1)
                        {
                            selectedMenu1=true;
                        }
                    }
                    else if (mousePointer.intersects(box2))
                    {
                        if (!soundPlayed)
                        {
                            hover.play();
                            soundPlayed=true;
                        }
                        if (!selectedMenu2)
                        {
                            selectedMenu2=true;
                        }
                    }
                    else
                    {
                        soundPlayed=false;
                        selectedMenu1=false;
                        selectedMenu2=false;
                    }
                    return super.mouseMoved(screenX, screenY);
                }

                @Override
                public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                    float delay = 0.45f; // seconds

                    if (selectedMenu1)
                    {
                        select.play();
                        showSelection=true;
                        return super.touchDown(screenX, screenY, pointer, button);
                    }
                    else if (selectedMenu2)
                    {
                        select.play();
                        Timer.schedule(new Timer.Task(){
                            @Override
                            public void run() {
                                showLeaderboard=true;
                            }
                        }, delay);
                    }
                    return super.touchDown(screenX, screenY, pointer, button);
                }
            });
        }

        if (showSelection)  //choosing ships
        {
                Gdx.input.setInputProcessor(new InputAdapter()
                {
                    @Override
                    public boolean mouseMoved(int screenX, int screenY)
                    {
                        Vector3 touchPosition = new Vector3();
                        touchPosition.set(screenX, screenY, 0);
                        game.camera.unproject(touchPosition);
                        mousePointer.setLocation((int)touchPosition.x, (int)touchPosition.y);

                        if (mousePointer.intersects(shipBox1))
                        {
                            if (!soundPlayed)
                            {
                                hover.play();
                                soundPlayed=true;
                            }
                            if (!selectedShip1)
                            {
                                selectedShip1=true;
                            }
                        }
                        else if (mousePointer.intersects(shipBox2))
                        {
                            if (!soundPlayed)
                            {
                                hover.play();
                                soundPlayed=true;
                            }
                            if (!selectedShip2)
                            {
                                selectedShip2=true;
                            }
                        }
                        else if (mousePointer.intersects(shipBox3))
                        {
                            if (!soundPlayed)
                            {
                                hover.play();
                                soundPlayed=true;
                            }
                            if (!selectedShip3)
                            {
                                selectedShip3=true;
                            }
                        }
                        else
                        {
                            soundPlayed=false; selectedShip1=false;
                            selectedShip2=false; selectedShip3 = false;
                        }
                        return super.mouseMoved(screenX, screenY);
                    }

                    @Override
                    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                        float delay = 0.45f;
                        if (selectedShip1)
                        {
                            Timer.schedule(new Timer.Task(){
                                @Override
                                public void run() {
                                    game.setScreen(new GameScreen(game, new Pistol()));
                                }
                            }, delay);
                        }
                        else if (selectedShip2)
                        {
                            Timer.schedule(new Timer.Task(){
                                @Override
                                public void run() {
                                    game.setScreen(new GameScreen(game, new Shotgun()));
                                }
                            }, delay);
                        }
                        else if (selectedShip3)
                        {
                            Timer.schedule(new Timer.Task(){
                                @Override
                                public void run() {
                                    game.setScreen(new GameScreen(game, new Laser()));
                                }
                            }, delay);
                        }
                        else
                        {
                            showSelection=false;
                        }
                        return super.touchDown(screenX, screenY, pointer, button);
                    }
                });

            showSelection();
        }

        if (showLeaderboard)
        {
            Gdx.input.setInputProcessor(new InputAdapter()
            {
                @Override
                public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                    showLeaderboard=false;
                    return super.touchDown(screenX, screenY, pointer, button);
                }

                @Override
                public boolean keyDown(int keycode) {
                    if (keycode== Input.Keys.ENTER)
                    {
                        showLeaderboard=false;
                    }
                    return super.keyDown(keycode);
                }
            });

            showLeaderboard();
        }
    }

    @Override
    public void hide() {
        planetTxt.dispose();
        music.dispose();
        hover.dispose();
        select.dispose();
    }

    private void showSelection()
    {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //show semi-transparent background
        game.shapeRenderer.setColor(0,0,0,0.8f);
        game.shapeRenderer.rect(0,0, 750, 1200);
        game.shapeRenderer.end();

        game.batch.begin();
        game.menuFont.setColor(1,1,1,0.5f);
        game.batch.draw(new Texture(Gdx.files.internal("Textures\\pistol_ship.png")), 300, 900, 150,150);
        game.menuFont.draw(game.batch, "Pistol", 321, 880);
        game.batch.draw(new Texture(Gdx.files.internal("Textures\\shotgun_ship.png")), 300, 600, 150,150);
        game.menuFont.draw(game.batch, "Shotgun", 298, 580);
        game.batch.draw(new Texture(Gdx.files.internal("Textures\\laser_ship.png")), 300, 300, 150,150);
        game.menuFont.draw(game.batch, "Laser", 325, 280);
        if (selectedShip1)
        {
            game.menuFont.setColor(1,1,1,1);
            game.menuFont.draw(game.batch, "Pistol", 321, 880);
        }
        else if (selectedShip2)
        {
            game.menuFont.setColor(1,1,1,1);
            game.menuFont.draw(game.batch, "Shotgun", 298, 580);
        }
        else if (selectedShip3)
        {
            game.menuFont.setColor(1,1,1,1);
            game.menuFont.draw(game.batch, "Laser", 325, 280);
        }
        game.batch.end();
    }

    private void showLeaderboard() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //show semi-transparent background
        game.shapeRenderer.setColor(0,0,0,0.925f);
        game.shapeRenderer.rect(0,0, 750, 1200);
        game.shapeRenderer.end();

        game.batch.begin();
        game.menuFont.setColor(1,1,1,1);
        game.titleFont.setColor(Color.valueOf("90A1B4FF"));
        game.titleFont.draw(game.batch, "Leaderboard", 160, 1105);
        game.titleFont.setColor(Color.valueOf("FFFFFFFF"));
        game.titleFont.draw(game.batch, "Leaderboard", 165, 1100);

        int rankNo=1; int printX = 50; int printY = 900;
        game.menuFont.draw(game.batch, "Rank", printX-25, printY+100);
        game.menuFont.draw(game.batch, "Ship", printX+95, printY+100);
        game.menuFont.draw(game.batch, "Score", printX+325, printY+100);
        game.menuFont.draw(game.batch, "Date", printX+485, printY+100);
        for (Run run : game.leaderBoard)
        {
            game.menuFont.draw(game.batch, Integer.toString(rankNo), printX, printY);
            game.menuFont.draw(game.batch, run.getShipType(), printX+95, printY);
            game.menuFont.draw(game.batch, run.getScoreString(), printX+325, printY);
            game.menuFont.draw(game.batch, run.getDate(), printX+485, printY);
            printY-=75; rankNo++;
        }
        game.batch.end();
    }

    private void systemPrintLeaderboard()
    {
        System.out.println(game.leaderBoard.size);
        for (Run run : game.leaderBoard)
        {
            System.out.println(run.getShipType());
            System.out.println(run.getScore());
            System.out.println(run.getShotsFired());
            System.out.println(run.getShotsMissed());
            System.out.println(run.getShotsDodged());
            System.out.println(run.getKillCount());
            System.out.println(run.getDate());
        }
    }
}
