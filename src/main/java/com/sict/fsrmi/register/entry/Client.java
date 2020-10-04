/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
package com.sict.fsrmi.register.entry;

import lombok.Data;


import java.util.Set;

@Data
public class Client {
    private String ip;
    private int port;
}
