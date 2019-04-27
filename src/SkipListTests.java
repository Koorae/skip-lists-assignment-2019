import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

/**
 * Some tests of skip lists.
 *
 * @author Samuel A. Rebelsky
 */
public class SkipListTests {

  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * Names of some numbers.
   */
  static final String numbers[] = {"zero", "one", "two", "three", "four", "five", "six", "seven",
      "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
      "seventeen", "eighteen", "nineteen"};

  /**
   * Names of more numbers.
   */
  static final String tens[] =
      {"", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};

  // +--------+----------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * A of strings for tests. (Gets set by the subclasses.)
   */
  SkipList<String, String> strings;

  /**
   * A sorted list of integers for tests. (Gets set by the subclasses.)
   */
  SkipList<Integer, String> ints;

  /**
   * A random number generator for the randomized tests.
   */
  Random random = new Random();

  /**
   * For reporting errors: a list of the operations we performed.
   */
  ArrayList<String> operations;



  // +---------+---------------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Set up everything. Unfortunately, @BeforeEach doesn't seem to be working, so we do this
   * manually.
   */
  @BeforeEach
  public void setup() {
    this.ints = new SkipList<Integer, String>((i, j) -> i - j);
    this.strings = new SkipList<String, String>((s, t) -> s.compareTo(t));
    this.operations = new ArrayList<String>();
  } // setup

  /**
   * Dump a SkipList to stderr.
   */
  static <K, V> void dump(SkipList<K, V> map) {
    System.err.print("[");
    map.forEach((key, value) -> System.err.println(key + ":" + value + " "));
    System.err.println("]");
  } // dump

  /**
   * Determine if an iterator only returns values in non-decreasing order.
   */
  static <T extends Comparable<T>> boolean inOrder(Iterator<T> it) {
    // Simple case: The empty iterator is in order.
    if (!it.hasNext()) {
      return true;
    }
    // Otherwise, we need to compare neighboring elements, so
    // grab the first element.
    T current = it.next();
    // Step through the remaining elements
    while (it.hasNext()) {
      // Get the next element
      T next = it.next();
      // Verify that the current node <= next
      if (current.compareTo(next) > 0) {
        return false;
      } // if (current > next)
      // Update the current node
      current = next;
    } // while
    // If we've made it this far, everything is in order
    return true;
  } // inOrder(Iterator<T> it)

  /**
   * Generate a value from a string.
   */
  static String value(String str) {
    return str.toUpperCase();
  } // key(String)

  /**
   * Generate a value from a non-negative integer.
   */
  static String value(Integer i) {
    return value(i, false);
  } // value(integer)

  /**
   * Generate a value from a non-negative integer; if skipZero is true, returns "" for zero.
   */
  static String value(Integer i, boolean skipZero) {
    if ((i == 0) && (skipZero)) {
      return "";
    } else if (i < 20) {
      return numbers[i];
    } else if (i < 100) {
      return (tens[i / 10] + " " + value(i % 10, true)).trim();
    } else if (i < 1000) {
      return (numbers[i / 100] + " hundred " + value(i % 100, true)).trim();
    } else if (i < 1000000) {
      return (numbers[i / 1000] + " thousand " + value(i % 1000, true)).trim();
    } else {
      return "really big";
    }
  } // value(i, skipZero)

  // +--------------------+------------------------------------------
  // | Logging operations |
  // +--------------------+

  /**
   * Set an entry in the ints list.
   */
  void set(Integer i) {
    operations.add("set(" + i + ");");
    ints.set(i, value(i));
  } // set(Integer)

  /**
   * Set an entry in the strings list.
   */
  void set(String str) {
    operations.add("set(\"" + str + "\");");
    strings.set(str, value(str));
  } // set(String)

  /**
   * Remove an integer from the ints list.
   */
  void remove(Integer i) {
    operations.add("remove(" + i + ");");
    ints.remove(i);
  } // remove(Integer)

  /**
   * Remove a string from the strings list.
   */
  void remove(String str) {
    operations.add("remove(\"" + str + "\");");
    strings.remove(str);
  } // remove(String)

  /**
   * Log a failure.
   */
  void log(String str) {
    System.err.println(str);
    operations.add("// " + str);
  } // log

