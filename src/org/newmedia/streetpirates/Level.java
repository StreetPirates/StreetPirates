/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.newmedia.streetpirates;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Gdx.*;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.*;

//import com.badlogic.gdx.maps.tiled.TiledMap;
//import com.badlogic.gdx.maps.tiled.TmxMapLoader;
//import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import java.util.Vector;  
import java.util.ArrayList;
import java.util.Arrays;


import org.newmedia.streetpirates.Character;

public class Level implements Screen { //, InputProcessor {
	private Texture texture_hero[];
	private Texture texture_compass[];
	private Texture texture_starfish[];
	private Texture texture_treasure[];
	private Texture texture_bluecar[], texture_bluecar_back, texture_bluecar_front, texture_bluecar_side;
	private Texture texture_redcar[], texture_redcar_back, texture_redcar_front, texture_redcar_side;
	private Texture texture_greencar[], texture_greencar_back, texture_greencar_front, texture_greencar_side;
	private OrthographicCamera camera;
	private TiledMap tiledMap;
	private TiledMap tiledCity;
	private TmxMapLoader maploader;
	private MapProperties prop;
	private OrthogonalTiledMapRenderer renderer, cityrenderer;
	private	TiledMapTileLayer layer, citylayer;
	private int columns;
	private int rows;
	private int num_starfish = 2, place_idx = 0;
	private PirateGame game;
	public Stage stage;
	private ArrayList<Character> car;
	private ArrayList<Character> badguy;
	private ArrayList<Character> starfish;
	private Character treasure;
	private Skin buttonSkin;
	
	public Character compass;
	public Character hero;
	public Character actor_picked;
	public boolean actor_dropped;
	public ArrayList<Character> route;
	public int tiletypes[][];
	public int cost[][];
	public int car_cost[][];
	
	/* map tileset ids hardcoded 
	public final int pavement_tileid = 1;
	public final int street_tileid = 4;
	public final int wall_tileid = 7;
	public final int pedestrianwalk_tileid = 10;
	public final int tileid_illegal_lowbound = 19;
	public final int types[] = {7, 19, 1, 10, 4};
	*/
	
	/* city tileset ids hardcoded */
	public final int TILE_TYPES = 5;
	public final int TILE_ILLEGAL_ID = 1;
	public final int TILE_PAVEMENT_ID = 2;
	public final int TILE_PEDESTRIANWALK_ID = 3;
	public final int TILE_STREET_ID = 4;
	public final int TILE_UNKNOWN_ID = 5;
	/* from stronger to weakest id type. In case types overlap in tile layers, the stronger type logically applies.
	 * E.g. a tile with a layer of pavement and street, is a pavement logically. This happens on rounded pavement corners.
	 * E.g. 
	 */
	public final int tile_pavement_types[] = {1, 37, 57, 39, 59};
	public final int tile_pedestrianwalk_types[] = {55};
	public final int tile_street_types[] = {11};
	
	public final int types[] = {7, 1, 10, 11};
	
	public int street_tilecost = 1;
	public int safe_tilecost = 1;
	public int wall_tilecost = 1000;
	public int tilewidth, tileheight, width, height;
	public int hero_move = 5;
	public int num_helpers;
	public boolean start_route;
	public boolean adventure_started;
	Texture imgbutton;
	TextureRegion imgbuttonregion;
	Window window;
	
