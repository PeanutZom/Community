package com.xinhao.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @Xinhao
 * @Date 2022/1/27
 * @Descrption
 */
@Component
public class SensitiveFilter {

    private TrieNode root = new TrieNode();
    private static Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    private String replacement = "*";

    @PostConstruct
    public void initTree(){
        try (
                InputStream inputStream = SensitiveFilter.class.getClassLoader().getResourceAsStream("sensitive-word.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                ){
            String keyword;
            while((keyword = bufferedReader.readLine())!=null){
                this.insertKeyword(keyword);
            }
            bufferedReader.readLine();
        } catch (IOException e) {
            logger.error("读取敏感词文件失败！"+e.getMessage());
        }
    }

    /**
     * 将一个keyword插入树中
     * @param keyword
     */
    public void insertKeyword(String keyword){
        char[] keywordArr = keyword.toCharArray();
        TrieNode cur = root;
        for(char keywordChar : keywordArr){
            if(cur.getChild(keywordChar)==null){
                cur.addChild(keywordChar, new TrieNode());
            }
            cur = cur.getChild(keywordChar);
        }
        cur.setKeyWordEnd(true);
    }

    /**
     * 此方法实现核心功能
     * @param rawContent
     * @return
     */
    public String filter(String rawContent){
        StringBuilder sb = new StringBuilder();
        char[] rawContentArr = rawContent.toCharArray();
        TrieNode cur = root;
        int leftCur;
        int rightCur;
        for(leftCur = 0; leftCur < rawContentArr.length; leftCur++){
            for(rightCur = leftCur; rightCur < rawContentArr.length; rightCur++){
                if(cur.getChild(rawContentArr[rightCur]) == null){
                    sb.append(rawContentArr[leftCur]);
                    cur = root;
                    break;
                }else{
                    cur = cur.getChild(rawContentArr[rightCur]);
                    if(cur.isKeyWordEnd()){
                        sb.append(replacement);
                        cur = root;
                        leftCur = rightCur;
                        break;
                    }
                }
            }

        }
        return sb.toString();
    }


    private class TrieNode{
        private boolean isKeyWordEnd = false;
        private Map<Character, TrieNode> children = new HashMap<>();

        public TrieNode getChild(char c) {
            return children.get(c);
        }
        public void addChild(char c, TrieNode node){
            children.put(c, node);
        }

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }
    }

}
