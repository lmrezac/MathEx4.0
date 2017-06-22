package com.ombda.mathx.values;

import java.util.List;
import java.util.Map;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Scope;
import com.ombda.mathx.Variables;

public class ObjectValue extends Value{
	public Variables variables = new Variables();
	private boolean finalized = false, immutable = false;
	public ObjectValue(Type t){
		super(t);
	}
	public ObjectValue(){
		this(Type.OBJECT);
	}
	// difference between this and seal is that in immutable, 
	// all submembers are set immutable, too (if they are objects)
	public void setImmutable(){
		immutable = true;
		seal();
		for(Map.Entry<String, Value> entry : variables.entrySet()){
			Value v = entry.getValue();
			if(v instanceof ObjectValue)
				((ObjectValue)v).setImmutable();
		}
	}
	public void seal(){
		finalize();
		for(String key : variables.keySet()){
			variables.setConstant(key);
		}
	}
	public void finalize(){
		finalized = true;
	}
	@Override
	public Value getMember(String name){
		Value v;
		if(variables.containsKey(name)){
			v = variables.get(name);
		}else if(type.hasMember(name)){
			v = type.getMember(name);
		}else v = super.getMember(name);
		if(v instanceof Function){
			((Function)v).bind(this);
		}
		return v;
	}
	@Override
	public void setMember(String name, Value value, boolean constant){
		if(finalized && !hasMember(name))
			throw new RuntimeException("Cannot add new members to a finalized object");
		variables.put(name, value);
		if(constant) variables.setConstant(name);
	}
	@Override
	public boolean hasMember(String name){
		return variables.containsKey(name) || super.hasMember(name);
	}
	@Override
	public Value call(Scope s, List<Value> params){
		Value v = getMember(Operators.call);
		if(v == null) return super.call(s, params);
		return v.call(s, params);
	}
	@Override
	public boolean isImmutable(){
		return immutable;
	}
}
