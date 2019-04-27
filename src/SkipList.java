import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.function.BiConsumer;
import java.lang.Math;

/**
 * An implementation of skip lists.
 */
public class SkipList<K, V> implements SimpleMap<K, V> {

  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The initial height of the skip list.
   */
  static final int INITIAL_HEIGHT = 20;

  // +---------------+-----------------------------------------------
  // | Static Fields |
  // +---------------+

  static Random rand = new Random();

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * Pointers to all the front elements.
   */
  ArrayList<SLNode<K, V>> front;

  /**
   * The comparator used to determine the ordering in the list.
   */
  Comparator<K> comparator;

  /**
   * The number of values in the list.
   */
  int size;

  /**
   * The current height of the skiplist.
   */
  int height;

  /**
   * The probability used to determine the height of nodes.
   */
  double prob = 0.5;

  /**
   * Counts the time spent by the function.
   */
  int stepCounter;
  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new skip list that orders values using the specified comparator.
   */
  public SkipList(Comparator<K> comparator) {
    this.front = new ArrayList<SLNode<K, V>>(INITIAL_HEIGHT);
    for (int i = 0; i < INITIAL_HEIGHT; i++) {
      front.add(null);
    } // for
    this.comparator = comparator;
    this.size = 0;
    this.height = INITIAL_HEIGHT;
  } // SkipList(Comparator<K>)

  /**
   * Create a new skip list that orders values using a not-very-clever default comparator.
   */
  public SkipList() {
    this((k1, k2) -> k1.toString().compareTo(k2.toString()));
  } // SkipList()


  // +-------------------+-------------------------------------------
  // | SimpleMap methods |
  // +-------------------+

  /**
   * Inserts a node or changes the value of the node according to the provided key in the SkipList
   * 
   * @precondition: key and value cannot be null
   */
  @Override
  public V set(K key, V value) {
    stepCounter = 0;

    // Null Pointer Exception if not fulfilling precondition
    if (key == null || value == null) {
      throw new NullPointerException();
    }

    // When empty just insert a node
    if (this.size <= 0) {
      SLNode<K, V> newAdd = new SLNode<K, V>(key, value, randomHeight());
      for (int i = 0; i < newAdd.next.size(); i++) {
        this.front.set(i, newAdd);
      }
      size++;
      return null;
    } else {
      // Initializing needed variables
      int level = height - 1;
      SLNode<K, V> curr = this.front.get(level);
      ArrayList<SLNode<K, V>> prevStorage = new ArrayList<SLNode<K, V>>(this.height);
      for (int i = 0; i < this.height; i++) {
        prevStorage.add(null);
      } // for

      // Decide on the node to hop on
      while (level > 0 && (curr == null || comparator.compare(curr.key, key) > 0)) {
        curr = this.front.get(--level);
      }

      // Check if the node that is matching the key
      if (comparator.compare(curr.key, key) == 0) {
        V rVal = curr.value;
        curr.value = value;
        return rVal;
      } else if (comparator.compare(curr.key, key) > 0) {
        level--;
      }

      // Going into the main loop for search, if found, then just change the value
      while (level >= 0) {
        if (curr.next(level) == null || comparator.compare(curr.next(level).key, key) > 0) {
          prevStorage.set(level, curr);
          level = level - 1;
        } else if (comparator.compare(curr.next(level).key, key) < 0) {
          curr = curr.next(level);
          stepCounter++;
        } else {
          return curr.next(level).value;
        }
      }

      // Insert the node
      SLNode<K, V> newAdd = new SLNode<K, V>(key, value, randomHeight());
      for (int i = 0; i < newAdd.next.size(); i++) {
        if (prevStorage.get(i) == null) {
          newAdd.next.set(i, this.front.get(i));
          this.front.set(i, newAdd);
        } else {
          newAdd.next.set(i, prevStorage.get(i).next(i));
          prevStorage.get(i).next.set(i, newAdd);
        }
      }
      size++;
      /*
       * System.out.println("List begin(Insert):" + key); printList();
       * System.out.println("List End");
       */
    }
    return null;
  } // set(K,V)

