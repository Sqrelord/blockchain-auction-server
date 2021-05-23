package edu.dhu.auction.web.service.impl;

import com.aliyun.oss.OSS;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import edu.dhu.auction.web.bean.Account;
import edu.dhu.auction.web.bean.Address;
import edu.dhu.auction.web.bean.dto.AccountDTO;
import edu.dhu.auction.web.common.configuration.AliyunOSSConfigurer;
import edu.dhu.auction.web.common.rocketmq.RocketMQProducer;
import edu.dhu.auction.web.repository.AccountRepository;
import edu.dhu.auction.web.repository.AddressRepository;
import edu.dhu.auction.web.service.AccountService;
import edu.dhu.auction.web.service.NodeService;
import edu.dhu.auction.web.util.AssertException;
import edu.dhu.auction.web.util.AssertUtils;
import edu.dhu.auction.web.util.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService, UserDetailsService {

    @Resource
    private AccountRepository accountRepository;
    @Resource
    private AddressRepository addressRepository;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Resource
    private NodeService nodeService;
    @Resource
    private OSS oss;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private RedisTemplate<String, String> redis;
    @Resource
    private RocketMQProducer producer;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account != null) {
            account.setAuthorities(AuthorityUtils.createAuthorityList("ROLE_AUTHED"));
            return account;
        } else {
            return null;
        }
    }

    @Override
    public Account addAccount(Account account) {
        AccountInfo accountInfo = nodeService.createAccount(account);
        account.setAccountIdentifier(accountInfo.getIdentifier().getId());
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }


    @Override
    public Account updateAccountInfo(Account account, AccountDTO newAccount) {
        account = getAccountById(account);
        BeanUtils.copy(newAccount, account);
        return accountRepository.save(account);
    }

    @Override
    public Account updatePassword(Account account, AccountDTO newAccount) {
        accountRepository.findById(account.getId()).ifPresent(it -> {
            if (!account.getPwdFirstChange()) {
                AssertUtils.isTrue(passwordEncoder.matches(newAccount.getOldPassword(), it.getPassword()), "原密码错误");
            }
            BeanUtils.copy(newAccount, it);
            it.setPassword(passwordEncoder.encode(it.getPassword()));
            it.setPwdFirstChange(false);
            accountRepository.save(it);
        });
        return null;
    }

    @Override
    public Account getAccountById(Account account) {
        return accountRepository.findById(account.getId()).orElseThrow(() -> AssertException.accountNotExist(account.getUsername()));
    }

    @Override
    public List<Address> addAddress(Account auth, Address address) {
        Account account = getAccountById(auth);
        address.setAccount(account);
        account.getAddresses().add(address);
        return accountRepository.save(account).getAddresses();
    }

    @Override
    public List<Address> getAddress(Account account) {
        return addressRepository.findAllByAccount_Id(account.getId());
    }

    @Override
    @Transactional
    public List<Address> deleteAddress(Account account, Long id) {
        addressRepository.deleteById(id);
        return getAddress(account);
    }

    @Override
    public void sendCaptcha(String phone) {
        producer.sendSmsMessage(phone);
    }

    @Override
    @Transactional
    public void loginByPhone(String phone, String captcha, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        checkCaptcha(phone, captcha);
        Account account = accountRepository.findByUsername(phone);
        if (account == null) {
            Account newAccount = new Account();
            newAccount.setUsername(phone);
            newAccount.setPassword(phone);
            newAccount.setPwdFirstChange(true);
            addAccount(newAccount);
        }
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(phone, phone);
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    public void loginByWx(Account newAccount, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Account account = accountRepository.findByUsername(newAccount.getUsername());
        if (account == null) {
            newAccount.setPassword(newAccount.getUsername());
            newAccount.setPwdFirstChange(true);
            addAccount(newAccount);
        }
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(newAccount.getUsername(), newAccount.getUsername());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    public String uploadAvatar(Account auth, MultipartFile file) throws IOException {
        AssertUtils.isFalse(Objects.isNull(file) || file.isEmpty(), "文件不能为空");
        AssertUtils.isFalse(StringUtils.isEmpty(file.getOriginalFilename()), "文件名不能为空");
        String suffix = Optional.of(file.getOriginalFilename()).map(name -> name.substring(name.lastIndexOf('.') + 1)).orElse("");
        String fileName = UUID.randomUUID().toString().replace("-", "").concat(".").concat(suffix);
        String path = "images/".concat(fileName);
        oss.putObject(AliyunOSSConfigurer.BUCKET_NAME, path, file.getInputStream());
        return AliyunOSSConfigurer.IMAGE_URL.concat(path);
    }

    private void checkCaptcha(String phone, String captcha) {
        String code = redis.opsForValue().get(phone);
        AssertUtils.isFalse(StringUtils.isEmpty(code), "验证码失效");
        AssertUtils.isTrue(code.equals(captcha), "验证码错误");
        redis.delete(phone);
    }
}
