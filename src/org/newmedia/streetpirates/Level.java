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
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.*;

//import com.badlogic.gdx.maps.tiled.TiledMap;
//import com.badlogic.gdx.maps.tiled.TmxMapLoader;
//import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import java.util.Vector;  
import java.util.ArrayList;
import java.util.Arrays;


import org.newmedia.streetpirates.Character;

public class Level implements ApplicationListener, InputProcessor {
	private Texture texture_hero;
	private Texture texture_starfish;
	private Texture texture_bluecar_back, texture_bluecar_front, texture_bluecar_side;
	private Texture texture_redcar_back, texture_redcar_front, texture_redcar_side;
	private Texture texture_greencar_back, texture_greencar_front, texture_greencar_side;
	//private SpriteCache cache;
	//private String texture_file; 
	//int tiledMapId;
	private OrthographicCamera camera;
	private TiledMap tiledMap;
	private TiledMap tiledCity;
	private TmxMapLoader maploader;
	private MapProperties prop;
	private OrthogonalTiledMapRenderer renderer;
	private	TiledMapTileLayer layer;
	private int columns;
	private int rows;
	private int num_starfish = 2, place_idx = 0;
	private Stage stage;
	//private Character[] car;
	private ArrayList<Character> car;
	private ArrayList<Character> badguy;
	private ArrayList<Character> starfish;
	private Character hero;
	private Screen screen;
	
	public int legal_car_tileid[] = {4, 10};
	public int pavement_tileid = 7;
	public int street_tileid = 4;
	public int wall_tileid = 1;
	public int pedestrianwalk_tileid = 10;
	public int tilewidth, tileheight, width, height;
	public int hero_move = 5;
	
	@Override
	public void create() {		
		
		//tiledMap = new TmxMapLoader().load("assets/map/map.tmx");
		tiledMap = new TmxMapLoader().load("assets/streetpirates-level1.tmx");
		//tiledCity = new TmxMapLoader().load("assets/city/City_oct28.tmx");
		prop = tiledMap.getProperties();
		
		texture_hero = new Texture(Gdx.files.internal("assets/pirate/front_walk1.png"));
		texture_bluecar_back = new Texture(Gdx.files.internal("assets/cars/BlueCar_back.png"));
		texture_bluecar_front = new Texture(Gdx.files.internal("assets/cars/BlueCar_front.png"));
		texture_bluecar_side = new Texture(Gdx.files.internal("assets/cars/BlueCar_side.png"));
		texture_redcar_back = new Texture(Gdx.files.internal("assets/cars/RedCar_back.png"));
		texture_redcar_front = new Texture(Gdx.files.internal("assets/cars/RedCar_front.png"));
		texture_redcar_side = new Texture(Gdx.files.internal("assets/cars/RedCar_side.png"));
		texture_greencar_back = new Texture(Gdx.files.internal("assets/cars/GreenCar_back.png"));
		texture_greencar_front = new Texture(Gdx.files.internal("assets/cars/GreenCar_front.png"));
		texture_greencar_side = new Texture(Gdx.files.internal("assets/cars/GreenCar_side.png"));
		texture_starfish = new Texture(Gdx.files.internal("assets/map/starfish.png"));//map_tiles.png"));
		
		//get tilewidth, height from tiledMap properties? should be both 60? 
		renderer = new OrthogonalTiledMapRenderer(tiledMap, 1/60f);
		layer = (TiledMapTileLayer)tiledMap.getLayers().get(0); // assuming the layer at index on contains tiles
		columns = layer.getWidth();
		rows = layer.getHeight();
		
		tilewidth = prop.get("tilewidth", Integer.class);
		tileheight = prop.get("tileheight", Integer.class);
		width = prop.get("width", Integer.class);
		height = prop.get("height", Integer.class);
		
		for (int i = 0 ; i < layer.getWidth(); i++)
			for (int j = 0 ; j < layer.getHeight(); j++)
				System.out.println("cell(" + i + "," + j + "): " + layer.getCell(i, j).getTile().getId());
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, columns, rows);
		renderer.setView(camera);
		
