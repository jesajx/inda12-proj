package se.unlogic.standardutils.populators;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.regex.Pattern;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.numbers.NumberUtils;

public class SocialSecurityPopulator extends BaseStringPopulator<String> implements BeanResultSetPopulator<String>, BeanStringPopulator<String>{

	Pattern pattern = Pattern.compile("^(19|20)[0-9]{6}-[0-9]{4}$");
	
	public SocialSecurityPopulator() {
		super();
	}

	private static final SocialSecurityPopulator POPULATOR = new SocialSecurityPopulator();

	public String populate(ResultSet rs) throws SQLException {
		return rs.getString(1);
	}

	public static SocialSecurityPopulator getPopulator(){
		return POPULATOR;
	}

	public String getValue(String value) {
		return value;
	}

	@Override
	public boolean validateDefaultFormat(String value) {
		
		// Syntax?
		if(!this.pattern.matcher(value).find()) {
			return false;
		}
			
		// Not even born?
		if(Integer.valueOf(value.substring(0, 4)) > Calendar.getInstance().get(Calendar.YEAR)) {
			return false;
		}
			
		// Valid by Luhn algorithm?
		return NumberUtils.isValidCC(this.format(value));

	}

	public Class<? extends String> getType() {
		return String.class;
	}
	
	/**
	 * Converts 12 digit "personnummer" to 10 digit personnummer
	 * Strips the dash character if present
	 * @param value
	 * @return
	 */
	protected String format(String value) {
		String formattedValue;
		if((formattedValue = value.replace("-", "")).length() == 12) {
			return formattedValue.substring(2);
		}
		return formattedValue;
	}
}
