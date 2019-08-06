package com.bridgelabz.fundoo.exception;

public class UserNotFound extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	int code;
	String msg;
	 public UserNotFound(String msg)
	 {
		super(msg);
	 }
	 
	 public UserNotFound(int code, String msg)
	 {
		 super(msg);
		 this.code =code;
	 }


}