  /**
   * Returns the value of a node given the key. However, if the key doesn't match anything that
   * exists in the SkipList.
   * 
   * @precondition: key cannot be null
   */
  @Override
  public V get(K key) {
    stepCounter = 0;

    // Null Pointer Exception if not fulfilling precondition
    if (key == null) {
      throw new NullPointerException("null key");
    } // if
    if (this.size == 0) {
      throw new IndexOutOfBoundsException("Empty");
    }
    int level = height - 1;
    SLNode<K, V> curr = this.front.get(level);

    // Decide on the node to hop on
    while (level > 0 && (curr == null || comparator.compare(curr.key, key) > 0)) {
      curr = this.front.get(--level);
    }

    // If the node hopped on matches the key, then just return, if not then it isn't there(avoid
    // while loop)
    if (comparator.compare(curr.key, key) == 0) {
      return curr.value;
    } else if (comparator.compare(curr.key, key) > 0) {
      level--;
    }

    // Going into the main loop for search, if found, then just return the value
    while (level >= 0) {
      if (curr.next(level) == null || comparator.compare(curr.next(level).key, key) > 0) {
        level--;
      } else if (comparator.compare(curr.next(level).key, key) < 0) {
        curr = curr.next(level);
        stepCounter++;
      } else {
        return curr.next(level).value;
      }
    }

    // If not found, throw Exception
    throw new IndexOutOfBoundsException("OutOfBounds");
  } // get(K,V)

  /**
   * Returns the size of the SkipList
   */
  @Override
  public int size() {
    return this.size;
  } // size()

  /**
   * Checks whether a key exists in the SkipList
   */
  @Override
  public boolean containsKey(K key) {

    // Use get to check if the SkipList contains the key
    try {
      get(key);
      return true;
    } catch (IndexOutOfBoundsException e) {
      return false;
    } catch (NullPointerException e2) {
      return false;
    }
  } // containsKey(K)

  /**
   * Removes the given node that contains the key from the SkipList.
   * 
   * @precondition: key cannot be null
   * @postcondition: returns the value of the removed value or return null if not found
   */
  @Override
  public V remove(K key) {
    stepCounter = 0;

    // Null Pointer Exception if not fulfilling precondition
    if (key == null) {
      throw new NullPointerException("null key");
    }

    // Can't remove when empty
    if (size == 0) {
      return null;
    }
    int level = this.front.size() - 1;
    SLNode<K, V> curr = this.front.get(level);
    ArrayList<SLNode<K, V>> prevStorage = new ArrayList<SLNode<K, V>>(this.height);
    for (int i = 0; i < this.height; i++) {
      prevStorage.add(null);
    }

    // Decide on the node to hop on
    while (level > 0 && (curr == null || comparator.compare(curr.key, key) >= 0)) {
      curr = this.front.get(--level);
    }

    // If it still is bigger, then it doesn't exist.
    // If it is equal, then connect the beginning to the removed's nexts.
    if (comparator.compare(curr.key, key) > 0) {
      return null;
    } else if (comparator.compare(curr.key, key) == 0) {
      for (int i = 0; i < curr.next.size(); i++) {
        this.front.set(i, curr.next(i));
      }
      size--;
      return curr.value;
    }

    // Main while loop
    while (level >= 0) {
      if (curr.next(level) == null || comparator.compare(curr.next(level).key, key) >= 0) {
        prevStorage.set(level, curr);
        level = level - 1;
      } else if (comparator.compare(curr.next(level).key, key) < 0) {
        curr = curr.next(level);
        stepCounter++;
      }
    }

    // Check whether if we found the node or not, if we did then remove it and connect the nodes
    // around it.
    if (prevStorage.get(0).next(0) == null) {
      return null;
    } else if (comparator.compare(prevStorage.get(0).next(0).key, key) > 0) {
      return null;
    } else {
      SLNode<K, V> target = curr.next(0);
      for (int i = 0; i < target.next.size(); i++) {
        if (prevStorage.get(i) == null) {
          this.front.set(i, target.next(i));
        } else {
          prevStorage.get(i).next.set(i, target.next(i));
        }
      }
      size--;
      return target.value;
    }
  } // remove(K)

