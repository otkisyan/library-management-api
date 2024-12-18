package org.example.reviewservice.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("book-service")
public interface BookClient {

    @RequestMapping(method = RequestMethod.GET, value = "/books/{bookId}/exists")
    Boolean bookExistsById(@PathVariable Long bookId);
}
