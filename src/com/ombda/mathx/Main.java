package com.ombda.mathx;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Main{
	public static void main(String[] args) throws FileNotFoundException{
		File f = new File("prog.mx");
		
		Program program = Compiler.compile(f);
		
		
		program.run();
		/**/
		/*
		System.out.println(Arrays.toString("a,b,c".split(",")));
		/**/
		/*Scanner scan = new Scanner(System.in);
		boolean flag = true;
		while(flag){
			String line = scan.nextLine();
			if(line.equals("STOP"))
				flag = false;
			else
				System.out.println(line.matches("[a-zA-Z_][a-zA-Z_0-9]*\\([a-zA-Z_][a-zA-Z_0-9]*(,[\\w_][a-zA-Z_0-9]*)*\\)"));
		}
		
		scan.close();/**/
	}
}
