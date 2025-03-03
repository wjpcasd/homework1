import java.io.IOException;
import java.io.InputStream;

//GET /index.html HTTP/1.1Host: www.example.comUser-Agent: Mozilla/5.0Accept: text/html```
//在这个示例中，请求方法是GET，URI是/index.html，HTTP协议版本是HTTP/1.1。请求头包含了Host、User-Agent和Accept等信息。
//需要注意的是，不同的请求方法（如GET、POST、PUT等）和不同的请求类型（如表单数据、JSON数据）可能会有不同的请求数据格式。以上是一个简单的示例，实际的请求数据格式可能更加复杂和详细。

//1.请求行（Request Line）：请求行包含了请求的方法、URI和HTTP协议的版本。例如：GET /index.html HTTP/1.1
//2.请求头（Request Headers）：请求头包含了关于请求的附加信息，如请求的主机、用户代理、内容类型等。请求头以键值对的形式表示，每个键值对占一行。例如：Host: www.example.com User-Agent: Mozilla/5.0 Content-Type: application/json
//3. 请求体（Request Body）：请求体是可选的，用于传输一些额外的数据，如表单数据、JSON数据等。请求体的格式取决于请求的内容类型。

public class Request {

    private InputStream input;
    private String uri;

    public Request(InputStream input) {
        this.input = input;
    }

    public void parser() {//解析从客户端发送过来的请求
        StringBuffer request = new StringBuffer();
        byte[] buffer = new byte[2048];
        int i = 0;

        try {
            i = input.read(buffer);//请求读到buffer里面
        } catch (IOException e) {
            e.printStackTrace();
            i = -1;
        }

        for(int k = 0; k < i; k++) {
            request.append((char)buffer[k]);
        }

        uri = parserUri(request.toString());//从请求中找到uri

    }

    private String parserUri(String requestData) {//从请求中解析出uri      //解释该请求以确定所请求的特定文件
        int index1, index2;
        index1 = requestData.indexOf(' ');//空格
        if(index1 != -1) {//存在第一个空格
            index2 = requestData.indexOf(' ', index1 + 1);//存放第二个空格
            if(index2 > index1) {
                return requestData.substring(index1 + 1, index2);
            }
        }

        return null;

    }

    public String getUri() {
        return uri;
    }
}
