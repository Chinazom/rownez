package com.scenic.rownezcoreservice.service.message;

public interface MessageServiceInterface {
     boolean sendMessage ();
     boolean sendMessage (String messageJson);
     void readMessage();
}
