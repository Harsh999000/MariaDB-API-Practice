package com.mariadb.mariadb.controller;

import org.springframework.web.bind.annotation.RestController;

import com.mariadb.mariadb.services.EstablishDBConnection;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class EstablishDBConnectionController {
    private final EstablishDBConnection establishDBConnection;

    public EstablishDBConnectionController(EstablishDBConnection establishDBConnection) {
        this.establishDBConnection = establishDBConnection;
    }

    @GetMapping("/connect-db1")
    public String establishConnection() {
        return establishDBConnection.getConnection() != null ? "Connection is live" : "Connection failed";
    }

}