	    Gdx.input.setInputProcessor(this);
		//Gdx.input.setInputProcessor(stage);
		
		stage = new Stage();
		stage.setCamera(camera);
		
		hero = new Character(texture_hero, 1, 1, tilewidth * 2, tileheight * 2, stage, this);
		//hero.addmoveToAction(5, 7, tilewidth, tileheight, 10f);
		
		car = new ArrayList<Character>();
		car.add(new Character(texture_bluecar_front, 6, 6, tilewidth, tileheight, stage, this));
		car.add(new Character(texture_greencar_front, 3, 6, tilewidth, tileheight, stage, this));
		car.add(new Character(texture_redcar_front, 2, 4, tilewidth, tileheight, stage, this));
		
		//starfish = new Character[num_starfishes];
		starfish = new ArrayList<Character>();
		starfish.add(new Character(texture_starfish, 1, 8, tilewidth, tileheight, stage, this));
		starfish.add(new Character(texture_starfish, 5, 8, tilewidth, tileheight, stage, this));
		//starfish.add(new Character(texture_starfish, 11, 4, tilewidth, tileheight, stage));
		//starfish.add(new Character(texture_starfish, 11, 5, tilewidth, tileheight, stage));
		
		//hero.followCharacter(starfish.get(0));
		//starfish.get(0).addClickListener();
		car.get(0).set_validtile(street_tileid);
		car.get(0).RandomMove();
		
		car.get(1).set_validtile(street_tileid);
		car.get(1).RandomMove();
		
		car.get(2).set_validtile(street_tileid);
		car.get(2).RandomMove();
	}
	
	
	@Override
	public void render() {		
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);		
		//camera.update();		
		
		int layers_id[] = {0};
		renderer.render(layers_id);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
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
	public void resize(int width, int height) {	
		 stage.setViewport(width, height, true);
	}
	
	@Override
	public boolean keyDown (int keycode) {
	   //
	   return false;
	}

	@Override
	public boolean keyUp (int keycode) {
	   return false;
	}

	public boolean is_tileid(float x, float y, int tileid) {
		int tilex = (int) (x / tilewidth);
		int tiley = (int) (y / tileheight);
		if (tilex >= this.width || tiley >= this.height)
			return false;
		if (layer.getCell(tilex, tiley).getTile().getId() == tileid)
			return true;
		else 
			return false;
	}
	
	@Override
	public boolean keyTyped (char character) {
		switch(character) {
			case 'i':
				//TODO: boundary check on edges of screen
				if (!is_tileid(hero.getX(), hero.getY() + hero_move, wall_tileid))
					hero.setPosition((float) (hero.getX()), (float)(hero.getY() + hero_move));
				break;
			case 'k':
				if (!is_tileid(hero.getX(), hero.getY() - hero_move, wall_tileid))
					hero.setPosition((float) (hero.getX()), (float)(hero.getY() - hero_move));
				break;
			case 'j':
				if (!is_tileid(hero.getX() - hero_move, hero.getY(), wall_tileid))
					hero.setPosition((float) (hero.getX() - hero_move), (float)(hero.getY()));
				break;
			case 'l':
				if (!is_tileid(hero.getX() + hero_move, hero.getY(), wall_tileid))
					hero.setPosition((float) (hero.getX() + hero_move), (float)(hero.getY()));
				break;	
				
		}
	   return false;
	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
	   System.out.println("touchDown x: " + x + " y: " + y);
	   if (place_idx < num_starfish) {
		   starfish.get(place_idx).setPosition(x, tileheight * height - y);   
		   place_idx++;
	   }
	   else hero.gotoPoint(this, x, tileheight * height - y, false, 0);//hero.followRoute(starfish);
	   return false;
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) { 
	   return false;
	}

   @Override
   public boolean touchDragged (int x, int y, int pointer) {
      return false;
   }

   @Override
   public boolean mouseMoved (int x, int y) {
	  //System.out.println("mouseMoved hello from console");
	  //handleclick(x,y); 
      return false;
   }

   @Override
   public boolean scrolled (int amount) {
      return false;
   }
	   
   boolean handleclick(int x, int y) {
	   
	   return true;
   }
}
