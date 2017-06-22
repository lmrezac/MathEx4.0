package com.ombda.mathx.statements;

import java.util.ArrayList;
import java.util.List;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.expressions.Reference;
import com.ombda.mathx.values.Type;
import com.ombda.mathx.values.Value;

public class ClassDefinition extends Statement{
	private List<VariableInitializer> values;
	private String name;
	private List<Reference> parent;
	public ClassDefinition(Position p, String name, List<VariableInitializer> v, List<Reference> parent){
		super(p);
		values = v;
		this.name = name;
		this.parent = parent;
	}
	
	@Override
	public void execute(Scope scope){
		Type t;
		if(parent == null)
			t = new Type(name);
		else{
			List<Type> parents = new ArrayList<>();
			for(Reference ref : this.parent){
				Value parent = ref.eval(scope);
				if(!(parent instanceof Type)) throw new RuntimeException("Invalid parent specified for class "+name+", it is not a type");
				parents.add((Type)parent);
			}
			t = new Type(name,parents);
		}
		
		scope.set(name, t);
		scope.setConstant(name);
		
		for(VariableInitializer v : values){
			Value val = v.value.eval(scope);
			if(v.immutable) val.setImmutable();
			t.setMember(v.name, val, v.constant || v.isFunction());
		}
		
		t.finalize();
		
	}
	
	public String toString(){
		String result = "class "+name+" "+(parent == null || parent.isEmpty()? "" : "extends "+parent.toString())+"{\n";
		for(VariableInitializer var : values){
			result += var.toString()+"\n";
		}
		return result + "}";
	}
}
