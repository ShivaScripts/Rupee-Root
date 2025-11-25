import { useEffect, useState, useContext } from "react";
import { Clock, Activity } from "lucide-react";
import axiosConfig from "../util/axiosConfig.jsx";
import { API_ENDPOINTS } from "../util/apiEndpoints.js";
import moment from "moment";
import { AppContext } from "../context/AppContext.jsx";

const RecentActivity = () => {
    const [activities, setActivities] = useState([]);
    // We use transactionTrigger to reload this list whenever a WebSocket update comes in!
    const { transactionTrigger } = useContext(AppContext);

    useEffect(() => {
        const fetchActivities = async () => {
            try {
                const response = await axiosConfig.get(API_ENDPOINTS.GET_RECENT_ACTIVITIES);
                if (response.status === 200) {
                    setActivities(response.data);
                }
            } catch (error) {
                console.error("Failed to fetch activities");
            }
        };

        fetchActivities();
    }, [transactionTrigger]); // Auto-refresh on new data

    return (
        <div className="card h-full">
            <div className="flex items-center gap-3 mb-4 border-b border-gray-100 pb-3">
                <div className="p-2 bg-blue-50 text-blue-600 rounded-full">
                    <Activity size={18} />
                </div>
                <h5 className="text-lg font-bold text-gray-800">Group Activity</h5>
            </div>

            <div className="space-y-4 max-h-[300px] overflow-y-auto pr-1 custom-scrollbar">
                {activities.length === 0 ? (
                    <p className="text-gray-400 text-sm text-center py-4">No recent activity</p>
                ) : (
                    activities.map((log) => (
                        <div key={log.id} className="flex gap-3 items-start">
                            <div className="mt-1 min-w-[6px] h-[6px] rounded-full bg-blue-400"></div>
                            <div>
                                <p className="text-sm text-gray-700 font-medium">
                                    {log.description}
                                </p>
                                <div className="flex items-center gap-2 mt-1">
                                    <span className="text-xs text-gray-400 bg-gray-50 px-1.5 py-0.5 rounded border border-gray-100">
                                        {log.userEmail?.split('@')[0]}
                                    </span>
                                    <span className="text-[10px] text-gray-400 flex items-center gap-1">
                                        <Clock size={10} />
                                        {moment(log.timestamp).fromNow()}
                                    </span>
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default RecentActivity;