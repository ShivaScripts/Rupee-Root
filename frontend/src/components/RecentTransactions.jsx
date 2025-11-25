import { ArrowRight } from "lucide-react";
import TransactionInfoCard from "./TransactionInfoCard.jsx";
import moment from "moment";
import { useContext } from "react";
import { AppContext } from "../context/AppContext.jsx";

const RecentTransactions = ({ transactions, onMore }) => {
    const { user } = useContext(AppContext);
    const showCreator = user?.groupId != null; // Only show name if in a group

    return (
        <div className="card">
            <div className="flex items-center justify-between">
                <h4 className="text-lg">Recent Transactions</h4>
                <button className="card-btn" onClick={onMore}>
                    More <ArrowRight className="text-base" size={15} />
                </button>
            </div>

            <div className="mt-6">
                {transactions?.slice(0, 5)?.map(item => (
                    <TransactionInfoCard
                        key={item.id}
                        title={item.name}
                        icon={item.icon}
                        date={moment(item.date).format("Do MMM YYYY")}
                        amount={item.amount}
                        type={item.type}
                        hideDeleteBtn
                        // Pass the name only if we are in a group
                        creatorName={showCreator ? item.creatorName : null} 
                    />
                ))}
            </div>
        </div>
    );
};

export default RecentTransactions;