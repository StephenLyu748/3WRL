package startDemo;

import algorithm.concept.CbOSEISI;
import algorithm.concept.DecisionConceptGenerator;
import algorithm.rule.NonRedundantRuleGenerator;
import algorithm.rule.ThreeWayRuleCandidateGenerator;
import utils.Context;
import utils.concept.Concept;
import utils.concept.Concept_SE_ISI;
import utils.item.ObjectIdMapper;
import utils.rule.ThreeWayRuleCandidate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static utils.util.makeSet;

/** Reproduces the four-object Table 2 supplied in the user's screenshot. */
public final class Table2RuleGenerate_start {
    public static void main(String[] args) throws Exception {
        Context condition = utils.readFile.FileIncomplete.readFile(resolve("SETest"));
        Context decision = utils.readFile.DecisionContextReader.readFile(resolve("DecisionTest"));
        if (condition.getObjs_size() != decision.getObjs_size()) {
            throw new IllegalArgumentException("Condition and decision row counts must match");
        }

        Queue<Concept_SE_ISI> concepts = new LinkedList<>();
        Concept_SE_ISI initial = new Concept_SE_ISI(
                makeSet(0), makeSet(condition.getAttrs_size()), makeSet(condition.getAttrs_size()));
        CbOSEISI.CbOSE_ISI_exe(condition, initial, 1, new HashMap<>(), concepts);
        List<Concept> decisions = DecisionConceptGenerator.generate(decision);
        List<ThreeWayRuleCandidate> rules = ThreeWayRuleCandidateGenerator.generate(
                concepts, decisions, ObjectIdMapper.identity(condition.getObjs_size()),
                condition.getObjs_size());
        List<ThreeWayRuleCandidate> nr = NonRedundantRuleGenerator.generate(rules);

        System.out.println("Condition attributes: 1=a, 2=b, 3=c, 4=d, 5=e, 6=f, 7=g");
        System.out.println("Decision attributes: 1=h, 2=i, 3=j");
        System.out.println("Condition concept count: " + concepts.size());
        System.out.println("Decision concept count: " + decisions.size());
        System.out.println("Candidate rule count: " + rules.size());
        System.out.println("Non-redundant rule count: " + nr.size());
        for (int i = 0; i < nr.size(); i++) {
            System.out.println("NR " + (i + 1) + ": " + nr.get(i));
        }
    }

    private static String resolve(String directory) {
        Path path = Paths.get("src", "BinaryContext", directory, "table2.txt");
        if (!Files.exists(path)) {
            path = Paths.get("BinaryContext", directory, "table2.txt");
        }
        return path.toString();
    }
}
