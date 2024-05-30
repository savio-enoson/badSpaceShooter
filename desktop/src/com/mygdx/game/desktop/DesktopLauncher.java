package com.mygdx.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.badSpaceShooter;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		//config.width = 750; config.height = 1200;
		config.addIcon("Textures\\game_logo.png", Files.FileType.Internal);
		config.foregroundFPS=90;

		new LwjglApplication(new badSpaceShooter(), config);
	}
}
