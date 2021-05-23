package edu.dhu.auction.web.service;

import edu.dhu.auction.web.bean.Account;
import edu.dhu.auction.web.bean.Address;
import edu.dhu.auction.web.bean.dto.AccountDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface AccountService {
    Account addAccount(Account account);

    Account updateAccountInfo(Account account, AccountDTO newAccount);

    Account updatePassword(Account account, AccountDTO newAccount);

    Account getAccountById(Account account);

    List<Address> addAddress(Account account, Address address);

    List<Address> getAddress(Account account);

    List<Address> deleteAddress(Account account, Long id);

    void loginByPhone(String phone, String captcha, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

    void sendCaptcha(String phone);

    String uploadAvatar(Account auth, MultipartFile file) throws IOException;

    void loginByWx(Account account, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
}
