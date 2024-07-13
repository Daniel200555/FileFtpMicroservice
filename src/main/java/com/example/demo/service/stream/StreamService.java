package com.example.demo.service.stream;

import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.zip.ZipOutputStream;

public sealed interface StreamService permits StreamServiceImpl {

    Mono<Resource> getVideo(String title);

    Mono<Resource> getPicture(String title);

    public Mono<Resource> getZip(String path);

}