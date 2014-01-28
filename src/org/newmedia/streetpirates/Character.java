package org.newmedia.streetpirates;

import java.util.ArrayList;
import java.util.Random;
import java.util.*;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import org.newmedia.streetpirates.Character;

public class Character extends Image {
	Level l;
	int validtile_id = 0;
	Random generator;
	long clock;
	Date date;
	
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int DOWN = 2;
	public static final int UP = 3;
	public static final long delta = 1000;
	
	//public Character(Texture  texture, int tilex, int tiley, float scalex, float scaley, Stage stage) {
	public Character(Texture  texture, int tilex, int tiley, int tilewidth, int tileheight, Stage stage, Level l) {
		super(texture);
		this.setX(tilex * tilewidth);
		this.setY(tiley * tileheight);
		this.setScale((float)tilewidth / (float)texture.getWidth(), (float)tileheight / (float)texture.getHeight() );
		//this.setHeight(texture.getHeight() * this.getScaleY());
		//this.setWidth(texture.getWidth() * this.getScaleX());
		this.setVisible(true);
		this.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());	
		stage.addActor(this);
		validtile_id = 0;
		date = new Date();
		clock = date.getTime();
		generator = new Random(clock);
		this.l = l;
	}
	
	public void set_validtile(int tileid) {
		this.validtile_id = tileid;
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
	
	public static boolean overlapRectangles (Actor r1, Actor r2) {
        if (r1.getX() < r2.getX() + r2.getWidth() && r1.getX() + r1.getWidth() > r2.getX() &&
        		r1.getY() < r2.getY() + r2.getHeight() && r1.getY() + r1.getHeight() > r2.getY())
            return true;
        else
            return false;
        //return x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y;
    }
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch,  parentAlpha);
		this.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		for (Actor a: this.getStage().getActors()) {
			if (a!= this && overlapRectangles (a, this)) {
			   //if (a.getActions(). != 0)
				 //  a.removeAction(a.getActions().first());
			   //if (this.getActions() != null)
			   //this.removeAction(a.getActions().first());
			   //a.clearActions();
			   //this.clearActions();
			   //System.out.println("Collision! A.x = " + this.getX() + "A.y = " + this.getY() + "B.x = " + a.getX() + "B.y = " + a.getY());
			   //System.out.println("Collision Widths:! A.width = " + this.getWidth() + "A.height = " + this.getHeight() + "B.width = " + a.getWidth() + "B.height = " + a.getHeight());
		   }	
			
		 }
		long newclock = date.getTime();
		if (newclock - clock > delta) {
			clock = newclock;
			System.out.println("will schedule a random move!");
			RandomMove();
		}
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
	
	//TODO: Use Actor.clearActions() to clear all actions in actor, e.g. if collision happens?!
	
	public void gotoPoint(Level l, float x, float y, boolean hard, int tileid) {
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
			
			SequenceAction sequence = new SequenceAction();
			sequence.addAction(addmoveToAction(mytilex * l.tilewidth, mytilex * l.tileheight, 3f));
			
			sequence.addAction(run(new java.lang.Runnable() {
			    public void run () {
			        System.out.println("Action complete!");
			    }
			}));
			this.addAction(sequence);
		}
	}
	
	public void followCharacter(Character next) {
		//this.addAction(addmoveToAction(next.getX(), next.getY(), 3f));
		MoveToAction moveAction = new MoveToAction();
		moveAction.setPosition(next.getX(), next.getY());
		//moveAction.setPosition(tilex * tilewidth, tiley * tileheight);
		moveAction.setDuration(3f);
		this.addAction(moveAction);
	}
	
	
	public void RandomMove() {
		boolean willmove = false;
		int mytilex = (int) (this.getX() / l.tilewidth);
		int mytiley = (int) (this.getY() / l.tileheight);
		int direction;
		int count = 0;
		SequenceAction sequence = new SequenceAction();
			
		/*if (validtile_id != 0) {
			//find all directions with valid tile
			if (is_tileid(this.getX() - l.tilewidth, this.getY(), validtile_id))
				cango_up = true;			
			if (is_tileid(this.getX() + l.tilewidth, this.getY(), validtile_id))
				cango_down = true;
			if (is_tileid(this.getX(), this.getY() - l.tileheight, validtile_id))
				cango_right = true;
			if (is_tileid(this.getX(), this.getY() + l.tileheight, validtile_id))
				cango[LEFT] = true;	
		}
		else {
			direction = generator.nextInt() % 4;
		}*/
		
		direction = generator.nextInt(4);
		switch(direction) {
			case LEFT:
				while (getX() > (count + 1) * l.tilewidth && 
						(validtile_id == 0 || l.is_tileid(this.getX() - (count + 1) *  l.tilewidth, this.getY(), validtile_id))) {
					count++;
					willmove = true;
					mytilex--;
				}
				break;
			case RIGHT:
				while (getX() + (count + 1) * l.tilewidth < l.tilewidth * (l.width - 1) &&
						(validtile_id == 0 || l.is_tileid(this.getX() + (count + 1) * l.tilewidth, this.getY(), validtile_id))) {
					count++;
					mytilex++;
					willmove = true;
				}
				break;
			case DOWN:
				while (getY() > (count + 1 ) * l.tileheight && 
						(validtile_id == 0 || l.is_tileid(this.getX(), this.getY() - (count + 1 ) * l.tileheight, validtile_id))) {
					count++;
					willmove = true;
					mytiley--;
				}
				break;
			case UP:
				while (getY() + (count + 1) * l.tileheight < l.tileheight * (l.width - 1) && 
						(validtile_id == 0 || l.is_tileid(this.getX(), this.getY() + (count + 1) * l.tileheight, validtile_id))) {
					count++;
					mytiley++;
					willmove = true;
				}
				break;
			default:
				break;
		}
		
		//System.out.println("Random move initiated? " + direction + " " + willmove);
		if (willmove == true) {
			System.out.println("Random move initiated " + direction);
			sequence.addAction(addmoveToAction(mytilex * l.tilewidth, mytiley * l.tileheight, 1f));
			//myActor.addAction(Actions.moveTo(100, 200, 0.7f, Interpolation.bounceOut));
		}
		
		sequence.addAction(run(new java.lang.Runnable() {
		    public void run () {
		    	//System.out.println("Random move completed ");
		        RandomMove();
		    }
		}));
		this.addAction(sequence);
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
