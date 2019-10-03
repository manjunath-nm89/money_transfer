package com.org.moneytransfer.resources;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BaseTestResource {

    public String getResponseFromBuffer(InputStream is) throws IOException {

        StringBuilder sb = new StringBuilder();

        DataInputStream bufResponse = new DataInputStream(is);

        while(bufResponse.available() > 0) {
            sb.append((char)bufResponse.read());
        }
        return sb.toString();
    }

}
