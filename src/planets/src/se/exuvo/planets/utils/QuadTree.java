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
	// 'side' twofold: width and height. Since we don't need that many methods from that class it
	// was just as easy to implement what was needed directly instead.
	Vector2 pos; // bottom left
	float side; // length of sides in Cube

	float mass; // total
	Vector2 massVector; // the sum of each (planet*planet.mass). Divide with this.mass to get center.

	QuadTree bl, br, tl, tr; // {bottom,top}{left,right}
	Entity entity; // TODO necessary to store entity? if we only update using updateMassCenter it would be unncessary.

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
			return true;
		} else {
			if (bl == null) {
				addSubtrees();
				quadrantOf(this.entity, mm, pm).add(this.entity, mm, pm);
				this.entity = null;
			}
			quadrantOf(entity, mm, pm).add(entity, mm, pm);
			float m = mm.get(entity).mass;
			Vector2 p = pm.get(entity).vec;
			this.massVector.add(p.cpy().mul(m));
			this.mass += m;
			return true;
		}
	}
	
	/**
	 * Searches for and deletes the given entity from the QuadTree.
	 * NOTE: mass and massVector is NOT UPDATED.
	 * @param entity Entity to add.
	 * @param mm mapper for masses.
	 * @param pm mapper for positions
	 * @return whether the entity was deleted. false iff the entity didn't exist in the tree.
	 */
	public boolean remove(Entity entity, ComponentMapper<Mass> mm, ComponentMapper<Position> pm) {
//		if (!contains(pm.get(entity).vec)) { // TODO shouldn't be necessary
//			return false;
//		}
		if (hasEntity()) {
			if (this.entity == entity) {
				this.entity = null;
				this.mass = 0f;
				this.massVector.set(0f,0f);
				return true;
			}
		} else if (hasChildren()) {
			QuadTree subtree = quadrantOf(this.entity, mm, pm);
			Vector2 oldCenter = subtree.massVector;
			if (subtree.remove(entity, mm, pm)) {
				this.mass -= mm.get(entity).mass;
				this.massVector.sub(oldCenter).add(subtree.massVector);
			}
			
		}
		return false;
	}
	
	
	
	
	public void updateAcceleration(Entity e, float theta, float G, ComponentMapper<Mass> mm, ComponentMapper<Position> pm, ComponentMapper<Acceleration> am) {
		if (isEmpty()) { // TODO also check if M is to small?
			return;
		}
		float M = this.mass;
		Vector2 p = pm.get(e).vec;
		Vector2 diff;
		
//		if (contains(p)) { // TODO this is only necessary if theta>=1
//			float m = mm.get(e).mass;
//			diff = this.massVector.cpy().sub(p.cpy().mul(m)).div(M-m).sub(p);
//		} else 
			diff = this.massVector.cpy().div(M).sub(p);
		
		float d2 = diff.len2();
		
		// s/d < theta
		if (this.entity != null || side*side < theta*theta*d2) { // TODO weight with mass.
			if (this.entity == e) { // TODO temporary hack
				return;
			}
			// F = G*m*M/d^2
			// F = m*a
			// a = G*M/d^2
			float a = G*M/d2;
			if (!Float.isNaN(a)) {
                diff.mul((float) (a * FastMath.inverseSqrt(d2))); // normalize and mul(a)
                am.get(e).vec.add(diff);
//				System.out.println(e+".a: "+am.get(e).vec);
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
		update(this, mm, pm);
		
//		updateMassCenter(mm, pm); // TODO faster? vs remove+add below
		
		// TODO faster with clear and re-add?
	}
	private void update(QuadTree root, ComponentMapper<Mass> mm, ComponentMapper<Position> pm) {
		if (hasEntity()) {
			if (!contains(pm.get(entity).vec)) {
				// TODO faster? vs updating whole tree afterwards (see above)
				root.remove(entity, mm, pm);
				root.add(entity, mm, pm);
			}
		} else if (hasChildren()){
			bl.update(root, mm, pm);
			br.update(root, mm, pm);
			tl.update(root, mm, pm);
			tr.update(root, mm, pm);
		}
	}

	
	
	
	public void updateMassCenter(ComponentMapper<Mass> mm, ComponentMapper<Position> pm) {
		// TODO
		if (hasEntity()) {
			this.mass = mm.get(entity).mass;
			this.massVector = pm.get(entity).vec.cpy().mul(mass);
		} else if (hasChildren()){
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
			this.mass = m1+m2+m3+m4;
			Vector2 p1 = bl.massVector.cpy(), p2 = br.massVector.cpy(),
					p3 = tl.massVector.cpy(), p4 = tr.massVector.cpy();
			this.massVector = p1.add(p2).add(p3).add(p4);
		}
	}
	
	
	
	
	public boolean contains(Vector2 pos) {
		return pos.x >= this.pos.x && pos.x < this.pos.x + side
				&& pos.y >= this.pos.y && pos.y < this.pos.y + side;
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
	
	
	
	
	
	private QuadTree quadrantOf(Entity e, ComponentMapper<Mass> mm, ComponentMapper<Position> pm) {
		float subside = side/2;
		Vector2 p = pm.get(e).vec;
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

	private void addSubtrees() {
		float subside = side / 2;

		bl = new QuadTree(pos.cpy(), subside);

		br = new QuadTree(pos.cpy(), subside);
		br.pos.x += subside;

		tl = new QuadTree(pos.cpy(), subside);
		tl.pos.y += subside;

		tr = new QuadTree(pos.cpy(), subside);
		tr.pos.x += subside;
		tr.pos.y += subside;
	}
}
