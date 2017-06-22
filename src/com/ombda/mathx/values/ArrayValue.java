package com.ombda.mathx.values;

import java.util.ArrayList;
import java.util.List;

public class ArrayValue extends ObjectValue{
	public List<Value> values;
	//in arrays, finalized means cannot change size / remove members
	boolean finalized = false, sealed = false, immutable = false;
	public ArrayValue(){
		this(new ArrayList<>());
	}
	public ArrayValue(List<Value> values){
		super(Type.ARRAY);
		this.values = values;
	}
	
	@Override
	public Value getMember(String name){
		if(name.equals("length")){
			return new NumberValue(values.size());
		}else return super.getMember(name);
	}
	@Override
	public void setMember(String name, Value v, boolean constant){
		if(name.equals("length")){
			if(!(v instanceof NumberValue)) throw new RuntimeException("Expected int, got "+v.type);
			double d = ((NumberValue)v).value;
			if(d != (int)d) throw new RuntimeException("Expected int, got float");
			int i = (int)d;
			if(finalized && i != values.size()) 
				throw new RuntimeException("Cannot change the size of a finalized array");
			while(values.size() >= i){
				values.remove(values.size()-1);
			}
			while(values.size() < i){
				values.add(Constants.UNDEFINED);
			}
		}else super.setMember(name, v, constant);
	}
	@Override
	public boolean hasMember(String name){
		return name.equals("length") || super.hasMember(name);
	}
	@Override
	public void setImmutable(){
		immutable = true;
		seal();
		for(Value v : values){
			v.setImmutable();
		}
	}
	@Override
	public void seal(){
		finalize();
		sealed = true;
	}
	@Override
	public void finalize(){
		finalized = true;
	}
	@Override
	public boolean isImmutable(){
		return immutable;
	}
}
