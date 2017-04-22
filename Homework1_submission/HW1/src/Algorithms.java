import LinearRegression.LinearRegression;
import DecisionTree.DecisionTree;

import java.util.Scanner;

public class Algorithms {
    public static void main(String args[]) throws Exception {
        System.out.println("\tAlgorithms");
        System.out.println("1) Linear Regression");
        System.out.println("2) Decision Tree");
        System.out.println("3) Exit\n");
        System.out.println("Enter the number corresponding to the algorithm you want to run:");
        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();
        switch(choice){
            case 1: LinearRegression lr = new LinearRegression();
                    lr.linearRegression();
                    break;
            case 2: DecisionTree dt = new DecisionTree();
                    dt.decisionTree();
                    break;
            case 3: System.exit(0);
        }
    }
}
