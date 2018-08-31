package editor;

public class Stack<GenericType> extends FastLinkedList<GenericType>
{
    public void push(GenericType x)
    {
        add(x);
        if (size > 100)
        {
            removeFirst();
        }
    }

    public GenericType get(int i)
    {
        Link l = sentinel.next;
        while (i != 0)
        {
            l = l.next;
            i = i - 1;
        }
        return l.element;
    }

    public GenericType peek()
    {
        return sentinel.previous.element;
    }

    private GenericType removeFirst()
    {
        Link toBeRemoved = sentinel.next;
        sentinel.next = sentinel.next.next;
        sentinel.next.previous = sentinel;
        return toBeRemoved.element;
    }

    public GenericType pop()
    {
        return remove();
    }

    public boolean isEmpty()
    {
        return size == 0;
    }

    public void clear()
    {
        Link l = sentinel.next;
        while (l != sentinel)
        {
            l.element = null;
            l = l.next;
        }
    }

}