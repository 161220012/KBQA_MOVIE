package kbqa;

import bean.Link;
import bean.QuestionTemplate;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import java.util.*;

import static database.Virtuoso_qu.Prefix;
import static database.Virtuoso_qu.vg;
import static kbqa.Sources.classSet;
import static kbqa.Sources.templateMap;

public class Response {
    public static String response(String question) {

        String reply = "";
        List<Link> links = ELNRL.sentenceRec(question);
        if (links.size() >= 1) {
            System.out.println("实体/关系链接结果:" + links);
            //System.out.println(chooseTemplate(links,question));
            List<Map.Entry<String, Double>> targetTemplates = TempControl.chooseTemplate(links, question);
            List<String> tnames = new ArrayList<>(); //最终使用的模板列表
            if (targetTemplates.size() > 0) {
                System.out.println("模板匹配结果:" + targetTemplates);

                for (Map.Entry<String, Double> t : targetTemplates) {
                    tnames.add(t.getKey());
                }
                //System.out.println("模板匹配结果:"+);
            } else {
                System.out.println("模板匹配失败，采用默认模板");
                if (links.size() == 1) {
                    tnames.add("info_1");
                } else if (links.size() == 2) {
                    tnames.add("common_1");
                } else if (links.size() == 3) {
                    tnames.add("judge");
                } else {
                    tnames.add("common_1");
                }
            }

            List<String> result = fillandAsk(tnames, links);
            if (result.size() == 0) {
                reply = "抱歉，这个问题暂时无法回答";
            }
            for (String res : result) {
                reply += res + "\n";
            }

        } else {
            System.out.println("很抱歉，无实体链接/关系链接结果，无法回答这个问题。");
        }

        return reply;

    }

    public static List<String> fillandAsk(List<String> names, List<Link> links) {
        List<String> result = new ArrayList<>();
        for (String name : names) {

            QuestionTemplate t = templateMap.get(name);
            String sparql = t.sparql;
            String newReplyTemp = t.reply;
            List<String> params = t.paramList;
            boolean[] isFilled = new boolean[t.slotList.size()];
            Arrays.fill(isFilled, false);
            List<Link> elinks = new LinkedList<>();
            List<Link> rlinks = new LinkedList<>();
            for (Link link : links) {
                if (classSet.contains(link.entity.type)) {
                    elinks.add(link);
                } else {
                    rlinks.add(link);
                }
            }

            if (elinks.size() == 0) {
                System.out.println("很抱歉，未能成功识别到实体，该问题无法作答");
                return result;
            }

            List<String> eslots = new LinkedList<>();
            List<String> rslots = new LinkedList<>();

            for (int i = 0; i < t.slotList.size(); i++) {
                if (t.slotList.get(i).endsWith("_entity")) {
                    eslots.add(t.slotList.get(i));
                } else {
                    rslots.add(t.slotList.get(i));
                }
            }

            for (int i = 0; i < eslots.size(); i++) {
                String slotName = eslots.get(i);
                String iri = "";
                String mention = "";
                if (i > elinks.size() - 1) {
                    iri = elinks.get(i % elinks.size()).entity.IRI;
                    mention = elinks.get(i % elinks.size()).entity.label;
                } else {
                    iri = elinks.get(i).entity.IRI;
                    mention = elinks.get(i).entity.label;
                }
                sparql = sparql.replaceAll("<" + slotName + ">", iri);
                newReplyTemp = newReplyTemp.replaceAll("<" + slotName + ">", mention);
                //System.out.println(sparql);
            }

            for (int i = 0; i < rslots.size(); i++) {
                String slotName = rslots.get(i);
                String iri = "";
                String mention = "";
                if (i > elinks.size() - 1) {
                    iri = rlinks.get(i % rlinks.size()).entity.IRI;
                    mention = rlinks.get(i % rlinks.size()).entity.label;
                } else {
                    iri = rlinks.get(i).entity.IRI;
                    mention = rlinks.get(i).entity.label;
                }
                sparql = sparql.replaceAll("<" + slotName + ">", iri);
                newReplyTemp = newReplyTemp.replaceAll("<" + slotName + ">", mention);
                System.out.println(sparql);
            }


            System.out.println("final sparql:" + sparql);
            sparql = Prefix + sparql;
            VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, vg);

            if (name.equals("judge")) {
                boolean b = vqe.execAsk();
                String newReply = newReplyTemp;
                for (String param : params) {
                    if (b) {
                        newReply = newReply.replaceAll("#" + param, "是");
                    } else {
                        newReply = newReply.replaceAll("#" + param, "不是");
                    }
                }
                result.add(newReply);
            } else {
                ResultSet resultSet = vqe.execSelect();
                if (!resultSet.hasNext()) {
                    //System.out.println("很抱歉，未查询到相关信息");
                    //return result;
                }
                while (resultSet.hasNext()) {
                    QuerySolution next = resultSet.next();
                    String newReply = newReplyTemp;
                    for (String param : params) {
                        String paramResult = next.get(param).toString();
                        String[] strArr = paramResult.split("#");
                        paramResult = strArr[strArr.length - 1].replace(">", "");
                        newReply = newReply.replaceAll("#" + param, paramResult);
                    }
                    //System.out.println("newRpely:" + newReply);
                    result.add(newReply);
                    //System.out.println(next.get(params.get(0)));
                }
                //System.out.println(resultSet);

            }

        }


        return result;
    }

}
