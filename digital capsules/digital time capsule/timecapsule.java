import java.util.*;
import java.io.*;
import java.text.*;

public class timecapsule{
    static Scanner sc=new Scanner(System.in);

    public static void main(String[] args)throws Exception{
        while(true){
            System.out.println("\n1.Create Capsule");
            System.out.println("2.Open Capsule");
            System.out.println("3.List Capsules");
            System.out.println("4.Exit");
            int ch=sc.nextInt();
            sc.nextLine();

            if(ch==1)create();
            else if(ch==2)open();
            else if(ch==3)list();
            else break;
        }
    }

    static void create()throws Exception{
        System.out.println("Enter capsule name:");
        String name=sc.nextLine();

        System.out.println("Enter message:");
        String msg=sc.nextLine();

        System.out.println("Set password:");
        String pass=sc.nextLine();

        System.out.println("Enter unlock date (yyyy-MM-dd HH:mm):");
        String date=sc.nextLine();

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d=sdf.parse(date);
        long unlock=d.getTime();

        FileWriter fw=new FileWriter(name+".txt");
        fw.write(msg+"\n");
        fw.write(pass+"\n");
        fw.write(Long.toString(unlock));
        fw.close();

        System.out.println("🔒 Capsule '"+name+"' created!");
    }

    static void open()throws Exception{
        System.out.println("Enter capsule name:");
        String name=sc.nextLine();

        File f=new File(name+".txt");

        if(!f.exists()){
            System.out.println("❌ Capsule not found");
            return;
        }

        BufferedReader br=new BufferedReader(new FileReader(f));
        String msg=br.readLine();
        String pass=br.readLine();
        long unlock=Long.parseLong(br.readLine());
        br.close();

        long current=System.currentTimeMillis();

        if(current<unlock){
            System.out.println("⏳ Still locked!");
            return;
        }

        int attempts=3;
        while(attempts>0){
            System.out.println("Enter password:");
            String p=sc.nextLine();

            if(p.equals(pass)){
                System.out.println("\n📜 Message from past:");
                System.out.println(msg);

                // delete after opening
                f.delete();
                System.out.println("🗑 Capsule deleted after opening");
                return;
            }else{
                attempts--;
                System.out.println("❌ Wrong password! Attempts left: "+attempts);
            }
        }

        System.out.println("🚫 Access blocked!");
    }

    static void list(){
        File dir=new File(".");
        String[] files=dir.list();

        System.out.println("\n📂 Available Capsules:");

        for(String file:files){
            if(file.endsWith(".txt")){
                System.out.println(file.replace(".txt",""));
            }
        }
    }
}