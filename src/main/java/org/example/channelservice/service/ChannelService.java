package org.example.channelservice.service;

import org.example.channelservice.domain.dto.request.ChannelRequest;
import org.example.channelservice.domain.dto.request.ChannelUpdateRequest;
import org.example.channelservice.domain.dto.response.ChannelResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse save(ChannelRequest channelRequest, MultipartFile file);


    ChannelResponse findById(UUID id);
    void delete(UUID id);
    List<ChannelResponse> findAll();
    ChannelResponse findByNicknameOrName(String name);
    List<ChannelResponse> findAllByOwnerId(UUID ownerId);
    ChannelResponse updateChannel(UUID channelId, ChannelUpdateRequest updateRequest);
}


