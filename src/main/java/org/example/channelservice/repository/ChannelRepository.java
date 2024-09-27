package org.example.channelservice.repository;

import org.example.channelservice.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ChannelRepository extends JpaRepository<ChannelEntity, UUID> {
    Boolean existsByNickName(String name);

    Optional<ChannelEntity> findByNickNameOrName(String nickName, String name);

    List<ChannelEntity> findByOwnerId(UUID ownerId);


}