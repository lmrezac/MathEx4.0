package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.Variables;
import com.ombda.mathx.values.Constants;
import com.ombda.mathx.values.Value;

public class MemberImmutableExpression extends MemberReference{
	public MemberReference expr;
	public String name;
	public MemberImmutableExpression(Position p, MemberReference ex){
		super(p, ex);
		this.name = ex.getName();
		if(Scope.isReservedWord(name)) throw new RuntimeException("Cannot use "+name+", it is a reserved word");
	}
	@Override
	public Value evalObject(Scope scope, Value v){
		if(v == Constants.UNDEFINED || v == null) return v;
		Value result = v.getMember(name);
		v.setMember(name, result, true);
		result.setImmutable();
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
