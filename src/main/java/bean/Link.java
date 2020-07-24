package bean;

public class Link {

    public Entity entity; //链接到的Entity/Relation
    public String mention; //句中的mention
    public int start; // start char index
    public int end; // end char index
    public double score; //


    public Link(Entity entity, String mention, int start, int end, double score) {
        this.entity = entity;
        this.mention = mention;
        this.start = start;
        this.end = end;
        this.score = score;
    }

    @Override
    public String toString() {
        return "Link{" +
                "entity=" + entity +
                ", mention='" + mention + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", score=" + score +
                '}';
    }
}
