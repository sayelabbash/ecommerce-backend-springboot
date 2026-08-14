package com.sayel.E_Commerce.exception;

public class TokenExpiredException extends RuntimeException{
   public TokenExpiredException(String message){
       super(message);
   }
}
