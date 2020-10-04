/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
package com.sict.fsrmi.register;

import com.sict.fsrmi.register.entry.RegisterServer;

public class RegisterContainer {
    public static void main(String[] args) {
        new RegisterServer().start();
    }
}
