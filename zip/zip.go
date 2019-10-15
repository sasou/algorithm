package main

import (
	"archive/zip"
	"encoding/base64"
	"encoding/json"
	"flag"
	"fmt"
	"io"
	"os"
	"strings"

	"github.com/axgle/mahonia"
)

var outDest = flag.String("out", "", "string类型参数")
var fileList = flag.String("file", "", "string类型参数")

type fileInfo struct {
	Name string `json:"name"`
	Path string `json:"path"`
}

func main() {
	flag.Parse()
	b, _ := base64.StdEncoding.DecodeString(*fileList)
	*fileList = strings.ReplaceAll(string(b), "\\", "")
	fmt.Println(*outDest)
	fmt.Println(*fileList)
	doCompress(*outDest, *fileList)
}

func doCompress(outDest string, fileList string) {
	var list []fileInfo
	err := json.Unmarshal([]byte(fileList), &list)
	if err == nil {
		var files []*os.File
		var names []string
		var fileos *os.File
		var err error
		var i = 0
		for _, file := range list {
			fileos, err = os.Open(file.Path)
			if err == nil {
				names = append(names, file.Name)
				files = append(files, fileos)
			}
			defer fileos.Close()
			i++
		}
		err = Compress(names, files, outDest)
		if err != nil {
			fmt.Println(err)
		}
	}
}

//压缩
func Compress(names []string, files []*os.File, dest string) error {
	os.MkdirAll(getDir(dest), 0755)
	d, _ := os.Create(dest)
	defer d.Close()
	w := zip.NewWriter(d)
	defer w.Close()
	var i = 0
	for _, file := range files {
		if len(names) > 0 {
			compress(names[i], file, "", w)
		} else {
			compress("", file, "", w)
		}
		i++
	}
	return nil
}

func compress(name string, file *os.File, prefix string, zw *zip.Writer) error {
	info, err := file.Stat()
	if err != nil {
		return err
	}
	if info.IsDir() {
		prefix = prefix + "/" + info.Name()
		fileInfos, err := file.Readdir(-1)
		if err != nil {
			return err
		}
		for _, fi := range fileInfos {
			f, err := os.Open(file.Name() + "/" + fi.Name())
			if err != nil {
				return err
			}
			err = compress(file.Name(), f, prefix, zw)
			if err != nil {
				return err
			}
		}
	} else {
		header, err := zip.FileInfoHeader(info)
		if err != nil {
			return err
		}
		if name == "" {
			header.Name = prefix + "/" + header.Name
		} else {
			header.Name = prefix + "/" + name
		}
		writer, err := zw.CreateHeader(header)
		if err != nil {
			return err
		}
		_, err = io.Copy(writer, file)
		file.Close()
		if err != nil {
			return err
		}
	}
	return nil
}

//解压
func unZip(zipFile, dest string) error {
	reader, err := zip.OpenReader(zipFile)
	if err != nil {
		return err
	}
	defer reader.Close()
	for _, file := range reader.File {
		rc, err := file.Open()
		if err != nil {
			return err
		}
		defer rc.Close()
		filename := dest + mahonia.NewDecoder("GB18030").ConvertString(file.Name)
		err = os.MkdirAll(getDir(filename), 0755)
		if err != nil {
			return err
		}
		w, _ := os.Create(filename)
		defer w.Close()
		_, err = io.Copy(w, rc)
		if err != nil {
			return err
		}
		w.Close()
		rc.Close()
	}
	return nil
}

func getDir(path string) string {
	pos := strings.LastIndex(path, "/")
	prefix := []byte(path)[0:pos]
	rs := []rune(string(prefix))
	pos = len(rs)
	return string(rs)
}

func pathExists(path string) (bool, error) {
	_, err := os.Stat(path)
	if err == nil {
		return true, nil
	}
	if os.IsNotExist(err) {
		return false, nil
	}
	return false, err
}
