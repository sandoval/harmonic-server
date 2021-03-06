package br.unb.tr2.harmonic.httpServer;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public enum Role {
    ADMIN("Admin"),
    USER("User");

    private String label;

    private Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
