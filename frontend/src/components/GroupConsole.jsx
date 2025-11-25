import { useState } from "react";
import { Activity, MessageCircle, Scale } from "lucide-react"; // Added Scale icon
import RecentActivity from "./RecentActivity.jsx";
import GroupChat from "./GroupChat.jsx";
import DebtSettlement from "./DebtSettlement.jsx"; // Import new component

const GroupConsole = () => {
    const [activeTab, setActiveTab] = useState("activity");

    // Helper function for tab styles
    const getTabClass = (tabName) => `
        flex-1 py-4 text-sm font-semibold flex items-center justify-center gap-2 transition-colors 
        ${activeTab === tabName 
            ? "text-purple-700 border-b-2 border-purple-700 bg-purple-50/30" 
            : "text-gray-500 hover:text-gray-700 hover:bg-gray-50"}
    `;

    return (
        <div className="card p-0 overflow-hidden h-full flex flex-col">
            {/* Header / Tabs */}
            <div className="flex border-b border-gray-100">
                <button
                    onClick={() => setActiveTab("activity")}
                    className={getTabClass("activity")}
                >
                    <Activity size={18} />
                    Activity
                </button>
                <button
                    onClick={() => setActiveTab("chat")}
                    className={getTabClass("chat")}
                >
                    <MessageCircle size={18} />
                    Chat
                </button>
                {/* --- NEW TAB --- */}
                <button
                    onClick={() => setActiveTab("debts")}
                    className={getTabClass("debts")}
                >
                    <Scale size={18} />
                    Settlements
                </button>
            </div>

            {/* Content Area */}
            <div className="flex-1 bg-white overflow-hidden">
                {activeTab === "activity" && (
                    <div className="p-5 h-full overflow-y-auto">
                        <RecentActivity />
                    </div>
                )}
                {activeTab === "chat" && <GroupChat />}
                {activeTab === "debts" && <DebtSettlement />}
            </div>
        </div>
    );
};

export default GroupConsole;