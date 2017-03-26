/**
 * Created by Dave on 3/26/17.
 */

import java.util.Scanner;

public class RemoteCaller {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a string to be rotated");
        String string = scanner.next();

        int x = 0;

        while (x < 1000) {
            string = Client.doStuff(string);
            x++;
        }
    }
}
