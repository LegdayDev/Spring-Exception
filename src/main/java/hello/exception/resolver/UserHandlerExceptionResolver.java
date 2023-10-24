package hello.exception.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.exception.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try{
            if(ex instanceof UserException){
                log.info("UserException resolver to 400");

                String acceptHeader = request.getHeader("accept"); //HTTP 메시지가 JSON 인지 아닌지 구분하기위해 accept 옵션 꺼내오기
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //HTTP 상태코드 수정

                if("application/json".equals(acceptHeader)){ //JSON 이면 JSON 형식으로 생성
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("ex", ex.getClass()); //예외타입
                    errorResult.put("message", ex.getMessage());// 메시지

                    String result = objectMapper.writeValueAsString(errorResult);// JSON -> String

                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write(result);

                    return new ModelAndView(); // 정상 흐름으로 WAS 까지 이동
                }else{ //JSON 이 아닐때
                    return new ModelAndView("error/500"); // resources/templates/error/500.html 가져옴
                }
            }
        }catch (IOException e){
            log.error("resolver ex",e);
        }
        return null;
    }
}
