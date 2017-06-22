package com.ombda.mathx;

import java.util.ArrayList;
import java.util.List;

public class Position{
	public final int line, pos;
	protected Position(int l, int p){
		line = l;
		pos = p;
	}
	public boolean equals(Object obj){
		return obj instanceof Position && ((Position)obj).line == this.line && ((Position)obj).pos == this.pos;
	}
	private static final List<List<Position>> interns = new ArrayList<>();
	public static Position get(int l, int p){
		while(interns.size() <= l)
			interns.add(new ArrayList<>());
		List<Position> line = interns.get(l);
		for(int i = line.size(); i <= p; i++){
			line.add(new Position(i,l));
		}
		return line.get(p);
	}
	public String toString(){
		return "line "+line+", position "+pos;
	}
	public Position add(int line, int pos){
		if(line == 0 && pos == 0) return this;
		return get(this.line + line, this.pos + pos);
	}
}