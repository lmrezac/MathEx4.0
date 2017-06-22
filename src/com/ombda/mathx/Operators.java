package com.ombda.mathx;

import com.ombda.mathx.errors.Unused;

public class Operators{
	/**
	 * Valid operator overrides
	 */
	public static final String
		plus = "plus", rplus = "rplus", 
		minus = "minus", rminus = "rminus",
		times = "times", rtimes = "rtimes",
		divide = "divide", rdivide = "rdivide",
		modulus = "modulus", rmodulus = "rmodulus",
		pow = "pow", rpow = "rpow",
		negate = "negate", positate = "abs",
		newinstance = "new", cast = "cast", call = "call",
		index = "index", indexAssign = "indexAssign",
		slice = "slice", sliceAssign = "sliceAssign",
		equals = "equals", compareTo = "compareTo", in = "contains";
	
	/**
	 * just for documentation purposes
	 */
	public static final String
		not = "not",
		notequals = "notEquals", sameInstance = "doubleEquals", notSameInstance = "notDoubleEquals",
		notin = "notIn",
		instanceOf = "instanceOf", notInstanceOf = "notInstanceOf",
		assign = "assign",
		plusAssign = "plusAssign", minusAssign = "minusAssign",
		timesAssign = "timesAssign", divideAssign = "divideAssign",
		modulusAssign = "modulusAssign", powAssign = "powAssign",
		/* unimplemented */ negateAssign = "negateAssign", /* unimplemented */ positateAssign = "positateAssign",
		/* unimplemented */ notAssign = "notAssign", 
		and = "and", or = "or", 
		leftShift = "leftShift", rightShift = "rightShift", unsignedRightShift = "unsignedRightShift",
		bitwiseAnd = "bitAnd", bitwiseOr = "bitOr", xor = "xor",
		/* unimplemented */ andAssign = "andAssign", /* unimplemented */ orAssign = "orAssign",
		/* unimplemented */ bitwiseAndAssign = "bitAndAssign", /* unimplemented */ bitwiseOrAssign = "bitOrAssign", /* unimplemented */ xorAssign = "xorAssign",
		/* unimplemented */ leftShiftAssign = "leftShiftAssign", /* unimplemented */ rightShiftAssign = "rightShiftAssign", /* unimplemented */ unsignedRightShiftAssign = "unsignedRightShiftAssign",
		memberAccess = "dispatch", memberAccessAssign = "dispatchAssign", memberAccessCallAssign = "dispatchCallAssign",
		nullCatchMemberAccess = "dispatchNullCatch", /* unimplemented */ nullCatchMemberAccessAssign = "dispatchNullCatchAssign",
		/* unimplemented */ multipleEquality = "multipleEquality", /* unimplemented */ multipleEqualityAnd = "multipleEqualityAnd",
		/* unimplemented */ multipleInequality = "multipleNotEquality", /* unimplemented */ multipleInequalityOr = "multipleNotEqualityOr",
		of = "of", decrement = "decrement", increment = "increment", extends_ = "extends";
	
	public static String getSymbol(String op){
		switch(op){
		case plus:
		case rplus: 
		case positate:
			return "+";
		case minus:
		case rminus:
		case negate:
			return "-";
		case times:
		case rtimes:
			return "*";
		case divide:
		case rdivide:
			return "/";
		case modulus:
		case rmodulus:
			return "%";
		case pow:
		case rpow:
			return "^";
		case newinstance:
			return "new";
		case cast:
			return "T()";
		case call:
			return "()";
		case index:
			return "[]";
		case indexAssign:
			return "[]=";
		case slice:
			return "[,]";
		case sliceAssign:
			return "[,]=";
		case equals:
			return "==";
		case compareTo:
			return "<>";
		case in:
			return "in";
		//documentation operator section
		case not:
			return "!";
		case notequals:
			return "!=";
		case sameInstance:
			return "===";
		case notSameInstance:
			return "!==";
		case notin:
			return "!in";
		case instanceOf:
			return "instanceof";
		case notInstanceOf:
			return "!instanceof";
		case assign:
			return "=";
		case plusAssign:
			return "+=";
		case minusAssign:
			return "-=";
		case timesAssign:
			return "*=";
		case divideAssign:
			return "/=";
		case modulusAssign:
			return "%=";
		case powAssign:
			return "^=";
		case negateAssign:
			return "--=";
		case positateAssign:
			return "++=";
		case notAssign:
			return "!!=";
		case and:
			return "&&";
		case or:
			return "||";
		case leftShift:
			return "<<";
		case rightShift:
			return ">>";
		case unsignedRightShift:
			return ">>>";
		case bitwiseAnd:
			return "&";
		case bitwiseOr:
			return "|";
		case xor:
			return "^^";
		case andAssign:
			return "&&=";
		case orAssign:
			return "||=";
		case bitwiseAndAssign:
			return "&=";
		case bitwiseOrAssign:
			return "|=";
		case xorAssign:
			return "^^=";
		case leftShiftAssign:
			return "<<=";
		case rightShiftAssign:
			return ">>=";
		case unsignedRightShiftAssign:
			return ">>>=";
		case memberAccess:
			return ".";
		case memberAccessAssign:
			return ". =";
		case memberAccessCallAssign:
			return ".=";
		case nullCatchMemberAccess:
			return "?.";
		case multipleEquality:
			return ".==";
		case multipleEqualityAnd:
			return ".==&";
		case multipleInequality:
			return ".!=";
		case multipleInequalityOr:
			return ".!=|";
		case of:
			return "of";
		case decrement:
			return "--";
		case increment:
			return "++";
		default:
			return op;
		}
	}
}
