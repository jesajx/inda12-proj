package com.gamadu.spaceshipwarrior.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.DelayedEntityProcessingSystem;
import com.gamadu.spaceshipwarrior.components.Expires;

public class ExpiringSystem extends DelayedEntityProcessingSystem {
	@Mapper
	ComponentMapper<Expires> em;

	public ExpiringSystem() {
		super(Aspect.getAspectForAll(Expires.class));
	}
	
	@Override
	protected void processDelta(Entity e, float accumulatedDelta) {
		Expires expires = em.get(e);
		expires.delay -= accumulatedDelta;
	}
	
	@Override
	protected void processExpired(Entity e) {
		e.deleteFromWorld();
	}
	
	@Override
	protected float getRemainingDelay(Entity e) {
		Expires expires = em.get(e);
		return expires.delay;
	}
}
