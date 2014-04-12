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
//import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.Map;
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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

import java.util.HashMap;
//import com.badlogic.gdx.maps.tiled.TiledMap;
//import com.badlogic.gdx.maps.tiled.TmxMapLoader;
//import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import java.util.Vector;  
import java.util.ArrayList;
import java.util.Arrays;


import org.newmedia.streetpirates.Character;
import org.newmedia.streetpirates.Character.MessageListener;

public class Level implements Screen { //, InputProcessor {
	private Texture texture_hero[], texture_hero_right[], texture_hero_left[], texture_hero_back[];
	private Texture texture_win[], texture_lose[];
	private Texture texture_compass[];
	private Texture texture_starfish[];
	private Texture texture_treasure[];
	private Texture texture_parrot[], texture_parrot_message[][];
	private Texture texture_bluecar_front[], texture_bluecar_back[], texture_bluecar_right[], texture_bluecar_left[];
	private Texture texture_redcar_front[], texture_redcar_back[], texture_redcar_right[], texture_redcar_left[];
	private Texture texture_greencar_front[], texture_greencar_back[], texture_greencar_right[], texture_greencar_left[];
	private Texture texture_bandits_grey[], texture_bandits_brown[], texture_bandits_purple[];
	private Texture texture_pirateflag[];
	private Texture texture_backButton[];
	public Texture texture_footstep[];
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
	public PirateGame game;
	public Stage stage;
	private ArrayList<Character> car;
	private ArrayList<Character> bandit;
	private ArrayList<Character> starfish;
	private Character pirateflag;
	private ArrayList<Character> treasure;
	public Character parrot, parrotMessage;
	private Skin buttonSkin;
	
	public Character compass;
	public Character hero;
	public Character actor_picked;
	public Character backButton;
	public boolean actor_dropped;
	public ArrayList<Character> route;
	public ArrayList<Character> footstep;
	public ArrayList<Character> characters; /*all characters in one list?*/
	public Character winSequence, loseSequence;
	public int tiletypes[][];
	public int cost[][];
	public int car_cost[][];
	public MessageListener parrotMessageListener, backButtonListener;
	public InputListener parrotListener; 
	
	/* entity are actors/object in the foreground
	 * Car
	 * Bandit
	 * Chest 
	 * */
	public final int ENTITY_TYPES = 4;
	//public final String pirateAssetPrefix = "hi";
	public final String banditpurpleAssetPrefix = "assets/city/bandits-purple.png";
	public final String carblueAssetPrefix = "assets/cars/blue_car_back.png";
	public final String treasureChestAssetPrefix = "assets/map/treasure1.png";
	
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
	public Vector2 routeCar[][];
	//public Vector2 routeCarA[] = { new Vector2(100, 100), new Vector2(600, 400) };
	//public Vector2 routeCarB[] = { new Vector2(400, 550), new Vector2(500, 260) };
	//public Vector2 routeCarC[] = { new Vector2(100, 350), new Vector2(400, 350) };
	
	public final int types[] = {7, 1, 10, 11};
	
	public int street_tilecost = 1;
	public int safe_tilecost = 1;
	public int wall_tilecost = 1000;
	public int tilewidth, tileheight, width, height;
	public int hero_move = 5;
	public int num_helpers;
	public boolean start_route;
	public boolean adventure_started, cityInteraction, gameOver;
	Texture imgbutton;
	TextureRegion imgbuttonregion;
	Window window;
	
