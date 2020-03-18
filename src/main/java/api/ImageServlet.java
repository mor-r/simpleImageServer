package api;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ImageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //HttpServletRequest req ——请求（方法，URL，各种header，body）
        //HttpServletResponse resp ——响应（状态码，各种header，body）

        //在网页上显示一个 hello world ，应该修改 “响应”（响应的body部分）
        resp.setStatus(200);
        resp.getWriter().write("hello");//这个代码就是把 hello 这个字符串放到 http 响应的 body 中了
    }
}
