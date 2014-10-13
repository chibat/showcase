package app.web.ajaxPolling;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Data;
import lombok.Getter;
import lombok.val;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author chibat
 *
 */
@Controller
@RequestMapping("/ajaxPolling")
public class AjaxPollingController {

    private final List<RequestDto> requestDtoList = new CopyOnWriteArrayList<>();

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${server.port}")
    private int port;

    //
    // html
    //

    @RequestMapping(value = "/registerForm", method = RequestMethod.GET)
    public String registerForm() {
        return "ajaxPolling/registerForm";
    }

    /**
     * 
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerForm(RegisterForm form, Model model) {
        if (StringUtils.isEmpty(form.getArg1())
                || StringUtils.isEmpty(form.getArg2())) {
            return "ajaxPolling/registerForm";
        }
        val map = new LinkedMultiValueMap<String, String>();
        map.add("arg1", form.getArg1());
        map.add("arg2", form.getArg2());
        // Mapに詰め替えないで、formのまま実行できないものか。
        restTemplate.postForEntity("http://localhost:" + port
                + "/ajaxPolling/register.json", map, String.class);
        model.addAttribute("messageFlag", true);
        return "ajaxPolling/registerForm";
    }

    @RequestMapping("/getKo")
    public String getKo() {
        return "ajaxPolling/getKo";
    }

    @RequestMapping("/getNg")
    public String getNg() {
        return "ajaxPolling/getNg";
    }

    //
    // json
    //

    /**
     * API的に呼んでもらうメソッド
     * 
     * @param arg1
     * @param arg2
     * @return
     */
    @RequestMapping(value = "/register.json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> registerJson(@RequestParam String arg1,
            @RequestParam String arg2) {
        val d = new RequestDto(arg1, arg2);
        this.requestDtoList.add(d);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @RequestMapping("/get.json")
    @ResponseBody
    public ResponseEntity<List<RequestDto>> getJson() {
        return new ResponseEntity<>(requestDtoList, HttpStatus.OK);
    }

    @RequestMapping(value = "/startProgress.json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<List<RequestDto>> startProgressJson(
            @RequestParam Integer id) {

        for (int i = 0; i < requestDtoList.size(); i++) {
            val r = requestDtoList.get(i);
            if (r.id == id) {
                requestDtoList.remove(i);
                break;
            }
        }
        return new ResponseEntity<>(requestDtoList, HttpStatus.OK);
    }

    @Getter
    public static class RequestDto {
        private static int sequence = 0;
        private final int id = sequence++;
        private final String arg1;
        private final String arg2;
        private final String requestTime = ZonedDateTime.now().toString();
        private Status status = Status.OPEN;

        public RequestDto(String arg1, String arg2) {
            this.arg1 = arg1;
            this.arg2 = arg2;
        }
    }

    @Data
    public static class RegisterForm {
        private String arg1;
        private String arg2;
    }

    public static enum Status {
        OPEN(0), PROGRESS(1), CLOSE(2);

        private int value;

        private Status(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }
    }
}
