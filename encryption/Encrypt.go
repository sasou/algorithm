package main

import (
	"bytes"
	"crypto/md5"
	"encoding/base64"
	"encoding/hex"
	"fmt"
	"math/rand"
	"time"
)

func main() {
	a := encode("hello world!", "abc")
	fmt.Println(decode(a, "abc"))
}

func encode(txt string, key string) string {
	txt = base64.StdEncoding.EncodeToString([]byte(txt))
	nh := randInt(0, 32)
	ch := byte(nh)
	key_ch := fmt.Sprintf("%s%s", key, string(ch))
	mdKey := md5V(key_ch)
	mdKey = mdKey[nh%8 : nh%8+nh%8+7]
	i, j, k := 0, 0, 0
	var buffer bytes.Buffer
	buffer.WriteByte(ch)
	for i = 0; i < len(txt); i++ {
		if k == len(mdKey) {
			k = 0
		}
		j = (nh + int(txt[i]) + int(mdKey[k])) % 128
		k++
		buffer.WriteByte(byte(j))
	}
	return hex.EncodeToString(buffer.Bytes())
}

func decode(txt string, key string) string {
	s, err := hex.DecodeString(txt)
	if err != nil {
		return ""
	}
	txt = string(s)
	ch := txt[0]
	nh := int(ch)
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
		j = int(txt[i]) - nh - int(mdKey[k])
		k++
		for j < 0 {
			j += 128
		}
		buffer.WriteByte(byte(j))
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
