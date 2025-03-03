import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class client extends JFrame implements Runnable,ActionListener {
    //图形化界面的初始化
    //north 上方
    private JMenuBar bar = new JMenuBar();
    private JMenu menu = new JMenu("关于");

    private JMenuItem about = new JMenuItem("关于本软件");
    private JMenuItem exit = new JMenuItem("退出");

    JPanel north = new JPanel();

    //west 右边面板
    JPanel east = new JPanel();
    DefaultListModel<String> dl = new DefaultListModel<String>();//用来修改JList
    private JList<String> userList = new JList<String>(dl);//用来展示和选择
    JScrollPane listPane = new JScrollPane(userList);//滚动面板存放在线好友

    //center 中间面板
    JPanel center = new JPanel();
    JTextArea jta = new JTextArea(10,20);//文本框
    JScrollPane js = new JScrollPane(jta);//滚动面板存放消息

    //发送消息的操作面板
    JPanel operPane = new JPanel();
    JLabel input = new JLabel("请输入:");
    JTextField jtf = new JTextField(24);//发送消息的文本框

    JButton jButton = new JButton("发消息");//发送消息的按钮
    JButton jButton2 = new JButton("班级内发消息");//发送消息的按钮

    private JButton jbt = new JButton("发送消息");
    private JButton jbt1 = new JButton("私发消息");
    private JButton jbt2 = new JButton("班级内私发消息");
    private BufferedReader br = null;
    private PrintStream ps = null;
    private String nickName = null;
    private String classnum=null;

    //私聊面板
    JTextArea jTextArea = new JTextArea(11,45);
    JScrollPane js1 = new JScrollPane(jTextArea);
    JTextField jTextField = new JTextField(25);
    String suser = new String();

    double MAIN_FRAME_LOC_X;//父窗口x坐标
    double MAIN_FRAME_LOC_Y;//父窗口y坐标

    boolean FirstSecret = true;//是否第一次私聊
    String sender=null;//私聊发送者的名字
    String receiver=null;//私聊接收者的名字

    //班级内私聊面板
    JTextArea jTextArea2 = new JTextArea(11,45);
    JScrollPane js2 = new JScrollPane(jTextArea2);
    JTextField jTextField2 = new JTextField(25);

    boolean FirstSecret2 = true;//是否第一次私聊

    public client(String str1,String str2) throws Exception{

        this.nickName=str1;
        this.classnum=str2;

        //north 菜单栏
        bar.add(menu);
        menu.add(about);
        menu.add(exit);
        about.addActionListener(this);
        exit.addActionListener(this);
        BorderLayout bl = new BorderLayout();//布局管理器
        north.setLayout(bl);
        north.add(bar,BorderLayout.NORTH);
        add(north,BorderLayout.NORTH);

        //east 好友列表
        Dimension dim = new Dimension(100,150);
        east.setPreferredSize(dim);//在使用了布局管理器后用setPreferredSize来设置窗口大小
        BorderLayout bl2 = new BorderLayout();//布局管理器
        east.setLayout(bl2);
        east.add(listPane,BorderLayout.CENTER);//显示好友列表
        add(east,BorderLayout.EAST);
        userList.setFont(new Font("隶书",Font.BOLD,18));//设置标题

        //center 聊天消息框  发送消息操作面板
        jta.setEditable(false);//消息显示框是不能编辑的
        jTextArea.setEditable(false);
        jTextArea2.setEditable(false);

        BorderLayout bl3 = new BorderLayout();//布局管理器
        center.setLayout(bl3);
        FlowLayout fl = new FlowLayout(FlowLayout.LEFT);//不顾管理器，左边对齐
        operPane.setLayout(fl);
        operPane.add(input);
        operPane.add(jtf);
        operPane.add(jbt);
        operPane.add(jbt1);
        operPane.add(jbt2);
        center.add(js,BorderLayout.CENTER);//js是消息展示框JScrollPane
        center.add(operPane,BorderLayout.SOUTH);//发送系统放在下面
        add(center,BorderLayout.CENTER);

        js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);//需要时才显示滚动条

        //鼠标事件，点击
        jbt.addActionListener(this);//群发消息
        jbt1.addActionListener(this);//私发消息
        jbt2.addActionListener(this);//班级群发
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);//关闭的方式

        //nickName = JOptionPane.showInputDialog("用户名：");

        this.setTitle(nickName + "的聊天室");
        this.setSize(800,400);
        this.setVisible(true);

        Socket s = new Socket("127.0.0.1", 9999);
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        ps = new PrintStream(s.getOutputStream());
        new Thread(this).start();//run()
        ps.println("LOGIN#" + nickName);//发送登录信息，消息格式：LOGIN#nickName

        jtf.setFocusable(true);//设置焦点

        //键盘事件，实现当输完要发送的内容后，直接按回车键，实现发送
        //监听键盘相应的控件必须是获得焦点（focus）的情况下才能起作用

        jtf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {//回车键判断
                    ps.println("MSG#" + nickName + "#" +  jtf.getText());//发送消息的格式：MSG#nickName#message
                    jtf.setText("");//发送完后，是输入框中内容为空
                }
            }
        });

        //私聊消息框按回车发送消息
        jTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleSS();//私聊信息
                }
            }
        });

        //班级内私聊消息框按回车发送消息
        jTextField2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    classSS();//班级群群聊信息
                }
            }
        });

        //监听系统关闭事件，退出时给服务器端发出指定消息
        this.addWindowListener(new WindowAdapter() {//退出的时候方便更新服务器的消息
            @Override
            public void windowClosing(WindowEvent e) {
                ps.println("OFFLINE#" + nickName);//发送下线信息，消息格式：OFFLINE#nickName
            }
        });

        this.addComponentListener(new ComponentAdapter() {//监听父窗口大小的改变
            public void componentMoved(ComponentEvent e) {
                Component comp = e.getComponent();
                MAIN_FRAME_LOC_X = comp.getX();
                MAIN_FRAME_LOC_Y = comp.getY();
            }
        });
    }

    public void run(){//客户端与服务器端发消息的线程
        while (true){
            try{
                String msg = br.readLine();//读取服务器是否发送了消息给该客户端
                String[] strs = msg.split("#");
                //判断是否为服务器发来的登陆信息
                if(strs[0].equals("LOGIN")){
                    if(!strs[1].equals(nickName)){//不是本人的上线消息就显示，本人的不显示
                        jta.append(strs[1] + "上线啦！\n");//显示别人上线了
                        dl.addElement(strs[1]);//DefaultListModel来更改JList的内容
                        userList.repaint();
                    }
                }
                //判断别人群发的消息
                else if(strs[0].equals("MSG")){
                    if(!strs[1].equals(nickName)){//别人说的
                        jta.append(strs[1] + "说：" + strs[2] + "\n");
                    }else{
                        jta.append("我说：" + strs[2] + "\n");
                    }
                }

                //USER消息，为新建立的客户端更新好友列表
                else if(strs[0].equals("USERS")){
                    dl.addElement(strs[1]);
                    userList.repaint();
                }

                //判断系统群发的消息
                else if(strs[0].equals("ALL")){
                    jta.append("系统消息：" + strs[1] + "\n");
                }

                //判断有人下线了
                else if(strs[0].equals("OFFLINE")){
                    if(strs[1].equals(nickName)) {//如果是自己下线的消息，说明被服务器端踢出聊天室，强制下线
                        javax.swing.JOptionPane.showMessageDialog(this, "您已退出聊天室！");//弹出界面，显示已经给踢出的聊天
                        this.setVisible(false);
                        return;
                        //System.exit(0);//退出系统
                    }
                    //提示别的人已经下线了
                    jta.append(strs[1] + "下线啦！\n");
                    dl.removeElement(strs[1]);//更改好友列表
                    userList.repaint();
                }

                //判断私发消息
                else if((strs[2].equals(nickName) || strs[1].equals(nickName)) && strs[0].equals("SMSG")){
                    if(!strs[1].equals(nickName)){//判断是谁私聊的谁
                        jTextArea.append(strs[1] + "说：" + strs[3] + "\n");//私发消息显示
                        jta.append("系统提示：" + strs[1] + "私信了你" + "\n");//取法消息提示
                    }else{
                        jTextArea.append("我说：" + strs[3] + "\n");
                    }
                }

                //判断是不是第一次私发消息
                else if((strs[2].equals(nickName) || strs[1].equals(nickName)) && strs[0].equals("FSMSG"))
                {
                    //初始化私聊窗口的信息
                    sender = strs[1];
                    receiver = strs[2];
                    //接收方第一次收到私聊消息，自动弹出私聊窗口
                    if(!strs[1].equals(nickName)) {
                        FirstSecret = false;//已经不是第一次私聊了
                        jTextArea.append(strs[1] + "说：" + strs[3] + "\n");
                        jta.append("系统提示：" + strs[1] + "私信了你" + "\n");
                        handleSec(strs[1]);
                    }
                    else {
                        jTextArea.append("我说：" + strs[3] + "\n");
                    }
                }
                //班级群发消息
                else if (strs[2].equals(classnum)&&strs[0].equals("QMSG")) {
                    if(!strs[1].equals(nickName)){//判断是谁私聊的谁
                        jTextArea2.append(strs[1] + "说：" + strs[3] + "\n");//私发消息显示
                        jta.append("系统提示：" + strs[1] + "邀请你进入班级群" + "\n");//取法消息提示
                    }else{
                        jTextArea2.append("我说：" + strs[3] + "\n");
                    }
                }
                //第一次班级群发消息
                else if (strs[2].equals(classnum)&&strs[0].equals("FQMSG")) {
                    //接收方第一次收到私聊消息，自动弹出私聊窗口
                    if(!strs[1].equals(nickName)) {
                        FirstSecret2 = false;//已经不是第一次私聊了
                        jTextArea2.append(strs[1] + "说：" + strs[3] + "\n");
                        jta.append("系统提示：" + strs[1] + "私信了你" + "\n");
                        classSec(classnum);
                    } else {
                        jTextArea2.append("我说：" + strs[3] + "\n");
                    }
                }
            }catch (Exception ex){//如果服务器端出现问题，则客户端强制下线
                javax.swing.JOptionPane.showMessageDialog(this, "您已退出聊天室！");
                this.setVisible(false);
                return;
                // System.exit(0);
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {//鼠标点击事件
        String label = e.getActionCommand();
        if(label.equals("发送消息")){//群发
            handleSend();
        }else if(label.equals("私发消息") && !userList.isSelectionEmpty()){//未点击用户不执行
            suser = userList.getSelectedValuesList().get(0);//获得被选择的用户
            handleSec(suser);//创建私聊窗口
            sender = nickName;
            receiver = suser;
        }else if(label.equals("发消息")){
            handleSS();//私发消息
        }else if(label.equals("关于本软件")){
            JOptionPane.showMessageDialog(this, "1.可以在聊天框中进行群聊\n\n2.可以点击选择用户进行私聊");
        }else if(label.equals("退出")){
            JOptionPane.showMessageDialog(this, "您已成功退出！");
            ps.println("OFFLINE#" + nickName);
            System.exit(0);
        }else if (label.equals("班级内私发消息")){
            classSec(classnum);//创建班级内聊天的窗口
        }else if (label.equals("班级内发消息")){
            classSS();
        }
        else{
            System.out.println("不识别的事件");
        }
    }

    public void handleSS(){//在私聊窗口中发消息
        String name=sender;
        if(sender.equals(nickName)) {
            name = receiver;
        }
        if(FirstSecret) {//第一次发消息
            System.out.println(jTextField.getText());
            ps.println("FSMSG#" + nickName + "#" + name + "#" + jTextField.getText());

            System.out.println("FSMSG#" + nickName + "#" + name + "#" + jTextField.getText());

            jTextField.setText("");
            FirstSecret = false;
        }
        else {//普通私发消息
            System.out.println(jTextField.getText());
            ps.println("SMSG#" + nickName + "#" + name + "#" + jTextField.getText());

            System.out.println("SMSG#" + nickName + "#" + name + "#" + jTextField.getText());

            jTextField.setText("");
        }
    }

    public void classSS(){//在班级群里面发消息
        if(FirstSecret2) {
            System.out.println(jTextField2);
            ps.println("FQMSG#" + nickName + "#" + classnum + "#" + jTextField2.getText());

            System.out.println("FQMSG#" + nickName + "#" + classnum + "#" + jTextField2.getText());

            jTextField2.setText("");
            FirstSecret2=false;
        }else {//普通私发消息
            System.out.println(jTextField2.getText());
            ps.println("QMSG#" + nickName + "#" + classnum + "#" + jTextField2.getText());

            System.out.println("QMSG#" + nickName + "#" + classnum + "#" + jTextField2.getText());

            jTextField2.setText("");
        }
    }

    public void handleSend(){//群发消息
        //发送信息时标识一下来源
        ps.println("MSG#" + nickName + "#" +  jtf.getText());

        System.out.println("MSG#" + nickName + "#" +  jtf.getText());

        //发送完后，是输入框中内容为空
        jtf.setText("");
    }

    public void classSec(String classid){
        JFrame jFrame = new JFrame();//新建了一个窗口 初始化私发的消息
        JPanel JPL = new JPanel();
        JPanel JPL2 = new JPanel();
        FlowLayout f2 = new FlowLayout(FlowLayout.LEFT);
        JPL.setLayout(f2);
        JPL.add(jTextField2);
        JPL.add(jButton2);
        JPL2.add(js2,BorderLayout.CENTER);
        JPL2.add(JPL,BorderLayout.SOUTH);
        jFrame.add(JPL2);

        jButton2.addActionListener(this);
        jTextArea2.setFont(new Font("宋体", Font.PLAIN,15));
        jFrame.setSize(400,310);
        jFrame.setLocation((int)MAIN_FRAME_LOC_X+20,(int)MAIN_FRAME_LOC_Y+20);//将私聊窗口设置总是在父窗口的中间弹出
        jFrame.setTitle(classid+"班班级群");
        jFrame.setVisible(true);

        jTextField2.setFocusable(true);//设置焦点

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {//关掉窗口之后
                jTextArea2.setText("");
            }
        });
    }

    public void handleSec(String name){ //建立私聊窗口
        JFrame jFrame = new JFrame();//新建了一个窗口 初始化私发的消息
        JPanel JPL = new JPanel();
        JPanel JPL2 = new JPanel();
        FlowLayout f2 = new FlowLayout(FlowLayout.LEFT);
        JPL.setLayout(f2);
        JPL.add(jTextField);
        JPL.add(jButton);
        JPL2.add(js1,BorderLayout.CENTER);
        JPL2.add(JPL,BorderLayout.SOUTH);
        jFrame.add(JPL2);

        jButton.addActionListener(this);
        jTextArea.setFont(new Font("宋体", Font.PLAIN,15));
        jFrame.setSize(400,310);
        jFrame.setLocation((int)MAIN_FRAME_LOC_X+20,(int)MAIN_FRAME_LOC_Y+20);//将私聊窗口设置总是在父窗口的中间弹出
        jFrame.setTitle("与" + name + "私聊中");
        jFrame.setVisible(true);

        jTextField.setFocusable(true);//设置焦点

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {//关掉窗口之后
                jTextArea.setText("");
                FirstSecret = true;
            }
        });
    }//私聊窗口

    public static void main(String[] args)throws Exception{
        new Login();
        new Login();
        new Login();
    }
}