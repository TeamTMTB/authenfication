package com.authenfication.api.application.web;


import com.authenfication.api.application.domain.Account;
import com.authenfication.api.application.domain.service.logic.social.KakaoService;
import com.authenfication.api.application.exception.CCommunicationException;
import com.authenfication.api.application.exception.CUserExistException;
import com.authenfication.api.application.repository.AccountRepository;
import com.authenfication.api.application.web.dto.KakaoProfile;
import com.authenfication.api.application.web.dto.RetKakaoAuth;
import com.google.gson.Gson;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/social/login")
public class SocialController {

    private final Environment env;
    private final RestTemplate restTemplate;
    private final Gson gson;
    private final KakaoService kakaoService;
    private final AccountRepository accountRepository;

    @Value("${spring.url.base}")
    private String baseUrl;

    @Value("${spring.social.kakao.client_id}")
    private String kakaoClientId;

    @Value("${spring.social.kakao.redirect}")
    private String kakaoRedirect;

    /**
     * 카카오 로그인 페이지
     */
    @ApiOperation(value = "카카오 로그인 페이지")
    @GetMapping
    public ModelAndView socialLogin(ModelAndView mav) {

        StringBuilder loginUrl = new StringBuilder()
                .append(env.getProperty("spring.social.kakao.url.login"))
                .append("?client_id=").append(kakaoClientId)
                .append("&response_type=code")
                .append("&redirect_uri=").append(baseUrl).append(kakaoRedirect);

        mav.addObject("loginUrl", loginUrl);
        mav.setViewName("social/login");
        return mav;
    }

    /**
     * 카카오 인증 완료 후 리다이렉트 화면
     */
    @ApiOperation(value = "카카오 인증 환료 후 리다이렉트")
    @GetMapping(value = "/kakao")
    public RetKakaoAuth redirectKakao(ModelAndView mav, @RequestParam String code) {

        return kakaoService.getKakaoTokenInfo(code);
    }




    @ApiOperation(value = "카카오 동의를 URL", notes = "카카오 동의를 요구하는 url를 가져온다.")
    @GetMapping("/code")
    public String socialLoginUrl() {

        StringBuilder loginUrl = new StringBuilder()
                .append(env.getProperty("spring.social.kakao.url.login"))
                .append("?client_id=").append(kakaoClientId)
                .append("&response_type=code")
                .append("&redirect_uri=").append(baseUrl).append(kakaoRedirect);

        return loginUrl.toString();
    }

    @ApiOperation(value = "소셜 계정 가입", notes = "소셜 계정 회원가입을 한다.")
    @PostMapping(value = "/signup/{provider}")
    public String signupProvider(@ApiParam(value = "서비스 제공자 provider", required = true, defaultValue = "kakao") @PathVariable String provider,
                                       @ApiParam(value = "소셜 access_token", required = true) @RequestParam String accessToken,
                                       @ApiParam(value = "이름", required = true) @RequestParam String name,
                                        @ApiParam(value = "이메일", required = true) @RequestParam String email) {

        KakaoProfile profile = kakaoService.getKakaoProfile(accessToken);
        System.out.println(profile.getProperties());
        Optional<Account> account = accountRepository.findByAccountIdAndProvider(String.valueOf(profile.getId()), provider);
        if(account.isPresent())
            throw new CUserExistException();


        System.out.println(String.valueOf(profile.getProperties()));
        accountRepository.save(Account.builder()
                .accountId(String.valueOf(profile.getId()))
                .provider(provider)
                .name(name)
                .email(email)
                .roles(Collections.singletonList("ROLE_USER"))
                .build());

        return "200";
    }

    //연결끊기
    @ApiOperation(value = "소셜 계정 동의 취소", notes = "소셜 계정 회원가입 할 때 동의 했던 내용들을 초기화 한다.")
    @PostMapping(value = "/unlink")
    public String signupProvider(@ApiParam(value = "소셜 access_token", required = true) @RequestParam String accessToken) {

        kakaoService.unlinkKakaoAuth(accessToken);

        return "200";
    }

}

