package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.Variables;
import com.ombda.mathx.values.Constants;
import com.ombda.mathx.values.Value;

public class MemberSealExpression extends MemberReference{
	public MemberReference expr;
	public String name;
	public MemberSealExpression(Position p, MemberReference ex){
		super(p, ex);
		this.name = ex.getName();
		if(Scope.isReservedWord(name)) throw new RuntimeException("Cannot use "+name+", it is a reserved word");
	}
	@Override
	public Value evalObject(Scope scope, Value v){
		if(v == Constants.UNDEFINED) return v;
		Value result = v.getMember(name);
		result.seal();
		return result;
	}
	@Override
	public String getName(){
		return name;
	}
	@Override
	protected Value setObject(Value v){
		return object = expr.object;
	}
}