	//@Override
	public Level(PirateGame game) {		
		
		this.game = game;
		//tiledMap = new TmxMapLoader().load("assets/map/map.tmx");
		//tiledMap = new TmxMapLoader().load("assets/streetpirates-level1.tmx");
		tiledMap = new TmxMapLoader().load("assets/streetpirates-level1-withcompass.tmx");
		//tiledCity = new TmxMapLoader().load("assets/streetpirates-city1-withcompass.tmx");
		tiledCity = new TmxMapLoader().load("assets/streetpirates-city1-withcompass-backup.tmx");
		prop = tiledMap.getProperties();
		
		texture_hero = new Texture[4];
		texture_hero[0] = new Texture(Gdx.files.internal("assets/pirate/front_walk1.png"));
		texture_hero[1] = new Texture(Gdx.files.internal("assets/pirate/front_walk2.png"));
		texture_hero[2] = new Texture(Gdx.files.internal("assets/pirate/front_walk3.png"));
		texture_hero[3] = new Texture(Gdx.files.internal("assets/pirate/front_walk4.png"));
		
		texture_hero_right = new Texture[4];
		texture_hero_right[0] = new Texture(Gdx.files.internal("assets/pirate/side_walk_1.png"));
		texture_hero_right[1] = new Texture(Gdx.files.internal("assets/pirate/side_walk_2.png"));
		texture_hero_right[2] = new Texture(Gdx.files.internal("assets/pirate/side_walk_3.png"));
		texture_hero_right[3] = new Texture(Gdx.files.internal("assets/pirate/side_walk_4.png"));
		
		texture_hero_left = new Texture[4];
		texture_hero_left[0] = new Texture(Gdx.files.internal("assets/pirate/left_walk_1.png"));
		texture_hero_left[1] = new Texture(Gdx.files.internal("assets/pirate/left_walk_2.png"));
		texture_hero_left[2] = new Texture(Gdx.files.internal("assets/pirate/left_walk_3.png"));
		texture_hero_left[3] = new Texture(Gdx.files.internal("assets/pirate/left_walk_4.png"));
		
		texture_hero_back = new Texture[4];
		texture_hero_back[0] = new Texture(Gdx.files.internal("assets/pirate/back_walk1.png"));
		texture_hero_back[1] = new Texture(Gdx.files.internal("assets/pirate/back_walk2.png"));
		texture_hero_back[2] = new Texture(Gdx.files.internal("assets/pirate/back_walk3.png"));
		texture_hero_back[3] = new Texture(Gdx.files.internal("assets/pirate/back_walk4.png"));
		
		texture_treasure = new Texture[1];
		texture_treasure[0] = new Texture(Gdx.files.internal("assets/map/treasure1.png"));
		
		texture_compass = new Texture[1];
		texture_compass[0] = new Texture(Gdx.files.internal("assets/map/compass.png"));
		
		texture_bluecar_back = new Texture[1];
		texture_bluecar_back[0] = new Texture(Gdx.files.internal("assets/cars/blue_car_back.png"));
		texture_bluecar_front = new Texture[1];
		texture_bluecar_front[0] = new Texture(Gdx.files.internal("assets/cars/blue_car_front.png"));
		texture_bluecar_right = new Texture[1];
		texture_bluecar_right[0] = new Texture(Gdx.files.internal("assets/cars/blue_car_side.png"));
		texture_bluecar_left = new Texture[1];
		texture_bluecar_left[0] = new Texture(Gdx.files.internal("assets/cars/blue_car_left.png"));
		
		
		texture_redcar_back = new Texture[1];
		texture_redcar_back[0] = new Texture(Gdx.files.internal("assets/cars/red_car_back.png"));
		texture_redcar_front = new Texture[1];
		texture_redcar_front[0] = new Texture(Gdx.files.internal("assets/cars/red_car_front.png"));
		texture_redcar_right = new Texture[1];
		texture_redcar_right[0] = new Texture(Gdx.files.internal("assets/cars/red_car_side.png"));
		texture_redcar_left = new Texture[1];
		texture_redcar_left[0] = new Texture(Gdx.files.internal("assets/cars/red_car_left.png"));
		
		texture_greencar_back = new Texture[1];
		texture_greencar_back[0] = new Texture(Gdx.files.internal("assets/cars/green_car_back.png"));
		texture_greencar_front = new Texture[1];
		texture_greencar_front[0] = new Texture(Gdx.files.internal("assets/cars/green_car_front.png"));
		texture_greencar_right = new Texture[1];
		texture_greencar_right[0] = new Texture(Gdx.files.internal("assets/cars/green_car_side.png"));
		texture_greencar_left = new Texture[1];
		texture_greencar_left[0] = new Texture(Gdx.files.internal("assets/cars/green_car_left.png"));
		
		//texture_starfish = new Texture(Gdx.files.internal("assets/map/starfish.png"));//map_tiles.png"));
		
		texture_starfish = new Texture[1];
		texture_starfish[0] = new Texture(Gdx.files.internal("assets/map/starfish-alpha.png"));//map_tiles.png")); 
		
		texture_parrot = new Texture[1];
		texture_parrot[0] = new Texture(Gdx.files.internal("assets/map/parrot_front.png"));//map_tiles.png"));
		
		texture_parrot_message = new Texture[5][1];
		for (int i = 1; i <= 5; i++) {
			texture_parrot_message[i - 1][0] = new Texture(Gdx.files.internal("assets/map/texts" + i + ".png"));	
		}
		
		texture_bandits_purple = new Texture[1];
		texture_bandits_purple[0] = new Texture(Gdx.files.internal("assets/city/bandits-purple.png"));//map_tiles.png"));
		texture_bandits_brown = new Texture[1];
		texture_bandits_brown[0] = new Texture(Gdx.files.internal("assets/city/bandits-brown.png"));//map_tiles.png"));
		texture_bandits_grey = new Texture[1];
		texture_bandits_grey[0] = new Texture(Gdx.files.internal("assets/city/bandits-grey.png"));//map_tiles.png"));
		
		texture_pirateflag = new Texture[1];
		texture_pirateflag[0] = new Texture(Gdx.files.internal("assets/map/nekrotiles2.png")); 
		
		texture_footstep = new Texture[1];
		//the blue footprints texture works well: we can place a starfish in the already established route
		//texture_footstep[0] = new Texture(Gdx.files.internal("assets/map/footstepsblue.png"));
		//FIXME:  this texture seems to cause a problem: we can't place a starfish in the already established route
		texture_footstep[0] = new Texture(Gdx.files.internal("assets/map/FOOTPRINTS.png")); //footsteps-3smalltile.png"));
		
		texture_win = new Texture[14];
		for (int i = 1; i <= 14; i ++)
			texture_win[i - 1] = new Texture(Gdx.files.internal("assets/city/xoros" + i + ".jpg"));
		
		texture_lose = new Texture[1];
		texture_lose[0] = new Texture(Gdx.files.internal("assets/city/listes_htta_maurh_diafaneia.png"));
		
		texture_backButton = new Texture[1];
		texture_backButton[0] = new Texture(Gdx.files.internal("assets/map/EXIT.png"));
		
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
		
		characters = new ArrayList<Character>();

		treasure = new ArrayList<Character>();
		compass = new Character(texture_compass, (float)13.5, 7, (float)2.5, stage, this);
		parrotMessage = new Character(texture_parrot_message[0], (float)13, 0, (float)3.0, stage, this);
		parrot = new Character(texture_parrot, (float)13, (float)0.8, (float)3.0, stage, this);
		backButton = new Character(texture_backButton, (float)15.5, (float)9.5, (float)0.5, stage, this);
		
		//for (int i = 1; i < texture_parrot_message.length; i++)
		parrotMessage.addFrameSeries(texture_parrot_message[1]);
		parrotMessage.addFrameSeries(texture_parrot_message[2]);
		parrotMessage.addFrameSeries(texture_parrot_message[3]);
		parrotMessage.addFrameSeries(texture_parrot_message[4]);
		
		parrotMessageListener = parrotMessage.addMessageListener(0, parrotMessage.getHeight(), 0, parrotMessage.getWidth(), Character.MESSAGE_STAY);
		backButtonListener = backButton.addMessageListener(0, backButton.getHeight(), 0, backButton.getWidth(), Character.MESSAGE_GOTO_MENU);
		
		parrotListener = new InputListener() {
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					parrotMessage.currentFrameSeriesIdx = (parrotMessage.currentFrameSeriesIdx + 1) % parrotMessage.numberFrameSeries;;
					return false;
				}
			};
			
