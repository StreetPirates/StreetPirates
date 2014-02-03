package org.newmedia.streetpirates;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx.*;

public class PirateGame extends Game {
	   Level level;
	   Menu menu;
	   //Level cityLevel;
	   //Level menuScreen;
	   @Override
       public void create() {
		       //menuScreen = new MenuScreen(this);
               level = new Level(this);
               //cityLevel = new CityLevel(this);
               setScreen(level);
               
       }
	   
	   
}
