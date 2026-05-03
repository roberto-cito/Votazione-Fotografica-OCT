package VotazioneFotografica.Config;

import VotazioneFotografica.Model.ControlFunction;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.naming.ldap.Control;
import java.io.IOException;

@Component
public class MaintenanceInterceptor implements HandlerInterceptor {

    private ControlFunction  controlFunction;

    public MaintenanceInterceptor(ControlFunction controlFunction) throws IOException {
        this.controlFunction=controlFunction;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (controlFunction.isControl2() && !request.getRequestURI().equals("/maintenance") && !request.getRequestURI().equals("/IfYouKnowThisURLShootYourself") && !request.getRequestURI().equals("/votazionefotografica/maintenance") && !request.getRequestURI().equals("/votazionefotografica/IfYouKnowThisURLShootYourself")) {
            if(request.getRequestURI().contains("votazionefotografica")) response.sendRedirect("/votazionefotografica/maintenance");
            else response.sendRedirect("/maintenance");
        }
        return true;
    }
}
