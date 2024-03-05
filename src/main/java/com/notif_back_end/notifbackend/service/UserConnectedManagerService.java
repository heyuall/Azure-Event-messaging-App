package com.notif_back_end.notifbackend.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserConnectedManagerService {
    private Set<String> connectedUserIds = new HashSet<>();

    public Set<String> getConnectedUserIds() {
        return connectedUserIds;
    }

    public void setConnectedUserIds(Set<String> connectedUserIds) {
        this.connectedUserIds = connectedUserIds;
    }

    public Set<String> connectUser(String id) {
        this.connectedUserIds.add(id);
        return this.connectedUserIds;
    }

    public Set<String> deconnectUser(String id) {
        this.connectedUserIds.remove(id);
        return this.connectedUserIds;
    }

    public boolean isConnectedUser(String id){
        return this.connectedUserIds.contains(id);
    }
}
