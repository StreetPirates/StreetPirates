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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.jglfw.*;// lwjgl.LwjglApplication;
//import com.badlogic.gdx.backends.jglfw.jglfwApplication;// lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.*; //LwjglApplication;

public class StreetPiratesMain {
	// cannot instantiate
		private StreetPiratesMain() {
			;
		}
		
	    /**
	     * @param args
	     */
	    public static void main(String[] args) {
	    	//Input inputProcessor = new Input();
	    	//Gdx.input.setInputProcessor(inputProcessor);
	    	
	    	new JglfwApplication(new PirateGame(), "Street Pirates", 960, 600);//, true );
	    	//new LwjglApplication(new PirateGame());
	    }

}
