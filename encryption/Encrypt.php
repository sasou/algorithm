<?php
/**
 * Encrypt 
 * 
 *  //demo
 *  $key = "abc"; //密钥
 *  $a = Encrypt::encode("hello world!", $key); //加密
 *  $b = Encrypt::decode($a, $key); //解密
 *  echo $b;
 * 
 */
class Encrypt {


    /**
     * 对字符串进行加密
     *
     * @param $txt
     * @param string $key
     * @return string
     */
    public static function encode($txt, $key = '')
    {
        $chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-=+";
        $nh = rand(0,64);
        $ch = $chars[$nh];
        $mdKey = md5($key.$ch);
        $mdKey = substr($mdKey,$nh%8, $nh%8+7);
        $txt = base64_encode($txt);
        $tmp = '';
        $i=0;$j=0;$k = 0;
        for ($i=0; $i<strlen($txt); $i++) {
            $k = $k == strlen($mdKey) ? 0 : $k;
            $j = ($nh+strpos($chars,$txt[$i])+ord($mdKey[$k++]))%65;
            $tmp .= $chars[$j];
        }
        return bin2hex($ch.$tmp);
    }

    /**
     * 对字符串进行解密
     *
     * @param $txt
     * @param string $key
     * @return bool|string
     */
    public static function decode($txt, $key = '')
    {
        $txt = pack("H*", $txt);
        $chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-=+";
        $ch = $txt[0];
        $nh = strpos($chars,$ch);
        $mdKey = md5($key.$ch);
        $mdKey = substr($mdKey,$nh%8, $nh%8+7);
        $txt = substr($txt,1);
        $tmp = '';
        $i=0;$j=0; $k = 0;
        for ($i=0; $i<strlen($txt); $i++) {
            $k = $k == strlen($mdKey) ? 0 : $k;
            $j = strpos($chars,$txt[$i])-$nh - ord($mdKey[$k++]);
            while ($j<0) $j+=65;
            $tmp .= $chars[$j];
        }
        return base64_decode($tmp);
    }
    
}
