import Dashboard from "../components/Dashboard.jsx";
import {useUser} from "../hooks/useUser.jsx";
import InfoCard from "../components/InfoCard.jsx";
import {Coins, Wallet, WalletCards} from "lucide-react";
import {addThousandsSeparator} from "../util/util.js";
import {useNavigate} from "react-router-dom";
import {useEffect, useState, useContext} from "react"; 
import axiosConfig from "../util/axiosConfig.jsx";
import {API_ENDPOINTS} from "../util/apiEndpoints.js";
import RecentTransactions from "../components/RecentTransactions.jsx";
import FinanceOverview from "../components/FinanceOverview.jsx";
import Transactions from "../components/Transactions.jsx";
import {AppContext} from "../context/AppContext.jsx"; 
import WhatIfCalculator from "../components/WhatIfCalculator.jsx";
import GroupConsole from "../components/GroupConsole.jsx"; // --- IMPORT THIS ---

const Home = () => {
    useUser();

    const { transactionTrigger } = useContext(AppContext);

    const navigate = useNavigate();
    const [dashboardData, setDashboardData] = useState(null);
    const [loading, setLoading] = useState(false);

    const fetchDashboardData = async () => {
        if (loading) return;

        setLoading(true);

        try {
            const response = await axiosConfig.get(API_ENDPOINTS.DASHBOARD_DATA);
            if (response.status === 200) {
                setDashboardData(response.data);
            }
        } catch (error) {
            console.error('Something went wrong while fetching dashboard data:', error);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        fetchDashboardData();
        return () => {};
    }, [transactionTrigger]); 

    return (
        <div>
            <Dashboard activeMenu="Dashboard">
                <div className="my-5 mx-auto">
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                        {/* Display the cards*/}
                        <InfoCard
                            icon={<WalletCards />}
                            label="Total Balance"
                            value={addThousandsSeparator(dashboardData?.totalBalance || 0)}
                            color="bg-purple-800"
                        />
                        <InfoCard
                            icon={<Wallet />}
                            label="Total Income"
                            value={addThousandsSeparator(dashboardData?.totalIncome || 0)}
                            color="bg-green-800"
                        />
                        <InfoCard
                            icon={<Coins />}
                            label="Total Expense"
                            value={addThousandsSeparator(dashboardData?.totalExpense || 0)}
                            color="bg-red-800"
                        />
                    </div>
                    
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-6">
                        {/* Recent transactions */}
                        <RecentTransactions
                            transactions={dashboardData?.recentTransactions}
                            onMore={() => navigate("/expense")}
                        />

                        {/* finance overview chart */}
                        <FinanceOverview
                            totalBalance={dashboardData?.totalBalance || 0}
                            totalIncome={dashboardData?.totalIncome || 0}
                            totalExpense={dashboardData?.totalExpense || 0}
                        />

                        {/* WHAT-IF CALCULATOR */}
                        <div className="md:col-span-2 lg:col-span-1">
                             <WhatIfCalculator 
                                totalIncomeThisMonth={dashboardData?.totalIncome || 0}
                                totalExpenseThisMonth={dashboardData?.totalExpense || 0}
                             />
                        </div>

                        {/* --- GROUP CONSOLE (ACTIVITY + CHAT) --- */}
                        <div className="md:col-span-2 lg:col-span-1">
                             <GroupConsole />
                        </div>
                        {/* -------------------------------------- */}

                        {/* Expense transactions */}
                        <Transactions
                            transactions={dashboardData?.recent5Expenses || []}
                            onMore={() => navigate("/expense")}
                            type="expense"
                            title="Recent Expenses"
                        />

                        {/* Income transactions */}
                        <Transactions
                            transactions={dashboardData?.recent5Incomes || []}
                            onMore={() => navigate("/income")}
                            type="income"
                            title="Recent Incomes"
                        />
                    </div>
                </div>
            </Dashboard>
        </div>
    )
}

export default Home;