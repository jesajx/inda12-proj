package se.exuvo.planets.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class Vector2Component extends Component {
	public Vector2 vec;
	
	public Vector2Component(Vector2 initial){
		vec = initial;
	}
	
	public Vector2Component(){
		vec = new Vector2();
	}
	
}
