package com.viking.ui;

import com.viking.map.Maps;
import com.viking.util.CommonUtil;
import com.viking.util.CustomLog;
import com.viking.util.Direction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 主窗口
 * Created by Viking on 2020/8/1
 */
public class MainFrame extends Frame implements KeyListener {

    private JLabel lab_wolf;

    private int index = -1;
    // 地图数组
    private byte[][] map;

    private int tx;
    private int ty;
    private int num = 0;// 任务完成计数器
    private int total = 0;//通关所需任务完成数

    private JLabel[][] sheeps;

    private static CustomLog log = new CustomLog(MainFrame.class);
    // 构造器
    public void init(){
        log.info("游戏加载中...");
        lab_wolf = null;
        num = 0;
        total = 0;
        map = Maps.getMap(index);
        sheeps = new JLabel[map.length][map[0].length];
        log.info("初始化游戏地图...");
        initGame();// 初始化游戏地图
        log.info("初始化窗口属性...");
        initFrame();// 初始化窗口属性
    }
    public void reload(){
        ++index;
        this.removeAll();
        init();
    }

    // 初始化游戏
    private void initGame(){
        Icon treeIcon = CommonUtil.getImageIcon("/static/img/tree.png");
        Icon cageIcon = CommonUtil.getImageIcon("/static/img/cage.png");
        Icon wolfIcon = CommonUtil.getImageIcon("/static/img/wolf_down.png");
        Icon sheepIcon = CommonUtil.getImageIcon("/static/img/sheep.png");
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == CommonUtil.CAGE) {
                    JLabel cage = new JLabel(cageIcon);
                    cage.setBounds(10 + 50 * j, 30 + 50 * i, 50, 50);
                    this.add(cage);
                    total++;
                } else if (map[i][j] == CommonUtil.TREE) {
                    JLabel tree = new JLabel(treeIcon);
                    tree.setBounds(10 + 50 * j, 30 + 50 * i, 50, 50);
                    this.add(tree);
                } else if (map[i][j] == CommonUtil.WOLF) {
                    lab_wolf = new JLabel(wolfIcon);
                    lab_wolf.setBounds(10 + 50 * j, 30 + 50 * i, 50, 50);
                    this.add(lab_wolf);
                    tx = j;
                    ty = i;
                } else if (map[i][j] == CommonUtil.SHEEP) {
                    JLabel sheep = new JLabel(sheepIcon);
                    sheep.setBounds(10 + 50 * j, 30 + 50 * i, 50, 50);
                    sheeps[i][j] = sheep;
                    this.add(sheep);
                }
            }
        }
        Icon bgpIcon = CommonUtil.getImageIcon("/static/img/backgroundPhoto.png");
        JLabel background = new JLabel(bgpIcon);
        background.setBounds(10,30,map[0].length*50,map.length*50);
        this.add(background);
    }
    // 初始化窗体信息
    private void initFrame(){
        this.setLayout(null);
        this.setTitle("推箱子v1.0");
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(map[0].length*50+20,map.length*50+40);
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        this.addKeyListener(this);// 设置监听
    }

    private void victory() {
        if (num == total) {
            // 移除窗体键盘事件，避免用户多余操作
            this.removeKeyListener(this);
            System.out.println("win");

            VictoryDialog victory = new VictoryDialog(this, "恭喜你取得了胜利");
            victory.setSize(400, 300);
            victory.setLocationRelativeTo(null);
            victory.setLayout(null);
            JLabel win = new JLabel(CommonUtil.getImageIcon("/static/img/win.jpg"));
            win.setBounds(2, 2, 380, 180);
            victory.setContent(win);
            JButton nextButton = new JButton("下一关");
            nextButton.setBounds(140, 200, 120, 30);
            victory.setNext(nextButton,this);
        }
    }

    // 上移
    private void moveUp(){
        int fd = map[ty - 1][tx];// 前面
        int ffd = ty - 2 < 0 ? 1 : map[ty - 2][tx];// 前面的前面

        // 不能走的情况
        if (noWay(fd,ffd)){
            notNeedMove(Direction.UP);
            return;
        }

        // 前方没有东西或者前方是笼子的情况 直接走
        if (fd == CommonUtil.BANK || fd == CommonUtil.CAGE) {
            moveWolf(Direction.UP);
            ty = ty - 1;
            return;
        }
        // 推着箱子可以往前走
        if (fd == CommonUtil.SHEEP && ffd == CommonUtil.BANK) {
            map[ty - 1][tx] = CommonUtil.BANK;
            map[ty - 2][tx] = CommonUtil.SHEEP;
        }
        // 推着箱子遇到了 目标地点
        if (fd == CommonUtil.SHEEP && ffd == CommonUtil.CAGE) {
            map[ty - 1][tx] = CommonUtil.BANK;
            map[ty - 2][tx] = CommonUtil.SHEEP_IN_CAGE;
            num++;
        }
        // 从目标里面把箱子推出来了
        if (fd == CommonUtil.SHEEP_IN_CAGE && ffd == CommonUtil.BANK) {
            map[ty - 1][tx] = CommonUtil.CAGE;
            map[ty - 2][tx] = CommonUtil.SHEEP;
            num--;
        }
        // 所在目标地 互换了
        if (fd == CommonUtil.SHEEP_IN_CAGE && ffd == CommonUtil.CAGE) {
            map[ty - 1][tx] = CommonUtil.CAGE;
            map[ty - 2][tx] = CommonUtil.SHEEP_IN_CAGE;
        }
        sheeps[ty - 1][tx].setLocation(10 + tx * 50, 30 + ty * 50 -  100);
        sheeps[ty - 2][tx] = sheeps[ty - 1][tx];
        sheeps[ty - 1][tx] = null;
        moveWolf(Direction.UP);
        ty = ty - 1;
    }
    // 下移
    private void moveDown(){
        int fd = map[ty + 1][tx];// 前面
        int ffd = ty + 2 >= map.length? 1 : map[ty + 2][tx];// 前面的前面

        // 不能走的情况
        if (noWay(fd,ffd)){
            notNeedMove(Direction.DOWN);
            return;
        }

        // 前方没有东西或者前方是笼子的情况 直接走
        if (fd == CommonUtil.BANK || fd == CommonUtil.CAGE) {
            moveWolf(Direction.DOWN);
            ty = ty + 1;
            return;
        }
        // 推着箱子可以往前走
        if (fd == CommonUtil.SHEEP && ffd == CommonUtil.BANK) {
            map[ty + 1][tx] = CommonUtil.BANK;
            map[ty + 2][tx] = CommonUtil.SHEEP;
        }
        // 推着箱子遇到了 目标地点
        if (fd == CommonUtil.SHEEP && ffd == CommonUtil.CAGE) {
            map[ty + 1][tx] = CommonUtil.BANK;
            map[ty + 2][tx] = CommonUtil.SHEEP_IN_CAGE;
            num++;
        }
        // 从目标里面把箱子推出来了
        if (fd == CommonUtil.SHEEP_IN_CAGE && ffd == CommonUtil.BANK) {
            map[ty + 1][tx] = CommonUtil.CAGE;
            map[ty + 2][tx] = CommonUtil.SHEEP;
            num--;
        }
        // 所在目标地 互换了
        if (fd == CommonUtil.SHEEP_IN_CAGE && ffd == CommonUtil.CAGE) {
            map[ty + 1][tx] = CommonUtil.CAGE;
            map[ty + 2][tx] = CommonUtil.SHEEP_IN_CAGE;
        }
        sheeps[ty + 1][tx].setLocation(10 + tx * 50, 30 + ty * 50 + 100);
        sheeps[ty + 2][tx] = sheeps[ty + 1][tx];
        sheeps[ty + 1][tx] = null;
        moveWolf(Direction.DOWN);
        ty = ty + 1;
    }
    // 左移
    private void moveLeft(){
        int fd = map[ty][tx - 1];// 前面
        int ffd = tx - 2 < 0 ? 1 : map[ty][tx - 2];// 前面的前面

        // 不能走的情况
        if (noWay(fd,ffd)){
            notNeedMove(Direction.LEFT);
            return;
        }

        // 前方没有东西或者前方是笼子的情况 直接走
        if (fd == CommonUtil.BANK || fd == CommonUtil.CAGE) {
            moveWolf(Direction.LEFT);
            tx = tx - 1;
            return;
        }
        // 推着箱子可以往前走
        if (fd == CommonUtil.SHEEP && ffd == CommonUtil.BANK) {
            map[ty][tx - 1] = CommonUtil.BANK;
            map[ty][tx - 2] = CommonUtil.SHEEP;
        }
        // 推着箱子遇到了 目标地点
        if (fd == CommonUtil.SHEEP && ffd == CommonUtil.CAGE) {
            map[ty][tx - 1] = CommonUtil.BANK;
            map[ty][tx - 2] = CommonUtil.SHEEP_IN_CAGE;
            num++;
        }
        // 从目标里面把箱子推出来了
        if (fd == CommonUtil.SHEEP_IN_CAGE && ffd == CommonUtil.BANK) {
            map[ty][tx - 1] = CommonUtil.CAGE;
            map[ty][tx - 2] = CommonUtil.SHEEP;
            num--;
        }
        // 所在目标地 互换了
        if (fd == CommonUtil.SHEEP_IN_CAGE && ffd == CommonUtil.CAGE) {
            map[ty][tx - 1] = CommonUtil.CAGE;
            map[ty][tx - 2] = CommonUtil.SHEEP_IN_CAGE;
        }
        sheeps[ty][tx - 1].setLocation(10 + tx * 50 - 100, 30 + ty * 50);
        sheeps[ty][tx - 2] = sheeps[ty][tx - 1];
        sheeps[ty][tx - 1] = null;
        moveWolf(Direction.LEFT);
        tx = tx - 1;
    }
    // 右移
    private void moveRight(){

        int fd = map[ty][tx + 1];// 前面
        int ffd = tx + 2 >= map[ty].length? 1 : map[ty][tx + 2];// 前面的前面

        // 不能走的情况
        if (noWay(fd,ffd)){
            notNeedMove(Direction.RIGHT);
            return;
        }

        // 前方没有东西或者前方是笼子的情况 直接走
        if (fd == CommonUtil.BANK || fd == CommonUtil.CAGE) {
            moveWolf(Direction.RIGHT);
            tx = tx + 1;
            return;
        }
        // 推着箱子可以往前走
        if (fd == CommonUtil.SHEEP && ffd == CommonUtil.BANK) {
            map[ty][tx + 1] = CommonUtil.BANK;
            map[ty][tx + 2] = CommonUtil.SHEEP;
        }
        // 推着箱子遇到了 目标地点
        if (fd == CommonUtil.SHEEP && ffd == CommonUtil.CAGE) {
            map[ty][tx + 1] = CommonUtil.BANK;
            map[ty][tx + 2] = CommonUtil.SHEEP_IN_CAGE;
            num++;
        }
        // 从目标里面把箱子推出来了
        if (fd == CommonUtil.SHEEP_IN_CAGE && ffd == CommonUtil.BANK) {
            map[ty][tx + 1] = CommonUtil.CAGE;
            map[ty][tx + 2] = CommonUtil.SHEEP;
            num--;
        }
        // 所在目标地 互换了
        if (fd == CommonUtil.SHEEP_IN_CAGE && ffd == CommonUtil.CAGE) {
            map[ty][tx + 1] = CommonUtil.CAGE;
            map[ty][tx + 2] = CommonUtil.SHEEP_IN_CAGE;
        }
        sheeps[ty][tx + 1].setLocation(10 + tx * 50 + 100, 30 + ty * 50);
        sheeps[ty][tx + 2] = sheeps[ty][tx + 1];
        sheeps[ty][tx + 1] = null;
        moveWolf(Direction.RIGHT);
        tx = tx + 1;
    }

    // 不需要移动，只是切换朝向
    private void notNeedMove(Direction direction){
        int x = (int) lab_wolf.getLocation().getX();
        int y = (int) lab_wolf.getLocation().getY();
        lab_wolf.setLocation(x , y);
        String path = "/static/img/";
        switch (direction){
            case UP: path += "wolf_up.png";break;
            case DOWN: path += "wolf_down.png";break;
            case LEFT: path += "wolf_left.png";break;
            case RIGHT: path += "wolf_right.png";break;
        }
        Icon i = CommonUtil.getImageIcon(path);
        lab_wolf.setIcon(i);
    }
    // 移动狼的位置
    private void moveWolf(Direction direction){
        int x = (int) lab_wolf.getLocation().getX();
        int y = (int) lab_wolf.getLocation().getY();
        String path = "/static/img/";
        switch (direction){
            case UP: {path += "wolf_up.png";y -= 50;map[ty-1][tx] = 5;}break;
            case DOWN: {path += "wolf_down.png"; y += 50;map[ty+1][tx] = 5;}break;
            case LEFT: {path += "wolf_left.png";x -= 50;map[ty][tx-1] = 5;}break;
            case RIGHT: {path += "wolf_right.png";x += 50;map[ty][tx+1] = 5;}break;
        }
        map[ty][tx] = 0;
        lab_wolf.setLocation(x , y);
        Icon i = CommonUtil.getImageIcon(path);
        lab_wolf.setIcon(i);
    }
    private boolean noWay(int fd, int ffd){
        return fd == CommonUtil.TREE
                || (fd == CommonUtil.SHEEP && (ffd == CommonUtil.TREE
                || ffd == CommonUtil.SHEEP || ffd == CommonUtil.SHEEP_IN_CAGE))
                || (fd == CommonUtil.SHEEP_IN_CAGE && (ffd == CommonUtil.TREE
                || ffd == CommonUtil.SHEEP || ffd == CommonUtil.SHEEP_IN_CAGE));
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key){
            case 37:moveLeft();break;// 左
            case 38:moveUp();break;// 上
            case 39:moveRight();break;// 右
            case 40:moveDown();break;// 下
        }
        // 胜利的判定
        victory();
    }
    public void keyTyped(KeyEvent e) {

    }
    public void keyReleased(KeyEvent e) {

    }
}
