package org.example.channelservice.service;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.example.channelservice.domain.dto.request.ChannelRequest;
import org.example.channelservice.domain.dto.request.ChannelUpdateRequest;
import org.example.channelservice.domain.dto.response.ChannelResponse;
import org.example.channelservice.entity.ChannelEntity;
import org.example.channelservice.exception.BaseException;
import org.example.channelservice.repository.ChannelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepository channelRepository;

    @Override
    public ChannelResponse save(ChannelRequest channelRequest) {
        if (channelRepository.existsByNickName(channelRequest.getNickName())) {
            throw new BaseException("This nickName already exists", HttpStatus.SC_CONFLICT);
        }
        ChannelEntity channelEntity = new ChannelEntity();
        channelEntity.setNickName(channelRequest.getNickName());
        channelEntity.setDescription(channelRequest.getDescription());
        channelEntity.setImagePath(channelRequest.getImagePath());
        channelEntity.setOwnerId(channelRequest.getOwnerId());
        channelRepository.save(channelEntity);

        return ChannelResponse.builder().
                nickName(channelEntity.getNickName())
                .description(channelEntity.getDescription())
                .imagePath(channelEntity.getImagePath())
                .ownerId(channelEntity.getOwnerId())
                .build();
    }

    @Override
    public ChannelResponse findById(UUID id) {
        ChannelEntity channelEntity = channelRepository.findById(id).orElseThrow(() -> new BaseException("Channel not found",
                HttpStatus.SC_NOT_FOUND));
        return ChannelResponse.builder()
                .nickName(channelEntity.getNickName())
                .description(channelEntity.getDescription())
                .imagePath(channelEntity.getImagePath())
                .ownerId(channelEntity.getOwnerId())
                .build();
    }

    @Override
    public void delete(UUID id) {
        ChannelResponse response = findById(id);
        channelRepository.deleteById(id);
    }

    @Override
    public List<ChannelResponse> findAll() {
        return channelRepository.findAll().stream().map(channel -> ChannelResponse.builder()
                        .nickName(channel.getNickName())
                        .description(channel.getDescription())
                        .imagePath(channel.getImagePath())
                        .ownerId(channel.getOwnerId())
                        .build())
                .toList();
    }

    @Override
    public ChannelResponse findByNicknameOrName(String searchValue) {
        ChannelEntity channelEntity = channelRepository.findByNickNameOrName(searchValue, searchValue)
                .orElseThrow(() -> new BaseException("Channel with this name or nickname not found", HttpStatus.SC_NOT_FOUND));
        return ChannelResponse.builder()
                .nickName(channelEntity.getNickName())
                .description(channelEntity.getDescription())
                .imagePath(channelEntity.getImagePath())
                .ownerId(channelEntity.getOwnerId())
                .build();
    }

    @Override
    public List<ChannelResponse> findAllByOwnerId(UUID ownerId) {
        List<ChannelEntity> channels = channelRepository.findByOwnerId(ownerId);
        return channels.stream().map(channel ->
                ChannelResponse.builder()
                        .nickName(channel.getNickName())
                        .description(channel.getDescription())
                        .imagePath(channel.getImagePath())
                        .ownerId(channel.getOwnerId())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public ChannelResponse updateChannel(UUID channelId, ChannelUpdateRequest updateRequest) {
        ChannelEntity channelEntity = channelRepository.findById(channelId)
                .orElseThrow(() -> new BaseException("Channel not found", HttpStatus.SC_NOT_FOUND));
        channelEntity.setName(updateRequest.getName());
        channelEntity.setDescription(updateRequest.getDescription());
        channelEntity.setNickName(updateRequest.getNickName());
        channelEntity.setImagePath(updateRequest.getImagePath());
        channelRepository.save(channelEntity);
        return ChannelResponse.builder()
                .nickName(channelEntity.getNickName())
                .description(channelEntity.getDescription())
                .imagePath(channelEntity.getImagePath())
                .ownerId(channelEntity.getOwnerId())
                .build();
    }

    // this is line for all videos count in one channel's method





}