	//@Override
	public Level(PirateGame game) {		
		
		this.game = game;
		//tiledMap = new TmxMapLoader().load("assets/map/map.tmx");
		//tiledMap = new TmxMapLoader().load("assets/streetpirates-level1.tmx");
		tiledMap = new TmxMapLoader().load("assets/streetpirates-level1-withcompass.tmx");
		tiledCity = new TmxMapLoader().load("assets/streetpirates-city1-withcompass.tmx");
		prop = tiledMap.getProperties();
		
		texture_hero = new Texture[4];
		texture_hero[0] = new Texture(Gdx.files.internal("assets/pirate/front_walk1.png"));
		texture_hero[1] = new Texture(Gdx.files.internal("assets/pirate/front_walk2.png"));
		texture_hero[2] = new Texture(Gdx.files.internal("assets/pirate/front_walk3.png"));
		texture_hero[3] = new Texture(Gdx.files.internal("assets/pirate/front_walk4.png"));
		
		texture_treasure = new Texture[1];
		texture_treasure[0] = new Texture(Gdx.files.internal("assets/map/treasure1.png"));
		
		texture_compass = new Texture[1];
		texture_compass[0] = new Texture(Gdx.files.internal("assets/map/compass.png"));
		
		texture_bluecar = new Texture[1];
		texture_bluecar[0] = new Texture(Gdx.files.internal("assets/cars/BlueCar_back.png"));
		//texture_bluecar_front = new Texture(Gdx.files.internal("assets/cars/BlueCar_front.png"));
		//texture_bluecar_side = new Texture(Gdx.files.internal("assets/cars/BlueCar_side.png"));
		
		texture_redcar = new Texture[1];
		texture_redcar[0] = new Texture(Gdx.files.internal("assets/cars/RedCar_back.png"));
		//texture_redcar_front = new Texture(Gdx.files.internal("assets/cars/RedCar_front.png"));
		//texture_redcar_side = new Texture(Gdx.files.internal("assets/cars/RedCar_side.png"));
		
		texture_greencar = new Texture[1];
		texture_greencar[0] = new Texture(Gdx.files.internal("assets/cars/GreenCar_back.png"));
		//texture_greencar_front = new Texture(Gdx.files.internal("assets/cars/GreenCar_front.png"));
		//texture_greencar_side = new Texture(Gdx.files.internal("assets/cars/GreenCar_side.png"));
		//texture_starfish = new Texture(Gdx.files.internal("assets/map/starfish.png"));//map_tiles.png"));
		
		texture_starfish = new Texture[1];
		texture_starfish[0] = new Texture(Gdx.files.internal("assets/map/starfish-alpha.png"));//map_tiles.png")); 
		
		layer = (TiledMapTileLayer)tiledMap.getLayers().get(0); // assuming the layer at index on contains tiles
		citylayer = (TiledMapTileLayer)tiledCity.getLayers().get(1); // assuming the layer at index on contains tiles
		columns = layer.getWidth();
		rows = layer.getHeight();
		tilewidth = prop.get("tilewidth", Integer.class);
		tileheight = prop.get("tileheight", Integer.class);
		width = prop.get("width", Integer.class);
		height = prop.get("height", Integer.class);
		
		renderer = new OrthogonalTiledMapRenderer(tiledMap, 1/(float)tilewidth);
		cityrenderer = new OrthogonalTiledMapRenderer(tiledCity, 1/(float)tilewidth);
		
		for (int i = 0 ; i < layer.getWidth(); i++)
			for (int j = 0 ; j < layer.getHeight(); j++) {
				//if (layer.getCell(i, j).getTile().getId() != citylayer.getCell(i, j).getTile().getId()) {
					//System.out.println("cell(" + i + "," + j + "): " + layer.getCell(i, j).getTile().getId() + " city: " + citylayer.getCell(i, j).getTile().getId());
				//}
				//System.out.println("cell(" + i + "," + j + "): " + layer.getCell(i, j).getTile().getId());
			}

		for (int i = 0 ; i < citylayer.getWidth(); i++)
			for (int j = 0 ; j < citylayer.getHeight(); j++) {
				//System.out.println("width " + citylayer.getWidth() + "height: " + citylayer.getHeight() + " " + "cell(" + i + "," + j + "): " + citylayer.getCell(i, j));//.getTile().getId());
			}
		
		tiletypes = create_types_tilemap(tiledCity); 
		cost = new int[this.width][this.height];
		car_cost = new int[this.width][this.height];
		calculate_cost();	
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, columns, rows);
		renderer.setView(camera);
		cityrenderer.setView(camera);
		
		stage = new Stage();
		stage.setCamera(camera);
		
		hero = new Character(texture_hero, 0, 0, (float)1.0, stage, this);
		treasure = new Character(texture_treasure, 11, 6, (float)2.0, stage, this);
		compass = new Character(texture_compass, (float)13.5, 7, (float)2.5, stage, this);
		
