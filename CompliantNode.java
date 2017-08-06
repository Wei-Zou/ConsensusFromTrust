import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {
    private static final int REPETITION_CRITERIA = 2;

    private boolean[] followees;
    private Set<Transaction> initTransactions;
    private Set<Transaction> proposalTransactions = new HashSet<Transaction>();
    private HashMap<Integer, Integer> txRepetition = new HashMap<Integer, Integer>();

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        // IMPLEMENT THIS
    }

    public void setFollowees(boolean[] followees) {
        this.followees = followees;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        initTransactions = pendingTransactions;
        proposalTransactions.addAll(pendingTransactions);
        for (Transaction tx: pendingTransactions) {
            txRepetition.put(tx.id, 1);
        }
    }

    public Set<Transaction> sendToFollowers() {
        return proposalTransactions;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        for (Candidate candidate: candidates) {
            if (!followees[candidate.sender]) {
                continue;
            }

            Transaction candidateTx = candidate.tx;
            int txId = candidateTx.id;

            if (txRepetition.containsKey(txId)) {
                txRepetition.put(txId, txRepetition.get(txId) + 1);
                if (txRepetition.get(txId) >= REPETITION_CRITERIA) {
                    proposalTransactions.add(candidateTx);
                }
            } else {
                txRepetition.put(txId, 1);
            }
        }
    }
}
