package com.gamadu.spaceshipwarrior.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.gamadu.spaceshipwarrior.SpaceshipWarrior;
import com.gamadu.spaceshipwarrior.components.Bounds;
import com.gamadu.spaceshipwarrior.components.Health;
import com.gamadu.spaceshipwarrior.components.Player;
import com.gamadu.spaceshipwarrior.components.Position;
import com.gamadu.spaceshipwarrior.components.Velocity;

public class RemoveOffscreenShipsSystem extends IntervalEntityProcessingSystem {
	@Mapper ComponentMapper<Position> pm;
	@Mapper ComponentMapper<Bounds> bm;

	public RemoveOffscreenShipsSystem() {
		super(Aspect.getAspectForAll(Velocity.class, Position.class, Health.class, Bounds.class).exclude(Player.class), 5);
	}

	@Override
	protected void process(Entity e) {
		Position position = pm.get(e);
		Bounds bounds = bm.get(e);
		
		if(position.y < -SpaceshipWarrior.FRAME_HEIGHT/2-bounds.radius) {
			e.deleteFromWorld();
		}
	}

}
