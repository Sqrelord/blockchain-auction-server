package edu.dhu.auction.web.controller;

import edu.dhu.auction.web.bean.Account;
import edu.dhu.auction.web.bean.Address;
import edu.dhu.auction.web.bean.dto.AccountDTO;
import edu.dhu.auction.web.bean.vo.AccountVo;
import edu.dhu.auction.web.service.AccountService;
import edu.dhu.auction.web.util.BeanUtils;
import edu.dhu.auction.web.util.JwtUtils;
import edu.dhu.auction.web.util.ResultEntity;
import edu.dhu.auction.web.util.ResultStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class AccountController {
    @Resource
    private AccountService accountService;

    @PostMapping("/user/register")
    public ResultEntity<Object> register(@RequestBody Account account) {
        Account newAccount = accountService.addAccount(account);
        return ResultEntity.build(ResultStatus.REGISTER_SUCCESS, BeanUtils.copy(newAccount, AccountVo.class));
    }

    @PostMapping("/user/login/wx")
    public void loginByWx(@RequestBody Account account, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        accountService.loginByWx(account, request, response);
    }

    @GetMapping("/user/captcha")
    public ResultEntity<Object> sendCaptcha(String phone) {
        accountService.sendCaptcha(phone);
        return ResultEntity.ok();
    }

    @PostMapping("/user/login/phone")
    public void loginByPhone(String phone, String captcha, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        accountService.loginByPhone(phone, captcha, request, response);
    }

    @GetMapping("/user/info")
    public ResultEntity<Object> getAccountInfo(@RequestHeader("token") String token) {
        Account auth = JwtUtils.getAccount(token);
        Account account = accountService.getAccountById(auth);
        return ResultEntity.ok(BeanUtils.copy(account, AccountVo.class));
    }

    @PostMapping("/user/edit")
    public ResultEntity<Object> editAccountInfo(@RequestHeader("token") String token, @RequestBody AccountDTO accountDTO) {
        Account auth = JwtUtils.getAccount(token);
        Account account = accountService.updateAccountInfo(auth, accountDTO);
        return ResultEntity.ok(BeanUtils.copy(account, AccountVo.class));
    }

    @PostMapping("/user/password")
    public ResultEntity<Object> changePassword(@RequestHeader("token") String token, @RequestBody AccountDTO accountDTO) {
        Account auth = JwtUtils.getAccount(token);
        Account account = accountService.updatePassword(auth, accountDTO);
        return ResultEntity.ok(account);
    }

    @PostMapping("/user/address")
    public ResultEntity<Object> addAddress(@RequestHeader("token") String token, @RequestBody Address address) {
        Account auth = JwtUtils.getAccount(token);
        List<Address> addresses = accountService.addAddress(auth, address);
        return ResultEntity.ok(addresses);
    }

    @GetMapping("/user/address")
    public ResultEntity<Object> getAddress(@RequestHeader("token") String token) {
        Account auth = JwtUtils.getAccount(token);
        List<Address> addresses = accountService.getAddress(auth);
        return ResultEntity.ok(addresses);
    }

    @DeleteMapping("/user/address")
    public ResultEntity<Object> deleteAddress(@RequestHeader("token") String token, Long id) {
        Account auth = JwtUtils.getAccount(token);
        List<Address> addresses = accountService.deleteAddress(auth, id);
        return ResultEntity.ok(addresses);
    }

    @PostMapping("/user/avatar")
    public ResultEntity<Object> uploadAvatar(@RequestHeader("token") String token, @RequestParam(required = false) MultipartFile file) throws IOException {
        Account auth = JwtUtils.getAccount(token);
        String url = accountService.uploadAvatar(auth, file);
        return ResultEntity.ok(url);
    }
}