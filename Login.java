import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Login extends JFrame implements MouseListener {

    //提前建好登陆和注册键的按钮对象
    JButton login = new JButton();
    JButton quit = new JButton();
    JTextField username = new JTextField();
    JTextField classid = new JTextField();

    //登陆界面包含和登陆相关的全部功能
    public Login() {
        //初始化界面
        jiemianJFrame();
        tupianJFrame();
        //显示界面
        this.setVisible(true);
    }

    private void tupianJFrame() {

        Font fontneirong = new Font("楷体_GB2312", Font.BOLD, 20);

        //导入用户名框对象
        JLabel nl=new JLabel("姓名");
        nl.setBounds(80,50,100,30);
        nl.setFont(fontneirong);

        username.setBounds(180, 50, 100, 30);
        username.setVisible(true);
        this.getContentPane().add(username);
        this.getContentPane().add(nl);

        //导入密码框对象
        JLabel cl=new JLabel("班级");
        cl.setBounds(80,100,100,30);
        cl.setFont(fontneirong);

        classid.setBounds(180, 100, 100, 30);
        classid.setVisible(true);
        this.getContentPane().add(classid);
        this.getContentPane().add(cl);

        login.setBounds(80,200,98,47);
        login.setText("登陆");
        login.setFont(fontneirong);
        login.addMouseListener(this);
        login.setVisible(true);
        this.getContentPane().add(login);

        quit.setBounds(230, 200, 98, 47);
        quit.setText("取消");
        quit.setFont(fontneirong);
        quit.addMouseListener(this);
        quit.setVisible(true);
        this.getContentPane().add(quit);
    }

    private void jiemianJFrame() {
        //设置界面的大小
        this.setSize(400, 300);
        //设置界面的名称
        this.setTitle("登录界面");
        //设置界面一直置顶
        this.setAlwaysOnTop(true);
        //设置界面居中
        this.setLocationRelativeTo(null);
        //设置界面的关闭方式
        this.setDefaultCloseOperation(2);
        //关闭图片的固定居中模式
        this.setLayout(null);
    }

    //点击按钮
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == login) {
            System.out.println("点击了登陆按钮");
            //保存输入的信息
            String UserNameIn = username.getText();
            String cid = classid.getText();

            System.out.println(UserNameIn);
            System.out.println(cid);

            //关闭当前登陆界面
            this.setVisible(false);
            //打开注册界面
            try {
                new client(UserNameIn,cid);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }else System.exit(0);
    }

    //点击不松
    @Override
    public void mousePressed(MouseEvent e) {
    }

    //点击松开
    @Override
    public void mouseReleased(MouseEvent e) {
    }

    //鼠标划入
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    //鼠标划出
    @Override
    public void mouseExited(MouseEvent e) {

    }
}
