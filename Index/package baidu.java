
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.alibaba.fastjson.JSON;

public class TestApi {

    public static void main(String[] args) throws Exception {
        /*  String ss= "中国";
        String  s=Base64.getEncoder().encodeToString(ss.getBytes("gbk"));
        System.out.println(s);
        byte[] b = Base64.getDecoder().decode(s);
        System.out.println(new String(b,"gbk"));*/
        //第一步，官方要求需要把图片转base64再转URLEncoder
        //获取图片base64编码
        String image = getImageStr("D:\\360Rec\\183410564149088537.jpg");
        String APIKey = "你的APIKey";
        String SecretKey = "你的SecretKey";
        //第二步, 通过API Key和Secret Key获取的access_token
        String access_token = getAuth(APIKey, SecretKey);
        System.out.println("access_token:  " + access_token);
        //拼接请求体
        String content = "access_token=" + URLEncoder.encode(access_token, "gbk")
                + "&image=" + URLEncoder.encode(image, "gbk");
        //表格识别url
        String imageUrl = "https://aip.baidubce.com/rest/2.0/solution/v1/form_ocr/request";
        //第三步，发送请求
        String response = sendPost(imageUrl, content);
        //解析结果拿到request_id再次发送结果接口请求获取识别内容
        String result = JSON.parseObject(response).get("result").toString();
        String request_id = JSON.parseObject(result.substring(1, result.length() - 1)).get("request_id").toString();
        System.out.println("request_id: " + request_id);

        Thread.sleep(1000 * 10);//由于解析有延时，所以睡眠一下
        //第四步，通过上面获取到的request_id去获取解析结果
        //获取结果url
        String resultURL = "https://aip.baidubce.com/rest/2.0/solution/v1/form_ocr/get_request_result";
        //拼接结果请求体
        String requestData = "access_token=" + URLEncoder.encode(access_token, "gbk")
                + "&request_id=" + URLEncoder.encode(request_id, "gbk")
                + "&result_type=excel";
        //发送结果请求
        sendPost(resultURL, requestData);
    }

    /**
     * 解析图片转成base64编码
     *
     * @param imgFile
     * @return
     * @throws Exception
     */
    public static String getImageStr(String imgFile) throws Exception {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        //String imgFile = "d:\\111.jpg";// 待处理的图片
        /* InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        StringBuffer buffer = new StringBuffer();
        BufferedReader bf = new BufferedReader(new FileReader(imgFile));
        String s = null;
        while ((s = bf.readLine()) != null) {//使用readLine方法，一次读一行
            buffer.append(s.trim());
        }
        String data = buffer.toString();
        // 对字节数组Base64编码
        //  BASE64Encoder encoder = new BASE64Encoder();

        // 返回Base64编码过的字节数组字符串
        // return encoder.encode(data.getBytes("gbk"));
        return Base64.getEncoder().encodeToString(data.getBytes("gbk"));
    }

    /**
     *
     * @param u url地址
     * @param param 跟在?后面的参数
     * @return
     */
    public static String sendPost(String u, String param) {
        StringBuffer sbf = new StringBuffer();
        try {
            URL url = new URL(u);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            // connection.addRequestProperty("role", "Admin");
            connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //  connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            if (!"".equals(param)) {
                // 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致
                //  String content = "字段名=" + URLEncoder.encode("字符串值", "编码");
                out.writeBytes(param);
            }
            out.flush();
            out.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines;
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sbf.append(lines);
            }
            System.out.println(sbf);
            reader.close();
            // 断开连接
            connection.disconnect();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sbf.toString();
    }

    /**
     * 通过API Key和Secret Key获取的access_token
     *
     * @param ak API Key
     * @param sk Secret Key
     * @return
     */
    public static String getAuth(String ak, String sk) {
        // 获取token地址  
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数  
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key  
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key  
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接  
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段  
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段  
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应  
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
            System.out.println("result:" + result);
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;

        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }
}
