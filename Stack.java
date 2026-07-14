public class Stack {

    private int top;
    private int capacity;
    private Object[] elements;

    public Stack(int capacity) {
        this.capacity = capacity;
        elements = new Object[capacity];
        top = -1;
    }

    public void push(Object data) {
        if (isFull()) {
            System.out.println("Stack overflow");
        } else {
            elements[++top] = data;
        }
    }

    public Object pop() {
        if (isEmpty()) {
            System.out.println("Stack is empty");
            return null;
        }

        Object removed = elements[top];
        top--;
        return removed;
    }

    public Object peek() {
        if (isEmpty()) {
            System.out.println("Stack is empty");
            return null;
        }

        return elements[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public boolean isFull() {
        return top + 1 == capacity;
    }

    public int size() {
        return top + 1;
    }
}
