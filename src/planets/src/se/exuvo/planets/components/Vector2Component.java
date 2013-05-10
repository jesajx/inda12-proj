package se.exuvo.planets.components;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.badlogic.gdx.math.Vector2;

/**
 * An baseclass for Component that holds a 2D-vector.
 */
public abstract class Vector2Component extends Component {
	
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
	
	public static Vector2 mean(ComponentMapper<? extends Vector2Component> map, Bag<Entity> entities){
		Vector2 sum = new Vector2();
		
		for(Entity e : entities){
			sum.add(map.get(e).vec);
		}
		
		return sum.div(entities.size());
	}
	
}
