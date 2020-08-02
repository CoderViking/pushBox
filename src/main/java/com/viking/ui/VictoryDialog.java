package com.viking.ui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Viking on 2020/8/1
 */
public class VictoryDialog extends JDialog implements ActionListener {

    private JLabel content;
    private JButton next;
    private Frame patent;


    public VictoryDialog(Frame owner, String title){
        super(owner,title);
        this.patent = owner;
        this.setVisible(true);
    }

    public JLabel getContent() {
        return content;
    }

    public void setContent(JLabel content) {
        this.content = content;
        this.getContentPane().add(this.content);
    }

    public JButton getNext() {
        return next;
    }

    public <T extends Frame> void setNext(JButton next,T parent) {
        this.next = next;
        this.patent = parent;
        this.add(next);
        next.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("下一关".equals(e.getActionCommand())){
            this.setVisible(false);
            if (patent instanceof MainFrame){
                MainFrame frame = (MainFrame) patent;
                frame.reload();
            }
        }
    }
}
