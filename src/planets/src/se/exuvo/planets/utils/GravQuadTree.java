package se.exuvo.planets.utils;

import java.util.ArrayList;
import java.util.List;

import se.exuvo.planets.components.Acceleration;
import se.exuvo.planets.components.Mass;
import se.exuvo.planets.components.Position;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.FastMath;

/**
 * Tree data-structure used to speed up the GravitationSystem.
 * A QuadTree represents an square containing entities.
 * Each QuadTree keeps track of the total mass and the center of mass
 * of all its entities.
 * 
 * Each QuadTree object is in one of 3 states: has an entity, has 4 subtrees or has neither.
 */
public class GravQuadTree {
	// http://en.wikipedia.org/wiki/Barnes-Hut_simulation
	// http://arborjs.org/docs/barnes-hut
	// http://www.cs.princeton.edu/courses/archive/fall03/cs126/assignments/barnes-hut.html

	// TODO should this class have direct access to entity-mappers (statically)?
	// or pass as needed?

	// We could store these in a com.badlogic.gdx.math.Rectangle, but it would store
	// 'side' twofold: width and height. Since we don't need that many methods from that class it
	// was just as easy to implement what was needed directly instead.
	VectorD2 pos; // bottom left
	double side; // length of sides in Cube

	double mass; // total
	VectorD2 massVector; // the sum of each (planet*planet.mass). Divide with this.mass to get center.

	GravQuadTree bl, br, tl, tr; // {bottom,top}{left,right}
	Entity entity; // TODO necessary to store entity? if we only update using updateMassCenter it would be unncessary.
	
	int size; // no of entities. (not nodes)

	
	
	
	/**
	 * Creates a new QuadTree spanning the given cube.
	 * 
	 * @param pos
	 *            bottomLeft corner of cube
	 * @param side
	 *            length of each side in cube
	 */
	public GravQuadTree(VectorD2 pos, double side) {
		this.pos = pos;
		this.side = side;
		massVector = pos.cpy();
	}

	
	
	
	
	
	public boolean add(Entity entity, ComponentMapper<Mass> mm, ComponentMapper<Position> pm) {
		if (!contains(pm.get(entity).vec)) {// TODO shouldn't be necessary
			return false;
		}
		
		if (isEmpty()) {
			this.entity = entity;
			this.mass = mm.get(entity).mass;
			this.massVector = pm.get(entity).vec.cpy().mul(mass);
			++size;
			return true;
		} else {
			if (bl == null) {
				addSubtrees();
				quadrantOf(this.entity, mm, pm).add(this.entity, mm, pm);
				this.entity = null;
			}
			quadrantOf(entity, mm, pm).add(entity, mm, pm);
			double m = mm.get(entity).mass;
			VectorD2 p = pm.get(entity).vec;
			this.massVector.add(p.cpy().mul(m));
			this.mass += m;
			++size;
			return true;
		}
	}
	
	private void addSubtrees() {
		double subside = side / 2;

		bl = new GravQuadTree(pos.cpy(), subside);

		br = new GravQuadTree(pos.cpy(), subside);
		br.pos.x += subside;

		tl = new GravQuadTree(pos.cpy(), subside);
		tl.pos.y += subside;

		tr = new GravQuadTree(pos.cpy(), subside);
		tr.pos.x += subside;
		tr.pos.y += subside;
	}
	
	
	
	
	public boolean remove(Entity entity, ComponentMapper<Mass> mm, ComponentMapper<Position> pm) {
		if (hasEntity()) {
			if (this.entity == entity) {
				this.entity = null;
				mass = 0f;
				massVector.set(0f,0f);
				--size;
				return true;
			}
		} else if (hasChildren()) {
			GravQuadTree sub = quadrantOf(entity, mm, pm);
			double oldMass = sub.mass;
			VectorD2 oldVector = sub.massVector.cpy();
			
			if (sub.remove(entity, mm, pm)) {
				--size;
				mass -= oldMass - sub.mass; 
				massVector.sub(oldVector).add(sub.massVector);
			}
		}
		return false;
	}
	
