package se.exuvo.planets.utils;

import se.exuvo.planets.components.Acceleration;
import se.exuvo.planets.components.Mass;
import se.exuvo.planets.components.Position;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.FastMath;
import com.badlogic.gdx.math.Vector2;

/**
 * Tree data-structure used to speed up the GravitationSystem.
 * A QuadTree represents an square containing entities.
 * Each QuadTree keeps track of the total mass and the center of mass
 * of all its entities.
 * 
 * Each QuadTree object is in one of 3 states: has an entity, has 4 subtrees or has neither.
 */
public class QuadTree {

	// TODO should this class have direct access to entity-mappers (statically)?
	// or pass as needed?

	// We could store these in a com.badlogic.gdx.math.Rectangle, but it would store
	// 'side' twofold: width and height. Since we don't need that many methods from the class it
	// was just as easy to implement what was needed directly instead.
	Vector2 pos; // bottom left
	float side; // length of sides in Cube

	float mass; // TODO use generics so that it can be used by the
				// collisionSystem too?
	Vector2 massCenter;

	QuadTree bl, br, tl, tr; // {bottom,top}{left,right}
	Entity entity; // TODO necessary to store entity?

	/**
	 * Creates a new QuadTree spanning the given cube.
	 * 
	 * @param pos
	 *            bottomLeft corner of cube
	 * @param side
	 *            length of each side in cube
	 */
	public QuadTree(Vector2 pos, float side) {
		this.pos = pos;
		this.side = side;
		massCenter = pos.cpy();
	}

	
	
	
	/**
	 * Inserts the given entity in this QuadTree.
	 * The tree is rearranged (depth added) and
	 * updated (mass and massCenter) as necessary.
	 * @param entity Entity to add.
	 * @param mm mapper to get mass of entities.
	 * @param pm mapper to get position of entities.
	 * @return whether the entity was successfully inserted.
	 * False iff the region represented by this QuadTree didn't contain the position of the entity. 
	 */
	public boolean add(Entity entity, ComponentMapper<Mass> mm, ComponentMapper<Position> pm) {
		if (!contains(pm.get(entity).vec)) {
			return false;
		}
		
		if (entity == null) {
			this.entity = entity;
			this.mass = mm.get(entity).mass;
			this.massCenter = pm.get(entity).vec.cpy();
			return true;
		} else {
			if (bl == null) { // no subtrees
				addSubtrees();
			}
			subAdd(entity, mm, pm);
			subAdd(this.entity, mm, pm);
			
			float m = mm.get(entity).mass;
			Vector2 p = pm.get(entity).vec;
			
			massCenter.mul(mass);
			massCenter.add(p.cpy().mul(m));
			mass += m;
			massCenter.mul(1/mass);
			
			return true;
		}
	}
	
	
	public void updateAcceleration(Entity e, float theta, float G, ComponentMapper<Mass> mm, ComponentMapper<Position> pm, ComponentMapper<Acceleration> am) {
		if (entity == null && bl == null) { // empty tree
			return;
		}
		
		Vector2 p = pm.get(e).vec;
		Vector2 diff = this.massCenter.cpy().sub(p);
		float d2 = diff.len2();
		
		
		// s/d < theta
		if (this.entity != null || side*side < theta*theta*d2) { // TODO weight with mass.
			// F = G*m*M/d^2
			// F = m*a
			// a = G*M/d^2
			float M = mm.get(this.entity).mass;
			float a = G*M/d2;
			if (!Float.isNaN(a)) {
                diff.mul((float) (a * FastMath.inverseSqrt(d2))); // normalize and mul(a)
				am.get(e).vec.add(diff);
			}
		} else {
			bl.updateAcceleration(e, theta, G, mm, pm, am);
			br.updateAcceleration(e, theta, G, mm, pm, am);
			tl.updateAcceleration(e, theta, G, mm, pm, am);
			tr.updateAcceleration(e, theta, G, mm, pm, am);
		}
	}

	public void update(ComponentMapper<Mass> mm, ComponentMapper<Position> pm) {
		// TODO
	}

	public void updateMassCenter(ComponentMapper<Mass> mm, ComponentMapper<Position> pm) {
		// TODO
		if (bl == null) {
			if (entity != null) {
				this.mass = mm.get(entity).mass;
				this.massCenter = pm.get(entity).vec.cpy();
			}
			// otherwise default values are already set.
		} else {
			bl.updateMassCenter(mm, pm);
			br.updateMassCenter(mm, pm);
			tl.updateMassCenter(mm, pm);
			tr.updateMassCenter(mm, pm);
			
			// http://arborjs.org/docs/barnes-hut
			// x = (x1*m1 + x2*m2) / m
			// y = (y1*m1 + y2*m2) / m  
			// (x,y) = (x1*m1+x2*m2, y1*m1+y2*m2) * 1/m
			// (x,y) = ((x1*m1,y1*m1) + (x2*m2,y2*m2)) * 1/m
			// (x,y) = (m1(x1,y1) + m2(x2,y2)) * 1/m
			
			float m1 = bl.mass, m2 = br.mass,
					m3 = tl.mass, m4 = tr.mass;
			Vector2 p1 = bl.massCenter.cpy().mul(m1), p2 = br.massCenter.cpy().mul(m2),
					p3 = tl.massCenter.cpy().mul(m3), p4 = tr.massCenter.cpy().mul(m4);
			this.mass = m1+m2+m3+m4;
			this.massCenter = p1.add(p2).add(p3).add(p4).mul(1/mass);
		}
	}
	
	public boolean contains(Vector2 pos) {
		return pos.x >= this.pos.x && pos.x < this.pos.x + side
				&& pos.y >= this.pos.y && pos.y < this.pos.y + side;
	}
	
	
	
	
	private void subAdd(Entity e, ComponentMapper<Mass> mm, ComponentMapper<Position> pm) {
		float subside = side/2;
		Vector2 p = pm.get(e).vec;
		if (p.x < subside) {
			if (p.y < subside) {
				bl.add(e, mm, pm);
			} else {
				tl.add(e, mm, pm);
			}
		} else {
			if (p.y < subside) {
				br.add(e, mm, pm);
			} else {
				tr.add(e, mm, pm);
			}
		}
	}

	private void addSubtrees() {
		float subside = side / 2;

		bl = new QuadTree(pos, subside);

		br = new QuadTree(pos.cpy(), subside);
		br.pos.x += subside;

		tl = new QuadTree(pos.cpy(), subside);
		tl.pos.y += subside;

		tr = new QuadTree(pos.cpy(), subside);
		tr.pos.x += subside;
		tr.pos.y += subside;
	}
}
