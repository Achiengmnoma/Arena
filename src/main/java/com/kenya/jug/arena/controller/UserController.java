package com.kenya.jug.arena.controller;
/*
 * MIT License
 *
 * Copyright (c) 2025 Kenya Java User Group
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import com.kenya.jug.arena.exception.AlreadyExistsException;
import com.kenya.jug.arena.io.UserRequest;
import com.kenya.jug.arena.io.UserResponse;
import com.kenya.jug.arena.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        var user = new UserRequest("","","","");
        model.addAttribute("user", user);
        return "register-page";
    }

    @PostMapping("/register")
    public String register(
            Model model,
            @Valid @ModelAttribute("user") UserRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", request);
            model.addAttribute(
                    "error",
                    "Registration failed. Please check highlighted fields.");
            return "register-page";
        }
        try {
            userService.createUser(request);
            return "redirect:/login";
        } catch (AlreadyExistsException e) {
            model.addAttribute("error", e.getMessage());
            return "register-page";
        }
    }

    @GetMapping("/user")
    public UserResponse getUser(@CurrentSecurityContext(expression = "authentication?.name") String email) {
        return userService.getUser(email);
    }
}
