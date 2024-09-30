package org.example.channelservice.clients;
import metube.com.dto.response.UserResponse;
import org.example.channelservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@Service
@FeignClient(name = "AUTH-SERVICE",configuration = FeignConfig.class)
public interface UserServiceClient {
    @GetMapping("/api/user/{id}")
    UserResponse findById(@PathVariable("id") UUID id);

}
