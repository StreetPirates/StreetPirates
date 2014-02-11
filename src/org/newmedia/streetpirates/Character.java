package org.newmedia.streetpirates;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import org.newmedia.streetpirates.Character;

public class Character extends Actor {
	Level l;
	int textid = 0, valid_tiles, illegal_tiles, guard_tiles, immune_tiles;
	int tileid_valid[]; //valid tile types to move on
	int tileid_illegal[]; //illegal tile types to move on
	int tileid_guard[]; //tile types to protect from target
	int tileid_immune[]; //tile types to protect from target
	Random generator;
	long clock, clock_lastmoved;
	//Date date;
	boolean random_move, can_move, in_action, moving, inCollision;
	SpriteBatch spriteBatch; 
	Texture currentFrame;
	TextureRegion imageregion[][], currentFrameRegion;
	Animation animation[];
	Character target, goal;
	float stateTime;
	int currentFrameSeriesIdx, numberFrameSeries;
	int movingDirection;
	int lastCollision, routeDirection, currentDirection;
	boolean useAutoRoute, inAutoRoute, emergencyMove;
	Vector2 autoRoute[];
	Vector2 autoRouteReverse[];
	LinkedList<Stack<Integer>> directionFrame;
	ArrayList<Character> footstepPartial;
	ArrayList<Action> actionList;
	
	public boolean pickable;
	public boolean is_picked;
	public static final int LEFT = 3;
	public static final int RIGHT = 2;
	public static final int DOWN = 0;
	public static final int UP = 1;
	public static final int CURRDIRECTION = -1;
	public static final long delta = 100000;
	public static final int MAX_TILE_TYPES = 3;
	
	public static final int MESSAGE_STAY = 1;
	public static final int MESSAGE_RESTART_LEVEL = 2;
	public static final int MESSAGE_GOTO_MENU = 3;
	
	//public Character(Texture  texture, int tilex, int tiley, float scalex, float scaley, Stage stage) {
	public Character(Texture texture[], float tilex, float tiley, float scaling, Stage stage, Level l) {
		imageregion = new TextureRegion[5][texture.length];
		animation = new Animation[5];
		for(int i = 0; i < texture.length; i++) {
			imageregion[0][i] = new TextureRegion(texture[i]);
		}
		this.numberFrameSeries = 1;
		this.currentFrameSeriesIdx = 0;
		
		this.setX(tilex * l.tilewidth);
		this.setY(tiley * l.tileheight);
		float texture_ratio = (float)texture[0].getHeight()/ (float)texture[0].getWidth();
		
		this.setScale((float)l.tilewidth / (float)texture[0].getWidth() * scaling, (float)l.tileheight / (float)texture[0].getHeight() * scaling * texture_ratio );
		this.setHeight(texture[0].getHeight() * this.getScaleY());
		this.setWidth(texture[0].getWidth() * this.getScaleX());
		this.animation[0] = new Animation(0.1f, imageregion[0]);
		spriteBatch = new SpriteBatch();
		
		this.setVisible(true);
		this.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		this.setTouchable(Touchable.enabled);
		
		//System.out.println("Character:! width = " + this.getWidth() + "height = " + this.getHeight() + " originx:  " + this.getX() + " originy: " + this.getY() );
		
		stage.addActor(this);
		tileid_valid = new int[MAX_TILE_TYPES];
		tileid_guard = new int[MAX_TILE_TYPES];
		tileid_illegal = new int[MAX_TILE_TYPES];
		tileid_immune = new int[MAX_TILE_TYPES];
		lastCollision = -1;
		valid_tiles = 0;
		guard_tiles = 0;
		illegal_tiles = 0;
		clock = System.nanoTime();//currentTimeMillis();
		clock_lastmoved = clock;
		generator = new Random(clock);
		this.l = l;
		this.pickable = false;
		this.is_picked = false;
		this.random_move = false;
		this.can_move = false;
		this.moving = false;
		this.in_action = false;
		this.inCollision = false;
		this.target = null;
		this.goal = null;
		this.useAutoRoute = false;
		this.inAutoRoute = false;
		this.emergencyMove = false;
		this.directionFrame = new LinkedList<Stack<Integer>>();
		this.currentDirection = DOWN;
		this.footstepPartial = new ArrayList<Character>();
		this.actionList = new ArrayList<Action>();
	}
	
	public void addFrameSeries(Texture texture[]) {
		for(int i = 0; i < texture.length; i++) {
			imageregion[numberFrameSeries][i] = new TextureRegion(texture[i]);
		}
		this.animation[numberFrameSeries] = new Animation(0.1f, imageregion[numberFrameSeries]);
		numberFrameSeries++;
	}
	
	public void set_moving(boolean set) {
		moving = set;
	}
	
	public void setFrameSeriesIdx(int idx) {
		currentFrameSeriesIdx = idx;
	}
	
	public void set_in_action(boolean set) {
		in_action = set;
	}
	
	public void set_pickable(boolean pick) {
		pickable = pick;
	}
	
	public void set_validtile(int tileid) {
		this.tileid_valid[valid_tiles] = tileid;
		this.valid_tiles++;
	}
	
	public void set_guardtile(int tileid) {
		this.tileid_guard[guard_tiles] = tileid;
		this.guard_tiles++;
	}
	
