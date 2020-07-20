/*
 * Copyright (C) GSX Techedu Inc. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.jenkinsDocker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author bjhl
 * @description
 * @team wuhan operational dev.
 * @date 2020/7/20 11:30 上午
 **/
@RestController
public class IndexController {

    @GetMapping("/hello")
    public String hello() {
        return "say hello to you ";
    }

}
