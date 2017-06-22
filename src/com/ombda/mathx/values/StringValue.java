package com.ombda.mathx.values;

public class StringValue extends Value{
	private final String value;
	private final NumberValue length;
	public StringValue(String s){
		super(Type.STRING);
		value = s;
		length = new NumberValue(value.length());
	}
	public String toString(){
		return value;
	}
	@Override
	public Value getMember(String name){
		if(name.equals("length"))
			return length;
		else return super.getMember(name);
	}
	@Override
	public void setImmutable(){}
	@Override
	public void seal(){}
	@Override
	public void finalize(){}
	@Override
	public boolean isImmutable(){
		return true;
	}
}
