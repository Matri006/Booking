package com.mary.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    private String description;

    private Boolean hasProjector = false;

    private Boolean hasWhiteboard = false;

    private String location;

    private Boolean isActive = true;

    @OneToMany(mappedBy = "room")
    private List<Booking> bookings;
}
