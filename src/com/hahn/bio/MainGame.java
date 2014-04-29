package com.hahn.bio;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import com.hahn.bio.boid.Boid;
import com.hahn.bio.util.Camera;
import com.hahn.bio.util.Config;
import com.hahn.bio.util.ConfigIO;
import com.hahn.bio.util.LibIO;

import static com.hahn.bio.util.Config.WORLD_SIZE;

public class MainGame extends BasicGame {
	public static final int WIDTH = 640, HEIGHT = 480;

	private World mWorld;
	private Camera mCamera;
	
	private Boid mSelected, mSelectedCompare;

	public MainGame() {
		super("BioSphere");
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		if (container.getFPS() > 0) {
			g.translate(-mCamera.getX(), -mCamera.getY());
			
			mWorld.draw(g);
			
			g.translate(mCamera.getX(), mCamera.getY());
			
			g.setColor(Color.white);
			g.drawString("Boids: " + World.boids.size(), 10, 30);
			
			drawGenomeForSelected(g);
		}
	}
	
	private void drawGenomeForSelected(Graphics g) {
		if (mSelected != null) {
			if (mSelectedCompare != null && mSelectedCompare.isGone()) {
				mSelectedCompare = null;
			}
			
			if (mSelected.isGone()) {
				mSelected = null;
				mSelectedCompare = null;
			} else {
				mSelected.drawGenome(g, mSelectedCompare);
				
				g.setColor(Color.white);
				g.drawString("Selected: " + mSelected.getId(), 150, 350);
				g.drawString("Energy: " + mSelected.getEnergy(), 300, 350);
				
				if (mSelectedCompare != null) {
					g.drawString("Compare:  " + mSelectedCompare.getId(), 150, 380);
					g.drawString("Energy: " + mSelectedCompare.getEnergy(), 300, 380);
				}
			}
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
	public void mouseReleased(int button, int x, int y) {
		x += mCamera.getX();
		y += mCamera.getY();
		
		if (button == 0) {
			Boid clicked = World.boids.getClicked(x, y); 
			if (clicked != null) mSelected = clicked;
		} else {
			Boid clicked = World.boids.getClicked(x, y); 
			if (clicked != null) mSelectedCompare = clicked;
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
		} else if (key == Keyboard.KEY_ESCAPE) {
			mSelected = null;
			mSelectedCompare = null;
		}
	}

	public static void main(String[] args) {
		LibIO.extract();
		
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
