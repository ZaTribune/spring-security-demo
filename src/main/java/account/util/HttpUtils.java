package account.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
public class HttpUtils {


    private HttpUtils(){

    }
    public static String extractUsernameBasicAuth(HttpServletRequest request) {

        try {
            String emailEncoded = request.getHeader("Authorization").split(" ")[1];
            return new String(Base64.decodeBase64(emailEncoded)).split(":")[0];
        } catch (Exception e) {
            log.error(e.getMessage());
            return  "Anonymous";
        }

    }
}
