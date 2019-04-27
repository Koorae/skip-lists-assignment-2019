import java.util.ArrayList;
import java.util.Random;

public class SkipListExp {
  public static void main(String[] args) {
    SkipList sL = new SkipList();
    int steps = 0;
    Random random = new Random(2);
    int rand;
    int amount = 2000;
    int totalSet = 0;
    int totalGet = 0;
    int totalRem = 0;
    int[] arr = new int[amount];
    for (int i = 0; i < amount; i++) {
      arr[i] = i;
    }
    for (int i = 0; i < amount; i++) {
      rand = random.nextInt(amount);
      int temp = arr[rand];
      arr[rand] = arr[i];
      arr[i] = temp;
    }
    System.out.println("Length: " + amount + "\n");
    for (int j = 0; j < 3; j++) {
      System.out.println("Trial" + (j + 1));
      System.out.println();
      for (int i = 0; i < amount; i++) {
        sL.set(arr[i] , i);
        steps += sL.stepCounter;
      }
      totalSet += steps;
      System.out.println("SetExp:");
      System.out.println(steps);
      steps = 0;
      for (int i = 0; i < amount; i++) {
        rand = random.nextInt(amount);
        sL.get(arr[rand]);
        steps += sL.stepCounter;
      }
      totalGet += steps;
      System.out.println("GetExp:");
      System.out.println(steps);
      steps = 0;
      for (int i = 0; i < amount; i++) {
        rand = random.nextInt(amount);
        sL.remove(arr[rand]);
        steps += sL.stepCounter;
      }
      totalRem += steps;
      System.out.println("RemoveExp:");
      System.out.println(steps);
      steps = 0;
      System.out.println();
    }
    System.out.println();
    System.out.println("Averages:");
    System.out.println("SetAverage: " + (totalSet / 3));
    System.out.println("GetAverage: " + (totalGet / 3));
    System.out.println("RemAverage: " + (totalRem / 3));
  }
}
