package com.ombda.mathx.values;

public class NumberValue extends Value{
	public final double value;
	public NumberValue(double d){
		super(Type.NUMBER);
		value = d;
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