  /**
   * Print code from a failing test.
   */
  void printTest() {
    System.err.println("@Test");
    System.err.println("  public void test" + random.nextInt(1000) + "() {");
    for (String op : operations) {
      System.err.println("    " + op);
    } // for
    System.err.println("  }");
    System.err.println();
  } // printTest()

  // +-------------+-----------------------------------------------------
  // | Basic Tests |
  // +-------------+

  /**
   * A really simple test. Add an element and make sure that it's there.
   */
  @Test
  public void simpleTest() {
    setup();
    set("hello");
    assertTrue(strings.containsKey("hello"));
    assertFalse(strings.containsKey("goodbye"));
  } // simpleTest()

  /**
   * Another simple test. The list should not contain anything when we start out.
   */
  @Test
  public void emptyTest() {
    setup();
    assertFalse(strings.containsKey("hello"));
  } // emptyTest()

  // +-----------------+-------------------------------------------------
  // | RandomizedTests |
  // +-----------------+

  /**
   * Verify that a randomly created list is sorted.
   */
  @Test
  public void testOrdered() {
    setup();
    // Add a bunch of values
    for (int i = 0; i < 100; i++) {
      int rand = random.nextInt(1000);
      set(rand);
    } // for
    if (!inOrder(ints.keys())) {
      System.err.println("inOrder() failed in testOrdered()");
      printTest();
      dump(ints);
      System.err.println();
      fail("The instructions did not produce a sorted list.");
    } // if the elements are not in order.
  } // testOrdered()

  /**
   * Adds elements in schuffling order and see if they are still in order.
   */
  @Test
  public void schuffleAdd() {
    setup();
    int value = 100;
    for (int i = 0; i < 20; i++) {
      set(value);
      int rand = random.nextInt(value);
      set(rand);
      int rand2 = random.nextInt(value) + value;
      set(rand2);
      value = value * (random.nextInt(2) + 1) / (random.nextInt(2) + 1);
      if (value <= 0) {
        value = 100;
      }
    }
    if (!inOrder(ints.keys())) {
      System.err.println("inOrder() failed in testOrdered()");
      printTest();
      dump(ints);
      System.err.println();
      fail("The instructions did not produce a sorted list.");
    } // if the elements are not in order.
  } // test scuffleAdd()

  /**
   * Removes the front node of the SkipList and see if it succeeds
   */
  @Test
  public void removeFront() {
    setup();
    int max = 0;
    for (int i = 0; i < 50; i++) {
      int rand = random.nextInt(200);
      max = Math.max(max, rand);
      set(rand);
    }
    remove(max);
    if (!inOrder(ints.keys())) {
      System.err.println("inOrder() failed in testOrdered()");
      printTest();
      dump(ints);
      System.err.println();
      fail("The instructions did not produce a sorted list.");
    } // Make sure in order
    if (ints.containsKey(max)) {
      log("After removing " + max + ", contains(" + max + ") succeeds");
    } // Make sure the removed node is not there
  } // test removeFront()

  /**
   * Removes the back node of the SkipList and see if it succeeds
   */
  @Test
  public void removeBack() {
    setup();
    int min = 0;
    for (int i = 0; i < 50; i++) {
      int rand = random.nextInt(200);
      min = Math.min(min, rand);
      set(rand);
    }
    remove(min);
    if (!inOrder(ints.keys())) {
      System.err.println("inOrder() failed in testOrdered()");
      printTest();
      dump(ints);
      System.err.println();
      fail("The instructions did not produce a sorted list.");
    } // Make sure in order
    if (ints.containsKey(min)) {
      log("After removing " + min + ", contains(" + min + ") succeeds");
    } // Make sure the removed node is not there
  } // test removeBack()

  /**
   * Add a thousand large elements and check whether all of them are contained in SkipList
   */
  @Test
  public void checkBigEverythingIncluded() {
    setup();
    int[] arr = new int[1000];
    for (int i = 0; i < 1000; i++) {
      int rand = random.nextInt(10000);
      set(rand);
      arr[i] = rand;
    }
    if (!inOrder(ints.keys())) {
      System.err.println("inOrder() failed in testOrdered()");
      printTest();
      dump(ints);
      System.err.println();
      fail("The instructions did not produce a sorted list.");
    } // Make sure in order
    for (int i = 0; i < 1000; i++) {
      if (!ints.containsKey(arr[i])) {
        log("contains(" + arr[i] + ") failed");
        printTest();
        dump(ints);
        fail(arr[i] + " is not in the skip list");
      }
    } // check everything is included
  } // test checkBigEverythingIncluded()

