package com.nahed.pouyan;

import java.util.ArrayList;
import java.util.List;

public class DictionaryLinkedList {

    private Node head;
    private boolean isCaseSensitive = true;

    public DictionaryLinkedList(boolean isCaseSensitive){
        this.isCaseSensitive = isCaseSensitive;
    }

    public DictionaryLinkedList(){}

    private class Node {

        private Node skipChar;
        private Node nextChar;
        private char data;
        private boolean end;

        public Node(char data){
            this.data = data;
        }
    }

    public void add(String word){
        if(!isCaseSensitive)
            word = word.toLowerCase();

        if(word.length() == 0)
            return;

        char[] chars = word.toCharArray();

        Node previousNode = null;
        Node currentNode = head;
        for(char c : chars){
            if(currentNode == null){
                currentNode = new Node(c);

                if(head == null)
                    head = currentNode;

                if(previousNode != null){
                    previousNode.nextChar = currentNode;
                }
                previousNode = currentNode;
                currentNode = currentNode.nextChar;
            }else if(currentNode.data == c){
                previousNode = currentNode;
                currentNode = currentNode.nextChar;
            }else{
                while(currentNode != null && currentNode.data != c){    // Skipping parallel characters
                    previousNode = currentNode;
                    currentNode = currentNode.skipChar;
                }

                if(currentNode == null){                    // No other parallel character (need to create a new parallel node
                    currentNode = new Node(c);
                    previousNode.skipChar = currentNode;
                    previousNode = currentNode;
                    currentNode = previousNode.nextChar;
                }else{                                      // Found a equal character (move to next)
                    previousNode = currentNode;
                    currentNode = currentNode.nextChar;
                }
            }
        }
        if(previousNode != null)
            previousNode.end = true;
    }


    public List<String> getAllWords(){
        return getAllWords(head, "");
    }


    /**
     * Getting all words from linked list using DFS algorithm
     * @param node
     * @param pre
     * @return
     */
    private List<String> getAllWords(Node node, String pre){
        List<String> words = new ArrayList<>();
        if(node == null)
            return words;

        String word = pre + node.data;      // Adding current word if it is valid
        if(node.end)
            words.add(word);

        words.addAll(getAllWords(node.nextChar, word));    // Adding all next words

        words.addAll(getAllWords(node.skipChar, pre));     // Adding all parallel words

        return words;
    }


    /**
     * Checking if given word contains in list
     * @param word
     * @return return true if dictionary contains word
     */
    public boolean contains(String word){
        if(!isCaseSensitive)
            word = word.toLowerCase();

        char[] chars = word.toCharArray();
        Node currentNode;
        if(head == null){
            return false;
        }else{
            currentNode = head;
        }

        for(int i=0; i<chars.length; i++){
            char c = chars[i];
            Node n = contains(currentNode, c);
            if(n != null){
                currentNode = n;                        // Changing current node to parallel found node
                if(i != chars.length-1)                 // Won't move next on the last character
                    currentNode = currentNode.nextChar;
            }else{
                return false;
            }
        }

        return currentNode.end;
    }


    /**
     * Finding node with data c in parallel side
     * @param node Starting node
     * @param c    Searching charactar
     * @return     Founded node
     */
    private Node contains(Node node, char c){
        Node tmp = node;
        while(tmp != null){
            if(tmp.data == c)
                return tmp;
            tmp = tmp.skipChar;
        }
        return null;
    }

    public void remove(String word){
        if(!isCaseSensitive)
            word = word.toLowerCase();

        char[] chars = word.toCharArray();
        Node currentNode;
        if(head == null){
            return;
        }else{
            currentNode = head;
        }

        for(int i=0; i<chars.length; i++) {
            char c = chars[i];
            Node n = contains(currentNode, c);
            if (n != null) {
                currentNode = n;                           // Changing current node to parallel found node
                if (i != chars.length - 1)                 // Won't move next on the last character
                    currentNode = currentNode.nextChar;
            } else {
                return;
            }
        }

        if(currentNode.end)
            currentNode.end = false;

    }

    /**
     * Get words count in dictionary
     * @return Number of words
     */
    public int getSize(){
        if(head == null)
            return 0;

        return subWords(head);
    }

    // Getting a node sub words using post order graph traverse (containing itself)
    private int subWords(Node n){
        if(n == null)
            return 0;

        int wordsCount = 0;

        if(n.nextChar != null)
            wordsCount += subWords(n.nextChar);

        if(n.skipChar != null)
            wordsCount += subWords(n.skipChar);

        if(n.end)
            wordsCount++;

        return wordsCount;
    }

    public void clean(){
        clean(head, null);
    }

    private void clean(Node n, Node previousNode){
        if(n == null)
            return;

        if(subWords(n) == 0){
            if(previousNode != null){               // Not head
                previousNode.skipChar = n.skipChar;
                clean(n.skipChar, n);
            }
        }else{
            clean(n.nextChar, n);
            clean(n.skipChar, n);
        }

    }

}
