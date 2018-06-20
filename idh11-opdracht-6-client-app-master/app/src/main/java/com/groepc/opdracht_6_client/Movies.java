package com.groepc.opdracht_6_client;

import java.io.Serializable;

/**
 * Created by perryfaro on 16-04-16.
 */
public class Movies implements Serializable {

    public String name;
    public String ip;
    public Integer port;
    public String movie;

    public Movies(String name, String ip, Integer port, String movie) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.movie = movie;
    }
}