  /**
   * Randomly set new nodes and remove existing nodes in the SkipList.
   */
  @Test
  public void randomSetandRemove() {
    setup();
    for (int i = 0; i < 500; i++) {
      int rand = random.nextInt(2);
      int randVal = random.nextInt(10000);
      ArrayList<Integer> arr = new ArrayList<Integer>();

      // If generated 0 then set
      if (rand == 0) {
        set(randVal);
        arr.add(randVal);
      }

      // If generated 1 then remove
      else {
        if (arr.size() <= 0) {
        } else {
          int randInd = random.nextInt(arr.size() - 1);
          remove(arr.get(randInd));
          arr.remove(arr.get(randInd));
        }
      }
    }
    if (!inOrder(ints.keys())) {
      System.err.println("inOrder() failed in testOrdered()");
      printTest();
      dump(ints);
      System.err.println();
      fail("The instructions did not produce a sorted list.");
    } // Make sure in order
  } // test randomSetandRemove()

  /**
   * Add 50 nodes to the Skiplist and remvoe them all and check if its empty
   */
  @Test
  public void addThenRemoveTillEmpty() {
    setup();
    int[] arr = new int[50];
    for (int i = 0; i < 50; i++) {
      int rand = random.nextInt(200);
      set(rand);
      arr[i] = rand;
    } // insert everything
    if (!inOrder(ints.keys())) {
      System.err.println("inOrder() failed in testOrdered()");
      printTest();
      dump(ints);
      System.err.println();
      fail("The instructions did not produce a sorted list.");
    } // Make sure in order
    for (int i = 0; i < 50; i++) {
      remove(arr[i]);
    } // remove everything
    if (ints.front.get(0) != null) {
      fail("The operation did not remove all the nodes.");
    } // ensure SkipList is empty
  } // test addThenRemoveTillEmpty()

  /**
   * Verify that a randomly created list contains all the values we added to the list.
   */
  @Test
  public void testContainsOnlyAdd() {
    setup();
    ArrayList<Integer> keys = new ArrayList<Integer>();

    // Add a bunch of values
    for (int i = 0; i < 100; i++) {
      int rand = random.nextInt(200);
      keys.add(rand);
      set(rand);
    } // for i
    // Make sure that they are all there.
    for (Integer key : keys) {
      if (!ints.containsKey(key)) {
        log("contains(" + key + ") failed");
        printTest();
        dump(ints);
        fail(key + " is not in the skip list");
      } // if (!ints.contains(val))
    } // for key
  } // testContainsOnlyAdd()

  /**
   * An extensive randomized test.
   */
  @Test
  public void randomTest() {
    setup();
    // Keep track of the values that are currently in the sorted list.
    ArrayList<Integer> keys = new ArrayList<Integer>();

    // Add a bunch of values
    boolean ok = true;
    for (int i = 0; ok && i < 1000; i++) {
      int rand = random.nextInt(1000);
      // Half the time we add
      if (random.nextBoolean()) {
        if (!ints.containsKey(rand)) {
          set(rand);
        } // if it's not already there.
        if (!ints.containsKey(rand)) {
          log("After adding " + rand + ", contains(" + rand + ") fails");
          ok = false;
        } // if (!ints.contains(rand))
      } // if we add
      // Half the time we remove
      else {
        remove(rand);
        keys.remove((Integer) rand);
        if (ints.containsKey(rand)) {
          log("After removing " + rand + ", contains(" + rand + ") succeeds");
          ok = false;
        } // if ints.contains(rand)
      } // if we remove
      // See if all of the appropriate elements are still there
      for (Integer key : keys) {
        if (!ints.containsKey(key)) {
          log("ints no longer contains " + key);
          ok = false;
          break;
        } // if the value is no longer contained
      } // for each key
    } // for i
    // Dump the instructions if we've encountered an error
    if (!ok) {
      printTest();
      dump(ints);
      fail("Operations failed");
    } // if (!ok)
  } // randomTest()

  public static void main(String[] args) {
    SkipListTests slt = new SkipListTests();
    slt.setup();
    slt.simpleTest();
  } // main
} // class SkipListTests

