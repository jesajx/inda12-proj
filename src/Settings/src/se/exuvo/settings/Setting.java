package se.exuvo.settings;


public class Setting {
	private String name = "";
	private String valueS;
	private boolean valueB;
	private int valueI;
	private float valueF;
	private char type;
	public static final char STRING = 's';
	public static final char BOOLEAN = 'b';
	public static final char INTEGER = 'i';
	public static final char FLOAT = 'f';
	
		public Setting(String name,String value){
			this.name = name;
			valueS = value;
			type = STRING;
		}
		
		public Setting(String name,boolean value){
			this.name = name;
			valueB = value;
			type = BOOLEAN;
		}
		
		public Setting(String name,int value){
			this.name = name;
			valueI = value;
			type = INTEGER;
		}
		
		public Setting(String name,float value){
			this.name = name;
			valueF = value;
			type = FLOAT;
		}
		
		public Setting(String name,String value, char type){
			this.name=name;
			switch(type){
				case STRING:
					valueS = value;
					break;
				case BOOLEAN:
					valueB = Boolean.parseBoolean(value);
					break;
				case INTEGER:
					valueI = Integer.parseInt(value);
					break;
				case FLOAT:
					valueF = Float.parseFloat(value);
					break;
				default:
					throw new InvalidTypeException("Trying create unknown setting type!");
			}
			this.type = type;
		}
		
		public char getType(){
			return type;
		}
		
		public String getName(){
			return name;
		}
		
		public String getStr(){
			if(type == STRING){
				return valueS;
			}else{
				throw new InvalidTypeException("Trying to read String from non-String setting!");
			}
		}
		
		public boolean getBol(){
			if(type == BOOLEAN){
				return valueB;
			}else{
				throw new InvalidTypeException("Trying to read boolean from non-boolean setting!");
			}
		}
		
		public int getInt(){
			if(type == INTEGER){
				return valueI;
			}else{
				throw new InvalidTypeException("Trying to read int from non-int setting!");
			}
		}
		
		public float getFloat(){
			if(type == FLOAT){
				return valueF;
			}else{
				throw new InvalidTypeException("Trying to read float from non-float setting!");
			}
		}
		
		public String getValue(){
			switch(type){
				case STRING:
					return valueS;
				case BOOLEAN:
					return Boolean.toString(valueB);
				case INTEGER:
					return Integer.toString(valueI);
				case FLOAT:
					return Float.toString(valueF);
				default:
					throw new InvalidTypeException("Trying to read casted String from not-initialized setting!");
			}
		}
		
		public void setStr(String value){
			if(type == STRING){
				valueS = value;
			}else{
				throw new InvalidTypeException("Trying to write String to non-String setting!");
			}
		}
		
		public void setBol(boolean value){
			if(type == BOOLEAN){
				valueB = value;
			}else{
				throw new InvalidTypeException("Trying to write boolean to non-boolean setting!");
			}
		}
		
		public void setInt(int value){
			if(type == INTEGER){
				valueI = value;
			}else{
				throw new InvalidTypeException("Trying to write int to non-int setting!");
			}
		}
		
		public void setFloat(float value){
			if(type == FLOAT){
				valueF = value;
			}else{
				throw new InvalidTypeException("Trying to write float to non-float setting!");
			}
		}
		
		
	static class InvalidTypeException extends RuntimeException{
			private static final long serialVersionUID = -5958204037127245704L;

			InvalidTypeException(){
				super();
			}
			
			InvalidTypeException(String arg0){
				super(arg0);
			}
			
			InvalidTypeException(Throwable arg0){
				super(arg0);
			}

			InvalidTypeException(String arg0, Throwable arg1){
				super(arg0, arg1);
			}
			
		}
}



