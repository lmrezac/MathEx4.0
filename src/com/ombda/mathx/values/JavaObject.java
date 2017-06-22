package com.ombda.mathx.values;

import java.util.Map;

public class JavaObject extends ObjectValue{
	private final Class<?> clazz;
	
	public JavaObject(Class<?> clazz){
		this.clazz = clazz;
	}
	@Override
	public boolean hasMember(String name){
		return false;
	}
	@Override
	public Value getMember(String name){
		return null;
	}
	@Override
	public void setMember(String name, Value value, boolean constant){
		
	}
	@Override
	public String toString(){
		return clazz.toString();
	}
	
	
	public static Value wrap(Object o){
		if(o == null) return null;
		else if(o instanceof Number) return new NumberValue(((Number)o).doubleValue());
		else if(o instanceof String) return new StringValue((String)o);
		else if(o instanceof Boolean) return (Boolean)o? BooleanValue.TRUE : BooleanValue.FALSE;
		/*else if(o instanceof Map){
			ObjectValue obj = new ObjectValue();
			obj.variables.putAll((Map<String,Value>)o);
			return obj;
		}*/
		else throw new RuntimeException("Cannot wrap "+o.getClass().getSimpleName());
	}
	public static Object unwrap(Value v, Class<?> hint){
		if(v == null || v == Constants.UNDEFINED) return null;
		if(v.getClass() == hint) return v;
		if(v instanceof NumberValue){
			double d = ((NumberValue)v).value;
			if(hint == byte.class || hint == Byte.class) return (byte)d;
			else if(hint == short.class || hint == Short.class) return (short)d;
			else if(hint == char.class || hint == Character.class) return (char)d;
			else if(hint == int.class || hint == Integer.class) return (int)d;
			else if(hint == long.class || hint == Long.class) return (long)d;
			else if(hint == float.class || hint == Float.class) return (float)d;
			return d;
		}else if(v instanceof StringValue){
			return v.toString();
		}else if(v instanceof ObjectValue){
			return ((ObjectValue)v).variables;
		}else if(v instanceof BooleanValue){
			return v == BooleanValue.TRUE;
		}else throw new RuntimeException("Cannot convert "+v.getClass().getSimpleName()+" to Java representation");
	}
}
