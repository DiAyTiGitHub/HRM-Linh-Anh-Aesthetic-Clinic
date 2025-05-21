package com.globits.hr.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class ExpressionUtil {
	public static Double eval(String exp, Hashtable<String,Double> hash) {
		List<String> args = new ArrayList<>();
		Enumeration<String> keys=  hash.keys();
	    while(keys.hasMoreElements()) {
	    	String s = keys.nextElement();
	    	if(hash.get(s)!=null) {
	    		args.add(s);
	    	}
	    }
	    
		String[]arr = new String [args.size()];
		args.toArray(arr);
	    Expression expression = new ExpressionBuilder(exp)
	    	      .variables(arr)
	    	      .build();
	    keys=  hash.keys();
	    while(keys.hasMoreElements()) {
	    	String s = keys.nextElement();
	    	if(hash.get(s)!=null) {
	    		expression.setVariable(s, hash.get(s));	
	    	}
	    }
	    Double result = expression.evaluate();
	    return result;
	}
	public static void main(String[] args) {
		
		Hashtable<String,Double> hash = new Hashtable<>();
		hash.put("MUC_PHU_CAP_THUC_TAP_CO_BAN", 4200000D);
		hash.put("SO_GIO_THUC_TAP_TRONG_THANG", 160D);
		hash.put("TONG_SO_GIO_THUC_TAP_TRONG_THANG", 208D);
		
		Double result = ExpressionUtil.eval("MUC_PHU_CAP_THUC_TAP_CO_BAN*SO_GIO_THUC_TAP_TRONG_THANG/TONG_SO_GIO_THUC_TAP_TRONG_THANG",hash);
		System.out.println(result);
	}
}
