package com.telegram.app;

import java.net.*;
import java.io.*;


public class NodeThread extends Thread {
  private DataInputStream  messageIn  =  null;
  private DataOutputStream messageOut = null;
  private Node node;
  private Socket socket;
  public int id;


  public NodeThread(Node node, Socket socket, int id) {
    this.node = node;
    this.socket = socket;
    this.id = id;
    System.out.println("Thread started");
  }

  public Socket getSocket(){
    return socket;
  }

  public void run() {
    while (true) {
      try {
        String msgText = messageIn.readUTF();
        Message message = parseMessage(msgText);
        this.node.receiveMessage(id, message);
      } catch (IOException ioe) {
        this.node.listener.messageReceived(new Message(null, "Error: " + ioe.getMessage(), "err"));
      }
    }
  }

  public void open() throws IOException {
    this.messageIn = new DataInputStream(this.socket.getInputStream());
    this.messageOut = new DataOutputStream(this.socket.getOutputStream());
  }


  public void sendMessage(Message msg) {
    char filler = '\u25CE';
    String message = String.join(Character.toString(filler), msg.author, (msg.timestamp).toString(), msg.messageContent, msg.type);
    try {
      this.messageOut.writeUTF(message);
    } catch (IOException ioe) {
      this.node.listener.messageReceived(new Message(null, "Error: " + ioe.getMessage(), "err"));
    }
  }

  private Message parseMessage(String msg) {
    String[] split = msg.split("[\u25CE]");
    Message message = new Message(split[0], split[2], split[3]);
    return message;
  }

}
