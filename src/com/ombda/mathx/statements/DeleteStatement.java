package com.ombda.mathx.statements;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.expressions.Expression;
import com.ombda.mathx.expressions.IndexExpression;
import com.ombda.mathx.expressions.MemberExpression;
import com.ombda.mathx.expressions.Reference;
import com.ombda.mathx.values.Constants;
import com.ombda.mathx.values.Value;

public class DeleteStatement extends Statement{
	private Expression value;
	public DeleteStatement(Position p, Expression v){
		super(p);
		this.value = v;
	}

	@Override
	public void execute(Scope scope){
		if(this.value instanceof Reference){
			scope.remove(((Reference)this.value).getName());
		}else if(this.value instanceof IndexExpression){
			IndexExpression idx = (IndexExpression)value;
			if(idx.range){
				idx.thingToIndex.eval(scope).getMember(Operators.indexAssign).call(scope, Arrays.asList(Constants.UNDEFINED,idx.index.eval(scope),idx.index2.eval(scope)));
			}else{
				idx.thingToIndex.eval(scope).getMember(Operators.indexAssign).call(scope, Arrays.asList(Constants.UNDEFINED,idx.index.eval(scope)));
			}
		}else if(this.value instanceof MemberExpression){
			MemberExpression mmb = (MemberExpression)value;
			Value v = mmb.expr.eval(scope);
			if(v == Constants.UNDEFINED) return;
			v.setMember(mmb.name, Constants.UNDEFINED, false);
		}else throw new RuntimeException("Cannot delete reference of type "+this.value.getClass().getName());
	}
	
}
