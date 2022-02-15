package com.example.db_test;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Web {
    private static String address = "http://ec2-3-37-237-18.ap-northeast-2.compute.amazonaws.com/";  // 사용하는 url 적기

    public static String insert(String content) {
        String result = "";
        try {
            //연결
            URL url = new URL(address + "insert.php");  // url 적기
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);  // 10초간 연결 시도
            conn.setRequestMethod("POST");    // 전송 방식은 POST
            conn.setDefaultUseCaches(false);
            conn.setDoInput(true);    // 서버에서 읽기 모드 지정
            conn.setDoOutput(true);    // 서버로 쓰기 모드 지정
            conn.setRequestProperty("Accept-Language", "ko-kr,ko;q=0.8,en-us;q=0.5,en;q=0.3");

            String param = URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(content, "UTF-8");
//            param += "&" + URLEncoder.encode("text", "UTF-8") + "=" + URLEncoder.encode(text, "UTF-8");

            //전송
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

            osw.write(param);
            osw.flush();

            //응답
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) { // 웹에 있는 텍스트 받아오는 코드들
                builder.append(str);
            }
            result = builder.toString();

            //닫기
            osw.close();
            br.close();

        } catch (IOException e) {
            result = "네트워크 연결 실패";  // 네트워크 문제인지 알기 위해
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<info> select() {
        ArrayList<info> result = new ArrayList<info>();
        try {
            //   URL 설정하고 접속하기
            URL url = new URL(address + "select.php");  // url 적기
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("POST");    // 전송 방식은 POST
            conn.setDefaultUseCaches(false);
            conn.setDoInput(true);    // 서버에서 읽기 모드 지정
            conn.setDoOutput(true);    // 서버로 쓰기 모드 지정
            conn.setRequestProperty("Accept-Language", "ko-kr,ko;q=0.8,en-us;q=0.5,en;q=0.3");

            //--------------------------
            //   Response Code
            //--------------------------
            //http.getResponseCode();

//            String param = URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(content, "UTF-8");
//            param += "&" + URLEncoder.encode("text", "UTF-8") + "=" + URLEncoder.encode(text, "UTF-8");

            //전송
//            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

//            osw.write(param);
//            osw.flush();
            //--------------------------
            //   서버에서 전송받기
            //--------------------------
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String str;
            result = new ArrayList<>();

            JSONObject object;
            while ((str = br.readLine()) != null) {
                object = new JSONObject(str);   // json 형태로 받아오가
                String number = object.getString("_list");
                String content = object.getString("content");

                info info = new info(number, content);
                result.add(info);
            }

//            osw.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String update(String number, String content) {
        String result = "";
        try {
            //연결
            URL url = new URL(address + "update.php");  // url 적기
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);  // 10초간 연결 시도
            conn.setRequestMethod("POST");    // 전송 방식은 POST
            conn.setDefaultUseCaches(false);
            conn.setDoInput(true);    // 서버에서 읽기 모드 지정
            conn.setDoOutput(true);    // 서버로 쓰기 모드 지정
            conn.setRequestProperty("Accept-Language", "ko-kr,ko;q=0.8,en-us;q=0.5,en;q=0.3");

            String param = URLEncoder.encode("number", "UTF-8") + "=" + URLEncoder.encode(number, "UTF-8");
            param += "&" + URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(content, "UTF-8");

            //전송
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

            osw.write(param);
            osw.flush();

            //응답
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) { // 웹에 있는 텍스트 받아오는 코드들
                builder.append(str);
            }
            result = builder.toString();
            //닫기
            osw.close();
            br.close();

        } catch (Exception e) {
            result = "네트워크 연결 실패";  // 네트워크 문제인지 알기 위해
            e.printStackTrace();
        }
        return result;
    }

    public static String delete(String number) {
        String result = "";
        try {
            //연결
            URL url = new URL(address + "delete.php");  // url 적기
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);  // 10초간 연결 시도
            conn.setRequestMethod("POST");    // 전송 방식은 POST
            conn.setDefaultUseCaches(false);
            conn.setDoInput(true);    // 서버에서 읽기 모드 지정
            conn.setDoOutput(true);    // 서버로 쓰기 모드 지정
            conn.setRequestProperty("Accept-Language", "ko-kr,ko;q=0.8,en-us;q=0.5,en;q=0.3");

            String param = URLEncoder.encode("number", "UTF-8") + "=" + URLEncoder.encode(number, "UTF-8");
//            param += "&" + URLEncoder.encode("text", "UTF-8") + "=" + URLEncoder.encode(text, "UTF-8");

            //전송
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

            osw.write(param);
            osw.flush();

            //응답
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) { // 웹에 있는 텍스트 받아오는 코드들
                builder.append(str);
            }
            result = builder.toString();

            //닫기
            osw.close();
            br.close();

        } catch (IOException e) {
            result = "네트워크 연결 실패";  // 네트워크 문제인지 알기 위해
            e.printStackTrace();
        }
        return result;
    }
}