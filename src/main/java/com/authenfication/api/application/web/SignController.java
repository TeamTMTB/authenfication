package com.authenfication.api.application.web;

import com.authenfication.api.application.config.security.JwtTokenProvider;
import com.authenfication.api.application.domain.Account;
import com.authenfication.api.application.domain.service.logic.social.KakaoService;
import com.authenfication.api.application.exception.CEmailSigninFailedException;
import com.authenfication.api.application.exception.CUserExistException;
import com.authenfication.api.application.exception.CUserNotFoundException;
import com.authenfication.api.application.repository.AccountRepository;
import com.authenfication.api.application.web.dto.KakaoProfile;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping(value="/v1")
public class SignController {
    private final AccountRepository accountRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final KakaoService kakaoService;


    @ApiOperation(value = "로그인", notes = "이메일 회원 로그인을 한다.")
    @PostMapping(value = "/signin")
    public String signin(@ApiParam(value = "회원ID : 이메일", required = true) @RequestParam String id,
                         @ApiParam(value = "비밀번호", required = true) @RequestParam String password) {

        Account acocunt = accountRepository.findByAccountId(id).orElseThrow(CEmailSigninFailedException::new);
        if (!passwordEncoder.matches(password, acocunt.getPassword()))
            throw new CEmailSigninFailedException();

        return jwtTokenProvider.createToken(String.valueOf(acocunt.getMsrl()), acocunt.getRoles());

    }

    @ApiOperation(value = "가입", notes = "회원가입을 한다.")
    @PostMapping(value = "/signup")
    public String signup(@ApiParam(value = "회원ID : 이메일", required = true) @RequestParam String accountId,
                         @ApiParam(value = "비밀번호", required = true) @RequestParam String password,
                         @ApiParam(value = "이메일", required = true) @RequestParam String email,
                         @ApiParam(value = "이름", required = true) @RequestParam String name) {

        accountRepository.save(Account.builder()
                .accountId(accountId)
                .password(passwordEncoder.encode(password))
                .name(name)
                .email(email)
                .roles(Collections.singletonList("ROLE_USER"))
                .build());


        return "200";
    }

    @ApiOperation(value = "소셜 로그인", notes = "소셜 회원 로그인을 한다.")
    @PostMapping(value = "/signin/{provider}")
    public String signinByProvider(
            @ApiParam(value = "서비스 제공자 provider", required = true, defaultValue = "kakao")
            @PathVariable String provider,
            @ApiParam(value = "소셜 access_token", required = true)
            @RequestParam String accessToken) throws Throwable {

        KakaoProfile profile = kakaoService.getKakaoProfile(accessToken);

        Account account = accountRepository.findByAccountIdAndProvider(String.valueOf(profile.getId()), provider).orElseThrow(CUserNotFoundException::new);
        return jwtTokenProvider.createToken(String.valueOf(account.getMsrl()), account.getRoles());
    }

    @ApiOperation(value = "소셜 계정 가입", notes = "소셜 계정 회원가입을 한다.")
    @PostMapping(value = "/signup/{provider}")
    public String signupProvider(@ApiParam(value = "서비스 제공자 provider", required = true, defaultValue = "kakao") @PathVariable String provider,
                                 @ApiParam(value = "소셜 access_token", required = true) @RequestParam String accessToken,
                                 @ApiParam(value = "이름", required = true) @RequestParam String name) {


        KakaoProfile profile = kakaoService.getKakaoProfile(accessToken);
        System.out.println(profile.getNickname());


        Optional<Account> account = accountRepository.findByAccountIdAndProvider(String.valueOf(profile.getId()), provider);
        if(account.isPresent())
            throw new CUserExistException();

        accountRepository.save(Account.builder()
                .accountId(String.valueOf(profile.getId()))
                .provider(provider)
                .name(name)
                .email(profile.getId()+"@naver.com")
                .roles(Collections.singletonList("ROLE_USER"))
                .build());

        return "200";
    }
}
