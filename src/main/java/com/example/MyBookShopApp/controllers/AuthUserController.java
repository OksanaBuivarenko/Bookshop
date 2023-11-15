package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.*;
import com.example.MyBookShopApp.dto.TransactionsDto;
import com.example.MyBookShopApp.dto.UserDto;
import com.example.MyBookShopApp.security.*;
import com.example.MyBookShopApp.services.*;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Controller
public class AuthUserController {
    private final BookstoreUserRegister userRegister;
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final SmsService smsService;
    private final EmailLinkService emailLinkService;
    private final JavaMailSender javaMailSender;
    private final PaymentService paymentService;
    private final PostponedService postponedService;
    private final CartService cartService;

    @ModelAttribute("profile")
    public UserDto userDto() {
        return new UserDto();
    }

    @GetMapping("/signin")
    public String handleSignIn(Model model, @AuthenticationPrincipal OAuth2User principal,
                               @CookieValue(name = "postponedContents", required = false) String postponedContents,
                               @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        return "signin";
    }

    @GetMapping("/signup")
    public String handleSignUp(Model model, @AuthenticationPrincipal OAuth2User principal,
                               @CookieValue(name = "postponedContents", required = false) String postponedContents,
                               @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        model.addAttribute("regForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestContactConfirmation(@RequestBody ContactConfirmationPayload
                                                                                    contactConfirmationPayload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    @PostMapping("/requestEmailConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestEmailConfirmation(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bookstore.sms.service@gmail.com");
        message.setTo(payload.getContact());
        SmsCode smsCode = new SmsCode(smsService.generateCode(), 300); //5 minutes
        smsService.saveNewCode(smsCode);
        message.setSubject("Bookstore email verification!");
        message.setText("Verification code is: " + smsCode.getCode());
        javaMailSender.send(message);
        response.setResult("true");
        return response;
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContact(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    @PostMapping("/reg")
    public String handleUserRegistration(RegistrationForm registrationForm, Model model) {
        if (bookstoreUserDetailsService.findUserByEmail(registrationForm.getEmail())) {
            model.addAttribute("errorMessage", "User with that email already used, try again with other email");
        } else {
            userRegister.registerNewUser(registrationForm);
            model.addAttribute("regOk", true);
        }
        return "signin";
    }

    @PostMapping("/login")
    @ResponseBody
    public ContactConfirmationResponse handleLogin(@RequestBody ContactConfirmationPayload payload,
                                                   HttpServletResponse httpServletResponse) {
        ContactConfirmationResponse loginResponse = userRegister.jwtLogin(payload);
        Cookie cookie = new Cookie("token", loginResponse.getResult());
        httpServletResponse.addCookie(cookie);
        return loginResponse;
    }

    @ModelAttribute("transactions")
    public List<Transactions> transactions() {
        return new ArrayList<>();
    }

    @GetMapping("/profile")
    public String handleProfile(Model model, @AuthenticationPrincipal OAuth2User principal,
                                @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        BookstoreUser currentUser = userRegister.getCurrentUser(principal);
        model.addAttribute("curUsr", currentUser);
        model.addAttribute("phone", userRegister.getPhone(currentUser));
        model.addAttribute("email", userRegister.getEmail(currentUser));
        model.addAttribute("transactions",
                paymentService.getTransactionsList(currentUser, 0, 50, "asc").getContent());
        return "profile";
    }

    @PostMapping("/profile_changed")
    public String sendEmailLink(@ModelAttribute UserDto userDto,
                                Model model, @AuthenticationPrincipal OAuth2User principal) {
        BookstoreUser currentUser = userRegister.getCurrentUser(principal);
        model.addAttribute("curUsr", currentUser);
        if (!userRegister.correctPassword(userDto)) {
            model.addAttribute("passNoEquals", "Пароли не совпадают или " +
                    "длинна пароля не равна 6 символам!");
        }
        if (userDto != null && userRegister.correctPassword(userDto)) {
            currentUser.setPasswordReply(userDto.getPasswordReply());
            currentUser.setNewName(userDto.getName());
            currentUser.setNewEmail(userDto.getEmail());
            currentUser.setNewPhone(userDto.getPhone());
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("bookstore.sms.service@gmail.com");
            String email = "";
            for (UserContact contact : currentUser.getContacts()) {
                if (contact.getType() == EnumContactType.EMAIL) {
                    email = contact.getContact();
                }
            }
            message.setTo(email);
            EmailLink emailLink = new EmailLink(emailLinkService.generateLink(currentUser.getName()), 300); //5 minutes
            emailLinkService.saveNewLink(emailLink);
            message.setSubject("Changing credentials!");
            message.setText("Link to change credentials: " + emailLink.getLink());
            javaMailSender.send(message);
            model.addAttribute("sendLink", "На вашу электронную почту направлена ссылка, " +
                    "перейдите по ней для подтверждения изменений в учетных данных!");
        }
        return "profile";
    }

    @GetMapping("/profile_changed/{link}")
    public String profileChanged(Model model, @AuthenticationPrincipal OAuth2User principal,
                                 @PathVariable(value = "link", required = false) String link,
                                 @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                 @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        BookstoreUser currentUser = userRegister.getCurrentUser(principal);
        model.addAttribute("curUsr", currentUser);
        String allLink = "http://localhost:8085/profile_changed/" + link;
        if (emailLinkService.verifyLink(allLink)) {
            userRegister.changedUser(currentUser, model);
            model.addAttribute("saveProfile", "Profile saved successfully!");
        } else {
            model.addAttribute("saveProfile", "Link is incorrect!");
        }
        return "profile";
    }

    @PostMapping("/payment")
    public String payment(@RequestParam("user") String hash, @RequestParam("sum") String sum, Model model,
                          @AuthenticationPrincipal OAuth2User principal) throws NoSuchAlgorithmException {
        BookstoreUser currentUser = userRegister.getCurrentUser(principal);
        model.addAttribute("curUsr", currentUser);
        paymentService.enrollmentTransaction(Integer.parseInt(sum), currentUser);
        paymentService.balanceEnrollment(Integer.parseInt(sum), currentUser);
        return "profile";
    }

    @GetMapping("/transactions")
    @ResponseBody
    public TransactionsDto getBalanceTransactionsMore(@RequestParam("offset") Integer offset,
                                                      @RequestParam("limit") Integer limit,
                                                      @RequestParam("sort") String sort,
                                                      @AuthenticationPrincipal OAuth2User principal, Model model) {
        BookstoreUser currentUser = userRegister.getCurrentUser(principal);
        model.addAttribute("curUsr", currentUser);
        return new TransactionsDto(paymentService.getTransactionsList(currentUser, offset, limit, sort).getContent());
    }
}
