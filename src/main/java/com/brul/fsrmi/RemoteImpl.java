package com.brul.fsrmi;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;

public class RemoteImpl extends UnicastRemoteObject implements Remote {
    Naming nUnicastRemoteObject = new Naming();

}
