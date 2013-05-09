package se.exuvo.planets.templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import se.exuvo.planets.utils.ClassFinder;

import com.artemis.World;

public class Loader {
	private static final Logger log = Logger.getLogger(Loader.class);
	private static List<Template> templates = new ArrayList<Template>();

	public static void init() {
		templates.clear();
		log.debug("Loading templates");
		try {
			List<Class<?>> l = ClassFinder.getClasses("se.exuvo.planets.templates");
			for (Class<?> c : l) {
				if (Template.class.isAssignableFrom(c) && !c.equals(Template.class)) {
					try {
						Class<? extends Template> cc = c.asSubclass(Template.class);
						Template p = cc.newInstance();
						if (p.getName() != null && !p.getName().equals("")) {
							templates.add(p);
							log.trace("Loaded template: " + p.getName());
						}
					} catch (Throwable e) {
						log.warn("Failed to load template: \"" + c.getSimpleName() + "\"", e);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			log.warn("Failed to load templates", e);
		} catch (IOException e) {
			log.warn("Failed to load templates", e);
		}
	}

	public void loadTemplate(Template t, World w) {
		int toRemove = w.getEntityManager().getActiveEntityCount();
		for (int id = 0; toRemove > 0; id++) {
			if (w.getEntityManager().isActive(id)) {
				w.getEntity(id).deleteFromWorld();
				toRemove--;
			}
		}
		
		t.load(w);
	}

	public List<Template> getTemplates() {
		return Collections.unmodifiableList(templates);
	}
}
