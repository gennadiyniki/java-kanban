package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> nodes = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
        nodes.put(task.getId(), last);
    }

    public void remove(int id) {
        Node node = nodes.get(id);
        if (node == null) {
            return;
        }
        if (node == first && node == last) {
            nodes.remove(id, node);
            first = null;
            last = null;
        } else if (node == first) {
            first = first.next;
            if (first != null) {
                first.setPrev(null);
            }
            nodes.remove(id, node);
        } else if (node == last) {
            last = last.prev;
            last.setNext(null);
            nodes.remove(id, node);
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            nodes.remove(id, node);
        }
    }

    private void linkLast(Task task) {
        Node node = new Node(task, last, null);
        if (first == null) {
            first = node;
        } else {
            last.next = node;
        }
        last = node;
    }

    private static class Node {
        Task value;
        Node prev;
        Node next;

        public Node(Task value, Node prev, Node next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyList = new ArrayList<>();
        if (first == null) {
            return historyList;
        }
        Node node = first;
        while (node != null) {
            historyList.add(node.value);
            node = node.next;
        }
        return historyList;
    }
}
