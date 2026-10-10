package startDemo;

import utils.Context;
import utils.concept.Concept;
import utils.concept.Concept_SE_ISI;
import utils.item.ObjectIdMapper;
import utils.rule.RuleParentRelation;
import utils.rule.ThreeWayRuleCandidate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static algorithm.concept.CbOSEISI.CbOSE_ISI_exe;
import static utils.readFile.DecisionContextReader.readFile;
import static utils.util.makeSet;

public class CandidateRuleGenerate_start {
    private static final int ORIGINAL_OBJECT_COUNT = 20;
    private static final int RETAINED_OBJECT_COUNT = 17;

    public static void main(String[] args) throws IOException {
        String conditionFilename = args.length > 0 ? args[0] : defaultConditionPath();
        String decisionFilename = args.length > 1 ? args[1] : defaultDecisionPath();

        Context conditionContext = utils.readFile.FileIncomplete.readFile(conditionFilename);
        Context decisionContext = readFile(decisionFilename);
        validateContexts(conditionContext, decisionContext);
        int objectCount = conditionContext.getObjs_size();
        // The 17-row RESS variants retain original IDs except 1, 6 and 19.
        ObjectIdMapper objectIdMapper = objectCount == ORIGINAL_OBJECT_COUNT
                ? ObjectIdMapper.identity(ORIGINAL_OBJECT_COUNT)
                : ObjectIdMapper.fromDeletedOriginalIds(ORIGINAL_OBJECT_COUNT, 1, 6, 19);

        System.out.println("Dataset mode: " + (objectCount == ORIGINAL_OBJECT_COUNT
                ? "original (20 objects)" : "retained (17 objects; deleted 1, 6, 19)"));

        Queue<Concept_SE_ISI> conditionConcepts = generateConditionConcepts(conditionContext);
        List<Concept> decisionConcepts =
                algorithm.concept.DecisionConceptGenerator.generate(decisionContext);

        List<ThreeWayRuleCandidate> rules = algorithm.rule.ThreeWayRuleCandidateGenerator.generate(
                conditionConcepts,
                decisionConcepts,
                objectIdMapper,
                objectCount
        );
        List<ThreeWayRuleCandidate> nonRedundantRules =
                algorithm.rule.NonRedundantRuleGenerator.generate(rules);
        List<RuleParentRelation> ruleParentRelations =
                algorithm.rule.NonRedundantRuleParentFinder.find(
                        nonRedundantRules,
                        conditionConcepts
                );

        System.out.println("Condition concept count: " + conditionConcepts.size());
        System.out.println("Decision concept count: " + decisionConcepts.size());
        System.out.println("Candidate rule count: " + rules.size());
        System.out.println("Non-redundant rule count: " + nonRedundantRules.size());
        for (int i = 0; i < ruleParentRelations.size(); i++) {
            RuleParentRelation relation = ruleParentRelations.get(i);
            System.out.println("NR " + (i + 1) + ": " + relation.getRule());
            System.out.println("  Direct parent count: " + relation.getDirectParents().size());

            for (int parentIndex = 0;
                 parentIndex < relation.getDirectParents().size();
                 parentIndex++) {
                Concept_SE_ISI parent = relation.getDirectParents().get(parentIndex);
                System.out.println(
                        "  Parent " + (parentIndex + 1) + ": "
                                + "extent_original="
                                + objectIdMapper.formatOriginalSet(parent.getExtent())
                                + ", extent_internal=" + parent.getExtent()
                                + ", B_low=" + parent.getIntent_min()
                                + ", B_high=" + parent.getIntent_max()
                );
            }
        }

        System.out.println("===== 全部条件概念 =====");
        int conditionIndex = 1;
        for (Concept_SE_ISI concept : conditionConcepts) {
            System.out.println(
                    "条件概念 " + conditionIndex++
                            + ": 原始外延="
                            + objectIdMapper.formatOriginalSet(concept.getExtent())
                            + ", B_low=" + concept.getIntent_min()
                            + ", B_high=" + concept.getIntent_max()
            );
        }

        System.out.println("===== 全部决策概念 =====");
        int decisionIndex = 1;
        for (Concept concept : decisionConcepts) {
            System.out.println(
                    "决策概念 " + decisionIndex++
                            + ": 原始外延="
                            + objectIdMapper.formatOriginalSet(concept.getExtent())
                            + ", 决策内涵=" + concept.getIntent()
            );
        }

        System.out.println("===== 全部候选规则 =====");
        int ruleIndex = 1;
        for (ThreeWayRuleCandidate rule : rules) {
            System.out.println("候选规则 " + ruleIndex++ + ": " + rule);
        }
    }

    private static Queue<Concept_SE_ISI> generateConditionConcepts(Context context) {
        Concept_SE_ISI initialConcept = new Concept_SE_ISI();
        initialConcept.setExtent(makeSet(0));
        initialConcept.setIntent_min(makeSet(context.getAttrs_size()));
        initialConcept.setIntent_max(makeSet(context.getAttrs_size()));

        Map<Integer, BitSet> nj = new HashMap<>();
        Queue<Concept_SE_ISI> concepts = new LinkedList<>();
        CbOSE_ISI_exe(context, initialConcept, 1, nj, concepts);
        return concepts;
    }

    private static void validateContexts(Context conditionContext, Context decisionContext) {
        int conditionCount = conditionContext.getObjs_size();
        int decisionCount = decisionContext.getObjs_size();
        if (conditionCount != decisionCount) {
            throw new IllegalArgumentException(
                    "Object count mismatch: condition=" + conditionCount
                            + ", decision=" + decisionCount + ". Both files must describe the same"
                            + " objects in the same row order. For original RESS (20 objects),"
                            + " provide all 20 decision rows, including original objects 1, 6 and 19;"
                            + " decision_example.txt contains only the 17 retained objects."
            );
        }
        if (conditionCount != ORIGINAL_OBJECT_COUNT && conditionCount != RETAINED_OBJECT_COUNT) {
            throw new IllegalArgumentException(
                    "Supported RESS modes: 20 original objects, or 17 objects after deleting"
                            + " original IDs 1, 6 and 19; received " + conditionCount
            );
        }
    }

    private static String defaultConditionPath() {
        return resolvePath("SETest", "RESS_DeleteObj1_6_19_A10.txt");
//        return resolvePath("SETest", "RESS_DelObj1_6_19_A10_100%_+.txt");
//        return resolvePath("SETest", "RESS_DelObj1_6_19_A10_100%_-.txt");
//        return resolvePath("SETest", "RESS_DelObj1_6_19_A10_25%_+.txt");
//        return resolvePath("SETest", "RESS_DelObj1_6_19_A10_50%_+.txt");
//        return resolvePath("SETest", "RESS_DelObj1_6_19_A10_75%_+.txt");
//        return resolvePath("SETest", "RESS_DeleteA10.txt");

    }

    private static String defaultDecisionPath() {
        return resolvePath("DecisionTest", "decision_example.txt");
//        return resolvePath("DecisionTest", "decision_original.txt");
    }

    private static String resolvePath(String directory, String filename) {
        Path fromProjectRoot = Paths.get("src", "BinaryContext", directory, filename);
        if (Files.exists(fromProjectRoot)) {
            return fromProjectRoot.toString();
        }
        return Paths.get("BinaryContext", directory, filename).toString();
    }
}
