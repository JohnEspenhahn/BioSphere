package com.hahn.bio;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import com.hahn.bio.util.Camera;
import com.hahn.bio.util.Config;
import com.hahn.bio.util.ConfigIO;

import static com.hahn.bio.util.Config.WORLD_SIZE;

public class MainGame extends BasicGame {
	public static final int WIDTH = 640, HEIGHT = 480;

	private World mWorld;
	private Camera mCamera;

	public MainGame() {
		super("BioSphere");
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		if (container.getFPS() > 0) {
			g.translate(-mCamera.getX(), -mCamera.getY());
			
			mWorld.draw(g);
			
			g.translate(mCamera.getX(), mCamera.getY());
			
			g.drawString("Boids: " + World.boids.size(), 10, 30);
		}
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		ConfigIO.read(new Config());
		
		mWorld = new World();
		mCamera = new Camera(WIDTH, HEIGHT, WORLD_SIZE, WORLD_SIZE);
	}

	@Override
	public void update(GameContainer container, int i) throws SlickException {		
		if (container.getFPS() > 0) {
			mCamera.update();
			mWorld.update();
		}
	}
	
	@Override
	public void keyPressed(int key, char c) {		
		if (key == Keyboard.KEY_LEFT) {
			mCamera.setVelX(-10);
		} else if (key == Keyboard.KEY_RIGHT) {
			mCamera.setVelX(10);
		} else if (key == Keyboard.KEY_UP) {
			mCamera.setVelY(-10);
		} else if (key == Keyboard.KEY_DOWN) {
			mCamera.setVelY(10);
		} else if (key == Keyboard.KEY_D) {
			Config.DEBUG = !Config.DEBUG;
		}
	}
	
	@Override
	public void keyReleased(int key, char c) {		
		if (key == Keyboard.KEY_LEFT) {
			mCamera.setVelX(0);
		} else if (key == Keyboard.KEY_RIGHT) {
			mCamera.setVelX(0);
		} else if (key == Keyboard.KEY_UP) {
			mCamera.setVelY(0);
		} else if (key == Keyboard.KEY_DOWN) {
			mCamera.setVelY(0);
		}
	}

	public static void main(String[] args) {
		try {
			AppGameContainer appgc;
			appgc = new AppGameContainer(new MainGame());
			appgc.setDisplayMode(WIDTH, HEIGHT, false);
			appgc.setTargetFrameRate(30);
			appgc.setAlwaysRender(true);
			appgc.start();
		} catch (SlickException ex) {
			Logger.getLogger(MainGame.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
