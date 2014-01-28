package org.newmedia.streetpirates;

import java.util.*;

import com.badlogic.gdx.math.Vector2;
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
	//Date date;
	boolean random_move;
	
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int DOWN = 2;
	public static final int UP = 3;
	public static final long delta = 5000;
	
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
		//date = new Date();
		generator = new Random(System.currentTimeMillis());
		clock = System.currentTimeMillis();
		this.l = l;
		this.random_move = false;
	}
	
	public void set_validtile(int tileid) {
		this.validtile_id = tileid;
	}
	
	public void set_random_move() {
		this.random_move = true;
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
		
		//List<Action> listactions = this.getActions().asList();
		if (this.random_move /*&& this.getActions().size() == 0*/ ) {
		long newclock = System.currentTimeMillis();
		if (newclock - clock > delta) {
			clock = newclock;
			//System.out.println("will schedule a random move?" + clock + " " + newclock);
			//RandomMove();
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
	
	public Stack<Vector2> getPath(int startx, int starty, int x, int y)
	{
	    //PriorityQueue<Vector2> openList = new PriorityQueue<Vector2>(10, new SearchNodeComparator());
		ArrayList<Vector2> openList = new ArrayList<Vector2>();
	    ArrayList<Vector2> closedList = new ArrayList<Vector2>();
	    //ArrayList<Vector2> path = new ArrayList<Vector2>();
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
	    	int currentgoal = 1000000;
	    	Vector2 current;
	    	current = openList.get(0);
	    	for (Vector2 d: openList) {
	    		if (costpathgoal[(int)d.x][(int)d.y] < currentgoal)
	    			current = d;
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
	                    if (costpath[nodex][nodey] <= distanceTraveled)
	                    {
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
	    while (backtrack == true) {
	    	int currentx = newx;
	    	int currenty = newy;
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
		if ((mytilex == tilex) || (mytiley == tiley)) {
			this.addAction(addmoveToAction(x, y, 3f));	
		}
		else {
			
			SequenceAction sequence = new SequenceAction();
			// the route can be ambiguous. 
			// We could try to find either the safest or the least safe path.
			// We don't need to find the optimal/safest route from a pavement. This is the player's part :)
			
			path = getPath(mytilex, mytiley, tilex, tiley);
			
			/*if (tilex < mytilex)
				mytilex--;
			else if (tilex > mytilex) 
				mytilex++;
			if (tiley < mytiley)
				mytiley--;
			else if (tiley > mytiley) 
				mytiley++;*/
			
			while (path.empty() == false) {
				Vector2 next = path.pop();
				sequence.addAction(addmoveToAction(next.x * l.tilewidth, next.y * l.tileheight, 3f));
				System.out.println("PATH x: " + next.x + " y: " + next.y);
			}
			
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
			sequence.addAction(addmoveToAction(mytilex * l.tilewidth, mytiley * l.tileheight, generator.nextFloat() * 3f + 0.5f));
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
