package com.ombda.mathx;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ombda.mathx.values.Constants;
import com.ombda.mathx.values.Value;

public class Variables implements Map<String,Value>{
	public Map<String,Value> values = new HashMap<String,Value>();
	private Map<String,Boolean> constants = new HashMap<String,Boolean>();
	
	public Variables(){}
	public Variables(Variables variables){
		this.values.putAll(variables.values);
		this.constants.putAll(variables.constants);
	}
	
	public boolean isConstant(String key){
		return constants.get(key);
	}
	public void setConstant(String key){
		constants.put(key, true);
	}
	@Override
	public int size(){
		return values.size();
	}

	@Override
	public boolean isEmpty(){
		return values.isEmpty();
	}

	@Override
	public boolean containsKey(Object key){
		return values.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value){
		return values.containsValue(value);
	}

	@Override
	public Value get(Object key){
		return values.get(key);
	}

	@Override
	public Value put(String key, Value value){
		///if(Scope.isReservedWord(key)) throw new RuntimeException("Cannot use "+key+", it is a reserved word");
		if(constants.containsKey(key) && constants.get(key)){
			//System.out.println("constant assignment "+key+" original = "+values.get(key));
			if(values.get(key) != Constants.UNDEFINED && values.get(key) != null & !key.equals("this"))
				throw new RuntimeException("Cannot modify constant value "+key);
		}
		if(!constants.containsKey(key))
			constants.put(key, false);
		return values.put(key, value);
	}

	@Override
	public Value remove(Object key){
		if(constants.containsKey(key) && constants.get(key))
			throw new RuntimeException("Cannot delete constant value "+key);
		constants.remove(key);
		return values.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Value> m){
		if(m instanceof Variables){
			Variables v = (Variables)m;
			this.constants.putAll(v.constants);
			this.values.putAll(v.values);
		}else this.values.putAll(m);
	}

	@Override
	public void clear(){
		this.values.clear();
		this.constants.clear();
	}

	@Override
	public Set<String> keySet(){
		return values.keySet();
	}

	@Override
	public Collection<Value> values(){
		return values.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, Value>> entrySet(){
		return values.entrySet();
	}
	
	public String toString(){
		return values.toString();
	}

}
