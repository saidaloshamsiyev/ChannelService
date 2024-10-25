package org.example.channelservice.controller;

import jakarta.servlet.annotation.MultipartConfig;
import lombok.RequiredArgsConstructor;
import org.example.channelservice.domain.dto.request.ChannelRequest;
import org.example.channelservice.domain.dto.request.ChannelUpdateRequest;
import org.example.channelservice.domain.dto.response.ChannelResponse;
import org.example.channelservice.service.ChannelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channel")
@RequiredArgsConstructor
@MultipartConfig(maxFileSize = 10 * 1024 * 1024,
maxRequestSize = 50 * 1024 * 1024,
fileSizeThreshold = 10 * 1024)
public class ChannelController {

    private final ChannelService channelService;
    @PostMapping(value = "/create")
    public ResponseEntity<ChannelResponse> createChannel(
            @RequestPart("imageFile") MultipartFile imageFile,
            @RequestPart("jsonData") ChannelRequest channelRequest) {

        ChannelResponse savedChannel = channelService.save(channelRequest, imageFile);
        return new ResponseEntity<>(savedChannel, HttpStatus.CREATED);
    }



    @GetMapping("/{id}")
    public ResponseEntity<ChannelResponse> getChannelById(@PathVariable UUID id) {
        ChannelResponse channel = channelService.findById(id);
        return ResponseEntity.ok(channel);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ChannelResponse> updateChannel(@PathVariable UUID id, @RequestBody ChannelUpdateRequest updateRequest) {
        ChannelResponse updatedChannel = channelService.updateChannel(id, updateRequest);
        return ResponseEntity.ok(updatedChannel);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID id) {
        channelService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ChannelResponse>> getAllChannels() {
        List<ChannelResponse> channels = channelService.findAll();
        return ResponseEntity.ok(channels);
    }
    @GetMapping("/search")
    public ResponseEntity<List<ChannelResponse>> getChannelsByNicknameOrName(@RequestParam("search") String searchValue) {
        List<ChannelResponse> channels = channelService.findByNicknameOrName(searchValue);
        return ResponseEntity.ok(channels);
    }


    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ChannelResponse>> getAllChannelsByOwnerId(@PathVariable UUID ownerId) {
        List<ChannelResponse> channels = channelService.findAllByOwnerId(ownerId);
        return ResponseEntity.ok(channels);
    }



//    @GetMapping("/{channelId}/videos/count")
//    public ResponseEntity<Long> getVideoCountByChannel(@PathVariable UUID channelId) {
//        long videoCount = channelService.countVideosByChannelId(channelId);
//        return ResponseEntity.ok(videoCount);
//    }
}

