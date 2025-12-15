package org.pkwmtt.utils;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "utils_kv")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UtilsProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "property_key", nullable = false, unique = true, length = 191)
    private String key;

    @Column(name = "property_value", columnDefinition = "VARCHAR(250)")
    private String value;

    @Column(name = "value_type")
    private String type;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public UtilsProperty(String key, String value, String type) {
        this.key = key;
        this.value = value;
        this.type = type;
        this.updatedAt = Instant.now();
    }
}

