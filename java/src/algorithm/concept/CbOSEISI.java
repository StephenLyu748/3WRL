package algorithm.concept;


import utils.Context;
import utils.concept.Concept_SE_ISI;
import utils.item.queueItem_InClose;

import java.util.*;

import static utils.util.*;

//SE-ISI 概念的生成算法
public class CbOSEISI {
    //private static int id=1;
    public static void CbOSE_ISI_exe(Context context, Concept_SE_ISI concept_se_isi, int v,
                                 Map<Integer, BitSet> nj, Queue<Concept_SE_ISI> res){
        Queue<queueItem_InClose> queue=new LinkedList<>();
        Map<Integer,BitSet> mj=new HashMap<>();
        int obj_size=context.getObjs_size();
        for(int j=v;j<=obj_size;j++){
            BitSet extent=concept_se_isi.getExtent();
            BitSet intent_min=concept_se_isi.getIntent_min();
            BitSet intent_max=concept_se_isi.getIntent_max();
            //Mj <- Nj
            mj.put(j,nj.get(j));
            //j 不属于 A and Nj属于A∩Vj
            if((extent.isEmpty()||!extent.get(j))&&is_subset_eq(intersection(extent,get_vj(j)),nj.get(j))){
                //W <- X ∩ {j}*
                BitSet W=intersection(intent_min,context.getObjs_min().get(j));
                BitSet Z=intersection(intent_max,context.getObjs_max().get(j));
                //if X=W and Y=Z then
                if(intent_min.equals(W) && intent_max.equals(Z)){
                    // A <- A ∪ {j}
                    extent.set(j);
                    concept_se_isi.setExtent(extent);
                }else{
                    // A ∩ Vj
                    BitSet A_Vj=intersection(extent,get_vj(j));
                    // W*j
                    BitSet W_j=intersection(get_attrs_min_shared(context,W),get_vj(j));
                    //Zj
                    BitSet Z_j=intersection(get_attrs_max_shared(context,Z),get_vj(j));
                    BitSet flag = intersection(W_j,Z_j);
                    //System.out.println(t.toString());
                    // if(A ∩ Vj = W*j)then
                    if(A_Vj.equals(flag)){
                        //PutInQueue(W,Z,j)
                        queueItem_InClose newItem=new queueItem_InClose(W,Z,j);
                        queue.offer(newItem);
                    }
                    else{
                        // Mj <- W*j
                        //BitSet W_Z = flag;
                        mj.put(j,flag);
                    }
                }
            }

        }
        //Concept_SE_ISI concept_temp = new Concept_SE_ISI(concept_se_isi.getExtent(), concept_se_isi.getIntent_min(), concept_se_isi.getIntent_max(),id);
        Concept_SE_ISI concept_temp = new Concept_SE_ISI(concept_se_isi.getExtent(), concept_se_isi.getIntent_min(), concept_se_isi.getIntent_max());
        //id++;
        res.offer(concept_temp);

        //while GetFromQueue(W,Z,j) do
        while (!queue.isEmpty()){
            queueItem_InClose item=queue.poll();
            //B<-A∪{j}
            BitSet B=(BitSet)concept_se_isi.getExtent().clone() ;
            B.set(item.getJ());
            Concept_SE_ISI newConcept=new Concept_SE_ISI(B,item.getW(),item.getZ());

            //System.out.println(res.size());
            CbOSE_ISI_exe(context,newConcept,item.getJ()+1,mj,res);
        }

    }
}
