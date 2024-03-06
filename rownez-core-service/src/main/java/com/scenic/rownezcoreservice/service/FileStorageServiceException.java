package com.scenic.rownezcoreservice.service;

public class FileStorageServiceException extends Exception{
    public FileStorageServiceException (String message){
        super(message);
    }
    public FileStorageServiceException (String message, Throwable cause){
        super(message,cause);
    }
}
