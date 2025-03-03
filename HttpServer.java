import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class HttpServer {
    //构建了一个常量字符串，当前用户工作目录+文件分隔符再+"webroot"目录名。
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";//结束判断
    private boolean shutdown = false;//结束标志

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.start();
    }

    //启动服务器，并接收用户请求进行处理
    public void  start() {
        ServerSocket serverSocket = null;//服务器端
        int  PORT = 8080;//端口号

        try {
            serverSocket = new ServerSocket(PORT, 1, InetAddress.getByName("127.0.0.1"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        //若请求的命令不为SHUTDOWN时，循环处理请求
        while(!shutdown) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;

            try {
                //创建socket进行请求处理
                socket = serverSocket.accept();            //当一个客户（浏览器）连接时，创建一个连接套接字
                input = socket.getInputStream();
                output = socket.getOutputStream();

                //接收请求
                Request request = new Request(input);      //从这个连接套接字接收 HTTP 请求
                request.parser();//根据请求找到uri          //解释该请求以确定所请求的特定文件

                //处理请求并返回结果
                Response response = new Response(output);
                response.setRequest(request);
                response.sendStaticResource();

                //关闭socket
                socket.close();

                //若请求命令为关闭，则关闭服务器，要先接受客户端的信息在判断是不是停止
                shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}
