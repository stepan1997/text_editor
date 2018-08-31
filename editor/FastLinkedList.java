package editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

public class FastLinkedList<GenericType> {

    protected Link sentinel;
    protected int size;
    protected Link currentNode;
    protected HashMap<Integer, Link> pointers = new HashMap<>();
    protected ArrayList<Link> links = new ArrayList<>();

    class Link {
        protected GenericType element;
        protected Link previous;
        protected Link next;

        public Link(GenericType element) {
            this.element = element;
        }

    }

    public FastLinkedList()
    {
        sentinel = new Link(null);
        sentinel.next = sentinel;
        sentinel.previous = sentinel;
        size = 0;
        currentNode = sentinel;
    }


    public void add(GenericType x)
    {
        Link pointer = currentNode.next;
        Link toBeAdded = new Link(x);
        toBeAdded.previous = currentNode;
        toBeAdded.next = pointer;
        pointer.previous = toBeAdded;
        currentNode.next = toBeAdded;
        currentNode = toBeAdded;
        size = size + 1;
    }

    public GenericType remove()
    {
        if (currentNode != sentinel) {
            Link toBeReturned = currentNode;
            Link previous = currentNode.previous;
            currentNode.next.previous = previous;
            currentNode.previous.next = currentNode.next;
            currentNode = currentNode.previous;
            size = size - 1;
            return toBeReturned.element;
        } else {
            return null;
        }
    }

    //Copies the contents into an ArrayDeque in linear time.
    public ArrayList<GenericType> copyTo()
    {
        ArrayList<GenericType> contentsArray = new ArrayList<>();
        Link l = sentinel.next;
        int index = 0;
        while (l != sentinel)
        {
            contentsArray.add(index, l.element);
            l = l.next;
            index = index + 1;
        }
        return contentsArray;

    }

    public void copyPointersTo()
    {
        ArrayList<Link> links = new ArrayList<>();
        Link l = sentinel.next;
        while (l != sentinel)
        {
            links.add(l);
            l = l.next;
        }
        this.links = links;
    }

    public void clearLinks()
    {
        links = null;
    }

    public void clearPointers()
    {
        pointers.clear();
    }

    public void curNodeToLeft()
    {
        if (currentNode == sentinel)
        {
            throw new NullPointerException();
        }
        currentNode = currentNode.previous;
    }

    public void curNodeToRight()
    {
        if (currentNode.next == sentinel)
        {
            throw new NullPointerException();
        }
        currentNode = currentNode.next;
    }

    public GenericType getNext()
    {
        if (currentNode.next == sentinel)
        {
            return null;
        }
        return currentNode.next.element;
    }

    public GenericType currentNode()
    {
        if (currentNode == sentinel)
        {
            return null;
        }
        return currentNode.element;
    }

    public GenericType getPrevious()
    {
        if (currentNode.previous == sentinel)
        {
            return null;
        }
        return sentinel.previous.element;
    }

    public void setCurrentNode(int i)
    {
        Link p = pointers.get(i);
        if (p == null)
        {
            throw new NullPointerException();
        }
        currentNode = p;
    }


    public void setCurrentNodetoLast() {
        currentNode = pointers.get(Collections.max(pointers.keySet()));
    }

}