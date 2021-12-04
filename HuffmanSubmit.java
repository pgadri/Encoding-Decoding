package com.company;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.PriorityQueue;

//Build a node class for HuffmanSubmit.
class Node implements Comparable<Node>{
    int frequency;
    char character;
    Node left;
    Node right;

    //constructor
    Node(int i, char Char ){
        this.character = Char;
        this.frequency = i;
        left= null;
        right=null;
    }
    Node(int i) {
        this.frequency=i;
        left = null;
        right = null;
    }
    public char getCharValue() {
        return character;
    }
    public int getIntegerValue() {
        return frequency;
    }

    @Override
    public int compareTo(Node other) {
        return this.frequency-other.frequency;
    }

    public void createEncodingMap(HashMap<Character, String> map, String prefix) {
        if (left == null) {
            map.put(character, prefix);
        }
        else {
            left.createEncodingMap(map, prefix + "0");
            right.createEncodingMap(map, prefix + "1");
        }
    }
}

//Huffmas submit method which implements Huffman.java, Binary in and Out to encode and decode out inpit files and out put files.
public class HuffmanSubmit implements Huffman{
    HashMap<Character, Integer> frequency; //checks for the frequencies of the keys.
    HashMap<Character, String> keyAndValueMap; //this pairs the keys with their respective values
    HashMap<String, Character> keyAndValueMapSwaped; //this swaps the keys with their values.
    int num = 0;

    public HuffmanSubmit() {
        frequency = new HashMap<>();
        keyAndValueMap = new HashMap<>();
        keyAndValueMapSwaped = new HashMap<>();
    }

    public void HuffmanTree (Node root, String Str) {
        if (root.left == null && root.right == null) {
            keyAndValueMap.put(root.getCharValue(), Str);
            keyAndValueMapSwaped.put(Str, root.getCharValue());
            return;
        }
        HuffmanTree(root.left, Str + "0");
        HuffmanTree(root.right, Str + "1");
    }

    //Make a priority queue of nodes.
    public Node PriorityQueue() {
        PriorityQueue<Node> Q = new PriorityQueue<>();
        for (char key : this.frequency.keySet()) {
            Node N = new Node(frequency.get(key), key);
            Q.offer(N);
        }
        while (Q.size() > 1) {
            Node N1 = Q.poll();
            Node N2 = Q.poll();

            Node newNode = new Node(N1.getIntegerValue() + N2.getIntegerValue());
            newNode.left = N1;
            newNode.right = N2;
            Q.offer(newNode);
        }
        return Q.poll();
    }

    //encode method to translate out input file into a freqFile which then outputs a file to be decoded.
    public void encode(String inputFile, String outputFile, String freqFile) {
        BinaryIn in;
        BinaryOut out;
        out = new BinaryOut(outputFile);

        in = new BinaryIn(inputFile);
        while (!in.isEmpty()) {
            char Char = in.readChar();
            frequency.put(Char, 0);
        }

        in = new BinaryIn(inputFile);
        while (!in.isEmpty()) {
            char Char = in.readChar();
            num++;
            frequency.put(Char, frequency.get(Char) + 1);
        }
        try {
            PrintWriter writer = new PrintWriter(freqFile);
        } catch (FileNotFoundException Exception) {
            Exception.printStackTrace();
        }
        BufferedWriter outputWriter = null;

        try {
            outputWriter = new BufferedWriter(new FileWriter(freqFile));
        } catch (IOException Exception) {
            Exception.printStackTrace();
        }

        for (char Char : frequency.keySet()) {
            try {
                String convert = Integer.toBinaryString(Char);
                while (convert.length() < 8) {
                    convert = "0" + convert;
                }
                outputWriter.write(convert + ":" + frequency.get(Char));
            } catch (IOException Exception) {
                Exception.printStackTrace();
            }

            try {
                outputWriter.newLine();
            } catch (IOException Exception) {
                Exception.printStackTrace();
            }
        }
        try {
            outputWriter.flush();
        } catch (IOException Exception) {
            Exception.printStackTrace();
        }
        try {
            outputWriter.close();
        } catch (IOException Exception) {
            Exception.printStackTrace();
        }

        Node root = PriorityQueue();
        HuffmanTree(root, "");

        in = new BinaryIn(inputFile);
        out.write(num);
        while (!in.isEmpty()) {
            char Char = in.readChar();
            String Str = keyAndValueMap.get(Char);
            char[] StringData = Str.toCharArray();

            for (char C : StringData) {
                if (C == '0') {
                    out.write(false);
                } else if (C == '1') {
                    out.write(true);
                }
            }
        }
        out.flush();
    }

    //decode method takes the output file and convert it back to the original file.
    public void decode(String inputFile, String outputFile, String freqFile) {

        BinaryIn in = new BinaryIn(inputFile);
        BinaryOut out = new BinaryOut(outputFile);
        HashMap<Character, Integer> map = new HashMap<>();

        BufferedReader BufferedReader = null;
        FileReader FileReader = null;
        try {
            FileReader = new FileReader(freqFile);
            BufferedReader = new BufferedReader(FileReader);
            String sCurrentLine;

            while ((sCurrentLine = BufferedReader.readLine()) != null) {
                String[] array = sCurrentLine.split(":");
                map.put((char) Integer.parseInt(array[0], 2), Integer.parseInt(array[1]));
            }
            frequency = map;
            keyAndValueMap = new HashMap<>();
            HuffmanTree(PriorityQueue(), "");

            in = new BinaryIn(inputFile);
            String Str = "";
            boolean decodeFile = true;
            int newNum = 0;
            int size = in.readInt();

            while (newNum < size) {
                while (!this.keyAndValueMapSwaped.containsKey(Str)) {
                    decodeFile = in.readBoolean();
                    if (decodeFile == true) {
                        Str = Str + "1";
                    } else if (decodeFile == false) {
                        Str = Str + "0";
                    }
                }
                out.write(keyAndValueMapSwaped.get(Str));
                Str = "";
                newNum++;
            }
            out.flush();
        } catch (IOException Exception) {
            Exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Huffman  huffman = new HuffmanSubmit();
        /*
         To run this code for the TEXT PART, please comment out the IMAGE PART.
         To run this code for the IMAGE PART, please comment out the TEXT PART
        */

        //IMAGE PART
        huffman.encode("ur.jpg", "ur.enc", "freq.txt");
        huffman.decode("ur.enc", "ur_dec.jpg", "freq.txt");

        // TEXT PART
        huffman.encode("alice30.txt", "alice30.enc", "alice30freq.txt");
        huffman.decode("alice30.enc", "alice30_dec.txt", "alice30freq.txt");
    }
}





