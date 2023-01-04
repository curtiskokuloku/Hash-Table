// Implementation of  hash table solutions to two common situations (general - unknown data, and specific - known data)
// with the goal of minimizing the number of key collisions.

// Created by Curtis Kokuloku (kokul003)

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class HashTable<T>{

    // Instance variables
    NGen<T>[] hashTable;
    private int size;
    public String type;

    /**
     * Constructor - initializes the hash table array, with its size and type from the user
     */
    @SuppressWarnings("unchecked")
    public HashTable() {
        type = getType();
        size = getSize();
        hashTable = new NGen[size];
    }

    /**
     * hash1() - a simple hash function (used for general case)
     * @param item - the item/key that is to be stored in the hash table
     * @return     - an integer value representing the key at which the item will be store
     */
    public int hash1(T item) {
        String token = item.toString();
        int hashValue = 0;

        for (int i = 0; i < token.length(); i++) {
            hashValue += (token.charAt(i) * token.length()) % size;
        }
        return (hashValue % size);
    }

    /**
     * hash2() - a second (different and improved) hash function (used for specific case)
     * @param item - the item/key that is to be stored in the hash table
     * @return     - a 'unique' integer value representing the key at which the item will be store
     */
    public int hash2(T item) {
        int hashValue = 0;

        String token = item.toString();
        int length = token.length();
        char[] charList = token.toCharArray();

        for (int i = 0; i < length; i++) {
            hashValue = (2069 * length * charList[i]) % size;
        }
        return hashValue;
    }

    /**
     * getSize() - helper method that gets size of the hash table that is a prime
     * @return   - an integer value representing the size
     */
    private int getSize() {
        int size = 0;
        if (type.equals("specific")) {
            size = getRandomPrime(150, 500);
        } else {
            size = getRandomPrime(100, 150);
        }
        return size;
    }

    /**
     * getType() - helper method to get the type of the hash table from the user
     * @return   - a string (word) representing the size
     */
    private String getType() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEnter the type of the hash table (general or specific): ");
        String type = scanner.nextLine();
        boolean valid = (type.equals("general") || type.equals("specific"));
        if (!valid) {
            System.out.println("\nInvalid type. Defaulting to general...");
            type = "general";
        }
        return type;
    }

    /**
     * getRandomPrime() - helper method that gets a random prime number in the range
     * @param lower - the smallest length of the hash table
     * @param upper - the largest length of the hash
     * @return      - a random prime integer value within bounds of the required range
     */
    private int getRandomPrime(int lower, int upper) {
        Random r = new Random();
        while (true) {
            int randomNumber = r.nextInt((upper - lower) + 1) + lower;
            if (isPrime(randomNumber)) {
                return randomNumber;
            }
        }
    }

    /**
     * isPrime() - helper method that returns a boolean depending on num
     * @param num - the given number we are checking
     * @return    - true if the number is prime, false otherwise
     */
    private boolean isPrime(int num) {
        // check if num is a multiple of 2
        if (num % 2 == 0) {
            return false;
        }
        // if not, then we check the odds
        for (int i = 3; i * i < num; i+=2) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * add() - adds an item to the hash table using the best performing hash function (Does NOT add duplicate items)
     * @param item - the item/key that is to be stored in the hash table
     */
    public void add(T item) {

        int hashValue;

        if (type.equals("specific")) {
            hashValue = hash2(item);     // the specific case
        } else {
            hashValue = hash1(item);     // the general case
        }

        NGen<T> currentKey = hashTable[hashValue];
        if (currentKey == null) {  // if there is no element at that hash key
            hashTable[hashValue] = new NGen<>(item, null);
        } else {
            // using chaining
            while (currentKey.getNext() != null) {
                if (currentKey.getData().equals(item)) {  // checking collision/duplicate items
                    return;
                }
                currentKey = currentKey.getNext();
            }
            if (!currentKey.getData().equals(item)) {  // checking collision/duplicate items
                currentKey.setNext(new NGen<>(item, null));
            }
        }
    }

    /**
     * addWordsFromFile() - Adds all words from a given file to the hash table using the add(T item) method
     * @param fileName - the given file
     */
    @SuppressWarnings("unchecked")
    public void addWordsFromFile(String fileName) {
        Scanner fileScanner = null;
        String word;
        try {
            fileScanner = new Scanner(new File(fileName));
        }
        catch (FileNotFoundException e) {
            System.out.println("File: " + fileName + " not found.");
            System.exit(1);
        }
        while (fileScanner.hasNext()) {
            word = fileScanner.next();
            word = word.replaceAll("\\p{Punct}", ""); // removes punctuation
            this.add((T) word);
        }
    }

    /**
     * display() - prints the indices of the hash table and the number of words "hashed" to each index.
     * Also prints:
     * - total number of unique words
     * - number of empty indices
     * - number of nonempty indices
     * - average collision length
     * - length of the longest chain
     */
    public void display() {
        int empty = 0, uniqueTokens = 0, nonEmpty = 0, longestChain = 0;
        double averageCollisionLength;

        for (int key = 0; key < hashTable.length; key++) {
            NGen<T> list = hashTable[key];
            if (list == null) {     // case 1: key is null
                empty++;
                System.out.println(key + ": " + 0);
            } else {                // case 2: key is not null
                nonEmpty++;
                int count = 0;
                NGen<T> currentKey = list;
                while (currentKey != null) {
                    uniqueTokens++;
                    count++;
                    currentKey = currentKey.getNext();
                }
                if (count > longestChain) {
                    longestChain = count;
                }
                System.out.println(key + ": " + count);
            }
        }
        averageCollisionLength = ((double) uniqueTokens / nonEmpty);

        printInformation(uniqueTokens, empty, nonEmpty, longestChain, averageCollisionLength);
    }

    /**
     * printInformation() - helper method that prints information about the hash map
     * @param token - number of unique words/tokens
     * @param empty - number of empty indices
     * @param nonEmpty  - number of non-empty indices
     * @param longest - length of the longest chain
     * @param average - average collision length
     */
    private void printInformation(int token, int empty, int nonEmpty, int longest, double average) {
        System.out.println();
        System.out.println("Length of the hash table: " + size);
        System.out.println();
        System.out.println("Number of unique words: " + token);
        System.out.println("Number of empty indices: " + empty);
        System.out.println("Number of non empty indices: " + nonEmpty);
        System.out.printf("Average collision length: %.4f \n", average);
        System.out.println("Length of longest chain: " + longest);
        System.out.println();
    }


    public static void main(String[] args) {
        // hash instances
        HashTable<String> hashTable = new HashTable<>();

        if (hashTable.type.equals("general")) { // general case
            System.out.println("\n[General case]\n");
            hashTable.addWordsFromFile("gettysburg.txt");
            hashTable.display();
        } else { // specific case
            System.out.println("[Specific case]\n");
            hashTable.addWordsFromFile("keywords.txt");
            hashTable.display();
        }
    }
}