		parrot.addListener(parrotListener);
		
		footstep = new ArrayList<Character>();
		
		bandit = new ArrayList<Character>();
		//bandit.add(new Character(texture_bandits_purple, 1, 8, (float)2.5, stage, this));
				
		car = new ArrayList<Character>();
		routeCar = new Vector2[3][2];
		
		starfish = new ArrayList<Character>();
		
		//hero = new ArrayList<Character>();
		
		//hero = new Character(texture_hero, 0, 0, (float)1.5, stage, this);
		
		
		HashMap<String, Texture[] > assetTextureMap = new HashMap<String, Texture[]>();
		HashMap<String, ArrayList<Character> > assetListMap = new HashMap<String, ArrayList<Character>>();
		
		
		//assetFileMap.put("pirate") = ;
		assetTextureMap.put("bandit-purple", texture_bandits_purple);
		assetTextureMap.put("bandit-brown", texture_bandits_brown);
		assetTextureMap.put("bandit-grey", texture_bandits_grey);
		assetTextureMap.put("bandit", texture_bandits_brown);
		assetTextureMap.put("pirateflag", texture_pirateflag);
		assetTextureMap.put("treasure", texture_treasure);
		assetTextureMap.put("starfish", texture_starfish);
		assetTextureMap.put("car-green", texture_greencar_front);
		assetTextureMap.put("car-green-back", texture_greencar_back);
		assetTextureMap.put("car-green-right", texture_greencar_right);
		assetTextureMap.put("car-green-left", texture_greencar_left);
		assetTextureMap.put("car-red", texture_redcar_front);
		assetTextureMap.put("car-red-back", texture_redcar_back);
		assetTextureMap.put("car-red-right", texture_redcar_right);
		assetTextureMap.put("car-red-left", texture_redcar_left);
		assetTextureMap.put("car-blue", texture_bluecar_front);
		assetTextureMap.put("car-blue-back", texture_bluecar_back);
		assetTextureMap.put("car-blue-right", texture_bluecar_right);
		assetTextureMap.put("car-blue-left", texture_bluecar_left);
		assetTextureMap.put("hero", texture_hero);
		assetTextureMap.put("hero-back", texture_hero_back);
		assetTextureMap.put("hero-left", texture_hero_left);
		assetTextureMap.put("hero-right", texture_hero_right);
		
