package in.shivam.rupeeroot.service;

import in.shivam.rupeeroot.dto.DebtSettlementDTO;
import in.shivam.rupeeroot.entity.ExpenseEntity;
import in.shivam.rupeeroot.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DebtSimplificationService {

    /**
     * Main Algorithm Method
     * Input: All Expenses of a Group, All Members of a Group
     * Output: Simplified List of "Who pays Whom"
     */
    public List<DebtSettlementDTO> simplifyDebts(List<ExpenseEntity> groupExpenses, List<ProfileEntity> groupMembers) {

        // --- 1. FILTERING STEP (Updated) ---
        // We consider two types of transactions:
        // A. Normal Expenses that are Splittable (isSplittable = true)
        // B. Settlement Transactions (isSettlement = true)
        List<ExpenseEntity> validTransactions = groupExpenses.stream()
                .filter(e -> Boolean.TRUE.equals(e.getIsSplittable()) || Boolean.TRUE.equals(e.getIsSettlement()))
                .collect(Collectors.toList());

        if (validTransactions.isEmpty() || groupMembers.size() <= 1) {
            return List.of();
        }

        // Step 2: Calculate Net Balance for each user
        Map<Long, BigDecimal> netBalanceMap = new HashMap<>();
        Map<Long, String> nameMap = new HashMap<>();

        // Initialize everyone with 0
        for (ProfileEntity member : groupMembers) {
            netBalanceMap.put(member.getId(), BigDecimal.ZERO);
            nameMap.put(member.getId(), member.getFullName());
        }

        // Step 3: Process Transactions
        for (ExpenseEntity expense : validTransactions) {
            Long payerId = expense.getProfile().getId();
            BigDecimal amount = expense.getAmount();

            if (Boolean.TRUE.equals(expense.getIsSettlement()) && expense.getSettledTo() != null) {
                // --- LOGIC A: SETTLEMENT (Direct Transfer) ---
                // Payer gives money -> Payer balance INCREASES (Credit)
                // Receiver gets money -> Receiver balance DECREASES (Debit/Debt is paid off)
                Long receiverId = expense.getSettledTo().getId();

                netBalanceMap.put(payerId, netBalanceMap.get(payerId).add(amount));
                netBalanceMap.put(receiverId, netBalanceMap.get(receiverId).subtract(amount));

            } else {
                // --- LOGIC B: GROUP EXPENSE (Split Bill) ---
                // Existing logic: Payer gets credit, everyone shares the debt
                BigDecimal splitAmount = amount.divide(new BigDecimal(groupMembers.size()), 2, RoundingMode.HALF_UP);

                // Payer gets full credit (+)
                netBalanceMap.put(payerId, netBalanceMap.get(payerId).add(amount));

                // Everyone (including payer) gets debit (-) for their share
                for (ProfileEntity member : groupMembers) {
                    Long memberId = member.getId();
                    netBalanceMap.put(memberId, netBalanceMap.get(memberId).subtract(splitAmount));
                }
            }
        }

        // Step 4: Separate Debtors (-) and Creditors (+)
        List<Long> debtors = new ArrayList<>();
        List<Long> creditors = new ArrayList<>();

        for (Map.Entry<Long, BigDecimal> entry : netBalanceMap.entrySet()) {
            // Filter out users with ~0 balance (Floating point tolerance)
            if (entry.getValue().abs().compareTo(new BigDecimal("0.1")) > 0) {
                if (entry.getValue().compareTo(BigDecimal.ZERO) < 0) {
                    debtors.add(entry.getKey());
                } else {
                    creditors.add(entry.getKey());
                }
            }
        }

        // Step 5: Greedy Matching (Recursive-like approach)
        List<DebtSettlementDTO> settlements = new ArrayList<>();

        // Sort to match biggest debtor with biggest creditor (Greedy)
        debtors.sort((id1, id2) -> netBalanceMap.get(id1).compareTo(netBalanceMap.get(id2)));
        creditors.sort((id1, id2) -> netBalanceMap.get(id2).compareTo(netBalanceMap.get(id1)));

        int i = 0;
        int j = 0;

        while (i < debtors.size() && j < creditors.size()) {
            Long debtorId = debtors.get(i);
            Long creditorId = creditors.get(j);

            BigDecimal debtorBalance = netBalanceMap.get(debtorId); // Negative
            BigDecimal creditorBalance = netBalanceMap.get(creditorId); // Positive

            // Find minimum of the two (absolute values)
            BigDecimal amountToSettle = debtorBalance.abs().min(creditorBalance);

            settlements.add(DebtSettlementDTO.builder()
                    .fromUserName(nameMap.get(debtorId))
                    .fromUserId(debtorId) // <--- SET THE DEBTOR ID HERE
                    .toUserName(nameMap.get(creditorId))
                    .amount(amountToSettle.setScale(2, RoundingMode.HALF_UP))
                    .toUserId(creditorId) // Important for the Pay Button
                    .build());

            // Update balances in memory
            BigDecimal newDebtorBalance = debtorBalance.add(amountToSettle);
            BigDecimal newCreditorBalance = creditorBalance.subtract(amountToSettle);

            netBalanceMap.put(debtorId, newDebtorBalance);
            netBalanceMap.put(creditorId, newCreditorBalance);

            // Move pointers if settled (close to zero)
            if (newDebtorBalance.abs().compareTo(new BigDecimal("0.1")) < 0) {
                i++;
            }
            if (newCreditorBalance.abs().compareTo(new BigDecimal("0.1")) < 0) {
                j++;
            }
        }

        return settlements;
    }
}