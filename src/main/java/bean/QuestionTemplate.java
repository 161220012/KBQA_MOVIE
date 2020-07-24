package bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestionTemplate {

    public String name; //Template名
    public List<String> slotList = new ArrayList<>(); //slot名列表
    public HashMap<String,List<String>> slotMap = new HashMap<>(); //slot名对应接受的type
    public List<String> paramList  = new ArrayList<>(); //param列表
    public String sparql; //sparql模板
    public List<String> regex = new ArrayList<>(); //正则模板列表
    public List<String> trigger = new ArrayList<>(); //触发词列表
    public String info; //Template相关信息
    public String reply; //回答
    public int entityNum; //实体个数
    public int relationNum; //关系个数




    public static QuestionTemplate fromJSON(JSONObject o){
        return JSONObject.toJavaObject(o,QuestionTemplate.class);
    }


    public static void main(String[] args){
        String str = FileUtil.ReadFileString("data/QA/question_templates.json");

        JSONArray array = JSONArray.parseArray(str);

        List<QuestionTemplate> questionTemplates = array.toJavaList(QuestionTemplate.class);
        for(QuestionTemplate t:questionTemplates){
            System.out.println(t);
        }



    }

    @Override
    public String toString() {
        return "QuestionTemplate{" +
                "name='" + name + '\'' +
                ", slotList=" + slotList +
                ", slotMap=" + slotMap +
                ", paramList=" + paramList +
                ", sparql='" + sparql + '\'' +
                ", regex=" + regex +
                ", trigger=" + trigger +
                ", info='" + info + '\'' +
                ", reply='" + reply + '\'' +
                ", entityNum=" + entityNum +
                ", relationNum=" + relationNum +
                '}';
    }
}
