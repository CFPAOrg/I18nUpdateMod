package org.cfpa.i18nupdatemod.download;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DownloadWindow {
    private DownloadManager manager;
    private JFrame frame;
    private JProgressBar bar;

    /**
     * 弹出一个窗口，包含一个进度条显示下载的进度
     * @param manager 对应的DownloadManager对象
     */
    public DownloadWindow(DownloadManager manager) {
        this.manager = manager;
        init();
    }

    private void init() {
        // 创建窗口
        frame = new JFrame();
        frame.setBounds(100, 100, 250, 100);
        frame.setTitle("I18n Update Mod");
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        frame.setContentPane(contentPane);
        contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        // 创建进度条
        bar = new JProgressBar();
        bar.setStringPainted(true);
        contentPane.add(bar);
        // 在下载未完成时禁止玩家关闭窗口
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // 进度条更新线程
        new Thread(() -> {
            while (!manager.isDone()) {
                bar.setValue((int) (manager.getCompletePercentage() * 100));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignore) {

                }
            }
            onDownloadFinish();
            if (manager.getStatus() == DownloadStatus.FAIL) {
                bar.setString("下载失败！");
                // 如果下载失败允许玩家关闭窗口
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            } else {
                // 如果下载完成自动关闭窗口
                frame.setVisible(false);
            }
        }, "I18n-Window-Thread").start();
    }

    public void showWindow() {
        frame.setVisible(true);
    }

    private static void onDownloadFinish() {
        //TODO 其实我也不知道能干啥
    }
}
