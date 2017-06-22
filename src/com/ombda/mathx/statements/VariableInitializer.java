package com.ombda.mathx.statements;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.expressions.Expression;
import com.ombda.mathx.expressions.FunctionLiteral;
import com.ombda.mathx.values.ObjectValue;
import com.ombda.mathx.values.Value;

public class VariableInitializer extends Statement{
	public final String name;
	public final Expression value;
	public final boolean immutable, constant;
	public VariableInitializer(Position p, String name, Expression value){
		this(p,name,value,false,false);
	}
	public VariableInitializer(Position p, String name, Expression value, boolean cons){
		this(p,name,value,cons,false);
	}
	public VariableInitializer(Position p, String name, boolean immute, Expression value){
		this(p,name,value,false,immute);
	}
	public VariableInitializer(Position p, String name, Expression value, boolean cons, boolean immute){
		super(p);
		this.name = name;
		this.value = value;
		this.constant = cons;
		this.immutable = immute;
	}
	
	@Override
	public void execute(Scope scope){
		Value v = value.eval(scope);
		scope.create(name, v);
		if(constant) scope.setConstant(name);
		if(immutable){
			scope.setConstant(name);
			if(v instanceof ObjectValue){
				((ObjectValue)v).setImmutable();
			}
		}
	}
	public boolean isFunction(){
		return value instanceof FunctionLiteral;
	}

}
