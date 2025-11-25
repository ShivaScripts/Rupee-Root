import moment from "moment";
import { Download, Mail } from "lucide-react";
import TransactionInfoCard from "./TransactionInfoCard.jsx";
import { useContext } from "react";
import { AppContext } from "../context/AppContext.jsx";

const ExpenseList = ({ transactions, onDelete, onDownload, onEmail }) => {
    const { user } = useContext(AppContext);
    const showCreator = user?.groupId != null;

    return (
        <div className="card">
            <div className="flex items-center justify-between">
                <h5 className="text-lg">All Expenses</h5>
                <div className="flex items-center justify-end gap-2">
                    <button className="card-btn" onClick={onEmail}>
                        <Mail size={15} className="text-base" /> Email
                    </button>
                    <button className="card-btn" onClick={onDownload}>
                        <Download size={15} className="text-base" /> Download
                    </button>
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4"> {/* Added gap for better spacing */}
                {transactions?.map((expense) => (
                    <TransactionInfoCard
                        key={expense.id}
                        title={expense.name}
                        icon={expense.icon}
                        date={moment(expense.date).format("Do MMM YYYY")}
                        amount={expense.amount}
                        type="expense"
                        onDelete={() => onDelete(expense.id)}
                        creatorName={showCreator ? expense.creatorName : null}
                    />
                ))}
            </div>
        </div>
    );
};

export default ExpenseList;