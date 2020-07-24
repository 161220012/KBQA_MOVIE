package kbqa;

import bean.Entity;
import bean.Link;
import utils.StringUtil;
import utils.TextSimilarity;

import java.util.*;

import static kbqa.Sources.entityMap;
import static kbqa.Sources.relationMap;
import static kbqa.Sources.entityLabelMap;
import static kbqa.Sources.relationLabelMap;


public class ELNRL {

    public static List<Link> sentenceRec(String sentence) {
        List<Link> links = new ArrayList<>();
        /*List<Term> segment = HanLP.segment(sentence);
        for(Term t:segment){
            System.out.println(t.word+":"+entitySearch(t.word,sentence));
        }*/

        Vector<String> ngram = StringUtil.get_ngram(sentence);
        //System.out.println(ngram);
        for (String uttr : ngram) {
            links.addAll(enrSearch(uttr,sentence));
            //System.out.println(uttr + ":" + enrSearch(uttr, sentence));
        }

        //System.out.println("初步链接结果:"+links);

        HashMap<Pair,Link> pairs = new HashMap<>();
        for (Link link : links) {//寻找start_end冲突的Link，取score较大者优先
            Pair curPair = new Pair(link.start, link.end);
            boolean foundFlag = false;
            List<Pair> pairList = new LinkedList<>(pairs.keySet());
            for (Pair p : pairList) {
                if (p.intersect(curPair)) { // mention有交集
                    foundFlag=true;
                    if (link.score > pairs.get(p).score) {// 新的link score更高
                        pairs.remove(p);
                        pairs.put(curPair, link);
                    }
                }
            }
            if(!foundFlag){
                pairs.put(curPair,link);
            }
        }

        links.clear();
        for(Pair pair:pairs.keySet()){
            links.add(pairs.get(pair));
        }


        links.sort((o1, o2) -> o1.start - o2.start);

        //System.out.println(links);

        return links;
    }


    private static List<Link> enrSearch(String utterance, String sentence) {
        int top_num =3;
        List<Link> links = new LinkedList<>();
        if (entityLabelMap.containsKey(utterance)) {//实体label完整命中
            Entity entity = new Entity(entityLabelMap.get(utterance), utterance, entityMap.get(entityLabelMap.get(utterance)));
            Link link = new Link(entity, utterance, sentence.indexOf(utterance), sentence.indexOf(utterance) + utterance.length(), 1.0);
            links.add(link);
            //Entity entity = new Entity();
        } else if (relationLabelMap.containsKey(utterance)) {//关系label完整命中
            Entity entity = new Entity(relationLabelMap.get(utterance), utterance, relationMap.get(relationLabelMap.get(utterance)));
            Link link = new Link(entity, utterance, sentence.indexOf(utterance), sentence.indexOf(utterance) + utterance.length(), 1.0);
            links.add(link);
        } else {//没有完整命中，需要计算相似度
            links.addAll(searchMap(utterance,entityLabelMap,entityMap,sentence));
            links.addAll(searchMap(utterance,relationLabelMap,relationMap,sentence));
        }
        if(links.size()>top_num){
            links = links.subList(0,top_num);
        }
        return links;
    }


    private static List<Link> searchMap(String utterance, HashMap<String,String> labelMap, HashMap<String,String> contentMap,String sentence){

        List<Link> links = new LinkedList<>();

        for (String key : labelMap.keySet()) {
            double semScore = TextSimilarity.pharseSimilarity(key, utterance); //语义相似度
            if(semScore>=0.7){//语义高度相似
                Entity entity = new Entity(labelMap.get(key),key,contentMap.get(labelMap.get(key)));
                Link link = new Link(entity, utterance, sentence.indexOf(utterance), sentence.indexOf(utterance) + utterance.length(), semScore);
                links.add(link);
            }else{
                double disScore = TextSimilarity.cmpDistance(key, utterance);//距离相似度
                double LCSScore = TextSimilarity.getLCS(key, utterance); //LCS相似度
                double textScore = 0.5*disScore+0.5*LCSScore;
                if(textScore>=0.7){//字面高度相似
                    Entity entity = new Entity(labelMap.get(key),key,contentMap.get(labelMap.get(key)));
                    Link link = new Link(entity, utterance, sentence.indexOf(utterance), sentence.indexOf(utterance) + utterance.length(), textScore);
                    links.add(link);
                }
            }
        }

        return links;
    }


    public static void main(String[] args) {
        Sources.init();
        TextSimilarity.init();
        String question = "奉俊昊导演了什么电影";
        question = StringUtil.question_handle(question);
        sentenceRec(question);
    }


}

class Pair{

    public int start;
    public int end;

    public Pair(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public boolean intersect(Pair other){
        if(end<=other.start){
            return false;
        }
        if(start>=other.end){
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return start == pair.start &&
                end == pair.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
