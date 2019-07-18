package main

import (
	"bytes"
	"crypto/md5"
	"encoding/base64"
	"encoding/hex"
	"fmt"
	"math/rand"
	"strings"
	"time"
)

func main() {
	a := easyEncode("hello world!", "abc")
	fmt.Println(easyDecode(a, "abc"))

}

func easyEncode(txt string, key string) string {
	chars := "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-=+"
	nh := randInt(0, 64)
	ch := chars[nh]
	key_ch := fmt.Sprintf("%s%s", key, string(ch))
	mdKey := md5V(key_ch)
	mdKey = mdKey[nh%8 : nh%8+nh%8+7]
	txt = base64.StdEncoding.EncodeToString([]byte(txt))
	i, j, k := 0, 0, 0
	var buffer bytes.Buffer
	buffer.WriteByte(ch)
	for i = 0; i < len(txt); i++ {
		if k == len(mdKey) {
			k = 0
		}
		j = (nh + strings.IndexByte(chars, txt[i]) + int(mdKey[k])) % 64
		k++
		buffer.WriteByte(chars[j])
	}
	return hex.EncodeToString(buffer.Bytes())
}

func easyDecode(txt string, key string) string {
	chars := "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-=+"
	s, err := hex.DecodeString(txt)
	if err != nil {
		return ""
	}
	txt = string(s)
	ch := txt[0]
	nh := strings.IndexByte(chars, ch)
	key_ch := fmt.Sprintf("%s%s", key, string(ch))
	mdKey := md5V(key_ch)
	mdKey = mdKey[nh%8 : nh%8+nh%8+7]
	txt = txt[1:len(txt)]
	i, j, k := 0, 0, 0
	var buffer bytes.Buffer
	for i = 0; i < len(txt); i++ {
		if k == len(mdKey) {
			k = 0
		}
		j = strings.IndexByte(chars, txt[i]) - nh - int(mdKey[k])
		k++
		for j < 0 {
			j += 64
		}
		buffer.WriteByte(chars[j])
	}
	b, err := base64.StdEncoding.DecodeString(buffer.String())
	if err != nil {
		return ""
	}
	return string(b)
}

func randInt(min int, max int) int {
	rand.Seed(int64(time.Now().UnixNano()))
	return min + rand.Intn(max-min)
}

func md5V(str string) string {
	h := md5.New()
	h.Write([]byte(str))
	return hex.EncodeToString(h.Sum(nil))
}