	/** Doesn't update mass and massVector. */
	public boolean dirtyRemove(Entity entity, ComponentMapper<Mass> mm, ComponentMapper<Position> pm) {
		if (hasEntity()) {
			if (this.entity == entity) {
				this.entity = null;
				--size;
				return true;
			}
		} else if (hasChildren()) {
			GravQuadTree sub = quadrantOf(entity, mm, pm);
			if (sub.remove(entity, mm, pm)) {
				--size;
			}
		}
		return false;

	}
	
	
	
	
	public void updateAcceleration(Entity e, float theta, float G, ComponentMapper<Mass> mm, ComponentMapper<Position> pm, ComponentMapper<Acceleration> am, float delta) {
		if (isEmpty()) { // TODO also check if M is to small?
			return;
		}
		double M = this.mass;
		VectorD2 p = pm.get(e).vec;
		VectorD2 diff;
		
//		if (contains(p)) { // TODO this is only necessary if theta>=1
//			float m = mm.get(e).mass;
//			diff = this.massVector.cpy().sub(p.cpy().mul(m)).div(M-m).sub(p);
//		} else 
			diff = this.massVector.cpy().div(M).sub(p);
		
		double d2 = diff.len2();
		
		// s/d < theta
		if (this.entity != null || side*side < theta*theta*d2) { // TODO weight with mass.
			if (this.entity == e) { // TODO temporary hack
				return;
			}
			// F = G*m*M/d^2
			// F = m*a
			// a = G*M/d^2
			double a = G*M/d2;
			double k = (float) (a * FastMath.inverseSqrt(d2)); // normalizes diff
			if (!Double.isNaN(k)) {
				VectorD2 accVec = am.get(e).vec;
				accVec.add(diff.mul(k).mul(delta));
			}
		} else {
			bl.updateAcceleration(e, theta, G, mm, pm, am, delta);
			br.updateAcceleration(e, theta, G, mm, pm, am, delta);
			tl.updateAcceleration(e, theta, G, mm, pm, am, delta);
			tr.updateAcceleration(e, theta, G, mm, pm, am, delta);
		}
	}
	
	
	
	
	public void update(ComponentMapper<Mass> mm, ComponentMapper<Position> pm) {
		List<Entity> moved = new ArrayList<Entity>();
		update(moved, mm, pm);
		for (Entity e : moved) {
			add(e, mm, pm);
		}
	}
	
	private void update(List<Entity> moved, ComponentMapper<Mass> mm, ComponentMapper<Position> pm) {
		if (hasEntity()) {
			if (entity.isActive()) {
				VectorD2 p = pm.get(entity).vec;
				if (!contains(p)) {
					moved.add(entity);
					--size;
					this.mass = 0f;
					this.massVector.set(0f,0f);
					entity = null;
				} else {
					mass = mm.get(entity).mass;
					massVector.set(p).mul(mass);
				}
			}
		} else if (hasChildren()){
			bl.update(moved, mm, pm);
			br.update(moved, mm, pm);
			tl.update(moved, mm, pm);
			tr.update(moved, mm, pm);
			
			size = bl.size + br.size + tl.size + tr.size;
			
			mass = bl.mass + br.mass + tl.mass + tr.mass;
				
			massVector.set(0f,0f);
			massVector.add(bl.massVector).add(br.massVector);
			massVector.add(tl.massVector).add(tr.massVector);
		}
	}
		
	
	
	
	public boolean contains(VectorD2 pos) {
		return pos.x >= this.pos.x && pos.x < this.pos.x + side
				&& pos.y >= this.pos.y && pos.y < this.pos.y + side;
	}
	
	public int size() {
		return size;
	}
	
	public boolean isEmpty() {
		return !hasChildren() && !hasEntity();
	}
	public boolean hasChildren() {
		return bl != null;
	}
	public boolean hasEntity() {
		return entity != null;
	}
	
	
	
	
	private GravQuadTree quadrantOf(Entity e, ComponentMapper<Mass> mm, ComponentMapper<Position> pm) {
		double subside = side/2;
		VectorD2 p = pm.get(e).vec;
		if (p.x < pos.x + subside) {
			if (p.y < pos.y + subside) {
				return bl;
			} else {
				return tl;
			}
		} else {
			if (p.y < pos.y + subside) {
				return br;
			} else {
				return tr;
			}
		}
	}

}
