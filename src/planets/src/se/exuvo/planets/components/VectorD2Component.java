package se.exuvo.planets.components;

import se.exuvo.planets.utils.VectorD2;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.Bag;

/**
 * An baseclass for Component that holds a 2D-vector.
 */
public abstract class VectorD2Component extends Component {
	
	/**
	 * The Vector2 stored in this Vector2Component.
	 */
	public VectorD2 vec;
	
	/**
	 * Create a new Vector2Component initially holding a (0,0)-vector.
	 */
	public VectorD2Component(){
		vec = new VectorD2();
	}
	
	/**
	 * Creates a new Vector2Component initially holding the given vector.
	 * @param initial the initial value of this Vector2Component.
	 */
	public VectorD2Component(VectorD2 initial){
		vec = initial;
	}
	
	public static VectorD2 mean(ComponentMapper<? extends VectorD2Component> map, Bag<Entity> entities){
		VectorD2 sum = new VectorD2();
		
		for(Entity e : entities){
			sum.add(map.get(e).vec);
		}
		
		return sum.div(entities.size());
	}
	
}
