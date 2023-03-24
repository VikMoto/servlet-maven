package goit.servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@WebServlet("/first")
public class HelloServlet extends HttpServlet {


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  ServletException, IOException {

        String name = parseName(req);

        resp.setContentType("text/html; charset=utf-8");
//        try (PrintWriter writer = resp.getWriter()) {
           PrintWriter writer = resp.getWriter();

             writer.write("<h1>Hare Krishna from ${name}!</h1>".replace("${name}", name));

            writer.write(getAllParameters(req));
            writer.write("<br>TIME<br>");
            String currentTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("дата: yyyy-MM-dd,   час: HH:mm:ss"));

//            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
//            System.out.println("currentTime = " + currentTime);

            writer.write(currentTime);

            writer.write("<br><br>");
            writer.write("<br>Headers<br>");

            writer.write(getAllHeaders(req));
            //reload page every 5 sec
//            resp.setHeader("Refresh", "5");

        writer.close();
    }

    private String parseName(HttpServletRequest request){
        if (request.getParameterMap().containsKey("name")){
            return request.getParameter("name");
        }
        return "unnamed";
    }

    private String getAllHeaders(HttpServletRequest request){
        //
        StringJoiner result = new StringJoiner("<br>");

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String headerName = headerNames.nextElement();

            String headerValues = request.getHeader(headerName);
//            System.out.println("parameterValues = " + parameterValues);
            result.add(headerName + " = " + headerValues);
//            System.out.println("result = " + result);
        }


        return result.toString();

    }

    private String getAllParametersJson(HttpServletRequest request) throws IOException {
        String body ;
        String result ;

            body = request
                    .getReader()
                    .lines()
                    .collect(Collectors.joining("\n"));

            Type type = TypeToken.getParameterized(Map.class, String.class, String.class).getType();

            Map<String, String> params = new Gson().fromJson(body, type);

             result = params.entrySet()
                    .stream()
                    .map(it -> it.getKey() + " = " + it.getValue())
                    .collect(Collectors.joining("\n"));



        return result;

    }

    private String getAllParameters(HttpServletRequest request) throws IOException {
        String contentType = request.getHeader("Content-Type");

        if ("application/json".equals(contentType)){
            return getAllParametersJson(request);
        } else {
            return getAllParametersUrlEncoded(request);
        }

    }
    private String getAllParametersUrlEncoded(HttpServletRequest request){
        //
        StringJoiner result = new StringJoiner("<br>");

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()){
            String parameterName = parameterNames.nextElement();

            String parameterValues = Arrays.toString(request.getParameterValues(parameterName));
//            System.out.println("parameterValues = " + parameterValues);
            result.add(parameterName + " = " + parameterValues);
//            System.out.println("result = " + result);
        }


        return result.toString();

    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
