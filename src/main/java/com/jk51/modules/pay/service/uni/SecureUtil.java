package com.jk51.modules.pay.service.uni;

import com.jk51.commons.string.StringUtil;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class SecureUtil {
	private static final Logger log = LoggerFactory.getLogger(SecureUtil.class);
	/**
	 * 算法常量： MD5
	 */
	private static final String ALGORITHM_MD5 = "MD5";
	/**
	 * 算法常量： SHA1
	 */
	private static final String ALGORITHM_SHA1 = "SHA-1";

	/**
	 * 算法常量：SHA1withRSA
	 */
	private static final String BC_PROV_ALGORITHM_SHA1RSA = "SHA1withRSA";

	/**
	 * md5计算.
	 * 
	 * @param datas
	 *            待计算的数据
	 * @return 计算结果
	 */
	public static byte[] md5(byte[] datas) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(ALGORITHM_MD5);
		md.reset();
		md.update(datas);
		return md.digest();
	}

	/**
	 * sha1计算.
	 * 
	 * @param data
	 *            待计算的数据
	 * @return 计算结果
	 */
	public static byte[] sha1(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(ALGORITHM_SHA1);
		md.reset();
		md.update(data);
		return md.digest();
	}

	/**
	 * md5计算后进行16进制转换
	 * 
	 * @param datas
	 *            待计算的数据
	 * @param encoding
	 *            编码
	 * @return 计算结果
	 */
	public static byte[] md5X16(String datas, String encoding) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		if(StringUtil.isEmpty(encoding == null))
			encoding = "UTF-8";
		byte[] bytes = md5(datas, encoding);
		StringBuilder md5StrBuff = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			if (Integer.toHexString(0xFF & bytes[i]).length() == 1) {
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & bytes[i]));
			} else {
				md5StrBuff.append(Integer.toHexString(0xFF & bytes[i]));
			}
		}
		return md5StrBuff.toString().getBytes(encoding);
	}

	/**
	 * sha1计算后进行16进制转换
	 * 
	 * @param data
	 *            待计算的数据
	 * @param encoding
	 *            编码
	 * @return 计算结果
	 */
	public static byte[] sha1X16(String data, String encoding) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		if(StringUtil.isEmpty(encoding == null))
			encoding = "UTF-8";
		byte[] bytes = sha1(data, encoding);
		StringBuilder sha1StrBuff = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			if (Integer.toHexString(0xFF & bytes[i]).length() == 1) {
				sha1StrBuff.append("0").append(
						Integer.toHexString(0xFF & bytes[i]));
			} else {
				sha1StrBuff.append(Integer.toHexString(0xFF & bytes[i]));
			}
		}
		return sha1StrBuff.toString().getBytes(encoding);
	}

	/**
	 * md5计算
	 * 
	 * @param datas
	 *            待计算的数据
	 * @param encoding
	 *            字符集编码
	 * @return
	 */
	public static byte[] md5(String datas, String encoding) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		if(StringUtil.isEmpty(encoding == null))
			encoding = "UTF-8";
		return md5(datas.getBytes(encoding));
	}

	/**
	 * sha1计算
	 * 
	 * @param datas
	 *            待计算的数据
	 * @param encoding
	 *            字符集编码
	 * @return
	 */
	public static byte[] sha1(String datas, String encoding) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		if(StringUtil.isEmpty(encoding == null))
			encoding = "UTF-8";
		return sha1(datas.getBytes(encoding));
	}

	/**
	 * 软签名
	 * 
	 * @param privateKey
	 *            私钥
	 * @param data
	 *            待签名数据
	 * @return 结果
	 * @throws Exception
	 */
	public static byte[] signBySoft(PrivateKey privateKey, byte[] data)
			throws Exception {
		byte[] result = null;
		Signature st = Signature.getInstance(BC_PROV_ALGORITHM_SHA1RSA, "BC");
		st.initSign(privateKey);
		st.update(data);
		result = st.sign();
		return result;
	}

	/**
	 * 软验证签名
	 * 
	 * @param publicKey
	 *            公钥
	 * @param signData
	 *            签名数据
	 * @param srcData
	 *            摘要
	 * @return
	 * @throws Exception
	 */
	public static boolean validateSignBySoft(PublicKey publicKey,
			byte[] signData, byte[] srcData) throws Exception {
		Signature st = Signature.getInstance(BC_PROV_ALGORITHM_SHA1RSA, "BC");
		st.initVerify(publicKey);
		st.update(srcData);
		return st.verify(signData);
	}

	/**
	 * BASE64解码
	 * 
	 * @param inputByte
	 *            待解码数据
	 * @return 解码后的数据
	 * @throws IOException
	 */
	public static byte[] base64Decode(byte[] inputByte) throws IOException {
		return Base64.decodeBase64(inputByte);
	}

	/**
	 * BASE64编码
	 * 
	 * @param inputByte
	 *            待编码数据
	 * @return 解码后的数据
	 * @throws IOException
	 */
	public static byte[] base64Encode(byte[] inputByte) throws IOException {
		return Base64.encodeBase64(inputByte);
	}

}