		assetListMap.put("treasure", treasure);
		assetListMap.put("bandit", bandit);
		assetListMap.put("bandit-purple", bandit);
		assetListMap.put("bandit-brown", bandit);
		assetListMap.put("bandit-grey", bandit);
		assetListMap.put("starfish", starfish);
		assetListMap.put("car", car);
		assetListMap.put("car-green", car);
		assetListMap.put("car-red", car);
		assetListMap.put("car-blue", car);
		//assetListMap.put("hero", hero);
	
		File fXmlFile = new File("assets/streetpirates-level1-placement.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		

	    try {
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
	 
		//optional, but recommended
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();
	 
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	 
		NodeList nList = doc.getElementsByTagName("object");
		
		System.out.println("----------------------------");
		
		 
		for (int temp = 0; temp < nList.getLength(); temp++) {
	 
			Node nNode = nList.item(temp);
			
			System.out.println("\nCurrent Element :" + nNode.getNodeName());
	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				String texEl, backEl, rightEl, leftEl, typeEl;
				Character character;
				
				Element eElement = (Element) nNode;
				
				System.out.println("X-coordinate : " + eElement.getAttribute("x"));
				System.out.println("Y-coordinate : " + eElement.getAttribute("y"));
				System.out.println("type : " + eElement.getAttribute("type"));
				
				//System.out.println(" : " + eElement.getAttribute("x"));
				ArrayList<Character> list = assetListMap.get(eElement.getAttribute("type"));
				Texture tex[] = assetTextureMap.get(eElement.getAttribute("type"));
				
				int tiley = Integer.parseInt(eElement.getAttribute("y"));
				int tilex = Integer.parseInt(eElement.getAttribute("x"));
				
				float scaling = Float.parseFloat(eElement.getAttribute("scaling"));
				character = new Character(tex, tilex, tiley, scaling, stage, this); 
				
				
				if (!eElement.getAttribute("extra").equals("")) {
					System.out.println("EXTRA  " + eElement.getAttribute("extra"));
					Texture extra[] = assetTextureMap.get(eElement.getAttribute("extra"));
					character.addFrameSeries(extra);
				}
				if (!eElement.getAttribute("back").equals("")) {
					Texture back[] = assetTextureMap.get(eElement.getAttribute("back"));
					character.addFrameSeries(back);
				}
				if (!eElement.getAttribute("right").equals("")) {
					Texture right[] = assetTextureMap.get(eElement.getAttribute("right"));
					character.addFrameSeries(right);
				}
				if (!eElement.getAttribute("left").equals("")) {
					Texture left[] = assetTextureMap.get(eElement.getAttribute("left"));
					character.addFrameSeries(left);
				}
				
				int nroutepoints = 0;
				NodeList nNodeChildren = nNode.getChildNodes();
				Vector2 route[] = new Vector2[2];
				
				for (int child = 0; child < nNodeChildren.getLength(); child++) {
					Node nChild = nNodeChildren.item(child);
					
					if (nChild.getNodeName().equals("routepoint") && nroutepoints < 2) {
						Element eChild = (Element) nChild;		
						route[nroutepoints] = new Vector2(Integer.parseInt(eChild.getAttribute("x")) * this.tilewidth,
								Integer.parseInt(eChild.getAttribute("y")) * this.tileheight);
						
						System.out.println(eChild.getAttribute("x"));
						System.out.println(eChild.getAttribute("y"));
						nroutepoints++;	
					}	
				}
				//delete route;
				if (nroutepoints > 0) 
					character.addAutoRoute(route);
				
				if (list !=null)
					list.add(character);
				
				if (eElement.getAttribute("type").equals("hero")) {
					hero = character;
					;
				}
			}
		}
		
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }		
		
		
		for(int i = 0; i < starfish.size(); i++) {
			starfish.get(i).set_pickable(true);
			starfish.get(i).set_illegaltile(TILE_STREET_ID);
			starfish.get(i).set_illegaltile(TILE_PEDESTRIANWALK_ID);
			starfish.get(i).set_illegaltile(TILE_ILLEGAL_ID);
		}

