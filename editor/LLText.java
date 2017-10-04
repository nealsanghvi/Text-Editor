package editor;

public class LLText<Item> {
    public class Node {
        public Item item;
        public Node next;
        public Node previous;

        public Node(Node previous, Item item, Node next) {
            this.item = item;
            this.next = next;
            this.previous = previous;

        }//end Node constructor
    }//end Node Class

    public Node sentinel;
    public Node cursor;
    public int size;

    public LLText() {
        this.sentinel = new Node(null, null, null);
        this.cursor = new Node(null, null, null);
        this.sentinel.next = this.cursor;
        this.sentinel.previous = this.cursor;
        this.cursor.next = this.sentinel;
        this.cursor.previous = this.sentinel;
        size = 0;
    }//end constructor for lldeque


    public Item getPrevious() {
        return cursor.previous.item;
    }

    public Item getNext() {
        return cursor.next.item;
    }

    public Item getCursorItem() {
        return cursor.item;
    }

    public void moveToSentinel() {
        cursor.previous.next = cursor.next;
        cursor.next.previous = cursor.previous;
        cursor.previous = sentinel;
        cursor.next = sentinel.next;
        sentinel.next.previous = cursor;
        sentinel.next = cursor;
    }

    public void addFrontCursor(Item thingtoadd) {

        Node oldLastNode = cursor.previous;
        Node newLastNode = new Node(oldLastNode, thingtoadd, cursor);
        oldLastNode.next = newLastNode;
        cursor.previous = newLastNode;
        size++;
    }

    public Item deleteBackCursor() {
        Node deleted = cursor.previous;
        if (this.isEmpty()) {
            return null;
        }
        Node newLast = cursor.previous.previous;
        newLast.next = cursor;
        cursor.previous = newLast;
        size--;
//        System.out.println("Inside deletebackcursor deleted item = " + deleted.item);
//        System.out.println("Previous Node" + cursor.previous.item);
//        System.out.println("Next Node" + cursor.next.item);
        return deleted.item;

    }

    public void moveLeftCursor() {
        Node newnext = cursor.previous;
        cursor.previous = cursor.previous.previous;
        newnext.next = cursor.next;
        cursor.next = newnext;
        newnext.next.previous = newnext;
        cursor.previous.next = cursor;
        newnext.previous = cursor;

    }

    public void moveRightCursor() {
        Node newback = cursor.next;
        cursor.next = cursor.next.next;
        newback.previous = cursor.previous;
        cursor.previous = newback;
        newback.previous.next = newback;
        cursor.next.previous = cursor;
        newback.next = cursor;

    }

    public void moveCursorLocation(Node nodeprev) {
        cursor.previous.next = cursor.next;
        cursor.next.previous = cursor.previous;
        nodeprev.next.previous = cursor;
        cursor.previous = nodeprev;
        cursor.next = nodeprev.next;
        nodeprev.next = cursor;
    }


    public boolean isEmpty() {
        return this.sentinel.next == this.cursor && this.cursor.next == this.sentinel;
    }

    public int size() {
        return size;
    }

}

