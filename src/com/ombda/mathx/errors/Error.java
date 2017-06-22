package com.ombda.mathx.errors;

import com.ombda.mathx.Position;
import com.ombda.mathx.values.Value;

public class Error extends RuntimeException{
	private static final long serialVersionUID = -3906040392698418670L;
	private Value value;
	private Position pos;
	public Error(Position pos){
		super(pos.toString());
		this.pos = pos;
	}
	public Error(Position pos, String msg){
		super(pos+": "+msg);
		value = null;
		this.pos = pos;
	}
	public Error(Position pos, Value v){
		super(pos+": "+v);
		value = v;
		this.pos = pos;
	}
	public Position getPosition(){
		return pos;
	}
	public Value getValue(){
		return value;
	}
}
