package org.example.channelservice.service;
import lombok.RequiredArgsConstructor;
import org.example.channelservice.clients.UserServiceClient;
import org.example.channelservice.domain.dto.request.ChannelRequest;
import org.example.channelservice.domain.dto.request.ChannelUpdateRequest;
import org.example.channelservice.domain.dto.response.ChannelResponse;
import org.example.channelservice.domain.dto.response.UserResponse;
import org.example.channelservice.entity.ChannelEntity;
import org.example.channelservice.exception.BaseException;
import org.example.channelservice.repository.ChannelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {
    private final String UPLOAD_DIR = "C:\\metube\\ChannelService\\src\\main\\resources";

    private final   ChannelRepository channelRepository;
    private  final UserServiceClient userServiceClient;


    @Override
    public ChannelResponse save(ChannelRequest channelRequest, MultipartFile file) {
        UUID ownerId = channelRequest.getOwnerId();

        if (channelRepository.existsByNickName(channelRequest.getNickName())) {
            throw new BaseException("This nickName already exists", HttpStatus.CONFLICT.value());
        }

        UserResponse userResponse = userServiceClient.getUser(ownerId);

        if(userResponse == null) {
            throw new BaseException("User not found", HttpStatus.NOT_FOUND.value());
        }
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        String UPLOAD_DIR = "C:\\metube\\ChannelService\\src\\main\\resources";

        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image file", e);
        }

        ChannelEntity channelEntity = new ChannelEntity();
        channelEntity.setName(channelRequest.getName());
        channelEntity.setNickName(channelRequest.getNickName());
        channelEntity.setDescription(channelRequest.getDescription());
        channelEntity.setImagePath(filePath.toString());
        channelEntity.setOwnerId(ownerId);
        channelRepository.save(channelEntity);

        return ChannelResponse.builder().
                name(channelEntity.getName()).
                nickName(channelEntity.getNickName())
                .description(channelEntity.getDescription())
                .imagePath(channelEntity.getImagePath())
                .ownerId(channelEntity.getOwnerId())
                .build();

    }





    @Override
    public ChannelResponse findById(UUID id) {
        ChannelEntity channelEntity = channelRepository.findById(id).orElseThrow(() -> new BaseException("Channel not found",
                HttpStatus.NOT_FOUND.value()));
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
                        .name(channel.getName())
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
                .orElseThrow(() -> new BaseException("Channel with this name or nickname not found",
                        HttpStatus.GONE.value()));
        return ChannelResponse.builder()
                .name(channelEntity.getName())
                .nickName(channelEntity.getNickName())
                .description(channelEntity.getDescription())
                .imagePath(channelEntity.getImagePath())
                .ownerId(channelEntity.getOwnerId())
                .build();
    }


    @Override
    public List<ChannelResponse> findAllByOwnerId(UUID ownerId) {

        UserResponse userResponse = userServiceClient.getUser(ownerId);
        if(userResponse == null){
            throw new BaseException("User not found", HttpStatus.NOT_FOUND.value());
        }

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
                .orElseThrow(() -> new BaseException("Channel not found", HttpStatus.NOT_FOUND.value()));

        UserResponse userResponse = userServiceClient.getUser(channelEntity.getOwnerId());
        if(userResponse == null){
            throw new BaseException("User not found", HttpStatus.NOT_FOUND.value());
        }

        channelEntity.setName(updateRequest.getName());
        channelEntity.setDescription(updateRequest.getDescription());
        channelEntity.setNickName(updateRequest.getNickName());
        channelEntity.setImagePath(updateRequest.getImagePath());
        channelEntity.setOwnerId(channelEntity.getOwnerId());
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
