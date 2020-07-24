package kbqa;

import bean.Link;
import bean.QuestionTemplate;

import java.util.*;

import static kbqa.Sources.*;
import static kbqa.Sources.relSet;

public class TempControl {

    public static List<Map.Entry<String, Double>> chooseTemplate(List<Link> links, String sentence) {

        HashSet<String> types = new HashSet<>();
        for (Link link : links) {
            types.add(link.entity.type);
        }
        HashSet<String> relatedTemplates = new HashSet<>();
        for (String type : types) {
            relatedTemplates.addAll(invertTypeMap.get(type));
        }

        if (relatedTemplates.size() > 0) {
            //System.out.println("相关模板:" + relatedTemplates);
        } else {
            //System.out.println("问句" + sentence + "未识别出模板");
            return new LinkedList<>();
        }

        HashMap<String, Double> templateScoreMap = new HashMap<>();

        for (String name : relatedTemplates) {
            QuestionTemplate t = templateMap.get(name);
            // 根据sentence,link和template进行打分
            templateScoreMap.put(name, rateTemplate(sentence, links, t));
        }

        //System.out.println(templateScoreMap);
        //List<String> templateNames = new LinkedList<>(templateScoreMap.keySet());
        //templateNames.sort((o1, o2) -> (int) Math.round(1000000 * (templateScoreMap.get(o2) - templateScoreMap.get(o1))));

        List<Map.Entry<String, Double>> targetTemplates = new LinkedList<>(templateScoreMap.entrySet());


        List<Map.Entry<String, Double>> result = new LinkedList<>();
        for (int i = 0; i < targetTemplates.size(); i++) {
            if (targetTemplates.get(i).getValue() >= 0.5) {
                result.add(targetTemplates.get(i));
            }
        }

        result.sort(((o1, o2) -> (int) Math.round(1000000*(o2.getValue() - o1.getValue()))));

        return result;

    }

    public static double rateTemplate(String sentence, List<Link> links, QuestionTemplate t) {

        double triggerScore = 0.0;
        int triggerHit = 0;
        for (String trigger : t.trigger) {
            //System.out.println("sentence:"+sentence+"\t trigger:"+trigger);
            if (sentence.contains(trigger)) {
                //sentenceScore+=0.5;
                triggerHit++;
            }
        }
        if (triggerHit > 0) {
            triggerScore = 1.0;
        }

        double regexScore = 0.0;
        int regexHit = 0;
        for (String regex : t.regex) {
            if (sentence.matches(regex)) {
                regexHit++;
            }
        }
        if (regexHit > 0) {
            regexScore = 1.0;
        }

        int elinkNum = 0;
        int rlinkNum = 0;
        for (Link link : links) {
            //统计link 个数
            if (classSet.contains(link.entity.type)) {
                elinkNum++;
            } else if (relSet.contains(link.entity.type)) {
                rlinkNum++;
            }
        }

        double linkNumScore = 0;
        if (elinkNum == t.entityNum) {
            linkNumScore += 0.5;
        }
        if (rlinkNum == t.relationNum) {
            linkNumScore += 0.5;
        }


        int typeMatch = 0;
        for (int i = 0; i < Math.min(links.size(), t.slotList.size()); i++) {
            String linkType = links.get(i).entity.type;
            List<String> slotACType = t.slotMap.get(t.slotList.get(i));
            if (slotACType.contains(linkType)) {
                typeMatch++;
            }
        }
        int totalSlot = Math.max(links.size(), t.slotList.size());
        double typeMatchScore = typeMatch / ((double) (totalSlot));

        //System.out.println(t.name+":"+triggerScore+" "+regexScore+" "+linkNumScore+" "+typeMatchScore);
        double finalScore = (triggerScore + regexScore + 2 * (0.7 * linkNumScore + 0.3 * typeMatchScore)) / 4;

        return finalScore;
    }
}
