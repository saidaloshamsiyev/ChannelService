package org.example.channelservice.controller;
import lombok.RequiredArgsConstructor;
import org.example.channelservice.domain.dto.request.ChannelRequest;
import org.example.channelservice.domain.dto.request.ChannelUpdateRequest;
import org.example.channelservice.domain.dto.response.ChannelResponse;
import org.example.channelservice.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/channel")
@RequiredArgsConstructor()
public class ChannelController {


    private final   ChannelService channelService;

    @PostMapping("/create")
    public ResponseEntity<ChannelResponse> createChannel(@RequestBody ChannelRequest channelRequest,
                                                         @RequestPart("imageFile") MultipartFile imageFile) {
        ChannelResponse savedChannel = channelService.save(channelRequest, imageFile);
        return new ResponseEntity<>(savedChannel, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChannelResponse> getChannelById(@PathVariable UUID id) {
        ChannelResponse channel = channelService.findById(id);
        return ResponseEntity.ok(channel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChannelResponse> updateChannel(@PathVariable UUID id, @RequestBody ChannelUpdateRequest updateRequest) {
        ChannelResponse updatedChannel = channelService.updateChannel(id, updateRequest);
        return ResponseEntity.ok(updatedChannel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID id) {
        channelService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<ChannelResponse>> getAllChannels() {
        List<ChannelResponse> channels = channelService.findAll();
        return ResponseEntity.ok(channels);
    }

    @GetMapping("/search")
    public ResponseEntity<ChannelResponse> getChannelByNicknameOrName(@RequestParam String searchValue) {
        ChannelResponse channel = channelService.findByNicknameOrName(searchValue);
        return ResponseEntity.ok(channel);
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

