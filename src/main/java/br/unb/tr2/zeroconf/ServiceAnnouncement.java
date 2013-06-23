package br.unb.tr2.zeroconf;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class ServiceAnnouncement implements Serializable {

    private static final long serialVersionUID = -628964790327472026L;

    private String service;

    private Long port;

    private InetAddress address;

    public ServiceAnnouncement() {
    }

    public ServiceAnnouncement(ServiceAnnouncement serviceAnnouncement) {
        this.service = serviceAnnouncement.getService();
        this.port = serviceAnnouncement.getPort();
        this.address = serviceAnnouncement.getAddress();
    }

    public ServiceAnnouncement(String service, Long port, InetAddress address) {
        this.service = service;
        this.port = port;
        this.address = address;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Long getPort() {
        return port;
    }

    public void setPort(Long port) {
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceAnnouncement that = (ServiceAnnouncement) o;

        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (port != null ? !port.equals(that.port) : that.port != null) return false;
        if (service != null ? !service.equals(that.service) : that.service != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = service != null ? service.hashCode() : 0;
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }
}
