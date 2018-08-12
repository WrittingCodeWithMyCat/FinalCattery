package by.epam.cattery.controller.filter;

import by.epam.cattery.controller.command.constant.MessageConst;
import by.epam.cattery.controller.command.constant.PathConst;
import by.epam.cattery.controller.command.constant.SessionConst;
import by.epam.cattery.util.ConfigurationManager;
import by.epam.cattery.entity.Role;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = { "/jsp/expert/*"})
public class ExpertFilter implements Filter {
    private static final String MAIN_PAGE = ConfigurationManager.getInstance().getProperty(PathConst.MAIN_PAGE);

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        HttpSession session = request.getSession();
        String locale = session.getAttribute(SessionConst.LOCALE).toString();

        if (session.getAttribute(SessionConst.ROLE) != Role.EXPERT) {

            if (session.getAttribute(SessionConst.LOGIN) == null) {
                session.setAttribute(SessionConst.LOG_IN_FAIL, ConfigurationManager.getInstance()
                        .getMessage(MessageConst.ACCESS_DENIED, locale));
            }
            response.sendRedirect(MAIN_PAGE);

        } else {
            chain.doFilter(request, response);
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }
}