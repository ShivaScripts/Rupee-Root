import { useEffect, useState, useContext } from "react";
import axiosConfig from "../util/axiosConfig.jsx";
import { API_ENDPOINTS } from "../util/apiEndpoints.js";
import { LoaderCircle, ArrowRight, CheckCircle, Wallet, Banknote, Lock } from "lucide-react";
import { AppContext } from "../context/AppContext.jsx";
import toast from "react-hot-toast";

const DebtSettlement = () => {
    const [debts, setDebts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [settlingId, setSettlingId] = useState(null);
    
    // Get the current logged-in user from Context
    const { user, transactionTrigger, setTransactionTrigger } = useContext(AppContext);

    const fetchDebts = async () => {
        setLoading(true);
        try {
            const response = await axiosConfig.get(API_ENDPOINTS.GET_GROUP_DEBTS);
            if (response.status === 200) {
                setDebts(response.data);
            }
        } catch (error) {
            console.error("Failed to fetch settlements", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchDebts();
    }, [transactionTrigger]);

    const handleSettle = async (debt) => {
        setSettlingId(debt.toUserId);
        try {
            const payload = {
                amount: debt.amount,
                settledToId: debt.toUserId,
                isSettlement: true // Explicitly flag it
            };
            const response = await axiosConfig.post(API_ENDPOINTS.SETTLE_DEBT, payload);
            if (response.status === 200) {
                toast.success(`Paid ₹${debt.amount} to ${debt.toUserName}`);
                setTransactionTrigger(prev => !prev);
            }
        } catch (error) {
            console.error("Settlement failed", error);
            toast.error("Failed to settle debt");
        } finally {
            setSettlingId(null);
        }
    };

    if (loading && debts.length === 0) {
        return (
            <div className="h-full flex flex-col items-center justify-center text-gray-400 gap-2">
                <LoaderCircle className="animate-spin w-8 h-8 text-purple-600" />
                <p className="text-sm">Calculating best way to settle...</p>
            </div>
        );
    }

    return (
        <div className="p-6 h-full overflow-y-auto custom-scrollbar">
            <div className="flex items-center justify-between mb-6">
                <h2 className="text-xl font-bold text-gray-800 flex items-center gap-2">
                    <Wallet className="w-6 h-6 text-purple-600" />
                    Smart Settlements
                </h2>
                <span className="text-xs bg-purple-100 text-purple-700 px-2 py-1 rounded-full font-medium">
                    Minimized Cash Flow
                </span>
            </div>

            {debts.length === 0 ? (
                <div className="flex flex-col items-center justify-center h-64 text-gray-400 gap-3 border-2 border-dashed border-gray-200 rounded-xl">
                    <CheckCircle className="w-12 h-12 text-green-500 opacity-50" />
                    <p className="font-medium">All debts are settled!</p>
                </div>
            ) : (
                <div className="space-y-4">
                    {debts.map((debt, index) => {
                        // Check if the current user is the one who owes money
                        const isMyDebt = user && user.id === debt.fromUserId;

                        return (
                            <div key={index} className="flex items-center justify-between bg-white p-4 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
                                {/* Payer */}
                                <div className="flex flex-col w-1/3">
                                    <span className="text-sm font-semibold text-gray-900">
                                        {isMyDebt ? "You" : debt.fromUserName}
                                    </span>
                                    <span className="text-xs text-red-500 font-medium">Pays</span>
                                </div>

                                {/* Amount & Action */}
                                <div className="flex flex-col items-center justify-center w-1/3 gap-2">
                                    <div className="flex items-center gap-2 w-full justify-center">
                                        <div className="h-[1px] w-full bg-gray-300"></div>
                                        <ArrowRight size={14} className="text-gray-400" />
                                        <div className="h-[1px] w-full bg-gray-300"></div>
                                    </div>
                                    
                                    <div className="flex items-center gap-2">
                                        <span className="font-bold text-gray-800 text-sm">₹{debt.amount}</span>
                                        
                                        {/* Only show Pay button if it's MY debt */}
                                        {isMyDebt ? (
                                            <button
                                                onClick={() => handleSettle(debt)}
                                                disabled={settlingId === debt.toUserId}
                                                className="flex items-center gap-1 bg-green-600 hover:bg-green-700 text-white text-xs px-3 py-1 rounded-md transition-colors disabled:opacity-50"
                                            >
                                                {settlingId === debt.toUserId ? (
                                                    <LoaderCircle className="w-3 h-3 animate-spin" />
                                                ) : (
                                                    <>
                                                        <Banknote className="w-3 h-3" /> Pay
                                                    </>
                                                )}
                                            </button>
                                        ) : (
                                            <span className="flex items-center gap-1 text-gray-400 text-xs bg-gray-100 px-2 py-1 rounded">
                                                <Lock className="w-3 h-3" /> Pending
                                            </span>
                                        )}
                                    </div>
                                </div>

                                {/* Receiver */}
                                <div className="flex flex-col items-end w-1/3">
                                    <span className="text-sm font-semibold text-gray-900">
                                        {user && user.id === debt.toUserId ? "You" : debt.toUserName}
                                    </span>
                                    <span className="text-xs text-green-600 font-medium">Receives</span>
                                </div>
                            </div>
                        );
                    })}
                </div>
            )}
        </div>
    );
};

export default DebtSettlement;