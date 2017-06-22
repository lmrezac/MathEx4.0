package com.ombda.mathx;

import java.util.List;

public class Definition extends Macro{

	public Definition(String name){
		super(name);
	}

	@Override
	public List<Token> replacement(){
		return null;
	}

	@Override
	public int matches(List<Token> tokens, int position){
		throw new RuntimeException("Simple definitions are not replacement macros");
	}

}
