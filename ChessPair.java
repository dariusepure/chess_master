public class ChessPair<K extends Comparable<K>, V> implements Comparable<ChessPair<K, V>> {
    private K key;
    private V value;

    public ChessPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public int compareTo(ChessPair<K, V> other) {
        return this.key.compareTo(other.key);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChessPair<?, ?> other = (ChessPair<?, ?>) obj;
        return key.equals(other.key) && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return 31 * key.hashCode() + value.hashCode();
    }

    @Override
    public String toString() {
        return "ChessPair{key=" + key + ", value=" + value + "}";
    }
}