		car = new ArrayList<Character>();
		car.add(new Character(texture_bluecar, 6, 6, (float)1.5, stage, this));
		car.add(new Character(texture_greencar, 3, 6, (float)1.5, stage, this));
		car.add(new Character(texture_redcar, 2, 4, (float)1.5, stage, this));
		
		//starfish = new Character[num_starfishes];
		starfish = new ArrayList<Character>();
		starfish.add(new Character(texture_starfish, 10, 2, (float)1.0, stage, this));
		starfish.add(new Character(texture_starfish, 11, 1, (float)1.0, stage, this));
		starfish.add(new Character(texture_starfish, 12, 0, (float)1.0, stage, this));
		//starfish.add(new Character(texture_starfish, 11, 5, (float)1.0, stage, this));
		
		hero.set_immunetile(TILE_PEDESTRIANWALK_ID);
		hero.set_illegaltile(TILE_ILLEGAL_ID);
		//hero.followCharacter(starfish.get(0));
		
		for(int i = 0; i < starfish.size(); i++) {
			starfish.get(i).set_pickable(true);	
		}

		for(int i = 0; i < car.size(); i++) {
			car.get(i).set_validtile(TILE_STREET_ID);
			car.get(i).set_validtile(TILE_PEDESTRIANWALK_ID);
			car.get(i).set_illegaltile(TILE_PAVEMENT_ID);
			car.get(i).set_illegaltile(TILE_ILLEGAL_ID);
			car.get(i).set_guardtile(TILE_STREET_ID);
			//car.get(i).set_random_move();
			//car.get(i).set_target(hero);	
		}
		
		route = new ArrayList<Character>();
		actor_picked = null;
		actor_dropped = false;
		start_route = false;
		num_helpers = starfish.size();
		/* tiles with id >= tileid will be illegal */
		adventure_started = false;
		
