package com.ombda.mathx.values;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Scope;
import com.ombda.mathx.Variables;
import com.ombda.mathx.errors.Return;
import com.ombda.mathx.expressions.FunctionLiteral;
import com.ombda.mathx.statements.Statement;

public class Function extends ObjectValue{
	protected Value boundObject = null;
	public final boolean isNative;
	private List<Statement> statements;
	private List<Param> params;
	private Variables includedVariables;
	private Param variadicName;
	public Function(){
		super(Type.FUNCTION);
		isNative = true;
		setMember(Operators.call,this,true);
	}
	public Function(List<Statement> list, List<Param> params, Map<String,Value> statics, Param variadicName){
		super(Type.FUNCTION);
		isNative = false;
		this.variadicName = variadicName;
		statements = list;
		this.params = params;
		if(statics == null || statics.isEmpty()){
			includedVariables = null;
		}else{
			includedVariables = new Variables();
			for(Map.Entry<String, Value> entry : statics.entrySet()){
				includedVariables.put(entry.getKey(), entry.getValue());
				includedVariables.setConstant(entry.getKey());
			}
		}
	}
	@Override
	public Value bind(Value v){
		boundObject = v;
		return this;
	}
	@Override
	public Value call(Scope s, List<Value> params){
		if(s == null) s = Scope.lastInstance;
		if(includedVariables == null){
			s.push();
		}else{
			s.with(includedVariables);
		}
		s.enterFunction();
		s.set("this", this.boundObject);
		
		s.setConstant("this");
		//System.out.println("setting _args to "+params);
		s.set("_args", new ArrayValue(params));
		int i;
		for(i = 0; i < Math.min(params.size(), this.params.size()); i++){
			Param param = this.params.get(i);
			Value v = params.get(i);
			if(param.immutable && !v.isImmutable()) throw new RuntimeException("Expected immutable value for parameter "+param.name);
			s.set(param.name, v);
			if(param.constant){
				s.setConstant(param.name);
			}
		}
		if(this.params.size() > params.size()){
			for(; i < this.params.size(); i++){
				Param param = this.params.get(i);
				s.set(param.name, param.defaultValue);
				if(param.constant)
					s.setConstant(param.name);
			}
		}else if(variadicName != null){
			if(params.size() > this.params.size()){
				List<Value> values = new ArrayList<>();
				for(; i < params.size(); i++){
					values.add(params.get(i));
				}
				s.set(variadicName.name, new ArrayValue(values));
			}else{
				s.set(variadicName.name, variadicName.defaultValue);
			}
			if(variadicName.constant)
				s.setConstant(variadicName.name);
		}
		try{
			for(Statement statement : statements)
				statement.execute(s);
		}catch(Return r){
			s.pop();
			return r.getValue();
		}
		s.pop();
	
		return Constants.UNDEFINED;
	}
	
	public static class Param{
		public final Value defaultValue;
		public final String name;
		public final boolean constant, immutable;
		public Param(String name, Value defaultValue, boolean constant, boolean immutable){
			this.name = name;
			this.defaultValue = defaultValue;
			this.constant = constant || immutable;
			this.immutable = immutable;
			if(immutable && !defaultValue.isImmutable()) throw new RuntimeException("Expected immutable value for parameter "+name);
		}
	}
}
