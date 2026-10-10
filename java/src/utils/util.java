package utils;

import java.io.IOException;
import java.util.BitSet;

public class util {
    /**
     * 由1..n-1构成的集合
     * @param n
     * @return
     */
    public static BitSet get_vj(int n){
        BitSet res=new BitSet();
        if(n==0) return res;
        res.set(1,n,true);
        return res;
    }

    /**
     * 由1..n构成的集合
     * @param n
     * @return
     */
    public static BitSet makeSet(int n){
        BitSet res=new BitSet();
        if(n==0) return res;
        res.set(1,n+1,true);
        return res;
    }

    //构造单点集
    public static BitSet makeSingleSet(int n){
        BitSet res=new BitSet();
        if(n==0) return res;
        res.set(n,true);
        return res;
    }

    /**
     * 集合交运算，并返回新集合
     * @param set1{1,2,3,4,5}
     * @param set2{3,4,6}
     * @return  res{3,4}
     */
    public static BitSet intersection(BitSet set1,BitSet set2){
        BitSet res=new BitSet();
        if(set1.isEmpty()||set2.isEmpty()){
            return res;
        }
        res=(BitSet)set1.clone();
        res.and(set2);
        return res;
    }

    /**
     * 集合交运算，并返回新集合
     * @param set1{1,2,3,4,5}
     * @param set2{3,4,6}
     * @return  res{3,4}
     */
    public static BitSet union (BitSet set1,BitSet set2){
        BitSet res;
        res=(BitSet)set1.clone();
        res.or(set2);
        return res;
    }


    /**
     * 集合是否包含
     * @param set1{1,2,3,4,5}
     * @param set2{1,2,3}
     * @return true
     */
    public static boolean is_subset_eq(BitSet set1, BitSet set2){
        if(set2==null)return true;
        if(set1.isEmpty())return false;

        BitSet s = intersection(set1,set2);
        boolean flag = s.equals(set2);
        return flag;
//        for(int i=set2.nextSetBit(0);i>=0;i=set2.nextSetBit(i+1)){
//            if(!set1.get(i)){
//                return false;
//            }
//        }
//        return true;
    }


    /**
     * 集合是否真包含
     * @param set1{1,2,3,4,5}
     * @param set2{1,2,3}
     * @return true
     */
    public static boolean is_subset(BitSet set1, BitSet set2){
        if (set1.equals(set2)){
            return false;
        }
        return is_subset_eq(set1,set2);
    }

    /**
     * 取多个对象中共同具有的属性集合，对应*算子
     * @param context 形式背景
     * @param objs  对象
     * @return
     */
    public static BitSet get_objs_shared(Context context,BitSet objs){
        if(objs.isEmpty()) return makeSet(context.getAttrs_size());
        BitSet res;
        res=(BitSet) context.getObjs().get(objs.nextSetBit(0)).clone();
        for(int i=objs.nextSetBit(0);i>=0;i=objs.nextSetBit(i+1)){
            res.and(context.getObjs().get(i));
        }
        return res;
    }

    /**
     * 取多个对象中共同不具有的属性，对应$\bar{*}$算子
     * @param context
     * @param objs
     * @return
     */
    public static BitSet get_objs_not_shared(Context context,BitSet objs){
        if(objs.isEmpty()) return makeSet(context.getAttrs_size());
        BitSet res;
        res=(BitSet) context.getObjs_n().get(objs.nextSetBit(0)).clone();
        for(int i=objs.nextSetBit(0);i>=0;i=objs.nextSetBit(i+1)){
            res.and(context.getObjs_n().get(i));
        }
        return res;
    }

    /**
     * 取共同拥有该属性集的对象集合，对应*算子
     * @param context
     * @param attrs
     * @return
     */
    public static BitSet get_attrs_shared(Context context,BitSet attrs){
        if(attrs.isEmpty()) return makeSet(context.getObjs_size());
        BitSet res=new BitSet();

        res=(BitSet) context.getAttrs().get(attrs.nextSetBit(0)).clone();
        //res.or(context.getAttrs().get(attrs.nextSetBit(0)));
        for(int i=attrs.nextSetBit(0);i>=0;i=attrs.nextSetBit(i+1)){
            res.and(context.getAttrs().get(i));
            if (res.isEmpty()) {
                break; // 如果 res 为空，则停止计算，无需继续迭代
            }
        }
        return res;
    }

