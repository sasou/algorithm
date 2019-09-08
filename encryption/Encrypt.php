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
        $txt = base64_encode($txt);
        $nh = rand(0,32);
        $ch = chr($nh);
        $mdKey = md5($key.$ch);
        $mdKey = substr($mdKey,$nh%8, $nh%8+7);
        $tmp = '';
        $i=0;$j=0;$k = 0;
        for ($i=0; $i<strlen($txt); $i++) {
            $k = $k == strlen($mdKey) ? 0 : $k;
            $j = ($nh+ord($txt[$i])+ord($mdKey[$k++]))%128;
            $tmp .= chr($j);
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
        $ch = $txt[0];
        $nh = ord($ch);
        $mdKey = md5($key.$ch);
        $mdKey = substr($mdKey,$nh%8, $nh%8+7);
        $txt = substr($txt,1);
        $tmp = '';
        $i=0;$j=0; $k = 0;
        for ($i=0; $i<strlen($txt); $i++) {
            $k = $k == strlen($mdKey) ? 0 : $k;
            $j = ord($txt[$i])- $nh - ord($mdKey[$k++]);
            while ($j<0) $j+=128;
            $tmp .= chr($j);
        }
        return base64_decode($tmp);
    }
    
}
