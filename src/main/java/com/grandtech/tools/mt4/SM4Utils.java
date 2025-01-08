package com.grandtech.tools.mt4;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SM4Utils {

	//秘钥
	private static String fSecretKey = "94DF266144497589";
	//向量
	private static String fIv = "94DF266144497589";
	//声明密钥和向量是否是32长度的十六进制的字符串，如果true则需要设置密钥向量都是十六进制的32长度字符串
	private static boolean hexString = false;

	/**
	 * ECB模式加密
	 * @param plainText
	 * @return
	 */
	public static String encryptData_ECB(String plainText) {
		try {
			SM4_Context ctx = new SM4_Context();
			ctx.isPadding = true;
			ctx.mode = SM4.SM4_ENCRYPT;
			
			byte[] keyBytes;
			if (hexString) {
				keyBytes = Util.hexStringToBytes(fSecretKey);
			} else {
				keyBytes = fSecretKey.getBytes();
			}
			
			SM4 sm4 = new SM4();
			sm4.sm4_setkey_enc(ctx, keyBytes);
			byte[] encrypted = sm4.sm4_crypt_ecb(ctx, plainText.getBytes("GBK"));
			String cipherText = new BASE64Encoder().encode(encrypted);
			if (cipherText != null && cipherText.trim().length() > 0) {
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher m = p.matcher(cipherText);
				cipherText = m.replaceAll("");
			}
			return cipherText;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * ECB解密
	 * @param cipherText
	 * @return
	 */
	public static String decryptData_ECB(String cipherText) {
		try {
			SM4_Context ctx = new SM4_Context();
			ctx.isPadding = true;
			ctx.mode = SM4.SM4_DECRYPT;
			
			byte[] keyBytes;
			if (hexString) {
				keyBytes = Util.hexStringToBytes(fSecretKey);
			} else {
				keyBytes = fSecretKey.getBytes();
			}
			
			SM4 sm4 = new SM4();
			sm4.sm4_setkey_dec(ctx, keyBytes);
			byte[] decrypted = sm4.sm4_crypt_ecb(ctx, new BASE64Decoder().decodeBuffer(cipherText));
			return new String(decrypted, "GBK");
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * CBC模式加密
	 * @param plainText
	 * @return
	 */
	public static String encryptData_CBC(String plainText) {
		try {
			SM4_Context ctx = new SM4_Context();
			ctx.isPadding = true;
			ctx.mode = SM4.SM4_ENCRYPT;
			
			byte[] keyBytes;
			byte[] ivBytes;
			if (hexString) {
				keyBytes = Util.hexStringToBytes(fSecretKey);
				ivBytes = Util.hexStringToBytes(fIv);
			}else{
				keyBytes = fSecretKey.getBytes();
				ivBytes = fIv.getBytes();
			}
			
			SM4 sm4 = new SM4();
			sm4.sm4_setkey_enc(ctx, keyBytes);
			byte[] encrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, plainText.getBytes("GBK"));
			String cipherText = new BASE64Encoder().encode(encrypted);
			if (cipherText != null && cipherText.trim().length() > 0) {
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher m = p.matcher(cipherText);
				cipherText = m.replaceAll("");
			}
			return cipherText;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * CBC模式传输数据解密
	 * @param cipherText
	 * @return
	 */
	public static String decryptData_CBC(String cipherText) {
		try {
			SM4_Context ctx = new SM4_Context();
			ctx.isPadding = true;
			ctx.mode = SM4.SM4_DECRYPT;
			
			byte[] keyBytes;
			byte[] ivBytes;
			if (hexString) {
				keyBytes = Util.hexStringToBytes(fSecretKey);
				ivBytes = Util.hexStringToBytes(fIv);
			} else {
				keyBytes = fSecretKey.getBytes();
				ivBytes = fIv.getBytes();
			}
			
			SM4 sm4 = new SM4();
			sm4.sm4_setkey_dec(ctx, keyBytes);
			byte[] decrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, new BASE64Decoder().decodeBuffer(cipherText));
			return new String(decrypted, "GBK");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) throws IOException  {
		String plainText = "adminrfdsfsd";

		String s = SM4Utils.encryptData_CBC(plainText);
		System.out.println("SM4 CBC模式加密："+s);

		String s1 = SM4Utils.decryptData_CBC(s);
		System.out.println("SM4 CBC模式解密："+s1);

		String s2 = SM4Utils.encryptData_ECB(plainText);
		System.out.println("SM4 ECB模式加密："+s2);

		String s3 = SM4Utils.decryptData_ECB(s2);
		System.out.println("SM4 ECB模式解密："+s3);
	}
}
