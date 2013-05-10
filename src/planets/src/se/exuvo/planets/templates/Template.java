package se.exuvo.planets.templates;

import org.apache.log4j.Logger;

import com.artemis.World;

public abstract class Template {
	public abstract void load(World world);
	public String getName() { return this.getClass().getSimpleName();};
	public abstract String getDescription();
	protected static Logger log = Logger.getLogger(Template.class);
	public String toString() { return getName(); };
}
