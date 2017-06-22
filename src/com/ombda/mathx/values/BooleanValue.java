package com.ombda.mathx.values;

public class BooleanValue extends Value{
	public static final BooleanValue TRUE = new BooleanValue(), FALSE = new BooleanValue();
	private BooleanValue(){
		super(Type.BOOLEAN);
	}
	public String toString(){
		if(this == TRUE) return "true";
		else return "false";
	}
	@Override
	public void setImmutable(){}
	@Override
	public void seal(){}
	@Override
	public void finalize(){}
	@Override
	public boolean isImmutable(){
		return false;
	}

}
