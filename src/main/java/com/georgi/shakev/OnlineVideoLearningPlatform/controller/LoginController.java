package com.georgi.shakev.OnlineVideoLearningPlatform.controller;

import com.georgi.shakev.OnlineVideoLearningPlatform.dto.LessonRequestDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.dto.UserRequestDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.dto.UserResponseDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.exception.ResourceNotFoundException;
import com.georgi.shakev.OnlineVideoLearningPlatform.service.LessonService;
import com.georgi.shakev.OnlineVideoLearningPlatform.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@Slf4j
public class LoginController {
    private final UserService userService;
    private final LessonService lessonService;

    @Autowired
    public LoginController(UserService userService, LessonService lessonService) {
        this.userService = userService;
        this.lessonService = lessonService;
    }

    @GetMapping
    public String redirectToHomePage() {
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String homePage(Model model,
                           HttpServletRequest request,
                           @RequestParam(value = "search", defaultValue = "")
                           String search,
                           @AuthenticationPrincipal User principal) {
        String sortBy = "id";
        int page = 0;
        int size = 3;

        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page"));
        }
        int allPages = lessonService.getAllLessons(page, size, sortBy, search).getTotalPages();
        if (page < 0 || page > allPages) {
            throw new ResourceNotFoundException("Invalid page number.");
        }

        LessonRequestDto lessonRequest = new LessonRequestDto();
        model.addAttribute("username", principal.getUsername());
        model.addAttribute("lessonRequest", lessonRequest);
        model.addAttribute("search", search);
        model.addAttribute("lessons",
                lessonService.getAllLessons(page, size, sortBy, search).getContent());
        model.addAttribute("page", page + 1);
        model.addAttribute("allPagesNumber", allPages);
        return "index";
    }

    @ModelAttribute("user")
    public UserRequestDto userRegistrationDto() {
        return new UserRequestDto();
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user")
                                      @Valid UserRequestDto registrationDto,
                                      BindingResult binding,
                                      RedirectAttributes redirectAttr) {
        if (binding.hasErrors()) {
            redirectAttr.addFlashAttribute("user", registrationDto);
            redirectAttr.addFlashAttribute("org.springframework.validation.BindingResult.project", binding);
            return "redirect:/register?error";
        }

        UserResponseDto newUser = userService.createUser(registrationDto);
        return "redirect:/register?success";
    }
}
