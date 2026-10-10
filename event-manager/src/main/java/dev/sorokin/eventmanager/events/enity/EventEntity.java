package dev.sorokin.eventmanager.events.enity;

import dev.sorokin.eventmanager.events.enums.EventStatus;
import dev.sorokin.eventmanager.location.entity.LocationEntity;
import dev.sorokin.eventmanager.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "event_name", nullable = false)
    private String name;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "max_places", nullable = false)
    private Integer maxPlaces;

    @Column(name = "occupied_places", nullable = false)
    private Integer occupiedPlaces;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status", nullable = false)
    private EventStatus eventStatus;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationEntity location;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity owner;


    @OneToMany(mappedBy = "event")
    private List<RegistrationEntity> registrations;
}
