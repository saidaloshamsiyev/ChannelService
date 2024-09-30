package org.example.channelservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.objenesis.instantiator.util.UnsafeUtils;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "subscriptions")
public class SubscriptionEntity extends BaseEntity {
    private UUID id;
    private UUID subscriberId;
    private UUID channelId;
}