		for(int i = 0; i < bandit.size(); i++) {
			bandit.get(i).set_target(hero);
			//bandit.get(i).addFrameSeries(texture_pirateflag);
			if (bandit.get(i).getNumberFrameSeries() > 1)
				bandit.get(i).setFrameSeriesIdx(1);
		}
		
		for(int i = 0; i < car.size(); i++) {
			car.get(i).set_validtile(TILE_STREET_ID);
			car.get(i).set_validtile(TILE_PEDESTRIANWALK_ID);
			car.get(i).set_illegaltile(TILE_PAVEMENT_ID);
			car.get(i).set_illegaltile(TILE_ILLEGAL_ID);
			car.get(0).set_guardtile(TILE_STREET_ID);
			//car.get(i).set_random_move();
			car.get(i).set_target(hero);
			//car.get(i).addAutoRoute(routeCar[i]);
			car.get(i).setVisible(false);
		}
		
		winSequence = new Character(texture_win, 0, 0, (float)13.0, stage, this);
		winSequence.setVisible(false);
		
		loseSequence = new Character(texture_lose, 0, 0, (float)13.0, stage, this);
		loseSequence.setVisible(false);
		
		route = new ArrayList<Character>();
		gameOver = true;
		actor_picked = null;
		actor_dropped = false;
		start_route = false;
		num_helpers = starfish.size();
		/* tiles with id >= tileid will be illegal */
		adventure_started = false;		
		cityInteraction = false;
	    
		hero.set_immunetile(TILE_PEDESTRIANWALK_ID);
		hero.set_illegaltile(TILE_ILLEGAL_ID);
		
