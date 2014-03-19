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
               menu = new Menu(this);
               //cityLevel = new CityLevel(this);
               setScreen(menu);
               //setScreen(level);           
       }
	   
	   public Level getCurrentLevel() {
		   return level;
	   }
	   
	   public Menu getMenu() {
		   return menu;
	   }
}
