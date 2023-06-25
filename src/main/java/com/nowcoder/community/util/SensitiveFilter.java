package com.nowcoder.community.util;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    //替换夫
    private static final String REPLACEMENT = "***";
    //根节点
    TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        try(//字节流要关闭，所以放在trycatch里
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            //字节流要转成字符流比较方便，然后再转成缓冲流
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            ) {
                String keyword;
                while ((keyword = reader.readLine()) != null) {
                    //加到前缀树
                    this.addKeyword(keyword);
                }


        } catch (IOException e){
            logger.error("加载敏感词文件失败 ：" + e.getMessage());

        }
    }
    //敏感词加到前缀树
    private void addKeyword(String keyword) {
        TrieNode tmpNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            Character c = keyword.charAt(i);
            TrieNode subNode = tmpNode.getSubNode(c);
            if (subNode == null) {
                subNode = new TrieNode();
                tmpNode.addSubNode(c, subNode);
            }
            //指向子节点，进入下轮循环
            tmpNode = subNode;
            //设置结束符
            if (i == keyword.length() - 1) {
                tmpNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤文本
     * @return 过滤后的文本
     */

    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        //指针1
        TrieNode tmpNode = rootNode;
        //指针2和3
        int begin = 0;
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();
        while (position < text.length()) {
            char c = text.charAt(position);
            //判断是否为特殊符号
            if (isSymbol(c)) {
                if (tmpNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            //检查下级节点
            tmpNode = tmpNode.getSubNode(c);
            if (tmpNode == null) {
                sb.append(text.charAt(begin));
                //进入下一位置
                position = ++begin;
                //重新指向根节点
                tmpNode = rootNode;
            } else if (tmpNode.isKeywordEnd()) {
                sb.append(REPLACEMENT);
                //进入下一位置
                begin = ++position;
                //重新指向根节点
                tmpNode = rootNode;
            } else {
                position++;
            }
        }
        //最后一批加入结果
        sb.append(text.substring(begin));
        return sb.toString();

    }
    //判断是否为特殊符号
    private boolean isSymbol(Character c) {
        //0x2E80 ~ 0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //前缀树
    private class TrieNode {

        //关键词结束的标识
        private boolean isKeywordEnd = false;
        //子节点 key是下级字符，value是下级节点，因为可能有多个孩子，所以用map
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }
        //获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

    }


}
