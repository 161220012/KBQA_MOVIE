package kbqa;

import bean.QuestionTemplate;
import com.alibaba.fastjson.JSONArray;
import utils.FileUtil;
import utils.StringUtil;
import utils.TxtUtil;

import java.util.*;

public class Sources {


    public static List<QuestionTemplate> templates;
    public static HashMap<String,QuestionTemplate> templateMap; //记录templateName和template的对应
    public static HashMap<String, Set<String>> invertTypeMap; //倒排索引，记录type和related Template
    public static HashMap<String, String> entityMap; //key是iri，value是type
    public static HashMap<String, String> relationMap; //key是iri，value是type
    public static HashMap<String, String> entityLabelMap; //key是label，value是iri
    public static HashMap<String, String> relationLabelMap; //key是label, value是iri
    public static final HashSet<String> classSet = new HashSet<>(Arrays.asList("<http://ws.nju.edu.cn/tcqa#Person>","<http://ws.nju.edu.cn/tcqa#Institution>","<http://ws.nju.edu.cn/tcqa#Movie>","<http://ws.nju.edu.cn/tcqa#Other>"));
    public static final HashSet<String> relSet = new HashSet<>(Arrays.asList("<http://www.w3.org/1999/02/22-rdf-syntax-ns#Relation>"));


    public static void init(){

        try {

            //初始化Templates
            JSONArray array = JSONArray.parseArray(FileUtil.ReadFileString("data/QA/question_templates.json"));
            templates = array.toJavaList(QuestionTemplate.class);
            templateMap = new HashMap<>();
            for(QuestionTemplate t:templates){
                templateMap.putIfAbsent(t.name,t);
            }

            //初始化倒排索引
            invertTypeMap = new HashMap<>();
            for(QuestionTemplate t:templates){
                String templateName = t.name;
                for(Map.Entry<String,List<String>> entry: t.slotMap.entrySet()){
                    for (String type:entry.getValue()){
                        invertTypeMap.putIfAbsent(type,new HashSet<>());
                        invertTypeMap.get(type).add(templateName);
                    }
                }
            }
            //System.out.println(invertTypeMap);

            entityMap = new HashMap<>();
            //List<String> entityStrings = TxtUtil.ReadFileList("output/entities.txt");
            List<String> entityStrings = FileUtil.ReadFileList("data/QA/entities.txt");
            for(String str:entityStrings){
                String[] split = str.split("\t");
                entityMap.putIfAbsent(split[0].trim(),split[1].trim());
            }

            relationMap = new HashMap<>();
            //List<String> relStrings = TxtUtil.ReadFileList("output/properties.txt");
            List<String> relStrings = FileUtil.ReadFileList("data/QA/properties.txt");
            for(String str:relStrings){
                String[] split = str.split("\t");
                relationMap.putIfAbsent(split[0].trim(),split[1].trim());
            }

            //System.out.println(entityMap);
            //System.out.println(relationMap);

            entityLabelMap = new HashMap<>();
            for(String key:entityMap.keySet()){
                String[] split = key.split("#");
                String label = split[1].replace(">","");
                entityLabelMap.putIfAbsent(label,key);
            }

            relationLabelMap = new HashMap<>();
            for(String key:relationMap.keySet()){
                String[] split = key.split("#");
                String label = split[1].replace(">","");
                relationLabelMap.putIfAbsent(label,key);
            }

            //System.out.println(entityLabelMap);
            //System.out.println(relationLabelMap);


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args){
        Sources.init();
    }


}
