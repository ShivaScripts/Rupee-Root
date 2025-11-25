import { Trash2 } from "lucide-react";
import { addThousandsSeparator } from "../util/util.js";

const TransactionInfoCard = ({ title, icon, date, amount, type, onDelete, hideDeleteBtn, creatorName }) => {
    return (
        <div className="group relative flex items-center gap-4 p-3 mt-2 rounded-lg hover:bg-gray-50 transition-colors border border-transparent hover:border-gray-100">
            
            {/* Icon Box */}
            <div className="w-12 h-12 rounded-full bg-gray-100 flex items-center justify-center text-2xl shrink-0">
                {icon}
            </div>

            {/* Details */}
            <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2">
                    <p className="text-sm font-medium text-gray-900 truncate">{title}</p>
                    
                    {/* --- CREATOR BADGE --- */}
                    {creatorName && (
                        <span className="px-1.5 py-0.5 text-[10px] font-medium bg-purple-100 text-purple-700 rounded-md border border-purple-200 shrink-0">
                            {creatorName}
                        </span>
                    )}
                    {/* --------------------- */}
                </div>
                <p className="text-xs text-gray-500 mt-0.5">{date}</p>
            </div>

            {/* Amount & Delete */}
            <div className="flex items-center gap-3">
                <p className={`font-semibold text-sm ${type === 'income' ? 'text-green-600' : 'text-red-600'}`}>
                    {type === 'income' ? '+' : '-'} ₹{addThousandsSeparator(amount)}
                </p>
                
                {!hideDeleteBtn && (
                    <button 
                        onClick={onDelete}
                        className="p-2 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-full transition-colors opacity-0 group-hover:opacity-100"
                    >
                        <Trash2 size={16} />
                    </button>
                )}
            </div>
        </div>
    );
};

export default TransactionInfoCard;