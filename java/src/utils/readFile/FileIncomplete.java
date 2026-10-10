package utils.readFile;


import utils.Context;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class FileIncomplete {
    public static Context readFile(String filename)throws IOException {
        Context res=new Context();
        Scanner sc=new Scanner(new FileReader(filename));
        //每一行中元素以“，”分隔
        String[] s=sc.nextLine().split(",");
        //第一行中有两个数据，第一个为形式背景行数row，第二个为形式背景列数col
        int row=Integer.parseInt(s[0]);
        int col=Integer.parseInt(s[1]);
        res.setObjs_size(row);
        res.setAttrs_size(col);
        //attrs_min 为最小完备化形式背景下该属性共有的对象集合
        Map<Integer,BitSet> attrs_min =new HashMap<>();
        //attrs_max 为最大完备化形式背景下该属性共有的对象集合
        Map<Integer,BitSet> attrs_max =new HashMap<>();
        //
        Map<Integer,BitSet> attrs_incomplete =new HashMap<>();
        //obj_min 为最小完备化形式背景下该属性共有的对象集合
        Map<Integer,BitSet> objs_min =new HashMap<>();
        //obj_max 为最大完备化形式背景下该属性共有的对象集合
        Map<Integer,BitSet> objs_max =new HashMap<>();


        int[][] context=new int[row][col];
        //对象为行，属性为列
        for(int i=0;i<row;i++){
            String[] temp=sc.nextLine().split(",");
            System.out.println(Arrays.toString(temp));
            BitSet obj_max=new BitSet();
            BitSet obj_min=new BitSet();
            for(int j=0;j<col;j++) {
                if (temp[j].equals("?")) {
                    context[i][j] = 3;
                    obj_max.set(j + 1);
                } else {
                    context[i][j] = Integer.parseInt(temp[j]);
                    //SE-ISI概念
                    if (context[i][j] == 1) {
                        obj_max.set(j + 1);
                        obj_min.set(j + 1);
                    }
                    //第二种SE-ISI概念
//                    if (context[i][j] == 0) {
//                        obj_max.set(j + 1);
//                        obj_min.set(j + 1);
//                    }
                }
            }
            objs_max.put(i+1,obj_max);
            objs_min.put(i+1,obj_min);

        }
        res.setObjs_max(objs_max);
        res.setObjs_min(objs_min);
        //转置形式背景中二元关系，对象变为列，属性变为行，
        int[][] context_t=transpose(row,col,context);
        for(int i=0;i<col;i++){
            BitSet attr_max=new BitSet();
            BitSet attr_min=new BitSet();
            BitSet attr_incomplete=new BitSet();
            for(int j=0;j<row;j++){
                if (context_t[i][j]==3){
                    //获取有？关系的背景
                    attr_incomplete.set(j+1);
                    attr_max.set(j+1);
                }
                else{
                    if (context_t[i][j]==1){
                        attr_max.set(j+1);
                        attr_min.set(j+1);
                    }
                    //第二种SE-ISI概念
//                    if (context_t[i][j]==0){
//                        attr_max.set(j+1);
//                        attr_min.set(j+1);
//                    }
                }

            }
            attrs_max.put(i+1,attr_max);
            attrs_min.put(i+1,attr_min);
            attrs_incomplete.put(i+1,attr_incomplete);
        }
        res.setAttrs_max(attrs_max);
        res.setAttrs_min(attrs_min);
        res.setAttrs_incomplete(attrs_incomplete);

        return res;
    }

    private static int[][] transpose(int row, int col, int[][] context) {
        int[][] res=new int[col][row];
        for(int i=0;i<row;i++){
            for(int j=0;j<col;j++){
                res[j][i]=context[i][j];
            }
        }
        return res;
    }
}
