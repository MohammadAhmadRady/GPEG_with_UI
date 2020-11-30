package JPEG;

import javafx.util.Pair;
import java.io.*;
import java.util.*;

class Node implements Comparator<Node>
{
    float freq;
    int first ;
    int second;
    Node left;
    Node right;
    @Override
    public int compare(Node F,Node S)
    {
        return (int) F.freq - (int) S.freq ;
    }
}

public class JPEG
{
    private static String input= "";
    private static String willSent = "";
    private static String totalMap= "";

    private static ArrayList<Pair<Integer,String>> number_code = new ArrayList<>();
    private static ArrayList<Pair<Integer,Integer>> zero_next = new ArrayList<>();
    private static ArrayList<Pair<Integer,Integer>> mixed = new ArrayList<>();

    private static ArrayList<Integer> category1 = new ArrayList<>();private static ArrayList<Integer> category2 = new ArrayList<>();
    private static ArrayList<Integer> category3 = new ArrayList<>();private static ArrayList<Integer> category4 = new ArrayList<>();

    private static Map<Pair<Integer,Integer>,Integer> the_map = new HashMap<>();
    private static Map<Pair<Integer,Integer>,String> LAST = new HashMap<>();

    private static int EndOfBits = 0;
    public JPEG(String in) throws Exception // Constructor
    {
        divide(takeInput("data.txt"));         // take category table file
        fillCategory();
        input = in;
        int numOf0s = 0;
        String [] code = input.split(" ");
        ArrayList<Integer> Arqam = new ArrayList<>();
        for(int i=0;i<code.length;i++)
        {
            Arqam.add(Integer.valueOf(code[i]));
        }
        for(int i=0;i<Arqam.size();i++)
        {
            if(Arqam.get(i)==0)
                numOf0s++;
            else
            {
                Pair <Integer,Integer> p = new Pair<>(numOf0s,Arqam.get(i));
                zero_next.add(p);
                System.out.println(numOf0s + "  "+Arqam.get(i));
                numOf0s=0;
            }
        }
        EndOfBits = numOf0s;
        fillMixed();
        fillTheMap();
        Building();
        System.out.println("EndOfBits: " + EndOfBits );
        System.out.println("Number of 0's and Next: " + zero_next);
        System.out.println("Tag and Huffman: " + LAST);
        System.out.println("Number and Code: " + number_code);

        workOnMixed();
        writeMap();
        LAST.clear();
        fillLAST();
        decompress();
    }
    private static void fillLAST() throws Exception
    {
        String theCode = takeInput("totalMap.txt");
        String [] val = theCode.split("#");
        for(int i=0;i<val.length;i++)
        {
            String [] tag = val[i].split(" ");
            int f = Integer.valueOf(tag[0]);
            int s = Integer.valueOf(tag[1]);
            Pair<Integer,Integer> p = new Pair<>(f,s);
            String c = tag[2];
            LAST.put(p,c);
        }
    }
    private static void writeMap() throws Exception
    {
        for (Map.Entry<Pair<Integer, Integer>, String> entry : LAST.entrySet())
        {
            Pair<Integer, Integer> p = new Pair<>(entry.getKey().getKey(),entry.getKey().getValue());
            String code = entry.getValue();
            totalMap += Integer.toString(p.getKey());
            totalMap += " ";
            totalMap += Integer.toString(p.getValue());
            totalMap += " ";
            totalMap += code;
            totalMap += "#";
        }
        WriteToFile(totalMap,"totalMap.txt");
    }
    private static void decompress() throws Exception
    {
        String right = "";
        String compressed = takeInput("compressed.txt");
        String [] codes = compressed.split(",");
        for(int i=0;i<codes.length;i++)
        {
            String temp = "";
            for(int j=0;j<codes[i].length();j++)
            {
                temp += codes[i].charAt(j);
                for (Map.Entry<Pair<Integer, Integer>, String> entry : LAST.entrySet())
                {
                    if(temp.equals(entry.getValue()))
                    {
                        Pair<Integer, Integer> p = new Pair<>(entry.getKey().getKey(),entry.getKey().getValue());
                        int zero = p.getKey();
                        int rest = p.getValue();
                        String the = "";
                        for (int k=0;k<zero;k++)
                        {
                            System.out.print(0 + " ");
                            right+="0";
                            right+=' ';
                        }
                        for(int r=codes[i].length()-rest;r<codes[i].length();r++) //
                            the+=codes[i].charAt(r);
                        j+=rest;
                        for (int q=0;q<number_code.size();q++)
                        {
                            Pair<Integer,String> z = number_code.get(q);
                            if(the.equals(z.getValue()))
                            {
                                System.out.print(z.getKey() + " ");
                                right+=Integer.toString(z.getKey());
                                right+=' ';
                                break;
                            }
                        }
                        temp="";
                    }
                }
            }
        }
        for (int i=0;i<EndOfBits;i++)
        {
            System.out.print("0 ");
            right+="0 ";
        }
        WriteToFile(right,"Original.txt");
    }
    private static void workOnMixed() throws Exception
    {
        for(int i=0;i<mixed.size();i++)
        {
            Pair<Integer,Integer> mix = mixed.get(i);
            Pair<Integer,Integer> zero = zero_next.get(i);
            String code = LAST.get(mix);
            willSent += code;
            int temp = zero.getValue();
            for(int j=0;j<number_code.size();j++)
            {
                Pair<Integer,String> a = number_code.get(j);
                if(temp == a.getKey())
                {
                    String oo = a.getValue();
                    willSent+=oo;
                    willSent+=",";
                    break;
                }
            }
        }
        System.out.println("willSent: " + willSent);
        WriteToFile(willSent,"compressed.txt");
    }
    private static void fillTheMap()
    {
        for(int i=0;i<mixed.size();i++)
        {
            Pair<Integer,Integer> p = mixed.get(i);
            if(!the_map.containsKey(p))
                the_map.put(p,1);
            else
            {
                int temp = the_map.get(p);
                the_map.put(p,++temp);
            }
        }
    }
    private static void fillMixed()
    {
        // zero_next have number of zeros    and    the next number
        for (int i=0;i<zero_next.size();i++)
        {
            if(category1.contains(zero_next.get(i).getValue()))
            {
                Pair<Integer,Integer> p = new Pair<>(zero_next.get(i).getKey(),1);
                mixed.add(p);
            }
            else if(category2.contains(zero_next.get(i).getValue()))
            {
                Pair<Integer,Integer> p = new Pair<>(zero_next.get(i).getKey(),2);
                mixed.add(p);
            }
            else if(category3.contains(zero_next.get(i).getValue()))
            {
                Pair<Integer,Integer> p = new Pair<>(zero_next.get(i).getKey(),3);
                mixed.add(p);
            }
            else if(category4.contains(zero_next.get(i).getValue()))
            {
                Pair<Integer,Integer> p = new Pair<>(zero_next.get(i).getKey(),4);
                mixed.add(p);
            }
        }
        System.out.println("Number of 0's and Category: " + mixed);
    }
    private static void fillCategory()
    {
        category1.add(1);category1.add(-1);
        category2.add(2);category2.add(3);category2.add(-2);category2.add(-3);
        category3.add(4);category3.add(5);category3.add(6);category3.add(7);category3.add(-4);category3.add(-5);category3.add(-6);category3.add(-7);
        category4.add(8);category4.add(9);category4.add(10);category4.add(-8);category4.add(-9);category4.add(-10);
    }
    private static void divide(String allDic) //  fill number_code array
    {
        String [] massive = allDic.split("#");
        for(int i=0;i<massive.length;i++)
        {
            String [] temp = massive[i].split(",");
            int t = Integer.valueOf(temp[0]);
            String tt = temp[1];
            Pair<Integer,String> p = new Pair<>(t,tt);
            number_code.add(p);
        }
    }
    private static String takeInput(String path) throws Exception  // to fill categories
    {
        File file = new File(path);
        BufferedReader READ = new BufferedReader(new FileReader(file));
        int q=READ.read();
        String data = new String("");
        while(q != -1)
        {
            data+=(char)q;
            q=READ.read();
        }
        return data;
    }
    private static void WriteToFile(String data,String path) throws Exception
    {
        File obj1 = new File(path);
        BufferedWriter w= new BufferedWriter(new FileWriter(obj1));
        w.write(data);
        w.close();
    }
    private static void Building()
    {
        PriorityQueue<Node> Q = new PriorityQueue<Node>(the_map.size(),new Node());
        for (Map.Entry<Pair<Integer, Integer>, Integer> entry : the_map.entrySet())
        {
            Node temp = new Node();
            temp.first = entry.getKey().getKey();
            temp.second = entry.getKey().getValue();
            temp.freq = entry.getValue();
            temp.left = null;
            temp.right = null;
            Q.add(temp);
        }
        Node root = new Node();
        while (Q.size()!=1)
        {
            Node Left = Q.poll();
            Node Right = Q.poll();
            Node temp = new Node();
            temp.freq = Left.freq + Right.freq;
            temp.left = Left;
            temp.right = Right;
            temp.first = 0;
            temp.second = 0;
            root = temp;
            Q.add(temp);
        }
        StoringCode(root,"");
    }
    private static void StoringCode(Node root, String code)
    {
        if (root.left  == null && root.right == null && root.first!=0 && root.second!=0 )
        {
            //willSent += root.sym+code+" ";
            Pair <Integer,Integer> t = new Pair<>(root.first,root.second);
            LAST.put(t,code);
            return;
        }
        StoringCode(root.right, code + "1");
        StoringCode(root.left, code + "0");
    }
}