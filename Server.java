import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{

    
    private ArrayList<ConnectionHandler> connections;
private ServerSocket server;
private boolean done;
private ExecutorService pool;
   public Server(){
    done=false;
    connections =new ArrayList<>();
   }
    @Override
    public void run() {
            try  {
                 server = new ServerSocket(9999);
                pool=Executors.newCachedThreadPool();
                 while (!done) {
                    Socket client= server.accept();
                    ConnectionHandler handler =new ConnectionHandler(client);
                    connections.add(handler);
                    pool.execute(handler);
                   
                }
                 } catch (IOException e) {
             
                e.printStackTrace();
            }
    
    }
    public void broadcast(String message){
        for(ConnectionHandler ch:connections){
            if(ch!=null){
                ch.SendMessgae(message);
            }
        }
    }
public void shutdown(){
    try{
    done=true;
    pool.shutdown();
    if(!server.isClosed()){
        server.close() ;
    }
for(ConnectionHandler ch :connections){
   ch.shutdown();
}
} catch(IOException e){
        e.printStackTrace();

    }

}

     class ConnectionHandler implements Runnable  {
        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String name;
        public ConnectionHandler(Socket client){
         this.client=client;       
        }
        
        
        @Override
        public void run(){
            try{
                out=new PrintWriter(client.getOutputStream(),true);
                in =new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("please enter your name");
                name=in.readLine();
                System.out.println(name+"connected");
                broadcast(name+"joined the chat");
            String message;
            while ((message =in.readLine())!=null) {
                if(message.startsWith("/name")){
                   String[] messagesplit=message.split("",2);
                   if(messagesplit.length==2){
                    broadcast(name+"renamed hiself to "+messagesplit[1] );
                System.out.println(name+"renamed hiself to "+messagesplit[1]);
                name=messagesplit[1]; 
                out.println("name has been change succefly");

                }else{
                   out.println("no name provied ");
                }
                    
                }
                else if(message.startsWith("/quit")){
                 broadcast(name+": quit the convertions");
                    shutdown();
                }
                else{
                    broadcast(name+": "+message);
                }
            }
            } catch(IOException e){
                e.printStackTrace();
            }

        }
        public void SendMessgae(String message){
            out.println(message);
        }
        
    public void shutdown(){
       try{
        in.close();
        out.close();
        if(!client.isClosed()){
            client.close();
        }
    } catch(IOException e){
        e.printStackTrace();
    }}
    }
public static void main(String[] args) {
    Server server= new Server();
    server.run();

}
}


