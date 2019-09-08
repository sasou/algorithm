package encrypt;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Random;

public class Encrypt {

	public static void main(String[] args) {
		String key = "abc";
		String a = encode("hello world!", key);
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
		txt = base64Encoder(txt);
		Random rand = new Random();
		int nh = rand.nextInt(32);
		int length = txt.length();
		String ch = String.valueOf(Character.toChars(nh));
		String mdKey = md5(key + ch);
		mdKey = mdKey.substring(nh % 8, nh % 8 + nh % 8 + 7);
		String retString = "";
		int i = 0, j = 0, k = 0, m = 0;
		for (i = 0; i < length; i++) {
			k = (k == mdKey.length()) ? 0 : k;
			m = (int) txt.charAt(i);
			j = (nh + m + ord(mdKey.charAt(k++))) % 128;
			retString += String.valueOf(Character.toChars(j));
		}
		return bin2hex(ch + retString);
	}

	/**
	 * decode
	 * 
	 * @param string txt
	 * @param string key;
	 * @return string
	 */
	public static String decode(String txt, String key) {
		txt = hex2bin(txt);
		String ch = txt.substring(0, 1);
		int nh = ord(ch);
		String mdKey = md5(key + ch);
		mdKey = mdKey.substring(nh % 8, nh % 8 + nh % 8 + 7);
		txt = txt.substring(1, txt.length());
		int length = txt.length();
		String retString = "";
		int i = 0, j = 0, k = 0, m = 0;
		for (i = 0; i < length; i++) {
			k = (k == mdKey.length()) ? 0 : k;
			m = (int) txt.charAt(i);
			j = (m - nh - ord(mdKey.charAt(k++)));
			while (j < 0) {
				j += 128;
			}
			retString += String.valueOf(Character.toChars(j));
		}
		return base64Decoder(retString);
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

	public static String bin2hex(String binString) {
		byte[] bytes;
		Formatter f = new Formatter();
		try {
			bytes = binString.getBytes();
			for (byte c : bytes)
				f.format("%02X", c);

			return (f.toString().toLowerCase());
		} finally {
			f.close();
		}
	}

	public static String hex2bin(String hexString) {
		if (!hexString.matches("^[0-9a-fA-F]+$")) {
			return null;
		}
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < hexString.length(); i += 2) {
			String str = hexString.substring(i, i + 2);
			output.append((char) Integer.parseInt(str, 16));
		}
		return output.toString();
	}

}
