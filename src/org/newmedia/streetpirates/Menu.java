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

import java.util.ArrayList;

import org.newmedia.streetpirates.Level.LevelListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
//import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Menu implements Screen { //implements Screen {
	PirateGame game;
	Stage stage;
	Group background;
	Texture menuTexture;
	Texture startMap, makeMap, settings, instructions;
	Texture pirateA[], pirateB[], storyTexture[], instructionTexture[], parrotTexture[];
	Image menuImage, instructionImage, parrot;
	private OrthographicCamera camera;
	
	MenuCharacter heroA, heroB;
	ArrayList<Button> btnlist;
	Button startMapBtn, makeMapBtn, settingsBtn, instructionsBtn;
	ButtonListener startMapListener;
	//StoryListener instructionsListener, storyImageListener;
	StoryListener storyImageListener;
	MessageListener instructionImageListener, instructionsListener;
	InputListener parrotListener;
	ArrayList<MenuCharacter> pirate;
	public int storyIdx, instructionIdx;
	public boolean storyStarts, instructionStarts;
	
	
	/**
	 * @param args
	 */
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub

	}*/
	
	public class MenuCharacter extends Actor {
		SpriteBatch spriteBatch;
		TextureRegion imageregion[], currentFrameRegion;
		boolean chosen, stickychosen;
		
		public MenuCharacter(Texture texture[], float x, float y, double scaling) {
			spriteBatch = new SpriteBatch();
			this.setPosition(x,  y);
			this.setScale((float)scaling, (float)scaling);
			this.setHeight(texture[0].getHeight() * this.getScaleY());
			this.setWidth(texture[0].getWidth() * this.getScaleX());
			this.setVisible(true);
			this.setTouchable(Touchable.enabled);
			this.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
			//System.out.println("ACTOR PICKED touchDown x: " + getWidth() + " y: " + getHeight());
			imageregion = new TextureRegion[2];
			imageregion[0] = new TextureRegion(texture[0]);
			imageregion[1] = new TextureRegion(texture[1]);
			chosen = false;
			stickychosen = false;
		}
		
		@Override
		public void draw(Batch batch, float parentAlpha) {
			super.draw(batch,  parentAlpha);
			
			if (chosen == false)
				currentFrameRegion = imageregion[0];
			else 
				currentFrameRegion = imageregion[1];
			
		    spriteBatch.begin();
	        spriteBatch.draw(currentFrameRegion, getX(), getY(), getWidth(), getHeight());        
	        spriteBatch.end();
	        this.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		}
		
	}
	
	public class MenuCharListener extends InputListener {
		Menu menu;
		MenuCharacter c;
		
		public MenuCharListener(Menu menu, MenuCharacter c) {
			this.menu = menu;
			this.c = c;
		}
		
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			//System.out.println("ACTOR PICKED touchDown x: " + x + " y: " + y);
			//y = menu.menuImage.getHeight() - y;
			if (event.getStageX() >= c.getX() && event.getStageX() < c.getRight() && event.getStageY() >= c.getY() && event.getStageY() < c.getTop())  {
				c.chosen = true;
				c.stickychosen = true;
				for (MenuCharacter other: menu.pirate) {
					if (other != c) {
						other.chosen = false;
	    				other.stickychosen = false;
					}
				}
			}
			
            return true;  // must return true for touchUp event to occur
        }
		
        public boolean mouseMoved(InputEvent event, float x, float y) {
        	boolean other_chosen = false;
        	//System.out.println("ACTOR MOUSE touchDown x: " + x + " y: " + y);
        	for (MenuCharacter other: menu.pirate) {
				if (other != c && other.stickychosen == true) {
					other_chosen = true;
				}
			}
        	if (other_chosen == false && event.getStageX() >= c.getX() && event.getStageX() < c.getRight() && event.getStageY() >= c.getY() && event.getStageY() < c.getTop())  {
				c.chosen = true;
				for (MenuCharacter other: menu.pirate) {
					if (other != c) {
						other.chosen = false;
					}
				}
        	}	
          	return false;
        }
	}
	
	public Button newBtn(Texture image, float x, float y) {
		Button btn = new Button(new TextureRegionDrawable(new TextureRegion(image)));
		btn.setTouchable(Touchable.enabled);
		btn.setPosition(x, y);
		btn.setVisible(true);
		btn.setWidth(image.getWidth());
		btn.setHeight(image.getHeight());
		btnlist.add(btn);
		background.addActor(btn);
		return btn;
	}
	
	public Menu(PirateGame game) {
		this.game = game;
		menuTexture = new Texture(Gdx.files.internal("assets/menu/menu_bg.jpg"));
		menuImage = new Image(menuTexture);
		menuImage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//menuImage.setWidth(Gdx.graphics.getWidth());
		//menuImage.setHeight(Gdx.graphics.getHeight());
		
		pirate = new ArrayList<MenuCharacter>();
		pirateA = new Texture[2];
		pirateB = new Texture[2];
		
		pirateA[0] = new Texture(Gdx.files.internal("assets/menu/PirateAlone.png"));
		pirateA[1] = new Texture(Gdx.files.internal("assets/menu/PirateParrot.png"));
		pirateB[0] = new Texture(Gdx.files.internal("assets/menu/PinkPirateAlone.png"));
		pirateB[1] = new Texture(Gdx.files.internal("assets/menu/PinkPirateParrot.png"));
		
		startMap = new Texture(Gdx.files.internal("assets/menu/DialekseXarth.png"));
		makeMap = new Texture(Gdx.files.internal("assets/menu/FtiakseXarth.png"));
		settings = new Texture(Gdx.files.internal("assets/menu/Prosarmogh.png"));
		instructions = new Texture(Gdx.files.internal("assets/menu/Odhgies_Omada.png"));
		
		parrotTexture = new Texture[1];
		parrotTexture[0] = new Texture(Gdx.files.internal("assets/map/parrot_front.png"));
		
		parrot = new Image(parrotTexture[0]);
		parrot.setVisible(false);
		
		storyTexture = new Texture[7]; 
		for (int i = 0; i < 7; i++)
			storyTexture[i] = new Texture(Gdx.files.internal("assets/storytelling/storytelling" + i + "_downsize.jpg"));
		
		instructionTexture = new Texture[5]; 
		for (int i = 1; i <= 5; i++)
			instructionTexture[i - 1] = new Texture(Gdx.files.internal("assets/map/texts" + i + ".png"));
			//instructionTexture[i - 1] = new Texture(Gdx.files.internal("assets/map/parrot-test" + i + "-large.png"));
		
		stage = new Stage();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 13, 10);
		stage.setCamera(camera);
		
		background = new Group();
		background.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		menuImage.setVisible(true);
		background.addActor(menuImage);
		stage.addActor(background);
		
		heroA = new MenuCharacter(pirateA, 320, 240, 0.8);
		heroB = new MenuCharacter(pirateB, 520, 240, 0.8);
		pirate.add(heroA);
        pirate.add(heroB);

        
		for (MenuCharacter c: pirate) {
			stage.addActor(c);
		}
		
		btnlist = new ArrayList<Button>();
		startMapBtn = newBtn(startMap, 25, 5);
		makeMapBtn = newBtn(makeMap, 325, 5);
		settingsBtn = newBtn(settings, 625, 5);
		instructionsBtn = newBtn(instructions, 700, 450);
		startMapListener = new ButtonListener(this.game);
		//instructionsListener = new StoryListener(this);
		instructionsListener = new MessageListener(this);
		storyIdx = 0;
		storyStarts = false;
		instructionStarts = true;
		instructionIdx = 0;
		
		this.storyImageListener = new StoryListener(this);
		this.instructionImageListener = new MessageListener(this);
		this.menuImage.addListener(this.storyImageListener);
		this.setButtonsVisible(false);
		menuImage.setDrawable(new TextureRegionDrawable(new TextureRegion(storyTexture[this.storyIdx % 7])));
		
		parrotListener = new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				
				if (instructionIdx >= 4) {
					instructionImage.removeListener(instructionImageListener);
					instructionImage.setVisible(false);
					instructionStarts = true;
					instructionIdx = 0;
					heroA.setVisible(true);
					heroB.setVisible(true);
					parrot.removeListener(parrotListener);
					background.removeActor(parrot);
					background.removeActor(instructionImage);
					
				}
				else {
					instructionIdx++;
					instructionImage.setDrawable(new TextureRegionDrawable(new TextureRegion(instructionTexture[instructionIdx % 5])));
				}
				return false;
			}
		};
		
	}
	
	public class ButtonListener extends InputListener {
		PirateGame game;
		
		public ButtonListener(PirateGame game) {
			this.game = game;
		}
		
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			//System.out.println("ACTOR PICKED touchDown x: " + x + " y: " + y);
            game.setScreen(game.getCurrentLevel());
            return true;  // must return true for touchUp event to occur
        }
		
        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
        	//System.out.println("ACTOR touchDown x: " + x + " y: " + y);
        }
        
	}
	
	public class StoryListener extends InputListener {
		Menu menu;
		
		public StoryListener(Menu menu) {
			this.menu = menu;
		}
		
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			//System.out.println("ACTOR PICKED touchDown x: " + x + " y: " + y);
			float stagex = event.getStageX();
			float stagey = event.getStageY();
			if (menu.storyStarts == true) {
				menu.storyImageListener = new StoryListener(this.menu);
				menu.menuImage.addListener(menu.storyImageListener);
				menu.setButtonsVisible(false);
				menu.storyIdx = 0;
				menuImage.setDrawable(new TextureRegionDrawable(new TextureRegion(storyTexture[menu.storyIdx % 7])));
				menu.storyStarts = false;
			}
			else if (menu.storyIdx >= 6 || (stagex >= 845 && stagey >= 568)) {
				menu.menuImage.removeListener(menu.storyImageListener);
				setButtonsVisible(true);
				menuImage.setDrawable(new TextureRegionDrawable(new TextureRegion(menuTexture)));
				menu.storyStarts = true;
				menu.storyIdx = 0;
			}
			else if (stagex >= 790 && stagex <= 860 && stagey >= 65 && stagey <= 130) {
				menu.storyIdx++;
				menuImage.setDrawable(new TextureRegionDrawable(new TextureRegion(storyTexture[menu.storyIdx % 7])));            	
			}
			else if (stagex >= 105 && stagex <= 170 && stagey >= 65 && stagey <= 130) {
				if (menu.storyIdx > 0) menu.storyIdx--;
				menuImage.setDrawable(new TextureRegionDrawable(new TextureRegion(storyTexture[menu.storyIdx % 7])));            	
			}
            return true;
        }
		
        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
        	//System.out.println("ACTOR touchDown x: " + x + " y: " + y);
        }
        
	}
	
	public class MessageListener extends InputListener {
		Menu menu;
		//Image 
		
		public MessageListener(Menu menu) {
			this.menu = menu;
		}
		
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			if (menu.instructionStarts == true) {
				menu.instructionImage = new Image(instructionTexture[0]);
				menu.instructionImage.setBounds(Gdx.graphics.getWidth()/2 - (float)0.5 * instructionTexture[0].getWidth(),
						Gdx.graphics.getHeight()/2 - instructionTexture[0].getHeight(),
						2 * instructionTexture[0].getWidth(),
						2 * instructionTexture[0].getHeight()
						//Gdx.graphics.getHeight()/2,
						//Gdx.graphics.getWidth()/2
						);
				parrot.addListener(parrotListener);
				parrot.setBounds(440, 400, instructionTexture[0].getWidth(), instructionTexture[0].getHeight());
				
				menu.instructionImage.setVisible(true);
				menu.parrot.setVisible(true);
				menu.instructionImageListener = new MessageListener(this.menu);
				menu.instructionImage.addListener(menu.instructionImageListener);
				menu.instructionStarts = false;
				menu.instructionIdx = 0;
				menu.instructionImage.setDrawable(new TextureRegionDrawable(new TextureRegion(instructionTexture[menu.instructionIdx % 5])));
				menu.background.addActor(menu.instructionImage);
				menu.background.addActor(menu.parrot);
				menu.heroA.setVisible(false);
				menu.heroB.setVisible(false);
				
			}
			else if (menu.instructionIdx >= 4) {
				menu.instructionImage.removeListener(menu.instructionImageListener);
				menu.instructionImage.setVisible(false);
				menu.instructionStarts = true;
				menu.instructionIdx = 0;
				menu.heroA.setVisible(true);
				menu.heroB.setVisible(true);
				parrot.removeListener(parrotListener);
				background.removeActor(menu.parrot);
				background.removeActor(menu.instructionImage);
				
			}
			else {
				menu.instructionIdx++;
				menu.instructionImage.setDrawable(new TextureRegionDrawable(new TextureRegion(instructionTexture[menu.instructionIdx % 5])));
			}
			
			//System.out.println("ACTOR PICKED touchDown x: " + x + " y: " + y);
            return true;  // must return true for touchUp event to occur
        }
		
        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
        	//System.out.println("ACTOR touchDown x: " + x + " y: " + y);
        }
        
	}
	
	
	public void setButtonsVisible(boolean visible) {
		for (Button btn: btnlist) {
			btn.setVisible(visible);
		}
		for (MenuCharacter c: pirate) {
			c.setVisible(visible);	
		}
	}
	
	@Override
	public void render(float delta) {		
	    //Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		
		stage.draw();
	}
	
	@Override
    public void show() {
         // called when this screen is set as the screen with game.setScreen();
		
		for (Button btn: btnlist) {
			;
		}
		
		startMapBtn.addListener(startMapListener);
		//instructionsBtn.addListener(instructionsListener);
		instructionsBtn.addListener(instructionImageListener);
		
		for (MenuCharacter c: pirate) {
			c.addListener(new MenuCharListener(this, c));	
		}
		
		//menuImage.addListener(new MenuListener(this));
		
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
	}
	
	public class MenuListener extends InputListener {
		Menu menu;
		
		public MenuListener(Menu menu) {
			this.menu = menu;
		}
		
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			//System.out.println("ACTOR PICKED touchDown x: " + x + " y: " + y);
			//y = menu.menuImage.getHeight() - y;
			for (MenuCharacter c: menu.pirate) {
				if (event.getStageX() >= c.getX() && event.getStageX() < c.getRight() && event.getStageY() >= c.getY() && event.getStageY() < c.getTop())  {
					System.out.println("PIRATE PICKED touchDown x: " + x + " y: " + y);
					c.chosen = true;
					c.stickychosen = true;
					for (MenuCharacter other: menu.pirate) {
						if (other != c) {
							other.chosen = false;
	    					other.stickychosen = false;
						}
					}
				}
			}
			
            return true;  // must return true for touchUp event to occur
        }
		
        public boolean mouseMoved(InputEvent event, float x, float y) {
        	boolean other_chosen = false;
        	//System.out.println("ACTOR MOUSE touchDown x: " + x + " y: " + y);
        	for (MenuCharacter c: menu.pirate) {
				if (c.stickychosen == true) {
					other_chosen = true;
				}
			}
        	
        	if (other_chosen == false) {
        		for (MenuCharacter c: menu.pirate) {
        			if (event.getStageX() >= c.getX() && event.getStageX() < c.getRight() && event.getStageY() >= c.getY() && event.getStageY() < c.getTop())  {
        				c.chosen = true;
            			for (MenuCharacter other: menu.pirate) {
            				if (other != c) {
            					other.chosen = false;
            				}
            			}
        			}
        		}
        	}	
          	return false;
        }
	}
	
	
	
}
