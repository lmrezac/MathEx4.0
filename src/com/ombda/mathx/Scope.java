package com.ombda.mathx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ombda.mathx.values.Constants;
import com.ombda.mathx.values.ObjectValue;
import com.ombda.mathx.values.Value;

public class Scope implements Map<String,Value>{
	List<Variables> scopes;
	public List<Integer> functionIndexes = new ArrayList<>();
	public static Scope lastInstance = null;
	public Scope(){
		scopes = new ArrayList<>();
		push();
		lastInstance = this;
	}
	private void funcIndexIncr(){
		for(int i = 0; i < functionIndexes.size(); i++)
			functionIndexes.set(i,functionIndexes.get(i)+1);
	}
	public boolean inFunction(){
		return !functionIndexes.isEmpty();
	}
	private void funcIndexDecr(){
		for(int i = 0; i < functionIndexes.size(); i++)
			functionIndexes.set(i,functionIndexes.get(i)-1);
		if(functionIndex() < 1){
			functionIndexes.remove(0);
		}
	}
	private int functionIndex(){
		return functionIndexes.get(0)+1;
	}
	public void push(){
		scopes.add(0,new Variables());
		if(inFunction()) funcIndexIncr();
	}
	public void pop(){
		scopes.remove(0);
		if(inFunction()) funcIndexDecr();
	}
	public void enterFunction(){
		functionIndexes.add(0,0);
	}
	public boolean isConstant(String key){
		//if(inFunction) return scopes.get(0).containsKey(key)? scopes.get(0).isConstant(key) : scopes.get(scopes.size()-1).containsKey(key)? scopes.get(scopes.size()-1).isConstant(key) : false;
		for(int i = 0; i < (inFunction()? functionIndex() : scopes.size()); i++){
			if(scopes.get(i).containsKey(key)){
				return scopes.get(i).isConstant(key);
			}
		}
		if(inFunction() && scopes.get(scopes.size()-1).containsKey(key)) return scopes.get(scopes.size()-1).isConstant(key);
		return false;
	}
	public void setConstant(String key){
		if(!scopes.get(0).containsKey(key) && this.containsKey(key))
			throw new RuntimeException("Cannot set a variable constant outside of the scope it was created in.");
		scopes.get(0).setConstant(key);
	}
	@Override
	public int size(){
		return scopes.get(0).size();
	}
	@Override
	public boolean isEmpty(){
		return scopes.get(0).isEmpty();
	}
	@Override
	public boolean containsKey(Object key){
		//if(inFunction) return scopes.get(0).containsKey(key) || scopes.get(scopes.size()-1).containsKey(key);
		for(int i = 0; i < (inFunction()? functionIndex() : scopes.size()); i++)
			if(scopes.get(i).containsKey(key) && scopes.get(i).get(key) != Constants.UNDEFINED)
				return true;
		if(inFunction()) return scopes.get(scopes.size()-1).containsKey(key);
		return false;
	}
	@Override
	public boolean containsValue(Object value){
		//if(inFunction) return scopes.get(0).containsValue(value) || scopes.get(scopes.size()-1).containsValue(value);
		for(int i = 0; i < (inFunction()? functionIndex() : scopes.size()); i++)
			if(scopes.get(i).containsValue(value))
				return true;
		if(inFunction()) return scopes.get(scopes.size()-1).containsValue(value);
		return false;
	}
	@Override
	public Value get(Object key){
	//	if(inFunction) return scopes.get(0).containsKey(key)? scopes.get(0).get(key) : scopes.get(scopes.size()-1).containsKey(key)? scopes.get(scopes.size()-1).get(key) : Constants.UNDEFINED;
		for(int i = 0; i < (inFunction()? functionIndex()+1 : scopes.size()); i++)
			if(scopes.get(i).containsKey(key))
				return scopes.get(i).get(key);
		if(inFunction() && scopes.get(scopes.size()-1).containsKey(key)) return scopes.get(scopes.size()-1).get(key);
		return Constants.UNDEFINED;
	}
	public Value set(String key, Value value){
		if(isReservedWord(key)) throw new RuntimeException("Cannot use "+key+", it is a reserved word");
		/*if(value == Constants.UNDEFINED){
			this.remove(key);
			return value;
		}*/
		
		for(int i = 0; i < (inFunction()? functionIndex()+1 : scopes.size()); i++)
			if(scopes.get(i).containsKey(key)){
				scopes.get(i).put(key, value);
				return value;
			}
		scopes.get(0).put(key, value);
		return value;
	}
	@Override
	public Value put(String key, Value value){
	//	if(isReservedWord(key)) throw new RuntimeException("Cannot use "+key+", it is a reserved word");
		return set(key,value);
	}
	@Override
	public Value remove(Object key){
		//if(inFunction) return scopes.get(0).containsKey(key)? scopes.get(0).remove(key) : scopes.get(scopes.size()-1).containsKey(key)? scopes.remove(scopes.size()-1).get(key) : Constants.UNDEFINED;
		for(int i = 0; i < (inFunction()? functionIndex() : scopes.size()); i++)
			if(scopes.get(i).containsKey(key))
				return scopes.get(i).remove(key);
		if(inFunction() && scopes.get(scopes.size()-1).containsKey(key)) return scopes.get(scopes.size()-1).remove(key);
		return Constants.UNDEFINED;
	}
	@Override
	public void putAll(Map<? extends String, ? extends Value> m){
		scopes.get(0).putAll(m);
	}
	@Override
	public void clear(){
		for(int i = 0; i < scopes.size(); i++)
			scopes.get(i).clear();
	}
	@Override
	public Set<String> keySet(){
		Set<String> set = new HashSet<String>(scopes.get(0).keySet());
		for(int i = 1; i < scopes.size(); i++)
			set.addAll(scopes.get(i).keySet());
		return set;
	}
	@Override
	public Collection<Value> values(){
		return scopes.get(0).values();
	}
	@Override
	public Set<java.util.Map.Entry<String, Value>> entrySet(){
		return scopes.get(0).entrySet();
	}
	
	public void with(Variables variables){
		this.scopes.add(0,new Variables(variables));
		if(inFunction()) funcIndexIncr();
	}
	
	
	private static final String[] RESERVED_WORDS = {"if","else","while","for","in",
			"instanceof","defined","and","or","xor","new","class","undefined","null",
			"true","false","const","seal","finalize","immutable","var","function",
			"typeof","delete","when","with","import","load","until","goto","break",
			"continue","return","enum","struct","throw","try","catch","void","loop",
			"of","extends","super","stop"};
	public static boolean isReservedWord(String s){
		for(String str : RESERVED_WORDS){
			if(s.equals(str)) return true;
		}
		return false;
	}
	public void create(String name, Value v){
		if(scopes.get(0).containsKey(name)) throw new RuntimeException("Variable "+name+" already exists");
		scopes.get(0).put(name,v);
	}
}
