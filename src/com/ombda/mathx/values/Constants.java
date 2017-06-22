package com.ombda.mathx.values;

public class Constants{
	public static Value UNDEFINED;
	static{
		UNDEFINED = new Type("undefined"){
			@Override
			public String toString(){
				return "undefined";
			}
			@Override
			public Value getMember(String name){
				if(name.equals("toString")){
					return Type.toStringBasic.bind(this);
				}else if(name.equals("equals")){
					return Type.equalsBasic.bind(this);
				}else if(name.equals("constructor"))
					return type;
				else throw new RuntimeException("Cannot get member "+name+" in undefined");
			}
			@Override
			public boolean hasMember(String name){
				return name.equals("toString") || name.equals("equals") || name.equals("constructor");
			}
			@Override
			public void setMember(String name, Value value, boolean constant){
				throw new RuntimeException("Cannot set member "+name+" in undefined");
			}
			
		};
		UNDEFINED.type = (Type)UNDEFINED;
	}
}
