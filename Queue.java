public class Queue {

    private int front;
    private int rear;
    private int size;
    private int capacity;
    private Object[] elements;

    public Queue(int capacity) {
        this.capacity = capacity;
        elements = new Object[capacity];
        front = 0;
        rear = -1;
        size = 0;
    }

    public void enqueue(Object data) {
        if (isFull()) {
            System.out.println("Queue overflow");
        } else {
            rear = (rear + 1) % capacity;
            elements[rear] = data;
            size++;
        }
    }

    public Object dequeue() {
        if (isEmpty()) {
            System.out.println("Queue is empty");
            return null;
        }

        Object removed = elements[front];
        elements[front] = null;
        front = (front + 1) % capacity;
        size--;
        return removed;
    }

    public Object peek() {
        if (isEmpty()) {
            System.out.println("Queue is empty");
            return null;
        }

        return elements[front];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public int size() {
        return size;
    }
}
