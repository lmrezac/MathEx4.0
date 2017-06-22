package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class ImmutableExpression extends Reference{
	private Reference ref;
	
	public ImmutableExpression(Position p, Reference ref){
		super(p);
		this.ref = ref;
	}
	
	
	@Override
	public Value eval(Scope scope){
		Value v = ref.eval(scope);
		v.setImmutable();
		scope.setConstant(ref.getName());
		return v;
	}

	@Override
	public String getName(){
		return ref.getName();
	}

}