	public void set_immunetile(int tileid) {
		this.tileid_immune[immune_tiles] = tileid;
		this.immune_tiles++;
	}
	
	public void set_illegaltile(int tileid) {
		this.tileid_illegal[illegal_tiles] = tileid;
		this.illegal_tiles++;
	}
	
	public int get_immune_tiles() {
		return this.immune_tiles;
	}
	
	public void set_random_move() {
		this.random_move = true;
	}
	
	public void set_can_move(boolean move) {
		this.can_move = move;
	}
	
	public void set_target(Character target) {
		this.target = target;
	}
	
	public void set_goal(Character target) {
		this.goal = target;
	}
	
	public Character get_target() {
		return target;
	}
	
	public void backtrackFootsteps() {
		for (Character c: footstepPartial) {
			c.setVisible(false);
			c.setSize(0,0);
			c.setPosition(0,0);
		}
		footstepPartial.clear();
	}
	
	public class MessageListener extends InputListener {
		Character c;
		Screen screen;
		float top, bottom, right, left;
		int finalMessage;
		
		/* create a listener that will close this message/actor,  when actor is clicked inside the box
		 * defined by bottom, top, leftm right parameters. These are relative to start of actor, and not screen coordinates*/
		public MessageListener(Character c, float bottom, float top, float left, float right, int finalMessage) {
			this.c = c;
			this.bottom = bottom;
			this.top = top;
			this.left = left;
			this.right = right;
			this.finalMessage = finalMessage;
		}
		
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			float actorx = event.getStageX() - c.getX();
			float actory = event.getStageY() - c.getY();
			System.out.println("ACTORYES touchDown stagex:" + event.getStageX() + " stagey:" + event.getStageY() +
			" actorx:" + actorx + " actory:" + actory +
			" bottom:" + bottom + " left:" + left + 
			" top:" + top + " right:" + right
			);
			
			if (this.finalMessage == MESSAGE_STAY) {
				System.out.println("? touchDown stagex:" + event.getStageX() + " stagey:" + event.getStageY() +
						" actorx:" + actorx + " actory:" + actory +
						" bottom:" + bottom + " left:" + left);
				c.currentFrameSeriesIdx = (c.currentFrameSeriesIdx + 1) % c.numberFrameSeries;
			}
			
