package com.gamadu.spaceshipwarrior.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.gamadu.spaceshipwarrior.components.Position;
import com.gamadu.spaceshipwarrior.components.Velocity;

public class MovementSystem extends EntityProcessingSystem {
	@Mapper ComponentMapper<Position> pm;
	@Mapper ComponentMapper<Velocity> vm;

	public MovementSystem() {
		super(Aspect.getAspectFor(Position.class, Velocity.class));
	}

	@Override
	protected void process(Entity e) {
		Position position = pm.get(e);
		Velocity velocity = vm.get(e);
		
		position.x += velocity.vectorX*world.delta;
		position.y += velocity.vectorY*world.delta;
	}

}
