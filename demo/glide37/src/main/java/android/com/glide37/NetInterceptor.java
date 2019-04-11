package android.com.glide37;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetInterceptor implements Interceptor {
    public NetInterceptor(){

    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder=chain.request().newBuilder();
        builder.addHeader("User-Agent",getUserAgent());
        builder.removeHeader("Connection");
        builder.addHeader("Connection","close");
        Request request= builder.build();
        return chain.proceed(request);
    }
    private String getUserAgent() {
        String userAgent = "http/image";
        return userAgent;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            try {
//                if(ctx!=null) {
//                    userAgent = WebSettings.getDefaultUserAgent(ctx);
//                }else {
//                    userAgent = System.getProperty("http.agent");
//                }
//            } catch (Exception e) {
//                userAgent = System.getProperty("http.agent");
//            }
//        } else {
//            userAgent = System.getProperty("http.agent");
//        }
//        try {
//            StringBuffer sb = new StringBuffer();
//            for (int i = 0, length = userAgent.length(); i < length; i++) {
//                char c = userAgent.charAt(i);
//                if (c <= '\u001f' || c >= '\u007f') {
//                    sb.append(String.format("\\u%04x", (int) c));
//                } else {
//                    sb.append(c);
//                }
//            }
//            return sb.toString();
//        }catch (Exception ex){}
//        return "okhttp/agent";
    }
}
