package org.newmedia.streetpirates;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
//import com.badlogic.gdx.maps.tiled.TiledMap;
//import com.badlogic.gdx.maps.tiled.TmxMapLoader;
//import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import java.util.ArrayList;  
import java.util.List;

public class Level implements ApplicationListener, InputProcessor {
	private Texture texture;	
	private SpriteCache cache;
	private String texture_file; 
	//int tiledMapId;
	private OrthographicCamera camera;
	private TiledMap tiledMap;
	private TiledMap tiledCity;
	private TmxMapLoader maploader;
	OrthogonalTiledMapRenderer renderer;
	TiledMapTileLayer layer;
	int columns;
	int rows;
	int tilewidth, tileheight;

	@Override
	public void create() {		
		
		tiledMap = new TmxMapLoader().load("assets/streetpirates-level1.tmx");
		//texture = gdx.Graphics.newTexture(gdx.Files.internal("map/map (2).jpg"));
		 
		/*cache = new SpriteCache();
		cache.beginCache();
		
		for(int y = 0; y < HEIGHT; y++) {
			for(int x = 0; x < WIDTH; x++) {
				int textureX = tileLayer[y][x] % 8;
				int textureY = tileLayer[y][x] / 8;		
				int tileX = x * TILE_SIZE;
				int tileY = (HEIGHT - 1 - y) * TILE_SIZE;
				cache.add(texture, tileX, tileY, 
							 1 + textureX * (TILE_SIZE + 1), 1 + textureY * (TILE_SIZE+1), 
							 TILE_SIZE, TILE_SIZE, Color.WHITE);
			}
		}
		tiledMapId = cache.endCache();*/
		
		//get tilewidth, height from tiledMap properties? should be both 60? 
		renderer = new OrthogonalTiledMapRenderer(tiledMap, 1/60f);
		layer = (TiledMapTileLayer)tiledMap.getLayers().get(0); // assuming the layer at index on contains tiles
		columns = layer.getWidth();
		rows = layer.getHeight();
		tilewidth = 60;
		tileheight = 60;
		camera = new OrthographicCamera();
		camera.setToOrtho(true, columns, rows);
		renderer.setView(camera);
		
		
		Gdx.input.setInputProcessor(this);
		
	}
	
	@Override
	public void render() {		
		//Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);		
		//camera.update();		
		//cache.setProjectionMatrix(cam.getCombinedMatrix());
		//cache.begin();		
		//cache.draw(tileMap);
		//cache.end();
		
		int layers_id[] = {0};
		renderer.render(layers_id);
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
	public void resize(int x, int y) {	
		
	}
	
	@Override
	public boolean keyDown (int keycode) {
	   return false;
	}

	@Override
	public boolean keyUp (int keycode) {
	   return false;
	}

	@Override
	public boolean keyTyped (char character) {
	   return false;
	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
	   System.out.println("touchDown x: " + x + " y: " + y);
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
	  handleclick(x,y); 
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
