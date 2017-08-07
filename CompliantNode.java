import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {
    private static final int REPETITION_CRITERIA = 2;

    private boolean[] followees;
    private Set<Transaction> proposalTransactions = new HashSet<Transaction>();
    private HashMap<Integer, Integer> txRepetition = new HashMap<Integer, Integer>();
    
    private Set<Integer> trustedFollowees = new HashSet<Integer>();
    private HashMap<Integer, Integer> followeeProposedTxCnt = new HashMap<Integer, Integer>();

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        // IMPLEMENT THIS
    }

    public void setFollowees(boolean[] followees) {
        this.followees = followees;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        proposalTransactions.addAll(pendingTransactions);
        for (Transaction tx: pendingTransactions) {
            txRepetition.put(tx.id, 1);
        }
    }

    public Set<Transaction> sendToFollowers() {
        return proposalTransactions;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        HashMap<Integer, Integer> currFolloweeProposedTxCnt = new HashMap<Integer, Integer>();

        for (Candidate candidate: candidates) {
            int sender = candidate.sender;
            if (!followees[sender]) {
                continue;
            }

            if (!currFolloweeProposedTxCnt.containsKey(sender)) {
                currFolloweeProposedTxCnt.put(sender, 1);
            } else {
                currFolloweeProposedTxCnt.put(sender, currFolloweeProposedTxCnt.get(sender) + 1);
            }
        }
        
        Iterator<Entry<Integer, Integer>> it = currFolloweeProposedTxCnt.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Integer, Integer> pair = it.next();
            int followee = pair.getKey();
            int currentProposedTxCnt = pair.getValue();
            
            if (!followeeProposedTxCnt.containsKey(followee)) {
                followeeProposedTxCnt.put(followee, currentProposedTxCnt);
                continue;
            }
            
            if (!trustedFollowees.contains(followee) && currentProposedTxCnt > followeeProposedTxCnt.get(followee)) {
                trustedFollowees.add(followee);
                followeeProposedTxCnt.put(followee, currentProposedTxCnt);
            }
        }
        
        for (Candidate candidate: candidates) {
            int sender = candidate.sender;
            if (!followees[sender]) {
                continue;
            }

            if (trustedFollowees.contains(sender)) {
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
}