  @Override
  public Iterator<K> keys() {
    return new Iterator<K>() {
      Iterator<SLNode<K, V>> nit = SkipList.this.nodes();

      @Override
      public boolean hasNext() {
        return nit.hasNext();
      } // hasNext()

      @Override
      public K next() {
        return nit.next().key;
      } // next()

      @Override
      public void remove() {
        nit.remove();
      } // remove()
    };
  } // keys()

  @Override
  public Iterator<V> values() {
    return new Iterator<V>() {
      Iterator<SLNode<K, V>> nit = SkipList.this.nodes();

      @Override
      public boolean hasNext() {
        return nit.hasNext();
      } // hasNext()

      @Override
      public V next() {
        return nit.next().value;
      } // next()

      @Override
      public void remove() {
        nit.remove();
      } // remove()
    };
  } // values()

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    SLNode<K, V> curr = this.front.get(0);
    while (curr.next(0) != null) {
      action.accept(curr.key, curr.value);
      curr = curr.next(0);
    }
  } // forEach

  // +----------------------+----------------------------------------
  // | Other public methods |
  // +----------------------+

  /**
   * Dump the tree to some output location.
   */
  public void dump(PrintWriter pen) {
    String leading = "          ";

    SLNode<K, V> current = front.get(0);

    // Print some X's at the start
    pen.print(leading);
    for (int level = 0; level < this.height; level++) {
      pen.print(" X");
    } // for
    pen.println();
    printLinks(pen, leading);

    while (current != null) {
      // Print out the key as a fixed-width field.
      // (There's probably a better way to do this.)
      String str;
      if (current.key == null) {
        str = "<null>";
      } else {
        str = current.key.toString();
      } // if/else
      if (str.length() < leading.length()) {
        pen.print(leading.substring(str.length()) + str);
      } else {
        pen.print(str.substring(0, leading.length()));
      } // if/else

      // Print an indication for the links it has.
      for (int level = 0; level < current.next.size(); level++) {
        pen.print("-*");
      } // for
      // Print an indication for the links it lacks.
      for (int level = current.next.size(); level < this.height; level++) {
        pen.print(" |");
      } // for
      pen.println();
      printLinks(pen, leading);

      current = current.next.get(0);
    } // while

    // Print some O's at the start
    pen.print(leading);
    for (int level = 0; level < this.height; level++) {
      pen.print(" O");
    } // for
    pen.println();
    // Forthcoming
  } // dump(PrintWriter)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Pick a random height for a new node.
   */
  int randomHeight() {
    int result = 1;
    while (rand.nextDouble() < prob) {
      result = result + 1;
    }
    return Math.min(INITIAL_HEIGHT, result);
  } // randomHeight()

  /**
   * Get an iterator for all of the nodes. (Useful for implementing the other iterators.)
   */
  Iterator<SLNode<K, V>> nodes() {
    return new Iterator<SLNode<K, V>>() {

      /**
       * A reference to the next node to return.
       */
      SLNode<K, V> next = SkipList.this.front.get(0);

      @Override
      public boolean hasNext() {
        return this.next != null;
      } // hasNext()

      @Override
      public SLNode<K, V> next() {
        if (this.next == null) {
          throw new IllegalStateException();
        }
        SLNode<K, V> temp = this.next;
        this.next = this.next.next.get(0);
        return temp;
      } // next();
    }; // new Iterator
  } // nodes()

  /**
   * Print some links (for dump).
   */
  void printLinks(PrintWriter pen, String leading) {
    pen.print(leading);
    for (int level = 0; level < this.height; level++) {
      pen.print(" |");
    } // for
    pen.println();
  } // printLinks
  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

} // class SkipList


/**
 * Nodes in the skip list.
 */
class SLNode<K, V> {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The key.
   */
  K key;

  /**
   * The value.
   */
  V value;

  /**
   * Pointers to the next nodes.
   */
  ArrayList<SLNode<K, V>> next;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new node of height n with the specified key and value.
   */
  public SLNode(K key, V value, int n) {
    this.key = key;
    this.value = value;
    this.next = new ArrayList<SLNode<K, V>>(n);
    for (int i = 0; i < n; i++) {
      this.next.add(null);
    } // for
  } // SLNode(K, V, int)

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+
  public SLNode<K, V> next(int n) {
    return this.next.get(n);
  }
} // SLNode<K,V>
