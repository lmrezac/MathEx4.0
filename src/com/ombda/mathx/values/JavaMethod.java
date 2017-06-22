package com.ombda.mathx.values;
import java.lang.reflect.Method;
import java.util.List;

import com.ombda.mathx.Scope;
public class JavaMethod extends Function{
	private Method m;
	public JavaMethod(Method m){
		this.m = m;
	}
	
	@Override
	public Value call(Scope s, List<Value> params){
		return null;
	}
	@Override
	public void setMember(String name, Value val, boolean constant){}
}
