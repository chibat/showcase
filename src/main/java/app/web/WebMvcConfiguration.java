package app.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import app.formatter.LocalDateTimeFormatter;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        super.addFormatters(registry);
        registry.addFormatter(new LocalDateTimeFormatter());
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return new ServletContainerCustomizer();
    }

    @Bean
    public FilterRegistrationBean getFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new CharacterEncodingFilter());
        return filterRegistrationBean;
    }

    private static final String ENCODING = "UTF-8";

    /**
     * リクエストのエンコーディングをUTF-8にします。
     */
    private static class CharacterEncodingFilter implements Filter {

        public void init(FilterConfig filterConfig) throws ServletException {
        }

        public void doFilter(ServletRequest servletRequest,
                ServletResponse servletResponse, FilterChain filterChain)
                throws IOException, ServletException {

            HttpServletRequest request = (HttpServletRequest) servletRequest;
            request.setCharacterEncoding(ENCODING);
            filterChain.doFilter(servletRequest, servletResponse);
        }

        public void destroy() {
        }
    }

    protected static class ServletContainerCustomizer implements
            EmbeddedServletContainerCustomizer {

        @Override
        public void customize(ConfigurableEmbeddedServletContainer factory) {
            factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404"));
        }
    }

}
