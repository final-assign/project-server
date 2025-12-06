package org.example.general;

import java.util.HashSet;

public class Session {

    final private HashSet<Long> sessionList;

    public Session(){

        sessionList = new HashSet<>();
    }

    public synchronized boolean addSession(Long id){

        if(sessionList.contains(id)) return false;
        sessionList.add(id);

        return true;
    }

    public synchronized void removeSession(Long id) {

        sessionList.remove(id);
    }


}
