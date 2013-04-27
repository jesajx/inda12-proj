package se.exuvo.planets.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * An baseclass for Component that holds a 2D-vector.
 */
public class Vector2Component extends Component { // TODO make abstract?
	
	/**
	 * The Vector2 stored in this Vector2Component.
	 */
	public Vector2 vec;
	
	/**
	 * Create a new Vector2Component initially holding a (0,0)-vector.
	 */
	public Vector2Component(){
		vec = new Vector2();
	}
	
	/**
	 * Creates a new Vector2Component initially holding the given vector.
	 * @param initial the initial value of this Vector2Component.
	 */
	public Vector2Component(Vector2 initial){
		vec = initial;
	}
	
}
