package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.Image;
import dao.ImageDao;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ImageServlet extends HttpServlet {
    /**
     * 查看图片属性：技能查看所有，也能查看指定图片
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //HttpServletRequest req ——请求（方法，URL，各种header，body），包含了请求中的所有信息
        //HttpServletResponse resp ——响应（状态码，各种header，body），要生成的结果就放到里面去
        //当前这个 doGet 方法就是要根据请求，生成响应

        //在网页上显示一个 hello world ，应该修改 “响应”（响应的body部分）
//        resp.setStatus(200);
//        resp.getWriter().write("hello");//这个代码就是把 hello 这个字符串放到 http 响应的 body 中了


        //考虑到查看所有图片属性和指定图片属性
        //通过 URL 中是否带有 imageId 参数来进行区分
        //存在 imageId 查看指定图片属性，否则就查看所有图片属性
        //如果 URL 中不存在 imageId 那么返回 null
        String imageId=req.getParameter("imageId");
        if(imageId==null||imageId.equals("")){
            //查看所有图片属性
            selectAll(req,resp);
        }else{
            //查看指定图片属性
            selectOne(imageId,resp);
        }
    }

    private void selectAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        //1、创建一个 ImageDao 对象，并查找数据库
        ImageDao imageDao=new ImageDao();
        List<Image> images=imageDao.selectAll();
        //2、把查找到的结果转成 JSON 格式的对象，并写回到 resp 对象
        Gson gson=new GsonBuilder().create();
        //   jsonData 就是一个 json 格式的字符串了，就和之前约定的格式是一样的了
        //  重点体会下面这行代码，这个方法的核心，gson 帮我们做了大量的格式转换工作
        //  只要之前的相关字段都约定成统一的命令，下面的操作就可以一步定位得完成整个转换
        String jsonData=gson.toJson(images);
        resp.getWriter().write(jsonData);
    }

    private void selectOne(String imageId, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        //1、创建一个 ImageDao 对象
        ImageDao imageDao=new ImageDao();
        Image image=imageDao.selectOne(Integer.parseInt(imageId));
        //2、使用 gson 把查到的数据转换成 json 格式，并写回给响应对象
        Gson gson=new GsonBuilder().create();
        String jsonData=gson.toJson(image);
        resp.getWriter().write(jsonData);
    }

    /**
     * 上传图片
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setContentType("text/html;charset=utf-8");//让浏览器以 UTF-8 的方式解析
//        resp.setStatus(200);
//        resp.getWriter().write("hello");

        //1、获取图片的属性信息，并且存入数据库

        //（1）需要创建一个 factory 对象和 upload 对象，这是为了获取图片属性做的准备工作（固定的逻辑）
        FileItemFactory factory=new DiskFileItemFactory();
        ServletFileUpload upload=new ServletFileUpload(factory);

        //（2）通过 upload 对象进一步解析请求（解析 HTTP 请求中奇怪的 body 中的内容）
        //FileItem 就代表一个上传的文件对象。
        //    理论上来说，HTTP 支持一个请求中同时上传多个文件
        List<FileItem> items=null;
        try {
            items=upload.parseRequest(req);
        } catch (FileUploadException e) {
            //出现异常说明解析出错
            e.printStackTrace();

            //告诉客户端具体的错误
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write("{\"ok\":false,\"reason\": \"请求解析失败\"}");
            return;
        }

        //（3）把 FileItem 中的属性提取出来，转换成 Image 对象，才能保存到数据库中
        //     当前只考虑一张图片的情况
        FileItem fileItem=items.get(0);
        Image image=new Image();
        image.setImageName(fileItem.getName());
        image.setSize((int)fileItem.getSize());
        //     手动获取一下当前的日期，并转换成格式化日期，yyyyMMdd==>年月日
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
        image.setUploadTime(simpleDateFormat.format(new Date()));
        image.setContentType(fileItem.getContentType());
        //     自己构造一个路径来保存，引入时间戳是为了让文件路径能够唯一
        image.setPath("./image/"+System.currentTimeMillis()+"_"+image.getImageName());
        //TODO MD5
        image.setMd5("11223344");

        //     存到数据库中
        ImageDao imageDao=new ImageDao();
        imageDao.insert(image);

        //2、获取图片的内容信息，并且写入到磁盘文件
        File file=new File(image.getPath());
        try {
            fileItem.write(file);
        } catch (Exception e) {
            e.printStackTrace();

            //告诉客户端具体的错误
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write("{\"ok\":false,\"reason\": \"写磁盘失败\"}");
            return;
        }

        //3、给客户端返回一个结果
        resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().write("{\"ok\"}:true");
    }

    /**
     * 删除指定图片
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1、先获取请求中的 imageId
        String imageId=req.getParameter("imageId");
        if(imageId==null||imageId.equals("")){
            resp.setStatus(200);
            resp.getWriter().write("{\"ok\":false,\"reason\":\"解析请求失败\"}");
            return;
        }
        //2、创建 ImageDao 对象，查看到该图片对象对应的相关属性（为了得到图片对应的文件路径）
        ImageDao imageDao=new ImageDao();
        Image image=imageDao.selectOne(Integer.parseInt(imageId));
        if(imageId==null){
            //此时请求中传入的 imageId 在数据库中不存在
            resp.setStatus(200);
            resp.getWriter().write("{\"ok\":false,\"reason\":\"imageId 在数据库中不存在\"}");
            return;
        }
        //3、删除数据库中的记录
        imageDao.delete(Integer.parseInt(imageId));
        //4、删除本地磁盘文件
        File file=new File(image.getPath());
        file.delete();
        resp.setStatus(200);
        resp.getWriter().write("{\"ok\"}:true");
    }
}