    /**
     * 取共同不具有该属性集的对象的集合，对应$\bar{*}$算子
     * @param context
     * @param attrs
     * @return
     */
    public static BitSet get_attrs_not_shared(Context context,BitSet attrs){
        if(attrs.isEmpty()) return makeSet(context.getObjs_size());
        BitSet res;
        res=(BitSet) context.getAttrs_n().get(attrs.nextSetBit(0)).clone();
        for(int i=attrs.nextSetBit(0);i>=0;i=attrs.nextSetBit(i+1)){
            res.and(context.getAttrs_n().get(i));
            if (res.isEmpty()) {
                break; // 如果 res 为空，则停止计算，无需继续迭代
            }
        }
        return res;
    }

    /**
     * 集合差运算，并返回集合
     * @param set1{1,2,3,4,5}
     * @param set2{3,4,6}
     * @return res{1,2,5}
     */
    public static BitSet difference(BitSet set1, BitSet set2){
        BitSet res=(BitSet) set1.clone();
        res.andNot(set2);
        return res;
    }
    /**
     * 取不完备形式背景中拥有最小相同属性的对象集合，对应SE-ISI的算子
     * @param context
     * @param attrs
     * @return
     */
    public static BitSet get_attrs_min_shared(Context context,BitSet attrs){
        if(attrs.isEmpty()) return makeSet(context.getObjs_size());
        BitSet res;
        res=(BitSet) context.getAttrs_min().get(attrs.nextSetBit(0)).clone();
        for(int i=attrs.nextSetBit(0);i>=0;i=attrs.nextSetBit(i+1)){
            res.and(context.getAttrs_min().get(i));
            if (res.isEmpty()) {
                break; // 如果 res 为空，则停止计算，无需继续迭代
            }
        }
        return res;
    }

    /**
     * 取不完备形式背景中拥有最大相同属性的对象集合，对应SE-ISI的算子
     * @param context
     * @param attrs
     * @return
     */
    public static BitSet get_attrs_max_shared(Context context,BitSet attrs){
        if(attrs.isEmpty()) return makeSet(context.getObjs_size());
        BitSet res;
        res=(BitSet) context.getAttrs_max().get(attrs.nextSetBit(0)).clone();
        for(int i=attrs.nextSetBit(0);i>=0;i=attrs.nextSetBit(i+1)){
            res.and(context.getAttrs_max().get(i));
            if (res.isEmpty()) {
                break; // 如果 res 为空，则停止计算，无需继续迭代
            }
        }
        return res;
    }


    /**
     * 取不完备形式背景中拥有最小相同属性的对象集合，对应SE-ISI的算子
     * @param context
     * @param objs
     * @return
     */
    public static BitSet get_objs_min_shared(Context context,BitSet objs){
        if(objs.isEmpty()) return makeSet(context.getObjs_size());
        BitSet res;
        res=(BitSet) context.getObjs_min().get(objs.nextSetBit(0)).clone();
        for(int i=objs.nextSetBit(0);i>=0;i=objs.nextSetBit(i+1)){
            res.and(context.getObjs_min().get(i));
            if (res.isEmpty()) {
                break; // 如果 res 为空，则停止计算，无需继续迭代
            }
        }
        return res;
    }

    /**
     * 取不完备形式背景中拥有最大相同属性的对象集合，对应SE-ISI的算子
     * @param context
     * @param objs
     * @return
     */
    public static BitSet get_objs_max_shared(Context context,BitSet objs){
        if(objs.isEmpty()) return makeSet(context.getObjs_size());
        BitSet res;
        res=(BitSet) context.getObjs_max().get(objs.nextSetBit(0)).clone();
        for(int i=objs.nextSetBit(0);i>=0;i=objs.nextSetBit(i+1)){
            res.and(context.getObjs_max().get(i));
            if (res.isEmpty()) {
                break; // 如果 res 为空，则停止计算，无需继续迭代
            }
        }
        return res;
    }



    public static void main(String[] args) throws IOException {



        //System.out.println(is_subset());


    }

}