		/*buttonSkin = new Skin(Gdx.files.internal("assets/ui/uiskin.json"));
		imgbutton = new Texture(Gdx.files.internal("assets/ui/uiskin.png"));
		imgbuttonregion = new TextureRegion(imgbutton);
		Button imgButton = new Button(new Image(imgbuttonregion), buttonSkin);
		ImageButtonStyle style = new ImageButtonStyle(buttonSkin.get(ButtonStyle.class));
		window = new Window("Dialog", buttonSkin);
		window.getButtonTable().add(new TextButton("X", buttonSkin)).height(window.getPadTop());
		window.setPosition(100, 100);
		window.add(imgButton);
		window.pack();
		window.setVisible(true);
		stage.addActor(window);
		//TextButton tbf = new TextButton("myButton", buttonSkin.getStyle(TextButtonStyle.class));
		*/
	}
	
	
	public ArrayList<Character> getCars() {
		return car;
	}
	
	public int getTileType(int id) {
		for (int i = 0; i < tile_street_types.length; i++) {
			if (id == this.tile_street_types[i])
				return TILE_STREET_ID;
		}
		for (int i = 0; i < tile_pavement_types.length; i++) {
			if (id == this.tile_pavement_types[i])
				return TILE_PAVEMENT_ID;
		}
		for (int i = 0; i < tile_pedestrianwalk_types.length; i++) {
			if (id == this.tile_pedestrianwalk_types[i])
				return TILE_PEDESTRIANWALK_ID;
		}
		return TILE_ILLEGAL_ID;
	}
	
	public int[][] create_types_tilemap(TiledMap map) {
		//MapProperties prop = tiledMap.getProperties();
		int type[][];
		TiledMapTileLayer layer;
		layer = (TiledMapTileLayer)map.getLayers().get(0); // assuming all layers have same dimension
		type = new int[layer.getWidth()][layer.getHeight()];
		
		for (int i = 0 ; i < layer.getWidth(); i++)
			for (int j = 0 ; j < layer.getHeight(); j++) {
				type[i][j] = TILE_UNKNOWN_ID;
				for(MapLayer l: map.getLayers()) {
				layer = (TiledMapTileLayer) l;
				TiledMapTileLayer.Cell cell = layer.getCell(i, j);
				if (cell != null) { 
					int newid = cell.getTile().getId();
					int newidtype = getTileType(newid);
					int idtype = type[i][j]; //(type[i][j] == -1) ? TILE_UNKNOWN_ID : getTileType(type[i][j]);
					//System.out.println("type(" + i + "," + j + "): " + idtype + " newidtype " + newidtype);
					if (newidtype < idtype) {
						type[i][j] = newidtype;
						//System.out.println("type(" + i + "," + j + "): changed to " + newidtype);
					}
				}
			}
		}
		for (int i = 0 ; i < citylayer.getWidth(); i++)
			for (int j = 0 ; j < citylayer.getHeight(); j++) {
				//System.out.println("type(" + i + "," + j + "): " + type[i][j]);
			}
		
		return type;
	}
	
	public void setup_adventure() {
		for(int i = 0; i < car.size(); i++) {
			car.get(i).set_random_move();
			car.get(i).set_target(hero);	
		}
	}
	
	@Override
	public void render(float delta) {		
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		int layers_id[] = {0};
		int city_layers_id[] = {0, 1};
		if (adventure_started == false)
			renderer.render(layers_id);
		else
			cityrenderer.render(city_layers_id);
		stage.act(Gdx.graphics.getDeltaTime());//delta);
		stage.draw();
		
	}
	
	public class LevelListener extends InputListener {
		Level l;
		public LevelListener(Level level) {
			l = level;
		}
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
           //y = tileheight * height - y;
		   //System.out.println("STAGE touchDown x: " + x + " y: " + y + " stagex:" + event.getStageX() + " stagey:" + event.getStageY());
           if (adventure_started && l.actor_picked == null && l.start_route == false) {
      		   l.hero.gotoPoint(l, x, y);
           }
           if (l.actor_dropped == true) {
        	   l.actor_picked = null;
        	   l.actor_dropped = false;
           }
           if (l.start_route == true) {
        	   l.start_route = false;
           }
           
      	   //hero.followRoute(starfish);
      	   return true;  // must return true for touchUp event to occur
    	}
    	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
    		//System.out.println("STAGE touchUp x: " + x + " y: " + y);
    	}
    
    	public boolean keyTyped(InputEvent event, char character) {
    		//System.out.println("STAGE keyTyped x: " + character);
    		if (adventure_started) {
    		hero.set_moving(true);
    		hero.clearActions();
    		switch(character) {
    			case 'i':
    				//TODO: boundary check on edges of screen
    				if (hero.getY() < (l.height - 1) * l.tileheight && !hero.illegal_tile(hero.getX(), hero.getY() + hero_move))
    					hero.setPosition((float) (hero.getX()), (float)(hero.getY() + hero_move));
    				break;
    			case 'k':
    				if (hero.getY() > hero_move && !hero.illegal_tile(hero.getX(), hero.getY() - hero_move))
    					hero.setPosition((float) (hero.getX()), (float)(hero.getY() - hero_move));
    				break;
    			case 'j':
    				if (hero.getX() > hero_move && !hero.illegal_tile(hero.getX() - hero_move, hero.getY()))
    					hero.setPosition((float) (hero.getX() - hero_move), (float)(hero.getY()));
    				break;
    			case 'l':
    				if (hero.getX() < (l.width - 1) * l.tilewidth && !hero.illegal_tile(hero.getX() + hero_move, hero.getY()))
    					hero.setPosition((float) (hero.getX() + hero_move), (float)(hero.getY()));
    				break;	
    		}
    	   }
    	   //hero.set_moving(false);	    		
    	   return true;
    	}
    	
    	public boolean keyUp(InputEvent event, int keycode) {
    		switch(keycode) {
				case 'i':
				case 'k':
				case 'j':
				case 'l':
					hero.set_moving(false);
					break;	
				default:
					break;
    		}
    		return true;
    	}
    	
    	public boolean mouseMoved(InputEvent event, float x, float y) {
    		//tweak coordinates... We want the moved point to be rougly in the "middle" of of actor, not bottom-left coordinates
    		if (l.actor_picked != null) {
    			l.actor_picked.setX(event.getStageX() - actor_picked.getWidth()/2);
    			l.actor_picked.setY(event.getStageY() - actor_picked.getHeight()/2);
    		}
    		return true;
    	}
	}
	
	@Override
    public void show() {
         // called when this screen is set as the screen with game.setScreen();
		for(int i = 0; i < starfish.size(); i++) {
			starfish.get(i).addClickListener();	
		}
		compass.addClickListener();
		stage.addListener(new LevelListener(this));
		Gdx.input.setInputProcessor(stage);
    }

	@Override
    public void hide() {
         // called when current screen changes from this to a different screen
    }
	
	@Override
	public void pause() {	
		
	}
	
	@Override
	public void resume() {
		
	}
	
	@Override
	public void dispose() {	
		
	}
	
	@Override
	public void resize(int w, int h) {	
		 stage.setViewport(w, h, true);
		 // FIXME: resized game is broken
		 /*tilewidth = tilewidth * w / this.width + w % this.width;
		 tileheight = tileheight * w / this.height + w % this.height;
		 for (Character c: car) {
			 c.resize(w, h);
		 }
		 for (Character c: starfish) {
			 c.resize(w, h);
		 }
		 hero.resize(w, h);*/
	}
	
	public int getTileId(float x, float y) {
		int tilex = (int) (x / tilewidth);
		int tiley = (int) (y / tileheight);
		if (tilex >= this.width || tiley >= this.height)
			return 0;
		return tiletypes[tilex][tiley];//layer.getCell(tilex, tiley).getTile().getId();
	}
	
	public boolean same_tile(float x1, float y1, float x2, float y2) {
		int tile1x = (int) (x1 / tilewidth);
		int tile1y = (int) (y1 / tileheight);
		int tile2x = (int) (x2 / tilewidth);
		int tile2y = (int) (y2 / tileheight);
		if (tile1x == tile2x && tile1y == tile2y)
			return true;
		return false;
	}

	public void calculate_cost() {
		for (int i = 0; i < this.width; i++)
			for (int j = 0; j < this.height; j++) {
				switch(tiletypes[i][j]) { //layer.getCell(i, j).getTile().getId()) {
					case TILE_STREET_ID:
						cost[i][j] = street_tilecost;
						car_cost[i][j] = street_tilecost;
						break;
					case TILE_PAVEMENT_ID:
					case TILE_PEDESTRIANWALK_ID:
						cost[i][j] = safe_tilecost;
						car_cost[i][j] = wall_tilecost;
						break;
					case TILE_ILLEGAL_ID:
					default:	
						cost[i][j] = wall_tilecost;
						car_cost[i][j] = wall_tilecost;
						break;
				}
				
			}
	}
	
	public ArrayList<Vector2> getNeighbors(Vector2 current) {
		ArrayList<Vector2> neighbors = new ArrayList<Vector2>();
		if (current.x > 0)
			neighbors.add(new Vector2(current.x - 1, current.y));
		if (current.y > 0)
			neighbors.add(new Vector2(current.x, current.y - 1));
		if (current.x < this.width - 1)
			neighbors.add(new Vector2(current.x + 1, current.y));
		if (current.y < this.height - 1)
			neighbors.add(new Vector2(current.x, current.y + 1));
		
		// TODO: decide if we use diagonal movement for hero. Cars should not use diagonal moving.
		/* TODO: decide
		if (current.x > 0 && current.y > 0)
			neighbors.add(new Vector2(current.x - 1, current.y - 1));
		if (current.x > 0 && current.y < this.height - 1)
			neighbors.add(new Vector2(current.x - 1, current.y + 1));
		if (current.x < this.width - 1 && current.y < this.height - 1)
			neighbors.add(new Vector2(current.x + 1, current.y + 1));
		if (current.x < this.width - 1 && current.y > 0)
			neighbors.add(new Vector2(current.x + 1, current.y - 1));
		*/	
		return neighbors;
	}
	   
   boolean handleclick(int x, int y) {
	   
	   return true;
   }
}
