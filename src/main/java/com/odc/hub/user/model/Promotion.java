package com.odc.hub.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "promotions")
public class Promotion {

    @Id
    private String id;

    private String name;
    private Instant startDate;
    private Instant endDate;
}
