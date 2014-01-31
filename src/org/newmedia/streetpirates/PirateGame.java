package org.newmedia.streetpirates;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx.*;

public class PirateGame extends Game {
	   Level gameScreen;
	   //Level menuScreen;
	   @Override
       public void create() {
               gameScreen = new Level(this);
               //anotherScreen = new AnotherScreen(this);
               setScreen(gameScreen);              
       }
	   
	   
}
