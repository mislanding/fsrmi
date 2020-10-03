package com.sict.fsrmi.common;


import java.io.IOException;
import java.nio.channels.Selector;

/**
 * @author lyy
 */
public class RpcSelector{
    private volatile static Selector selector;
    public static Selector getSelector() throws IOException {
        if(selector==null){
            synchronized (Selector.class) {
                if(selector==null){
                    selector = Selector.open();
                }
            }
        }
        return selector;
    }
}
