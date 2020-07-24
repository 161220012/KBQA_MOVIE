package kbqa;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import utils.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class Test {

    public static void main(String[] args){

        /*String regex = "^.*了什么.*$";
        String question = "绿皮书讲了什么？";
        System.out.println(question.matches(regex));*/

        System.out.println(HanLP.segment("绿皮书讲了什么?"));

        /*String regex = "^[^是]*是[^是]*$";
        //Pattern pattern = Pattern.compile(regex);
        String question = "谁主演了美国丽人？";
        System.out.println(question.matches(regex));
        question = "李安是断背山的导演吗？";
        System.out.println(question.matches(regex));
        question = "李安是断背山吗？";
        System.out.println(question.matches(regex));*/
        //System.out.println(HanLP.segment("李安是断背山的导演吗？"));

        /*CoNLLSentence sentence = HanLP.parseDependency("徐先生还具体帮助他确定了把画雄鹰、松鼠和麻雀作为主攻目标。");
        System.out.println(sentence);
        // 可以方便地遍历它
        for (CoNLLWord word : sentence)
        {
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
        }
        // 也可以直接拿到数组，任意顺序或逆序遍历
        CoNLLWord[] wordArray = sentence.getWordArray();
        for (int i = wordArray.length - 1; i >= 0; i--)
        {
            CoNLLWord word = wordArray[i];
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
        }
        // 还可以直接遍历子树，从某棵子树的某个节点一路遍历到虚根
        CoNLLWord head = wordArray[12];
        while ((head = head.HEAD) != null)
        {
            if (head == CoNLLWord.ROOT) System.out.println(head.LEMMA);
            else System.out.printf("%s --(%s)--> ", head.LEMMA, head.DEPREL);
        }*/


        /*List<String> strings = FileUtil.ReadFileList("data/QA/movie_triples.nt");
        //List<String> entities = new LinkedList<>();
        //List<String> properties = new LinkedList<>();
        HashMap<String,String> entityMap = new HashMap<>();
        HashMap<String,String> relMap = new HashMap<>();

        HashSet<String> classSet = new HashSet<>(Arrays.asList("<http://ws.nju.edu.cn/tcqa#Person>","<http://ws.nju.edu.cn/tcqa#Institution>","<http://ws.nju.edu.cn/tcqa#Movie>","<http://ws.nju.edu.cn/tcqa#Other>"));
        HashSet<String> relSet = new HashSet<>(Arrays.asList("<http://www.w3.org/1999/02/22-rdf-syntax-ns#Relation>"));

        for(String line:strings){
            //line = line.replaceAll("\\.","");
            line = line.substring(0,line.length()-1);
            String[] split = line.split("\\s");
            System.out.println(line);
            if(classSet.contains(split[2].trim())){
                entityMap.putIfAbsent(split[0].trim(),split[2].trim());
                *//*if(!entities.contains(split[0].trim())){
                    entities.add(split[0].trim());
                }*//*
            }
            if(relSet.contains(split[2].trim())){
                relMap.putIfAbsent(split[0].trim(),split[2].trim());
                *//*if(!properties.contains(split[0].trim())){
                    properties.add(split[0].trim());
                }*//*
            }

            //System.out.println(Arrays.toString(line.split("\t")));
        }

        //System.out.println(entities);
        //System.out.println(properties);

        try {
            BufferedWriter bfw = new BufferedWriter(new FileWriter(new File("output/entities.txt")));
            for(String e:entityMap.keySet()){
                bfw.write(e+"\t"+entityMap.get(e)+"\n");
            }
            bfw.close();
            BufferedWriter bfw1 = new BufferedWriter(new FileWriter(new File("output/properties.txt")));
            for(String p:relMap.keySet()){
                bfw1.write(p+"\t"+relMap.get(p)+"\n");
            }
            bfw1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


    }

}
