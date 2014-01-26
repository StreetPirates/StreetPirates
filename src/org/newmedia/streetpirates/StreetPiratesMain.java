package org.newmedia.streetpirates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.jglfw.*;// lwjgl.LwjglApplication;

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
	    	new JglfwApplication(new Level(), "Street Pirates", 780, 600, true );    
	    }

}
