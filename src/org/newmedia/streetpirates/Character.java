package org.newmedia.streetpirates;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
	boolean random_move, in_action, moving;
	SpriteBatch spriteBatch; 
	Texture currentFrame;
	TextureRegion imageregion[], currentFrameRegion;
	Animation animation;
	Character target;
	float stateTime;
	
	public boolean pickable;
	public boolean is_picked;
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int DOWN = 2;
	public static final int UP = 3;
	public static final long delta = 1000000;
	public static final int MAX_TILE_TYPES = 3;
	
	//public Character(Texture  texture, int tilex, int tiley, float scalex, float scaley, Stage stage) {
	public Character(Texture texture[], float tilex, float tiley, float scaling, Stage stage, Level l) {
		imageregion = new TextureRegion[texture.length];
		for(int i = 0; i < texture.length; i++) {
			imageregion[i] = new TextureRegion(texture[i]);
		}
		
		this.setX(tilex * l.tilewidth);
		this.setY(tiley * l.tileheight);
		this.setScale((float)l.tilewidth / (float)texture[0].getWidth() * scaling, (float)l.tileheight / (float)texture[0].getHeight() * scaling );
		//this.setHeight(texture[0].getHeight());
		//this.setWidth(texture[0].getWidth());
		this.setHeight(texture[0].getHeight() * this.getScaleY());
		this.setWidth(texture[0].getWidth() * this.getScaleX());
		this.animation = new Animation(0.1f, imageregion);
		spriteBatch = new SpriteBatch();
		
		//this.setSize(width, height);
		
		this.setVisible(true);
		this.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		this.setTouchable(Touchable.enabled);
		
		System.out.println("Character:! width = " + this.getWidth() + "height = " + this.getHeight() + " originx:  " + this.getX() + " originy: " + this.getY() );
		
		stage.addActor(this);
		tileid_valid = new int[MAX_TILE_TYPES];
		tileid_guard = new int[MAX_TILE_TYPES];
		tileid_illegal = new int[MAX_TILE_TYPES];
		tileid_immune = new int[MAX_TILE_TYPES];
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
		this.moving = false;
		this.in_action = false;
		this.target = null;
	}
	
	public void set_moving(boolean set) {
		moving = set;
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
	
	public void set_target(Character target) {
		this.target = target;
	}
	
	public Character get_target() {
		return target;
	}
	
	public class CharacterListener extends InputListener {
		Character character;
		
		public CharacterListener(Character c) {
			character = c;
		}
		
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            System.out.println("ACTOR touchDown x: " + x + " y: " + y + " stagex:" + event.getStageX() + " stagey:" + event.getStageY() + " actorx:" + getX() + " actory:" + getY());
            if (character.pickable == true) {
            	if (l.actor_picked == null) {
            		
            	    System.out.println("ACTOR PICKED touchDown x: " + x + " y: " + y);
            		l.actor_picked = character;
            	}
            	else {
            		System.out.println("ACTOR DROPPED touchDown x: " + x + " y: " + y);
            		l.actor_dropped = true;
            		l.route.add(character);
            		//l.route.add(Vector2(character.getX(), character.getY());
            		//.num_helpers++;
            	}
            }
            else if (character == l.compass) {
            	//l.setup_city();
            	l.start_route = true;
            	l.hero.followRoute(l.route);
            	l.city_enabled = true;
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
	
	/* fulldim of 1.0 means the overlap will trigger when the full bounding boxes start to collide
	 * fulldim of 0.5 means the overlap will trigger when the boxes are merged into each other by roughly half
	 * etc. 
	 */
	public static boolean overlapRectangles (Actor r1, Actor r2, float fulldim) {
        if (r1.getX() < r2.getX() + r2.getWidth() * fulldim && r1.getX() + r1.getWidth() * fulldim > r2.getX() &&
        		r1.getY() < r2.getY() + r2.getHeight() * fulldim && r1.getY() + r1.getHeight() * fulldim > r2.getY())
            return true;
        else
            return false;
        //return x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y;
    }
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch,  parentAlpha);
		
		stateTime += Gdx.graphics.getDeltaTime();
		if (moving == true)
			currentFrameRegion = animation.getKeyFrame(stateTime, true);
		else 
			currentFrameRegion = imageregion[0];
		
	    spriteBatch.begin();
        spriteBatch.draw(currentFrameRegion, getX(), getY(), getWidth(), getHeight());        
        spriteBatch.end();

		this.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		//for (Actor a: this.getStage().getActors()) {
		for (Character a: l.getCars()) {
			//Character c = (Character)a;
			if (a!= this && overlapRectangles (a, this, (float)0.5)) {
			   //if (a.getActions(). != 0)
					//  a.removeAction(a.getActions().first());
			   //if (this.getActions() != null)
					//this.removeAction(a.getActions().first());
			   /*TODO: moving boolean flag is reset in last action, so there is chance a character stays in in_Action/moving limbo (i.e. true flags) forever.
			    * so find a better way of removing a specific action. or resetting the flag at draw function or elsewhere.
			    */
			    
				a.clearActions();
			    this.clearActions();
			    
		        a.set_moving(false);
		        a.set_in_action(false);
		        this.set_moving(false);
		        this.set_in_action(false);
			   //System.out.println("Collision! A.x = " + this.getX() + "A.y = " + this.getY() + "B.x = " + a.getX() + "B.y = " + a.getY() + " A.width = " + this.getWidth() + "A.height = " + this.getHeight() + "B.width = " + a.getWidth() + "B.height = " + a.getHeight());
			    
			    // if a car or bad guy, we should pop a message, reset hero to starting position and retry map
			    // there's a problem here, only if actor has a target, e.g. if hero hits a starfish, it 's ok :)
			    if ((this == l.hero ||  a == l.hero) && (this.target != null || a.get_target() != null)) {
			    	l.hero.setPosition(0,0);
			    }
		   }	
			
		}
		
		/* if target character has moved to an immune tile, cancel pending actions. 
		 * We don't want a car to overrun a hero on a pedestrian walk because the random move
		 * was planned before the hero moved there.
		 * TODO: Ideally we should only stop actions that go the hero's location... how to do that?
		 */
		if (target != null && target.immune_tile(target.getX(), target.getY()) &&
				//(java.lang.Math.abs(target.getX() - this.getX()) < l.tilewidth * 3) &&
				//(java.lang.Math.abs(target.getY() - this.getY()) < l.tileheight * 3)
				overlapRectangles (target, this, (float)2.0)
				) {
			System.out.println("AVOIDED PIRATE PEDESTRIAN! WHEYWEEEEE" + getX() + " " + getY());
			this.clearActions();
		}
		
		//List<Action> listactions = this.getActions().asList();
		if (this.random_move /*&& this.getActions().size() == 0*/ ) {
		long newclock = System.nanoTime();//currentTimeMillis();
		if (newclock - clock > delta && in_action == false || (newclock - clock_lastmoved > 5000 * delta)) {
			in_action = false;
			moving = false;
			clock = newclock;
			clock_lastmoved = newclock;
			//System.out.println("will schedule a random move?" + clock + " " + newclock);
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
	
	public void followRoute(ArrayList<Character> route) {
		int mytilex = (int) (this.getX() / l.tilewidth);
		int mytiley = (int) (this.getY() / l.tileheight);
		int tilex, tiley;
		float destx = this.getX(), desty = this.getY();
		
		Stack<Vector2> path;
		
		this.clearActions();
		SequenceAction sequence = new SequenceAction();
		this.moving = true;
		this.in_action = true;
		
		for(Character dest: route) {
			//tweak coordinates... We want the rout eto pass through middle (sort of) of actor, not bottom-left coordinates
			destx = dest.getX() + dest.getWidth()/4;
			desty = dest.getY() + dest.getHeight()/4;
			tilex = (int) (destx / l.tilewidth);
			tiley = (int) (desty / l.tileheight);
			// the route can be ambiguous. 
			// We could try to find either the safest or the least safe path.
			// We don't need to find the optimal/safest route from a pavement. This is the player's part :)	
			path = getPath(mytilex, mytiley, tilex, tiley);
		
			while (path.empty() == false) {
				Vector2 next = path.pop();	
				sequence.addAction(moveTo(next.x * l.tilewidth, next.y * l.tileheight, 0.5f));
				//System.out.println("PATH x: " + next.x + " y: " + next.y);
			}
			//TODO: maybe we just need to go to tile, not exact position for route? so comment next line...
			//sequence.addAction(moveTo(dest.getX(), dest.getY(), 0.5f));
			//System.out.println("LAST PATH x: " + x + " y: " + y);
			mytilex = tilex;
			mytiley = tiley;
		}
		
		sequence.addAction(moveTo(destx, desty, 0.5f));
		sequence.addAction(run(new java.lang.Runnable() {
		    public void run () {
		        System.out.println("Action complete!");
		        moving = false;
		        in_action = false;
		    }
		}));
		this.addAction(sequence);
		//clear the route!
		route.clear();
	}

	//TODO: Handle collision with hero!!!! sometime he is hit even on pedwalk?!
	//TODO: Menu + buttons + parrot + compass
	//TODO: route needs to be modified when picking up a starfish again
	//TODO: Add bad guys
	//TODO: Add different randomness on moving actors
	//TODO: Fix scaling and resize
	//TODO: Accurate point clicking?! done
	//TODO: Fix bounds, don't let actors leave screen! can cause a crash
	//TODO: Review all clearActions() calls to actors. Use Actor.clearActions() to clear all actions in actor, e.g. if collision happens?!
	//TODO: Animations, add side animations depending on direction of movement
	//TODO: if hit by a car/bad guy, reset hero to beginning
	//TODO: Intro storytelling
	
	/* A* pathfinding on the fully connected tiledmap grid. Uses tile costs from Level class */
	public Stack<Vector2> getPath(int startx, int starty, int x, int y)
	{
	    //PriorityQueue<Vector2> openList = new PriorityQueue<Vector2>(10, new SearchNodeComparator());
		ArrayList<Vector2> openList = new ArrayList<Vector2>();
	    ArrayList<Vector2> closedList = new ArrayList<Vector2>();
	    Stack<Vector2> path = new Stack<Vector2>();
	    
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
	                	System.out.println("ILLEGAL TILE IN PATHFINDING: " + nodex +  " " + nodey);
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
	    System.out.println("LAST PATH BUILD STACK");
	    while (backtrack == true) {
	    	int currentx = newx;
	    	int currenty = newy;
	    	System.out.println("STACK TILE IN PATHFINDING: " + currentx +  " " + currenty);
	    	path.push(parents[currentx][currenty]);
	    	newx = (int)parents[currentx][currenty].x;
	    	newy = (int)parents[currentx][currenty].y;
	    	if (newx == startx && newy == starty)
	    		break;
	    }
	    return path;
	}
	
	
	public void gotoPoint(Level l, float x, float y) {//, boolean hard) { //, int tileid) {
		int tilex = (int) (x / l.tilewidth);
		int tiley = (int) (y / l.tileheight);
		int mytilex = (int) (this.getX() / l.tilewidth);
		int mytiley = (int) (this.getY() / l.tileheight);
		Stack<Vector2> path;
		
		//tweak coordinates for hero only... We want the chosen point to be rougly in the "middle" of of actor, not bottom-left coordinates
		if (this == l.hero) { 
			if (x >= this.getWidth() / 2) {
				x -= this.getWidth()/2;
			}
			if (y >= this.getHeight() / 4) {
				y -= this.getHeight()/4;
			}
		}
		
		this.clearActions();
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
				
				sequence.addAction(moveTo(next.x * l.tilewidth, next.y * l.tileheight, 0.5f));
				//System.out.println("PATH x: " + next.x + " y: " + next.y);
			}
			sequence.addAction(moveTo(x, y, 0.5f));
			//System.out.println("LAST PATH x: " + x + " y: " + y);		
		}
		
		sequence.addAction(run(new java.lang.Runnable() {
		    public void run () {
		        System.out.println("Action complete!");
		        moving = false;
		        in_action = false;
		    }
		}));
		this.addAction(sequence);
	}
	
	
	public void followCharacter(Character next) {
		//this.addAction(addmoveToAction(next.getX(), next.getY(), 3f));
		MoveToAction moveAction = new MoveToAction();
		moveAction.setPosition(next.getX(), next.getY());
		//moveAction.setPosition(tilex * tilewidth, tiley * tileheight);
		moveAction.setDuration(3f);
		this.addAction(moveAction);
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
		int tileid = l.getTileId(x, y);
		for (int i = 0; i < illegal_tiles; i++) {
			if (tileid == tileid_illegal[i]) { // && this == l.hero) {
					System.out.println("ILLEGAL PATH x: " + x + " y: " + y);
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
		
		//System.out.println("Random move initiated? " + direction + " " + willmove);
		if (willmove == true) {
			System.out.println("Random move initiated " + direction);
			//sequence.addAction(moveTo(mytilex * l.tilewidth, mytiley * l.tileheight, generator.nextFloat() * 3f + 0.5f));
			gotoPoint(l, mytilex * l.tilewidth, mytiley * l.tileheight);
		}
	}
	
	public void moveToTileOrTarget() {
		if (target != null && guard_tile(target.getX(), target.getY())) {
			//try to move to target, if they are on tile of type tileid
			// e.g. car will find hero pirate, if he is on a street tile!
			gotoPoint(l, target.getX(), target.getY());
		}
		else {		
			RandomMove();
		}
	}


}
