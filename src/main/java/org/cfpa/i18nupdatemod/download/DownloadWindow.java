package org.cfpa.i18nupdatemod.download;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DownloadWindow {
    private DownloadManager manager;
    private JFrame frame;
    private JProgressBar bar;

    public DownloadWindow(DownloadManager manager) {
        this.manager = manager;
        init();
    }

    private void init() {
        // 初始化窗口
        frame = new JFrame();
        Integer width = 450;
        Integer height = 100;

        // 开始修改主界面
        frame.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - height) / 25 * 11, width, height);
        frame.setTitle("汉化资源包更新进度条");

        // 内容界面？
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        frame.setContentPane(contentPane);
        contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        // 绘制进度条
        bar = new JProgressBar();
        bar.setPreferredSize(new Dimension(width / 5 * 4, height / 4));
        bar.setStringPainted(true);
        contentPane.add(bar);

        // 关闭操作
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //进度条更新线程
        new Thread(() -> {
            while (!manager.isDone()) {
                bar.setValue((int) (manager.getCompletePercentage() * 100));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignore) {
                    ignore.printStackTrace();
                }
            }
            onDownloadFinish();
            if (manager.getStatus() == DownloadStatus.FAIL) {
                bar.setString("下载失败！");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            } else {
                frame.setVisible(false);
            }
        }, "I18n-Window-Thread").start();
    }

    public void showWindow() {
        frame.setVisible(true);
    }

    private static void onDownloadFinish() {
        //TODO，我也不知道要做撒
    }
}
