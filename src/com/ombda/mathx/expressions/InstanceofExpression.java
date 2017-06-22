package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.BooleanValue;
import com.ombda.mathx.values.Type;
import com.ombda.mathx.values.Value;

public class InstanceofExpression extends BinaryExpression{
	public final boolean not;
	public InstanceofExpression(Position p, Expression l, Expression r, boolean not){
		super(p, l, r);
		this.not = not;
	}

	@Override
	public Value eval(Scope scope){
		Value v = left.eval(scope);
		Value t = right.eval(scope);
		if(!(t instanceof Type)) throw new RuntimeException("Invalid operands for operator instanceof: "+v.type+" & "+t.type);
		boolean b = v.type == null? false : v.type.Extends((Type)t);
		if(not) b = !b;
		return b? BooleanValue.TRUE : BooleanValue.FALSE;
	}

}
