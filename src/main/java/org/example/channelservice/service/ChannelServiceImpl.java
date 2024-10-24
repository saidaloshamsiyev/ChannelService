package org.example.channelservice.service;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import metube.com.dto.request.UserNotificationRequest;
import org.example.channelservice.clients.UserServiceClient;
import org.example.channelservice.domain.dto.request.ChannelRequest;
import org.example.channelservice.domain.dto.request.ChannelUpdateRequest;
import org.example.channelservice.domain.dto.response.ChannelResponse;
import org.example.channelservice.domain.dto.response.UserResponse;
import org.example.channelservice.entity.ChannelEntity;
import org.example.channelservice.exception.BaseException;
import org.example.channelservice.kafka.ChannelProducer;
import org.example.channelservice.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

    private final ChannelProducer channelProducer;
    private final ChannelRepository channelRepository;
    private final UserServiceClient userServiceClient;

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;



    @Override
    public ChannelResponse save(ChannelRequest channelRequest, MultipartFile file) {
        UUID ownerId = channelRequest.getOwnerId();

        if (file == null || file.isEmpty()) {
            throw new BaseException("Uploaded file is empty", HttpStatus.BAD_REQUEST.value());
        }


        if (channelRepository.existsByNickName(channelRequest.getNickName())) {
            throw new BaseException("This nickName already exists", HttpStatus.CONFLICT.value());
        }


        UserResponse userResponse = userServiceClient.getUser(ownerId);
        if (userResponse == null) {
            throw new BaseException("User not found", HttpStatus.NOT_FOUND.value());
        }


        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();


        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        try {

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }


        ChannelEntity channelEntity = new ChannelEntity();
        channelEntity.setName(channelRequest.getName());
        channelEntity.setNickName(channelRequest.getNickName());
        channelEntity.setDescription(channelRequest.getDescription());
        channelEntity.setImagePath(fileName);
        channelEntity.setOwnerId(ownerId);
        channelRepository.save(channelEntity);


/*
        channelProducer.produce("channel",new UserNotificationRequest(channelEntity.getDescription(),"channel create"));
*/



        return ChannelResponse.builder()
                .name(channelEntity.getName())
                .nickName(channelEntity.getNickName())
                .description(channelEntity.getDescription())
                .imagePath("https://" + bucketName + "." + region + ".digitaloceanspaces.com/" + fileName)
                .ownerId(channelEntity.getOwnerId())
                .subscriberCount(channelEntity.getSubscriberCount())
                .build();
    }


    @Override
    public ChannelResponse findById(UUID id) {
        ChannelEntity channelEntity = channelRepository.findById(id).orElseThrow(() -> new BaseException("Channel not found",
                HttpStatus.NOT_FOUND.value()));
        return ChannelResponse.builder()
                .name(channelEntity.getName())
                .nickName(channelEntity.getNickName())
                .description(channelEntity.getDescription())
                .imagePath(channelEntity.getImagePath())
                .ownerId(channelEntity.getOwnerId())
                .subscriberCount(channelEntity.getSubscriberCount())
                .build();
    }

    @Override
    public void delete(UUID id) {
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
                        .subscriberCount(channel.getSubscriberCount())
                        .build())
                .toList();
    }

    @Override
    public List<ChannelResponse> findByNicknameOrName(String searchValue) {
        List<ChannelEntity> channelEntities = channelRepository
                .findAllByNickNameContainingIgnoreCaseOrNameContainingIgnoreCase(searchValue, searchValue);

        if (channelEntities.isEmpty()) {
            throw new BaseException("Channel with this name or nickname not found", HttpStatus.GONE.value());
        }
        return channelEntities.stream()
                .map(channelEntity -> ChannelResponse.builder()
                        .name(channelEntity.getName())
                        .nickName(channelEntity.getNickName())
                        .description(channelEntity.getDescription())
                        .imagePath(channelEntity.getImagePath())
                        .ownerId(channelEntity.getOwnerId())
                        .subscriberCount(channelEntity.getSubscriberCount())
                        .build())
                .toList();
    }


    @Override
    public List<ChannelResponse> findAllByOwnerId(UUID ownerId) {

        UserResponse userResponse = userServiceClient.getUser(ownerId);
        if (userResponse == null) {
            throw new BaseException("User not found", HttpStatus.NOT_FOUND.value());
        }

        List<ChannelEntity> channels = channelRepository.findByOwnerId(ownerId);
        return channels.stream().map(channel ->
                ChannelResponse.builder()
                        .name(channel.getName())
                        .nickName(channel.getNickName())
                        .description(channel.getDescription())
                        .imagePath(channel.getImagePath())
                        .ownerId(channel.getOwnerId())
                        .subscriberCount(channel.getSubscriberCount())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public ChannelResponse updateChannel(UUID channelId, ChannelUpdateRequest updateRequest) {
        ChannelEntity channelEntity = channelRepository.findById(channelId)
                .orElseThrow(() -> new BaseException("Channel not found", HttpStatus.NOT_FOUND.value()));

        UserResponse userResponse = userServiceClient.getUser(channelEntity.getOwnerId());
        if (userResponse == null) {
            throw new BaseException("User not found", HttpStatus.NOT_FOUND.value());
        }

        channelEntity.setName(updateRequest.getName());
        channelEntity.setDescription(updateRequest.getDescription());
        channelEntity.setNickName(updateRequest.getNickName());
        channelEntity.setImagePath(updateRequest.getImagePath());
        channelEntity.setOwnerId(channelEntity.getOwnerId());
        channelRepository.save(channelEntity);
        return ChannelResponse.builder()
                .name(channelEntity.getName())
                .nickName(channelEntity.getNickName())
                .description(channelEntity.getDescription())
                .imagePath(channelEntity.getImagePath())
                .ownerId(channelEntity.getOwnerId())
                .subscriberCount(channelEntity.getSubscriberCount())
                .build();
    }

    @Override
    public void incrementSubscribeCount(UUID channelId) {
        ChannelEntity channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new BaseException("Channel not found", HttpStatus.NOT_FOUND.value()));

        if (channel.getSubscriberCount() == null) {
            channel.setSubscriberCount(0);
        }
        channel.setSubscriberCount(channel.getSubscriberCount() + 1);
        channelRepository.save(channel);
    }

    @Override
    public void decrementSubscribeCount(UUID channelId) {
        ChannelEntity channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new BaseException("Channel not found", HttpStatus.NOT_FOUND.value()));

        if (channel.getSubscriberCount() == null) {
            channel.setSubscriberCount(0);
        }
        if (channel.getSubscriberCount() > 0) {
            channel.setSubscriberCount(channel.getSubscriberCount() - 1);
            channelRepository.save(channel);
        }

    }


    // this is line for all videos count in one channel's method


}
