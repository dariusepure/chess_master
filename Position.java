public class Position implements Comparable<Position> {
    private char x;
    private int y;
    public Position(char x,int y){
        this.x=x;
        this.y=y;
    }
    public char getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    @Override
    public int compareTo(Position other){
        if(this.y!=other.y){
            return Integer.compare(this.y,other.y);
        }
        return Character.compare(this.x,other.x);
    }
    @Override
    public boolean equals(Object obj){
        if(this==obj)
            return true;
        if(obj==null || this.getClass()!=obj.getClass())
            return false;
        Position other=(Position)obj;
        return this.x==other.x && this.y==other.y;
    }
    @Override
    public String toString(){
        return ""+x+y;
    }
}