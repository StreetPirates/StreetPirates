package org.newmedia.streetpirates;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx.*;

public class PirateGame extends Game {
	   Level mapLevel;
	   Level cityLevel;
	   //Level menuScreen;
	   @Override
       public void create() {
		       //menuScreen = new MenuScreen(this);
               mapLevel = new Level(this);
               //cityLevel = new CityLevel(this);
               setScreen(mapLevel);              
       }
	   
	   
}
