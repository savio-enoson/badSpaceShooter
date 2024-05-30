package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.io.*;
import java.util.Iterator;
import java.util.Scanner;

public class badSpaceShooter extends Game {
	OrthographicCamera camera;
	FitViewport viewport;
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;

	FreeTypeFontGenerator generator;
	FreeTypeFontGenerator.FreeTypeFontParameter parameter;
	BitmapFont titleFont;
	BitmapFont menuFont;

	Array<Circle> stars;
	long lastSpawnTime;
	Array<Run> leaderBoard;

	@Override
	public void create () {
		//setup rendering objects
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera();
		//WORLD UNIT / SCALE
		camera.setToOrtho(false, 750, 1200);
		viewport = new FitViewport(750,1200);
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);

		//read from current leaderboard file
		leaderBoard = new Array<>();

		//setup text and font
		generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts\\title_italic.ttf"));
		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size= 60;
		titleFont = generator.generateFont(parameter);
		generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts\\regular_text.ttf"));
		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size= 18;
		menuFont = generator.generateFont(parameter);

		setScreen(new MainScreen(this));
	}

	@Override
	public void dispose() {
		generator.dispose();
		titleFont.dispose();
		menuFont.dispose();
		batch.dispose();
		shapeRenderer.dispose();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	void generateStars ()
	{
		Circle star = new Circle();
		star.radius= MathUtils.random(1,3);
		star.x = MathUtils.random(0, 750);
		star.y = 1210;
		stars.add(star);
		lastSpawnTime = TimeUtils.nanoTime();
	}

	void startSpawningStars(int spawnRate, float speedMultiplier)
	{
		Gdx.gl.glEnable(GL20.GL_BLEND);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(1,1,1, MathUtils.random(0.1f, 1));	//opacity e antara 10% - 100%

		if(TimeUtils.nanoTime() - lastSpawnTime >= spawnRate) generateStars();

		for (Iterator<Circle> iter = stars.iterator(); iter.hasNext(); )
		{
			Circle star = iter.next();
			star.y -= MathUtils.random(500, 750) * speedMultiplier * Gdx.graphics.getDeltaTime();
			if(star.y < -5) iter.remove();
		}

		for(Circle star: stars)
		{
			shapeRenderer.circle(star.x, star.y, star.radius);
		}
		shapeRenderer.end();
	}

	void readFromLeaderboard() throws IOException {
		File file = new File(Gdx.files.internal("Common\\high_score.txt").path());
		Scanner input = new Scanner(file).useDelimiter(",");
		for (int i=0; i<10; i++)
		{
			if (input.hasNext())
			{
				leaderBoard.add(new Run(input.next(), input.next(), input.next(), input.next(), input.next(), input.next(), input.next()));
			}
		}
		leaderBoard.sort(new sortByScore());
	}

	void writeToLeaderboard() throws IOException {
		File file = new File(Gdx.files.internal("Common\\high_score.txt").path());
		FileWriter writer = new FileWriter(file);

		for (Run run : leaderBoard)
		{
			writer.write(run.getShipType() + ",");
			writer.write(run.getScore() + ",");
			writer.write(run.getShotsFired() + ",");
			writer.write(run.getShotsMissed() + ",");
			writer.write(run.getShotsDodged() + ",");
			writer.write(run.getKillCount() + ",");
			writer.write(run.getDate() + ",");
		}
		writer.close();
	}
}