			else if ((actorx >= left && actorx <= right) &&
			   (actory >= bottom && actory <= top)) {
				System.out.println("GOGOGO touchDown stagex:" + event.getStageX() + " stagey:" + event.getStageY() +
						" actorx:" + actorx + " actory:" + actory +
						" bottom:" + bottom + " left:" + left
						);
				l.hero.resetHeroLevel();
				c.setVisible(false);
				c.removeListener(this);
				if (this.finalMessage == MESSAGE_GOTO_MENU)
					l.game.setScreen(l.game.getMenu());
				else if (this.finalMessage == MESSAGE_RESTART_LEVEL)
					l.game.setScreen(l.game.getCurrentLevel());
			}
			return true;
		}
	}
	
	public class CharacterListener extends InputListener {
		Character character;
		
		public CharacterListener(Character c) {
			character = c;
		}
		
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            //System.out.println("ACTOR touchDown x: " + x + " y: " + y + " stagex:" + event.getStageX() + " stagey:" + event.getStageY() + " actorx:" + getX() + " actory:" + getY());
			if (l.adventure_started == false || l.cityInteraction == true) {
            if (character.pickable == true) {
            	if (l.actor_picked == null) {		
            	    //System.out.println("ACTOR PICKED touchDown x: " + x + " y: " + y);
            		l.actor_picked = character;
            		if (l.route.contains(character)) {
            			//System.out.println("STARFISH REPICKED touchDown x: " + x + " y: " + y);
            			l.route.remove(character);
            			character.backtrackFootsteps();
            			//character.addFootsteps(l.route);
            		}
            	}
            	else if (!l.actor_picked.illegal_tile(event.getStageX(), event.getStageY())) {
            		//int tilex = (int) l.actor_picked.getX()/l.tilewidth;
            		//int tiley = (int) l.actor_picked.getY()/l.tileheight;
            		int tilex = (int) event.getStageX()/l.tilewidth;
                	int tiley = (int) event.getStageY()/l.tileheight;
            		l.actor_picked.setX(tilex * l.tilewidth);
            		l.actor_picked.setY(tiley * l.tileheight);
            		l.actor_dropped = true;
            		character.addFootsteps(l.route);
            		l.route.add(character);
            	}
            }
            else if (character == l.compass) {
            	l.start_route = true;
            	l.hero.followRoute(l.route);
            	l.setup_adventure();
            }
			}
            return false;  // must return true for touchUp event to occur
        }
		
        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
        	//System.out.println("ACTOR touchDown x: " + x + " y: " + y);
        }
        
	}
	
	public void addClickListener() {
		this.addListener(new CharacterListener(this));
	}
	
	public MessageListener addMessageListener(float bottom, float top, float left, float right, int finalMessage) {
		MessageListener msg = new MessageListener(this, bottom, top, left, right, finalMessage);
		this.addListener(msg);
		return msg;
	}
	
	/* fulldim of 1.0 means the overlap will trigger when the full bounding boxes start to collide
	 * fulldim of 0.5 means the overlap will trigger when the boxes are merged into each other by roughly half
	 * etc. 
	 */
	//TODO: separate collision percentag crietria for x, y. More specifically, focus on lower part of hero (where feet are)
	public static boolean overlapChunkyRectangles (Actor r1, Actor r2, float fulldim) {
        if (r1.getX() < r2.getX() + r2.getWidth() * fulldim && r1.getX() + r1.getWidth() * fulldim > r2.getX() &&
        		r1.getY() < r2.getY() + r2.getHeight() * fulldim && r1.getY() + r1.getHeight() * fulldim > r2.getY())
            return true;
        else
            return false;
        //return x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y;
    }
	
	public static boolean overlapRectangles (Actor r1, Actor r2, float dimx, float dimy) {
        if (java.lang.Math.abs(r1.getX() + r1.getWidth()/2 - r2.getX() - r2.getWidth()/2) < java.lang.Math.max(r1.getWidth(), r2.getWidth())* dimx &&
        		java.lang.Math.abs(r1.getY() - r2.getY()) < java.lang.Math.min(r1.getWidth(), r2.getHeight())* dimy )
            return true;
        else
            return false;
        //return x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y;
    }
	
	public static Vector2 findTile(int tilex, int tiley, int direction) {
		Vector2 next = new Vector2(tilex, tiley);
		switch (direction) {
			case DOWN:
				next.y--;
				break;
			case UP:
				next.y++;
				break;
			case RIGHT:
				next.x++;
			case LEFT:
				next.x--;
			default:
				break;
		}
		return next;
	}
	
	public void saveActions() {
		for (Action a: this.getActions()) {
			System.out.println("SAVE MOVE ");
			actionList.add(a);
			
		}
		
		for (int i = 0; i < this.actionList.size(); i++) {
			this.removeAction(this.actionList.get(i));
		}
		
		for (int i = 0; i < this.actionList.size(); i++) {
			this.addAction(delay(1f));
			this.addAction(this.actionList.get(i));
		}
		
		//this.flushActionsFrames();
	}
	
	public void restoreActions() {
		for (Action a: this.getActions()) {
			System.out.println("RESTORE MOVE ");
			actionList.remove(a);
			this.addAction(a);
		}
	}
	
	public void resetHeroLevel() {
		if (this != l.hero)
			return;
		this.setPosition(0,0);
		this.flushActionsFrames();
		this.set_moving(false);
    	this.set_in_action(false);
		l.resetLevel();
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch,  parentAlpha);
		
		stateTime += Gdx.graphics.getDeltaTime();
		if (moving == true)
			currentFrameRegion = animation[currentFrameSeriesIdx].getKeyFrame(stateTime, true);
		else 
			currentFrameRegion = imageregion[currentFrameSeriesIdx][0];
		
	    spriteBatch.begin();
        spriteBatch.draw(currentFrameRegion, getX(), getY(), getWidth(), getHeight());        
        spriteBatch.end();

		this.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		
		/* if target character has moved to an immune tile, cancel pending actions. 
		 * We don't want a car to overrun a hero on a pedestrian walk because the random move
		 * was planned before the hero moved there.
		 * TODO: Ideally we should only stop actions that go the hero's location... how to do that?
		 */
		if (target != null && target.immune_tile(target.getX() + target.getWidth()/2, target.getY()) &&
				overlapRectangles (target, this, (float)1.0, (float)1.0) && (this.emergencyMove == false)
				) {
			//System.out.println("AVOIDED PIRATE PEDESTRIAN! WHEYWEEEEE" + getX() + " " + getY());
			this.flushActionsFrames();
			if (this.useAutoRoute) { 
	        	this.inAutoRoute = false;
	        }
			//return;
		}
		
		/* if character is on target's immune tile (e.g. car on pedwalk), and target is apporaching the pedwalk 
		 * we have a big problem... either target has to pause, or the current character has to move away immediately
		 * from the illegal tile. Probably both.
		 */
		if (target != null && target.immune_tile(this.getX(), this.getY()) &&
				overlapRectangles (target, this, (float)1.0, (float)1.0) && (this.emergencyMove == false)
				) {
			//System.out.println("ON PEDESTRIAN WALK WHILE! " + getX() + " " + getY());
			this.flushActionsFrames();
			int tilex = (int)this.getX() / l.tilewidth;
			int tiley = (int)this.getY() / l.tileheight;
			int direction = 0;
			boolean validFound = false;
			this.emergencyMove = true;
			while (validFound == false) {
				Vector2 newtile = findTile(tilex, tiley, direction);
				if (!illegal_tile(newtile.x * l.tilewidth, newtile.y * l.tileheight) &&
						!target.immune_tile(newtile.x * l.tilewidth, newtile.y * l.tileheight)) {
					gotoPoint(l, newtile.x * l.tilewidth, newtile.y * l.tileheight, 0.03f);
					//System.out.println("ON PEDESTRIAN WALK WHILE! from " + tilex + " " + tiley + " GOTO " + newtile.x + " " + newtile.y);
					validFound = true;
					SequenceAction sequence = new SequenceAction();
					sequence.addAction(run(new java.lang.Runnable() {
					    public void run () {
					    	//System.out.println("DONE ON PEDESTRIAN WALK WHILE! ");
					        emergencyMove = false; //action works when this is commented out. Why?
					    }
					}));
					this.addAction(after(sequence)); //adding the last part above (resetting emergencyMove) as an afterAction make sthe emergency move work
				}
				direction++;
			}
			
			if (this.useAutoRoute) { 
	        	this.inAutoRoute = false;
	        }
			return;
		}
		
		
		for (Character a: l.getBandits()) {
			if (a!= this && a.get_target() == this && overlapRectangles (a, this, (float)0.4, (float)0.2)) {
				l.loseSequence.setVisible(true);
			    l.loseSequence.addListener(new MessageListener(l.loseSequence, 550, 600, 730, 780, MESSAGE_RESTART_LEVEL));
			    this.resetHeroLevel();
			}
		}
		
		for (Character a: l.getCars()) {
			if (a!= this && a.get_target() == this && overlapRectangles (a, this, (float)0.4, (float)0.2)) {
				l.loseSequence.setVisible(true);
			    l.loseSequence.addListener(new MessageListener(l.loseSequence, 550, 600, 730, 780, MESSAGE_RESTART_LEVEL));
			    this.resetHeroLevel();
			}
		}
		
		if (this == l.hero && overlapRectangles (l.hero.goal, this, (float)0.4, (float)0.2)) {
				// need victory message - You reached the treasure!
			    l.winSequence.setVisible(true);
			    l.winSequence.set_moving(true);
			    l.winSequence.addListener(new MessageListener(l.winSequence, 550, 600, 730, 780, MESSAGE_GOTO_MENU));
			    this.resetHeroLevel();
		}
		
		//for (Actor a: this.getStage().getActors()) {
		for (Character a: l.getCars()) {
			//Character c = (Character)a;
			if (a!= this && overlapRectangles (a, this, (float)0.4, (float)0.2) && !this.inCollision) {
			   /*TODO: moving boolean flag is reset in last action, so there is chance a character stays in in_Action/moving limbo (i.e. true flags) forever.
			    * so find a better way of removing a specific action. or resetting the flag at draw function or elsewhere.
			    */
			    
			    // if a car or bad guy, we should pop a message, reset hero to starting position and retry map
			    // there's a problem here, only if actor has a target, e.g. if hero hits a starfish, it 's ok :)
			    if (this == a.get_target()) {
			    	this.setPosition(0,0);
			    	break;
			    }
				
				a.flushActionsFrames();
				this.flushActionsFrames();
			    this.inCollision = true;
			    a.inCollision = true;
			    this.set_moving(false);
		        this.set_in_action(false);
			    a.set_moving(false);
		        a.set_in_action(false);
		        
		        if (a.useAutoRoute) { 
		        	a.routeDirection = ~a.routeDirection;
		        	a.inAutoRoute = false;
		        }
		        
		        if (this.useAutoRoute) {
		        	a.inAutoRoute = false;
		        	this.routeDirection = ~this.routeDirection;
		        }
			    /*System.out.println("Collision! A.x = " + this.getX() + "A.y = " 
			    		+ this.getY() + "B.x = " + a.getX() + "B.y = " + a.getY() 
			    		+ " A.width = " + this.getWidth() + "A.height = " + this.getHeight() 
			    		+ "B.width = " + a.getWidth() + "B.height = " + a.getHeight());*/
			    
			    //System.out.println("Collision! " + this.currentDirection + " " + a.currentDirection);
		   }	
		}
		
		
		if (this.inCollision) {
			int currentCollisions = 0;
			   for (Character a: l.getCars()) {
					//Character c = (Character)a;
					if (a!= this && overlapRectangles (a, this, (float)0.4, (float)0.2)) {
						currentCollisions++;
						break;
					}
			   }
			   if (currentCollisions == 0) {
				   //System.out.println("Reset collision! " + currentDirection );
				   this.inCollision = false;   
			   }
		   }
		
		//List<Action> listactions = this.getActions().asList();
		if (this.can_move /*&& this.getActions().size() == 0*/ ) {
		long newclock = System.nanoTime();//currentTimeMillis();
		if (newclock - clock > delta && in_action == false || (newclock - clock_lastmoved > 5000 * delta)) {
			in_action = false;
			moving = false;
			clock = newclock;
			clock_lastmoved = newclock;
			moveToTileOrTarget();
		}
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
	
	/*public void resize(int w, int h) {
		//this.setX(getX() * l.tilewidth);
		//this.setY(getY() * tileheight);
		this.setScale((float)l.tilewidth / (float)this.imageregion[0].getRegionWidth(), (float)l.tileheight / (float)this.imageregion[0].getRegionHeight() );
		this.setHeight(imageregion[0].getRegionHeight() * this.getScaleY());
		this.setWidth(imageregion[0].getRegionWidth() * this.getScaleX());
	}*/
	
	public void addFrameChangeAction(SequenceAction sequence) {
		sequence.addAction(run(new java.lang.Runnable() {
		    public void run () {
		    		Stack<Integer> dirstack = directionFrame.peekFirst();
		    		if (dirstack != null && dirstack.empty()) {
		    			directionFrame.remove();
		    			dirstack = directionFrame.peekFirst();
		    		}
		    		if (dirstack != null) {
		    			int dir = dirstack.pop();
		    			if (dir != CURRDIRECTION) {
		    				currentDirection = dir;
		    				//System.out.println("DIRECTIONFRAME SERIES: " + dir);
		        			setFrameSeriesIdx(dir);	      
		    			}
		    			else {
		    				setFrameSeriesIdx(currentDirection);
		    			}
		    		}
		    }
		}));
	}
	
	public void endRouteSequence(SequenceAction sequence) {
		sequence.addAction(run(new java.lang.Runnable() {
		    public void run () {
		        //System.out.println("Action complete!");
		        moving = false;
		        in_action = false;
		        //emergencyMove = false;
		        if (directionFrame.peekFirst() != null) {
		        	directionFrame.remove();
		        }
		    }
		}));
	}
	
	public void followRoute(ArrayList<Character> route) {
		int mytilex = (int) (this.getX() / l.tilewidth);
		int mytiley = (int) (this.getY() / l.tileheight);
		int tilex, tiley;
		float destx, desty;
		
		Stack<Vector2> path;
		
		this.flushActionsFrames();
		SequenceAction sequence = new SequenceAction();
		this.moving = true;
		this.in_action = true;
		
		for(Character dest: route) {
			
			destx = dest.getX();
			desty = dest.getY();
			tilex = (int) (destx / l.tilewidth);
			tiley = (int) (desty / l.tileheight);
			// the route can be ambiguous. 
			// We could try to find either the safest or the least safe path.
			// We don't need to find the optimal/safest route from a pavement. This is the player's part :)	
			path = getPath(mytilex, mytiley, tilex, tiley);
		
			while (path.empty() == false) {
				Vector2 next = path.pop();
				
				if (numberFrameSeries > 1) {
					addFrameChangeAction(sequence);
				}
				
				sequence.addAction(moveTo(next.x * l.tilewidth, next.y * l.tileheight, 0.6f));
			}
			//TODO: maybe we just need to go to tile, not exact position for route? so comment next line...
			sequence.addAction(moveTo(dest.getX(), dest.getY(), 0.5f));
			//System.out.println("LAST PATH x: " + x + " y: " + y);
			mytilex = tilex;
			mytiley = tiley;
		}
		//TODO: no matching directionFrame for this move
		//sequence.addAction(moveTo(destx, desty, 0.5f));
		
		endRouteSequence(sequence);
		this.addAction(sequence);
		//clear the route!
		route.clear();
	}

	public void addAutoRoute(Vector2 route[]) {
		int size = route.length;
		autoRoute = new Vector2[size];
		autoRouteReverse = new Vector2[size];
		for (int i = 0; i < size; i++) {
			autoRoute[i] = route[i];
			autoRouteReverse[size - i - 1] = route[i];
		}
		useAutoRoute = true;
		routeDirection = 0;
	}
	
	public void loopRoutePoints(Vector2 route[]) {
		
		Stack<Vector2> path;
		int mytilex = (int) (this.getX() / l.tilewidth);
		int mytiley = (int) (this.getY() / l.tileheight);
		int tilex;
		int tiley;
		
		//this.clearActions();
		this.flushActionsFrames();
		SequenceAction sequence = new SequenceAction();
		this.moving = true;
		this.in_action = true;
		for(Vector2 dest: route) {
			//tweak coordinates?... We want the route to pass through middle (sort of) of actor, not bottom-left coordinates
			
			tilex = (int) (dest.x) / l.tilewidth;
			tiley = (int) (dest.y) / l.tileheight;	
			//System.out.println("PATH x: " + mytilex + " y: " + mytiley + " x:" + tilex + " y:" + tiley);
			path = getPath(mytilex, mytiley, tilex, tiley);
		
			while (path.empty() == false) {
				Vector2 next = path.pop();
				
				sequence.addAction(moveTo(next.x * l.tilewidth, next.y * l.tileheight, 0.3f));
				if (numberFrameSeries > 1) {
					addFrameChangeAction(sequence);
				}
			}
			
			//TODO: maybe we just need to go to tile, not exact position for route? so comment next line...
			//sequence.addAction(moveTo(dest.getX(), dest.getY(), 0.5f));
			mytilex = tilex;
			mytiley = tiley;
		}
		
		sequence.addAction(run(new java.lang.Runnable() {
		    public void run () {
		        moving = false;
		        in_action = false;
		        if (directionFrame.peekFirst() != null) {
		        	directionFrame.remove();
		        }
		        routeDirection = ~routeDirection;
		        inAutoRoute = false;
		    }
		}));
			
		
		this.addAction(sequence);
	}

	public void addFootsteps(ArrayList<Character> route) {
		
		int mytilex, mytiley, tilex, tiley;
		float destx, desty;
		
		if (route.size() != 0) {
			mytilex = (int) route.get(route.size()-1).getX() / l.tilewidth;
			mytiley = (int) route.get(route.size()-1).getY() / l.tileheight;
		}
		else {
			mytilex = (int) l.hero.getX() / l.tilewidth;
			mytiley = (int) l.hero.getY() / l.tileheight;
		}
		
		Stack<Vector2> path;
		
		//tweak coordinates... We want the route to pass through middle (sort of) of actor, not bottom-left coordinates
		destx = this.getX() + this.getWidth()/4;
		desty = this.getY() + this.getHeight()/4;
		tilex = (int) (destx / l.tilewidth);
		tiley = (int) (desty / l.tileheight);	
		path = l.hero.getPath(mytilex, mytiley, tilex, tiley);
		//System.out.println("DRAW FOOTLIST from x:" + mytilex + " and y: " + mytiley + "to x: " + tilex + " y: " + tiley);
		while (path.empty() == false) {
			Vector2 next = path.pop();
			Character newstep = new Character(l.texture_footstep, next.x, next.y, (float)1.0, this.getStage(), l);
			

			/*Stack<Integer> dirstack = l.hero.directionFrame.peekFirst();
    		if (dirstack != null && dirstack.empty()) {
    			l.hero.directionFrame.remove();
    			dirstack = l.hero.directionFrame.peekFirst();
    		}
			if (dirstack != null) {
			int dir = dirstack.pop();
			if (dir != CURRDIRECTION) {
				System.out.println("DIRECTIONFRAME FOOTSTEPS SERIES: " + dir);
				switch (dir) {
					case DOWN:
						newstep.setRotation((float)180.0);
						break;
					case RIGHT:
						newstep.setRotation((float)90.0);
						break;
					case LEFT:
						newstep.setRotation((float)270);
						break;
					}
				}
			}*/			
			
			this.footstepPartial.add(newstep);
			l.footstep.add(newstep);
			//System.out.println("DRAW FOOTLIST from x:" + next.x + " and y: " + next.y);
		}
		//l.footstep.add(new Character(l.texture_footstep, tilex, tiley, (float)0.75, this.getStage(), l));
		//cleanup directionFrame stack of hero
		l.hero.directionFrame.clear();
	}
	
	
	//TODO: Menu + buttons + parrot + compass
	//TODO: route needs to be modified when picking up a starfish again
	//TODO: Some characters should not collide with each other,
	//TODO: Fix scaling and resize
	//TODO: Accurate point clicking?! done
	//TODO: Review all clearActions() calls to actors. Use Actor.clearActions() to clear all actions in actor, e.g. if collision happens?!
	//TODO: Animations, add side animations depending on direction of movement done
	//TODO: Add different randomness on moving actors or make preset routes
	
	/* A* pathfinding on the fully connected tiledmap grid. Uses tile costs from Level class */
	public Stack<Vector2> getPath(int startx, int starty, int x, int y)
	{
	    //PriorityQueue<Vector2> openList = new PriorityQueue<Vector2>(10, new SearchNodeComparator());
		ArrayList<Vector2> openList = new ArrayList<Vector2>();
	    ArrayList<Vector2> closedList = new ArrayList<Vector2>();
	    Stack<Vector2> path = new Stack<Vector2>();
	    Stack<Integer> direction = new Stack<Integer>();
	    
	    if ((x == startx) && (y == starty))
			return path;
	    
	    int costpath[][] = new int[l.width][l.height];
	    int costpathgoal[][] = new int[l.width][l.height];
	    Vector2 parents[][] = new Vector2[l.width][l.height];

	    Vector2 start = new Vector2(startx, starty);

	    
	    costpath[startx][starty] = 0;
	    costpathgoal[startx][starty] = 0;

	    openList.add(start);

	    while (openList.size() > 0)
	    {
	    	int currentgoal = 1000;
	    	Vector2 current;
	    	current = openList.get(0);
	    	for (Vector2 d: openList) {
	    		if (costpathgoal[(int)d.x][(int)d.y] < currentgoal) {
	    			current = d;
	    			currentgoal = costpathgoal[(int)d.x][(int)d.y];
	    		}
	    	}
	        	        
	        if (current.x == x && current.y == y) {
	        	break;
	        }
	        else
	        {
	            ArrayList<Vector2> neighbours = l.getNeighbors(current);
	            
	            for (int i = 0; i < neighbours.size(); i++)
	            {
	                Vector2 node = neighbours.get(i);
	                int nodex = (int) node.x;
	            	int nodey = (int) node.y;
	                //System.out.print("Inspecting node" + node.getValue().toString());

	                int distanceTraveled = costpath[(int)current.x][(int)current.y] + l.cost[nodex][nodey];
	                if (illegal_tile(nodex * l.tilewidth, nodey * l.tileheight)) {
	                	
	                	distanceTraveled += 10000;
	                }
	                int heuristic = java.lang.Math.abs(nodex - x) + java.lang.Math.abs(nodey - y);

	                if (!openList.contains(node) && !closedList.contains(node))
	                {

	                    costpath[nodex][nodey] = distanceTraveled;
	                    costpathgoal[nodex][nodey] = distanceTraveled + heuristic;
	                    parents[nodex][nodey] = current;
	                    openList.add(node);
	                }
	                else if(openList.contains(node))
	                {
	                    if (costpath[nodex][nodey] > distanceTraveled)
	                    {
	                    	costpath[nodex][nodey] = distanceTraveled;
	                    	costpathgoal[nodex][nodey] = distanceTraveled + heuristic;
	                    	parents[nodex][nodey] = current;
	                    }
	                }
	            }
	            openList.remove(current);
	            closedList.add(current);
	        }
	    }
	    boolean backtrack = true;
	    int newx = x;
	    int newy = y;
	    //System.out.println("LAST PATH BUILD STACK");
	    while (backtrack == true) {
	    	int currentx = newx;
	    	int currenty = newy;
	    	//System.out.println("STACK TILE IN PATHFINDING: " + currentx +  " " + currenty);
	    	path.push(parents[currentx][currenty]);
	    	newx = (int)parents[currentx][currenty].x;
	    	newy = (int)parents[currentx][currenty].y;
	    	
	    	//if (illegal_tile(newx, newy))
    			//System.out.println("ILLEGAL TILE IN PATH: " + currentx +  " " + currenty);
	    	
	    	if (this.numberFrameSeries > 1) {
	    		int dir = getDirection(newx, newy, currentx, currenty);
	    		direction.push(dir);
	    	}
	    	if (newx == startx && newy == starty)
	    		break;
	    }
	    directionFrame.add(direction);
	    return path;
	}
	
	public int getDirection(int oldx, int oldy, int newx, int newy) {
		if (oldy == newy) {
			if (oldx < newx) 
				return RIGHT;
			else if (oldx > newx)
				return LEFT;
		}
		if (oldx == newx) {
			if (oldy < newy)
				return UP;
			else if (oldy > newy)
				return DOWN;
		}
		return CURRDIRECTION;
	}
	
	public void gotoPoint(Level l, float x, float y, float speed) {//, boolean hard) {
		int tilex = (int) (x / l.tilewidth);
		int tiley = (int) (y / l.tileheight);
		int mytilex = (int) (this.getX() / l.tilewidth);
		int mytiley = (int) (this.getY() / l.tileheight);
		float duration = speed;
		Stack<Vector2> path;
		
		//tweak coordinates for hero only... We want the chosen point to be rougly in the "middle" of of actor, not bottom-left coordinates
		//System.out.println("PATH x: " + x + " y: " + y + "width: " + this.getWidth() + "height: " + this.getHeight());
		if (this == l.hero) { 
			if (x >= this.getWidth() / 2) {
				x -= this.getWidth()/2;
			} else x = 0;
			if (y >= this.getHeight() / 4) {
				y -= this.getHeight()/4;
			} else y = 0;
		}
		
		this.flushActionsFrames();
		SequenceAction sequence = new SequenceAction();
		this.moving = true;
		this.in_action = true;
		
		//TODO: this case is needed for now , otherwise the path planning for the same tile somehow  gies nullpointer exception
		if (this == l.hero &&  (mytilex == tilex) || (mytiley == tiley)) {
			sequence.addAction(moveTo(x, y, 3f));	
		}
		else {	
			
			// the route can be ambiguous. 
			// We could try to find either the safest or the least safe path.
			// We don't need to find the optimal/safest route from a pavement. This is the player's part :)	
			path = getPath(mytilex, mytiley, tilex, tiley);
		
			while (path.empty() == false) {
				Vector2 next = path.pop();
				
				if (numberFrameSeries > 1) {
					addFrameChangeAction(sequence);
				}
				
				sequence.addAction(moveTo(next.x * l.tilewidth, next.y * l.tileheight, duration));
				//System.out.println("PATH x: " + next.x + " y: " + next.y);
			}
			//TODO: no matching directionFrame for this move
			sequence.addAction(moveTo(x, y, 0.5f));
		}
		endRouteSequence(sequence);
		
		this.addAction(sequence);
	}
	
	public void flushActionsFrames() {
		//if (this.emergencyMove == false ) {
		this.clearActions();
		Stack<Integer> a;
		while ((a = this.directionFrame.pollFirst()) != null) {
			a.clear();
		//}
		}
	}
	
	public ArrayList<Vector2> getTileList(float x, float y) {
		ArrayList<Vector2> tiles = new ArrayList<Vector2>();
		int tilex = (int) (x / l.tilewidth);
		int tiley = (int) (y / l.tileheight);
		tiles.add(new Vector2(tilex, tiley));
		float width = this.getWidth();
		float height = this.getHeight();
		int tilexExtra = tilex;
		int tileyExtra = tiley;
		width -= l.tilewidth;
		height -= l.tileheight;
		
		while (width > l.tilewidth/8) {
			tilexExtra++;
			//System.out.println("EXPAND tile for x: "+ tilex + " y: " + tiley + "with x: " + tilexExtra + " y: " + tileyExtra);
			tiles.add(new Vector2(tilexExtra, tiley));
			width -= l.tilewidth;
		}
		
		while (height > l.tileheight/8) {
			tileyExtra++;
			tiles.add(new Vector2(tilex, tileyExtra));
			height -= l.tileheight;
		}
			
		return tiles;
	}
	
	public boolean valid_tile(float x, float y) {
		int tileid = l.getTileId(x, y);
		for (int i = 0; i < valid_tiles; i++) {
			if (tileid == tileid_valid[i])		
				return true;

		}
		return false;
	}
	
	public boolean guard_tile(float x, float y) {
		int tileid = l.getTileId(x, y);
		for (int i = 0; i < guard_tiles; i++) {
			if (tileid == tileid_guard[i])
				return true;
		}
		return false;
	}
	
	public boolean immune_tile(float x, float y) {
		int tileid = l.getTileId(x, y);
		for (int i = 0; i < immune_tiles; i++) {
			if (tileid == tileid_immune[i])
				return true;
		}
		return false;
	}
	
	public boolean illegal_tile(float x, float y) {
		
		if (l.getCars().contains(this)) {
			ArrayList<Vector2> tiles = getTileList(x, y);
			for (Vector2 d: tiles) {
				int tileid = l.getTileId(d.x * l.tilewidth, d.y * l.tileheight);		
				for (int i = 0; i < valid_tiles; i++) {
					if (tileid == tileid_illegal[i]) {
						//System.out.println("illegal expanded tile x: "+ d.x + " y: " + d.y);
						return true;
					}
				}
			}
			return false;
		}
		
		int tileid = l.getTileId(x, y);
		for (int i = 0; i < illegal_tiles; i++) {
			if (tileid == tileid_illegal[i]) {
				return true;
			}
		}
		return false;
	}

	public void RandomMove() {
		boolean willmove = false;
		int mytilex = (int) (this.getX() / l.tilewidth);
		int mytiley = (int) (this.getY() / l.tileheight);
		int direction;
		int count = 0;
		SequenceAction sequence = new SequenceAction();
		
		direction = generator.nextInt(4);
		switch(direction) {
			case LEFT:
				while (getX() > (count + 1) * l.tilewidth && 
						(valid_tiles == 0 || valid_tile(this.getX() - (count + 1) *  l.tilewidth, this.getY())) &&
						(target == null || target.get_immune_tiles() == 0 ||
						l.same_tile(this.getX() - (count + 1) *  l.tilewidth, this.getY(), target.getX(), target.getY()) == false || 
						target.immune_tile(target.getX(), target.getY()) == false )
						) {
					count++;
					willmove = true;
					mytilex--;
				}
				break;
			case RIGHT:
				while (getX() + (count + 1) * l.tilewidth < l.tilewidth * (l.width - 1) &&
						(valid_tiles == 0 || valid_tile(this.getX() + (count + 1) * l.tilewidth, this.getY())) &&
						(target == null || target.get_immune_tiles() == 0 ||
						l.same_tile(this.getX() + (count + 1) *  l.tilewidth, this.getY(), target.getX(), target.getY()) == false || 
						target.immune_tile(target.getX(), target.getY()) == false )
						) {
					count++;
					mytilex++;
					willmove = true;
				}
				break;
			case DOWN:
				while (getY() > (count + 1 ) * l.tileheight && 
						(valid_tiles == 0 || valid_tile(this.getX(), this.getY() - (count + 1 ) * l.tileheight)) &&
						(target == null || target.get_immune_tiles() == 0 ||
						l.same_tile(this.getX(), this.getY() - (count + 1 ) * l.tileheight, target.getX(), target.getY()) == false || 
						target.immune_tile(target.getX(), target.getY()) == false )
						) {
					count++;
					willmove = true;
					mytiley--;
				}
				break;
			case UP:
				while (getY() + (count + 1) * l.tileheight < l.tileheight * (l.width - 1) && 
						(valid_tiles == 0 || valid_tile(this.getX(), this.getY() + (count + 1) * l.tileheight)) &&
						(target == null || target.get_immune_tiles() == 0 ||
						l.same_tile(this.getX(), this.getY() + (count + 1 ) * l.tileheight, target.getX(), target.getY()) == false || 
						target.immune_tile(target.getX(), target.getY()) == false )
						) {
					count++;
					mytiley++;
					willmove = true;
				}
				break;
			default:
				break;
		}
		if (willmove == true) {
			//System.out.println("Random move initiated " + direction);
			//sequence.addAction(moveTo(mytilex * l.tilewidth, mytiley * l.tileheight, generator.nextFloat() * 3f + 0.5f));
			gotoPoint(l, mytilex * l.tilewidth, mytiley * l.tileheight, 0.3f);
		}
	}
	
	public void moveToTileOrTarget() {
		if (emergencyMove == false && useAutoRoute == true && inAutoRoute == false) {
			loopRoutePoints(routeDirection == 0 ? autoRoute : autoRouteReverse);
			inAutoRoute = true;
		}
		
		else if (target != null && guard_tile(target.getX()  + target.getWidth()/2 , target.getY())) {
			//try to move to target, if they are on tile of type tileid
			// e.g. car will find hero pirate, if he is on a street tile!
			//int tilex = (int)(target.getX()/ l.tilewidth);
			int tilex = (int)((target.getX() + target.getWidth()/2)/l.tilewidth);
			int tiley = (int)target.getY()/l.tileheight;
			if (!illegal_tile(tilex * l.tilewidth, tiley * l.tileheight)) {
				//System.out.println("ATTACK " + target.getX() + " " + target.getY() + " tilex: " +tilex + "tiley: " + tiley);
				gotoPoint(l, tilex * l.tilewidth, tiley * l.tileheight, 0.03f);
			}	
		}
		else if (emergencyMove == false && random_move == true){		
			RandomMove();
		}
	}
}