		hero.addFrameSeries(texture_hero_back);
		hero.addFrameSeries(texture_hero_right);
		hero.addFrameSeries(texture_hero_left);
	    hero.set_goal(treasure.get(0));
	}
	
	public void resetLevel(boolean gotoMap) {
		actor_picked = null;
		actor_dropped = false;
		start_route = false;
		/* tiles with id >= tileid will be illegal */
		if (gotoMap)
			adventure_started = false;		
		cityInteraction = false;
		route.clear();
		
		//TODO: specify character positions in xml or other format for level parsing
		
		for(int i = 0; i < car.size(); i++) {
			car.get(i).flushActionsFrames();
			car.get(i).set_can_move(false);
			car.get(i).setVisible(false);
			car.get(i).setFrameSeriesIdx(0);
			car.get(i).setStartPosition();
		}
		
		hero.setStartPosition();
		cleanFootTrail();
		
		for(int i = 0; i < starfish.size(); i++) {
			starfish.get(i).setStartPosition();	
		}
		
		for(int i = 0; i < bandit.size(); i++) {
			if (bandit.get(i).getNumberFrameSeries() > 1)
				bandit.get(i).setFrameSeriesIdx(1);
		}
		
	}
	
	public ArrayList<Character> getCars() {
		return car;
	}
	
	public ArrayList<Character> getBandits() {
		return bandit;
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
	
	public void cleanFootTrail() {
		for(int i = 0; i < footstep.size(); i++) {
			footstep.get(i).setVisible(false);
			footstep.get(i).setSize(0,0);
			footstep.get(i).setPosition(0,0);
		}
		footstep.clear();
	}
	
	public void setup_adventure() {
		if (adventure_started == false) {
			for(int i = 0; i < car.size(); i++) {
				car.get(i).set_can_move(true);
				car.get(i).setVisible(true);
				car.get(i).inAutoRoute = false;
				//car.get(i).set_target(hero);	
			}
			for(int i = 0; i < bandit.size(); i++) {
				bandit.get(i).set_target(hero);
				bandit.get(i).setFrameSeriesIdx(0);
			}
			adventure_started = true;
			parrotMessage.setVisible(false);
			parrotMessage.removeListener(parrotMessageListener);
		}
		cleanFootTrail();
	}
	
	@Override
	public void render(float delta) {		
		//Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
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
           if (adventure_started && cityInteraction && l.actor_picked == null && l.start_route == false) {
      		   l.hero.gotoPoint(l, x, y, 0.5f);
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
    		if (adventure_started && cityInteraction) {
    		hero.set_moving(true);
    		hero.clearActions();
    		switch(character) {
    			case 'i':
    				//TODO: boundary check on edges of screen
    				if (hero.getY() < (l.height - 1) * l.tileheight && !hero.illegal_tile(hero.getX() + hero.getWidth()/2, hero.getY() + hero_move)) {
    					hero.setPosition((float) (hero.getX()), (float)(hero.getY() + hero_move));
    					hero.setFrameSeriesIdx(1);
    				}
    				break;
    			case 'k':
    				if (hero.getY() > hero_move && !hero.illegal_tile(hero.getX() + hero.getWidth()/2, hero.getY() - hero_move)) {
    					hero.setPosition((float) (hero.getX()), (float)(hero.getY() - hero_move));
    					hero.setFrameSeriesIdx(0);
    				}
    				break;
    			case 'j':
    				if (hero.getX() > hero_move && !hero.illegal_tile(hero.getX()  + hero.getWidth()/2 - hero_move, hero.getY())) {
    					hero.setPosition((float) (hero.getX() - hero_move), (float)(hero.getY()));
    					hero.setFrameSeriesIdx(3);
    				}
    				break;
    			case 'l':
    				if (hero.getX() < (l.width - 1) * l.tilewidth && !hero.illegal_tile(hero.getX() + hero.getWidth()/2 + hero_move, hero.getY())) {
    					hero.setPosition((float) (hero.getX() + hero_move), (float)(hero.getY()));
    					hero.setFrameSeriesIdx(2);
    				}
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
					System.out.println("STAGE keyUp x: " + keycode);
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
		parrotMessage.setVisible(true);
		parrotMessage.setFrameSeriesIdx(0);
		parrotMessage.addListener(parrotMessageListener);
		
		backButton.setVisible(true);
		backButton.addListener(backButtonListener);
		Gdx.input.setInputProcessor(stage);
		
		winSequence.setVisible(false);
		loseSequence.setVisible(false);
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
	
	public int getTileIdRaw(int tilex, int tiley) {
		if (tilex >= this.width || tiley >= this.height)
			return 0;
		return tiletypes[tilex][tiley];
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
