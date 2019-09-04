package encrypt;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Random;

public class Encrypt {

	public static void main(String[] args) {
		String key = "abc";
		String a = encode("中国，hello!", key);
		System.out.println(decode(a, key));
	}

	/**
	 * encode
	 * 
	 * @param string txt
	 * @param string key;
	 * @return string
	 */
	public static String encode(String txt, String key) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-=+";
		Random rand = new Random();
		int nh = rand.nextInt(64);
		String ch = chars.substring(nh, nh + 1);
		String mdKey = md5(key + ch);
		mdKey = mdKey.substring(nh % 8, nh % 8 + nh % 8 + 7);
		txt = base64Encoder(txt);
		String tmp = "";
		int i = 0, j = 0, k = 0;
		for (i = 0; i < txt.length(); i++) {
			k = (k == mdKey.length()) ? 0 : k;
			j = (nh + chars.indexOf(txt.charAt(i)) + ord(mdKey.charAt(k++))) % 64;
			tmp += chars.substring(j, j + 1);
		}
		tmp = ch + tmp;
		return bin2Hex(tmp);
	}

	/**
	 * decode
	 * 
	 * @param string txt
	 * @param string key;
	 * @return string
	 */
	public static String decode(String txt, String key) {
		txt = hex2Bin(txt);
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-=+";
		String ch = txt.substring(0, 1);
		int nh = chars.indexOf(ch);
		String mdKey = md5(key + ch);
		mdKey = mdKey.substring(nh % 8, nh % 8 + nh % 8 + 7);
		txt = txt.substring(1, txt.length());
		String tmp = "";
		int i = 0, j = 0, k = 0;
		for (i = 0; i < txt.length(); i++) {
			k = (k == mdKey.length()) ? 0 : k;
			j = chars.indexOf(txt.charAt(i)) - nh - ord(mdKey.charAt(k++));
			while (j < 0)
				j += 64;
			tmp += chars.substring(j, j + 1);
		}
		return base64Decoder(tmp);
	}

	public static String md5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes("UTF-8");
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			String rec = new String(str);
			return rec.toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String base64Encoder(String txt) {
		Base64.Encoder encoder = Base64.getEncoder();
		byte[] textByte = null;
		try {
			textByte = txt.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encoder.encodeToString(textByte);
	}

	public static String base64Decoder(String txt) {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] textByte = decoder.decode(txt);

		try {
			return new String(textByte, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static int ord(char c) {
		return c < 0x80 ? c : ord(Character.toString(c));
	}

	public static int ord(String s) {
		return s.length() > 0 ? (s.getBytes(StandardCharsets.UTF_8)[0] & 0xff) : 0;
	}

	public static String bin2Hex(String binStr) {
		char[] chars = binStr.toCharArray();
		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			hex.append(Integer.toHexString((int) chars[i]));
		}
		return hex.toString();
	}

	public static String hex2Bin(String hexStr) {
		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		for (int i = 0; i < hexStr.length() - 1; i += 2) {
			String output = hexStr.substring(i, (i + 2));
			int decimal = Integer.parseInt(output, 16);
			sb.append((char) decimal);
			temp.append(decimal);
		}
		return sb.toString();
	}

}
