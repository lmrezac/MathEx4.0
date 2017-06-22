package com.ombda.mathx.values;

import java.util.ArrayList;
import java.util.List;

import com.ombda.mathx.Scope;

public abstract class Value{
	public Type type;
	public Value(Type t){
		this.type = t;
	}
	public Value call(Scope s, List<Value> params){
		throw new RuntimeException("Cannot call "+type);
	}
	public Value bind(Value v){
		return this;
	}
	public abstract boolean isImmutable();
	public abstract void setImmutable();
	public abstract void seal();
	public abstract void finalize();
	public String toString(){
		if(type == null) return "null";
		if(type.hasMember("toString")){
			Value v = type.getMember("toString");
			if(v instanceof Function){
				Function f = (Function)v;
				f.bind(this);
			}
			return v.call(null,new ArrayList<>()).toString();
		}else return super.toString();
	}
	public Value getMember(String name){
		if(name.equals("toString")){
			if(type.hasMember("toString")){
				Value v = type.getMember("toString");
				if(v instanceof Function){
					Function f = (Function)v;
					f.bind(this);
				}
				return v;
			}else{
				Type.toStringBasic.bind(this);
				return Type.toStringBasic;
			}
		}else if(name.equals("constructor"))
			return type;
		else if(type.hasMember(name)){
			return type.getMember(name).bind(this);
		}
		else return Constants.UNDEFINED;
	}
	public void setMember(String name, Value value, boolean constant){
		throw new RuntimeException("Cannot set member "+name+" in "+this.type);
	}
	public boolean hasMember(String name){
		return name.equals("toString") || name.equals("constructor");
	}
}
