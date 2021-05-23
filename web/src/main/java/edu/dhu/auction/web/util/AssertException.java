package edu.dhu.auction.web.util;

import cn.hutool.core.text.StrFormatter;

public class AssertException extends IllegalArgumentException {

    private static final long serialVersionUID = 196848963974913538L;

    public static AssertException accountNotExist(Object message) {
        return new AssertException("该账号不存在:{}", message);
    }

    public static AssertException lotNotExist(Object message) {
        return new AssertException("该拍卖品不存在:{}", message);
    }

    public static AssertException auctionNotExist(Object message) {
        return new AssertException("该拍卖不存在:{}", message);
    }

    public AssertException(String s) {
        super(s);
    }

    public AssertException(final String pattern, final Object... arg) {
        super(StrFormatter.format(pattern, arg));
    }
}
