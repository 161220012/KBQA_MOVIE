package kbqa;

import bean.Link;
import bean.QuestionTemplate;
import database.Virtuoso_qu;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import utils.TextSimilarity;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

import static database.Virtuoso_qu.vg;
import static database.Virtuoso_qu.Prefix;
import static utils.StringUtil.question_handle;
import static kbqa.Sources.templateMap;
import static kbqa.Sources.invertTypeMap;
import static kbqa.Sources.classSet;
import static kbqa.Sources.relSet;

public class Main {


    public static void main(String[] args) {
        try {
            Virtuoso_qu.init(); //初始化知识库连接
            TextSimilarity.init(); //初始化相似度计算模块
            Sources.init(); //初始化词典资源

            System.out.println("您好,请问需要什么帮助吗?");
            while (true) {
                System.out.print("::  ");
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
                String sentence = input.readLine();
                sentence = question_handle(sentence); //问句预处理
                String reply = "";
                try {
                    reply = Response.response(sentence);
                } catch (Exception o) {
                    o.printStackTrace();
                }
                System.out.print(">>  " + reply + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
