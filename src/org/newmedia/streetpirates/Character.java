package org.newmedia.streetpirates;

import java.util.ArrayList;

import javax.sound.midi.Sequence;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import org.newmedia.streetpirates.Character;

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
	
	public void gotoPointSoft(Level l, float x, float y) {
		int tilex = (int) (x / l.tilewidth);
		int tiley = (int) (y / l.tileheight);
		int mytilex = (int) (this.getX() / l.tilewidth);
		int mytiley = (int) (this.getY() / l.tileheight);
		if ((mytilex == tilex) || (mytiley == tiley)) {
			this.addAction(addmoveToAction(x, y, 3f));	
		}
		else {
			// the route can be ambiguous. 
			// We could try to find either the safest or the least safe path.
			// We don't need to find the optimal/safest route from a pavement. This is the player's part :)
			if (tilex < mytilex)
				mytilex--;
			else if (tilex > mytilex) 
				mytilex++;
			if (tiley < mytiley)
				mytiley--;
			else if (tiley > mytiley) 
				mytiley++;
				this.addAction(addmoveToAction(mytilex * l.tilewidth, mytilex * l.tileheight, 3f));
		}
	}
	
	public void gotoPointHard(Level l, float x, float y, int tileid[]) {
		
	}
	
	public void followCharacter(Character next) {
		//this.addAction(addmoveToAction(next.getX(), next.getY(), 3f));
		MoveToAction moveAction = new MoveToAction();
		moveAction.setPosition(next.getX(), next.getY());
		//moveAction.setPosition(tilex * tilewidth, tiley * tileheight);
		moveAction.setDuration(3f);
		this.addAction(moveAction);
	}
	
	
	public void MoveToActionTileOrTarget(Level l, int tileid, Character target) {
		int tilex = (int)( getX() / l.tilewidth);
		int tiley = (int)( getY() / l.tileheight);
		if (l.is_tileid(target.getX(), target.getY(), tileid)) {
			//try to move to target
		}
		else {
			
		
		}
	}


}
