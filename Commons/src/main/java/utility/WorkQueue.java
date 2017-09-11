package utility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by mattgoodson on 30/08/17.
 * A thread safe work queue for sharing tasks between threads
 */
public class WorkQueue {

    private ArrayBlockingQueue<QueueMessage> queue;

    public WorkQueue(int capacity) {
        this.queue = new ArrayBlockingQueue<>(capacity);
    }


    /**
     * Adds a single element to the tail of the queue
     * @param clientId Integer
     * @param message byte[]
     */
    public void put(Integer clientId, byte[] message) {
        this.queue.offer(new QueueMessage(clientId, message));
    }

    /**
     * Add a single element to the tail of the queue
     * @param clientId client's id
     * @param header header of the message
     * @param body body part of the message
     */
    public void put(Integer clientId, byte[] header, byte[] body) {
        this.queue.offer(new QueueMessage(clientId, header, body));
    }


    /**
     * Removes and returns the element at the head of the queue
     * @return QueueMessage
     */
    public QueueMessage pop() {
        return this.queue.poll();
    }


    /**
     * Removes and returns all elements in the queue
     * @return List
     */
    public List<QueueMessage> drain() {
        List<QueueMessage> process = new ArrayList<>();
        this.queue.drainTo(process);
        return process;
    }

}
