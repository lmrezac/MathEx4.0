package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Constants;
import com.ombda.mathx.values.NumberValue;
import com.ombda.mathx.values.StringValue;
import com.ombda.mathx.values.Value;

public class IndexAssignExpression extends IndexExpression{
	public final Expression value;
	public final boolean operate;
	public final String operator;
	
	public IndexAssignExpression(Position p, Expression value, Expression thingToIndex, Expression i, Expression i2, String operator){
		super(p, thingToIndex, i, i2);
		this.value = value;
		this.operate = operator != null;
		this.operator = operator;
	}
	public IndexAssignExpression(Position p, Expression value, Expression thingToIndex, Expression i, String operator){
		super(p, thingToIndex, i);
		this.value = value;
		this.operate = operator != null;
		this.operator = operator;
	}
	@Override
	public Value eval(Scope scope){
		Value thing = thingToIndex.eval(scope);
		if((thingToIndex instanceof MemberNullCatchExpression || thingToIndex instanceof MemberNullCatchAssignExpression) && (thing == null || thing == Constants.UNDEFINED)){
			return thing;
		}
		Value value = this.value.eval(scope);
		Value i = index.eval(scope);
		if(thing instanceof StringValue && thingToIndex instanceof VariableExpression){
			String str = thing.toString();
			int i1, i2;
			{
				if(!(i instanceof NumberValue)){
					throw new RuntimeException("Cannot index string with "+i.type);
				}
				double d = ((NumberValue)i).value;
				if(d != (int)d) throw new RuntimeException("Cannot index string with float");
				i1 = (int)d;
				if(i1 < 0) i1 += str.length()-1;
			}
			if(range){
				Value v2 = index2.eval(scope);
				if(!(v2 instanceof NumberValue)){
					throw new RuntimeException("Cannot index string with "+v2.type);
				}
				double d = ((NumberValue)v2).value;
				if(d != (int)d) throw new RuntimeException("Cannot index string with float");
				i2 = (int)d;
				if(i2 < 0) i2 += str.length()-1;
			}else{
				i2 = i1 + 1;
			}
			
			
			
			String replacement;
			
			if(operate){
				String original = str.substring(i1,i2);
				
				if(operator.equals(Operators.plus)){
					if(!(value instanceof StringValue)) throw new RuntimeException("Cannot assign a substring to "+value.type);
					replacement = original+value.toString();
				}else if(operator.equals(Operators.minus)){
					replacement = value.toString();
					if(original.endsWith(replacement))
						replacement = original.substring(0, original.lastIndexOf(replacement));
				}else if(operator.equals(Operators.times)){
					replacement = new StringValue(original).getMember(Operators.times).call(scope, Arrays.asList(value)).toString();
				}else throw new RuntimeException("Invalid operator for index assign expression on a string: "+operator);
			}else{
				if(!(value instanceof StringValue)) throw new RuntimeException("Cannot assign a substring to "+value.type);
				replacement = value.toString();
			}
			
			str = str.substring(0, i1) + replacement + str.substring(i2);
			
			Value result = new StringValue(str);
			scope.set(((VariableExpression)this.thingToIndex).name, result);
			
			return result;
		}
		Value result;
		if(range){
			Value start = i, end = index2.eval(scope);
			
			if(operate){
				Value indexed = thing.getMember(Operators.slice).call(scope, Arrays.asList(start,end));
				result = indexed.getMember(operator).call(scope, Arrays.asList(value));
				result = thing.getMember(Operators.sliceAssign).call(scope, Arrays.asList(result,start,end));
			}else{
				result = thing.getMember(Operators.sliceAssign).call(scope, Arrays.asList(value,start,end));
			}
			return result;
		}else{
			if(operate){
				Value indexed = thing.getMember(Operators.index).call(scope, Arrays.asList(i));
				result = indexed.getMember(operator).call(scope, Arrays.asList(value));
				result = thing.getMember(Operators.indexAssign).call(scope, Arrays.asList(result,i));
			}else{
				result = thing.getMember(Operators.indexAssign).call(scope, Arrays.asList(value,i));
			}
		}
		return result;
	}

}
