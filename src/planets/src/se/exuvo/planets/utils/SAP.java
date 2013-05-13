package se.exuvo.planets.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Radius;
import se.exuvo.planets.components.Velocity;
import se.exuvo.planets.systems.CollisionSystem;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;

public class SAP {
	// http://jitter-physics.com/wordpress/?tag=sweep-and-prune
	// http://www.codercorner.com/SAP.pdf
	
	
	private Map<Entity, BoundingBox> boxes = new HashMap();
	private List<BoundingBox> xList = new ArrayList<BoundingBox>();
	
	
	/** No check is done if it already is in the SAP. */
	public void add(Entity e, ComponentMapper<Position> pm, ComponentMapper<Radius> rm, ComponentMapper<Velocity> vm) {
		VectorD2 p = pm.get(e).vec;
		double r = rm.get(e).radius;
		VectorD2 v = vm.get(e).vec;
		BoundingBox b = new BoundingBox(e, p, r, v);
		boxes.put(e, b);
	}
	
	public void remove(Entity e) {
		BoundingBox b = boxes.remove(e);
		xList.remove(b);
	}
	
	public void update(ImmutableBag<Entity> entities, ComponentMapper<Radius> rm) {
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			BoundingBox b = boxes.get(e);
			// TODO shouldn't need to update pos and vel. only r.
			b.r = rm.get(e).radius;
			b.update();
		}
	}
	
	public void sort() { // insertionsort
		int len = xList.size();
		for (int i = 0; i < len; i++) {
			BoundingBox b1 = xList.get(i);
			int j = i - 1;
			while (j >= 0 && b1.xmin < xList.get(j).xmin) {
				j--;
			}
			j++;
			if (j < i) {
				for (int k = i; k > j;) {
					int next = k - 1;
					xList.set(k, xList.get(next));
				}
				xList.set(j, b1);
			}
		}
	}
	
	
	public void getCollisions(Entity e, List<Collision> cs, double timeLimit) {
		BoundingBox b = boxes.get(e);
		int jOffset = 0;
		for (BoundingBox b2 : xList) {
			if (b.overlaps(b2)) {
				check(b, b2, cs, timeLimit);
			}
		}
	}
	
	
	public void getAllCollisions(List<Collision> cs, double timeLimit) {
		List<BoundingBox> activeList = new ArrayList<BoundingBox>();
		int jOffset = 0;
		for (BoundingBox b1 : xList) {
			for (int j = jOffset; j < activeList.size();) {
				BoundingBox b2 = activeList.get(j);
				
				if (b1.xmax < b2.xmin) {
					jOffset++; // instead of activeList.remove(b2);
				} else {
					if (b1.ymax > b2.ymin && b1.ymin < b2.ymax) {
						check(b1, b2, cs, timeLimit);
					}
					j++;
				}
			}
			activeList.add(b1);
		}
	}
	
	
	private void check(BoundingBox b1, BoundingBox b2, List<Collision> cs, double timeLimit) {
		double t = CollisionSystem.collisionTime(b1.p, b1.r, b1.v, b2.p, b2.r, b2.v);
		if (!Double.isNaN(t) && t >= 0 && t < timeLimit) {
			Collision c = new Collision(b1.e, b2.e, t);
		}
	}
	
		
	private static class BoundingBox implements Comparable<BoundingBox> {
		public Entity e;
		public VectorD2 p, v;
		public double r;
		public double xmin, xmax, ymin, ymax;
		
		public BoundingBox(Entity e, VectorD2 p, double r, VectorD2 v) {
			this.e = e;
			this.p = p;
			this.v = v;
			this.r = r;
			update();
		}
		
		public void update() {
			double k = r + v.len();
			xmin = p.x - k;
			xmax = p.x + k;
			ymin = p.y - k;
			ymax = p.y + k;
		}
		
		public boolean overlaps(BoundingBox b) {
			return xmin < b.xmax && xmax >= b.xmin && 
					ymin < b.ymax && ymax >= b.ymin;
		}
		
		@Override
		public int compareTo(BoundingBox o) {
			return Double.compare(xmin, o.xmin);
		}
	}

}
