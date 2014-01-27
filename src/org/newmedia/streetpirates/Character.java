package org.newmedia.streetpirates;

import java.util.ArrayList;

import javax.sound.midi.Sequence;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Character extends Image {
	
	//public Character(Texture  texture, int tilex, int tiley, float scalex, float scaley, Stage stage) {
	public Character(Texture  texture, int tilex, int tiley, int tilewidth, int tileheight, Stage stage) {
		super(texture);
		
		this.setHeight(texture.getHeight());
		this.setWidth(texture.getWidth());
		this.setX(tilex * tilewidth);
		this.setY(tiley * tileheight);
		this.setScale((float)tilewidth / (float)texture.getWidth(), (float)tileheight / (float)texture.getHeight() );
		this.setVisible(true);
		stage.addActor(this);
		
	}
	
	public void addClickListener() {
		this.addListener(
		    new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("touchDown x: " + x + " y: " + y);
                return true;  // must return true for touchUp event to occur
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            	System.out.println("touchDown x: " + x + " y: " + y);
            }
		    }
		);
	}
	
	
	
	//public void addmoveToAction(int tilex, int tiley, int tilewidth, int tileheight, float duration) {
	public MoveToAction addmoveToAction(float x, float y, float duration) {
		MoveToAction moveAction = new MoveToAction();
		moveAction.setPosition(x, y);
		//moveAction.setPosition(tilex * tilewidth, tiley * tileheight);
		moveAction.setDuration(duration);
		return moveAction;
	}
	
	
	public void followRoute(ArrayList<Character> route) {
		SequenceAction sequence = new SequenceAction();
		
		for (Character next: route) {
			//this.addmoveToAction(next.getX(), next.getY(), 3f);
			MoveToAction moveAction = new MoveToAction();
			moveAction.setPosition(next.getX(), next.getY());
			moveAction.setDuration(3f);
			sequence.addAction(moveAction);
		}
		this.addAction(sequence);
	}
	
	public void followCharacter(Character next) {
		this.addAction(addmoveToAction(next.getX(), next.getY(), 3f));
		MoveToAction moveAction = new MoveToAction();
		moveAction.setPosition(next.getX(), next.getY());
		//moveAction.setPosition(tilex * tilewidth, tiley * tileheight);
		moveAction.setDuration(3f);
		this.addAction(moveAction);
	}
	


}
