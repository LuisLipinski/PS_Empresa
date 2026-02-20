package com.mypetadmin.ps_empresa.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {
    @GetMapping("/version")
    public String version() {
        return System.getenv("RENDER_GIT_COMMIT");
    }
}
