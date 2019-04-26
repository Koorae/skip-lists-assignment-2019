import java.util.ArrayList;
import java.util.Random;

public class SkipListExp {
  public static void main(String[] args) {
    SkipList sL = new SkipList();
    int steps = 0;
    ArrayList<Integer> storage = new ArrayList<Integer>();
    Random random = new Random();
    int rand;
    int amount = 20;
    int totalSet = 0;
    int totalGet = 0;
    int totalRem = 0;
    for (int i = 0; i < 100; i++) {
      rand = random.nextInt(200);
      sL.set(rand, rand);
    }
    System.out.println("Length: " + amount + "\n");
    for (int j = 0; j < 3; j++) {
      System.out.println("Trial" + (j + 1));
      System.out.println();
      for (int i = 0; i < amount; i++) {
        rand = random.nextInt(200);
        sL.set(rand, rand);
        storage.add(rand);
        steps += sL.stepCounter;
      }
      totalSet += steps;
      System.out.println("SetExp:");
      System.out.println(steps);
      steps = 0;
      for (int i = 0; i < amount; i++) {
        sL.get(storage.get(i));
        steps += sL.stepCounter;
      }
      totalGet += steps;
      System.out.println("GetExp:");
      System.out.println(steps);
      steps = 0;
      for (int i = 0; i < amount; i++) {
        sL.get(storage.get(i));
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
