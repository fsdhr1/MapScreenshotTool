package com.grandtech.tools.mt4;

/**
 * @author yubj at 2020/9/15
 */
public interface SM4_ScrKey {
    //public static final int CONNECT_LINE=4;
    public String readSecretKey();// = "50799692ABADCF0D";
    public String readIV();// = "UISwD9fW6cFh9SNS";
    public boolean readHex();//= false;
}
