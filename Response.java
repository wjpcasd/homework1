import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Response {

    private OutputStream output;

    private  Request request;//主要用于传输uri

    private static final int BUFFER_SIZE = 1024;

    public Response(OutputStream output) {
        this.output = output;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    //发送一个静态资源给客户端，若本地服务器有对应的文件则返回，否则返回404页面
    public void sendStaticResource() {
        byte[] buffer = new byte[BUFFER_SIZE];
        int ch;
        FileInputStream fis = null;
        try {
            File file = new File(HttpServer.WEB_ROOT,request.getUri());//请求的文件路径
            System.out.println(file.getPath());
            if(file.exists()) {//请求的文件存在
                fis = new FileInputStream(file);
                ch = fis.read(buffer);//读取缓存区并返回字节数
                while(ch != -1) {
                    output.write(buffer, 0, ch);
                    ch = fis.read(buffer, 0, BUFFER_SIZE);
                }
            } else {//请求的文件不存在返回404页面
                String errorMessage = "HTTP/1.1 404 File Not Found \r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: 24\r\n" +
                        "\r\n" +
                        "<h1>File Not Found!</h1>";
                output.write(errorMessage.getBytes());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {//运行完之后，结束收尾
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}